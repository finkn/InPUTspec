/*-- $Copyright (c) 2014 Christoffer Fink$

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package net.finkn.inputspec.v050;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.finkn.inputspec.tools.Helper;
import net.finkn.inputspec.tools.types.AccessorTester;
import net.finkn.inputspec.tools.types.InitTester;

import org.junit.Before;
import org.junit.Test;

import se.miun.itm.input.model.InPUTException;
import se.miun.itm.input.model.design.IDesign;

/**
 * Tests how/when parameters are initialized in InPUT4j v0.5.
 * {@link #creatingDesignDoesNotTriggerInitialization} and
 * {@link #fetchingOuterParameterTriggersInitialization} together show that
 * initialization is lazy.
 * {@link #fetchingInnerDoesNotTriggerInitialization} and
 * {@link #settingInnerDoesNotTriggerInitialization} demonstrate
 * even more laziness. That is, even accessing the inner parameter
 * does not cause the outer parameter to be initialized.
 *
 * @author Christoffer Fink
 */
public class SparamInitTest {

  private final AccessorTest.TestParameters test =
    AccessorTest.TestParameters.builder()
    .inner(SparamTest.DATA_PARAM)
    .outer(SparamTest.TESTER_PARAM)
    .type(AccessorTester.class)
    .build();

  private final String testerId = test.testerParam.getId();
  private final String dataId = test.dataId;

  private IDesign design;

  @Before
  public void setup() throws InPUTException {
    InitTester.resetGlobal();
    design = Helper.design(test.withoutConstructor, test.testerParam);
  }

  /** Creating a design does not initialize the parameter. */
  @Test
  public void creatingDesignDoesNotTriggerInitialization() throws Throwable {
    int instanceBaseline = InitTester.getInstanceCount();
    Helper.design(test.withoutConstructor, test.testerParam);
    int instanceCount = InitTester.getInstanceCount();
    assertThat(instanceCount, is(equalTo(instanceBaseline)));
  }

  /**
   * Fetching the outer parameter triggers initialization.
   * @see AccessorTest#initializationInvokesSetterIfEnabled
   */
  @Test
  public void fetchingOuterParameterTriggersInitialization() throws Throwable {
    int instanceBaseline = InitTester.getInstanceCount();
    design.getValue(testerId);
    int instanceCount = InitTester.getInstanceCount();
    assertThat(instanceCount, is(equalTo(instanceBaseline + 1)));
  }

  /**
   * Fetching the inner parameter does not trigger initialization of the
   * outer parameter.
   */
  @Test
  public void fetchingInnerDoesNotTriggerInitialization() throws Throwable {
    int instanceBaseline = InitTester.getInstanceCount();
    design.getValue(dataId);
    int instanceCount = InitTester.getInstanceCount();
    assertThat(instanceCount, is(equalTo(instanceBaseline)));
  }

  /**
   * Setting the inner parameter does not trigger initialization of the
   * outer parameter.
   */
  @Test
  public void settingInnerDoesNotTriggerInitialization() throws Throwable {
    int instanceBaseline = InitTester.getInstanceCount();
    design.setValue(dataId, 1);
    int instanceCount = InitTester.getInstanceCount();
    assertThat(instanceCount, is(equalTo(instanceBaseline)));
  }

  /**
   * Initialization is only triggered once.
   * @see SparamTest#gettingAnObjectReturnsSameInstance
   */
  @Test
  public void onlyFirstFetchTriggersInitialization() throws Throwable {
    int instanceBaseline = InitTester.getInstanceCount();
    design.getValue(testerId);
    design.getValue(testerId);
    design.getValue(testerId);
    int instanceCount = InitTester.getInstanceCount();
    assertThat(instanceCount, is(equalTo(instanceBaseline + 1)));
  }

  /**
   * Even though no setter is invoked if the outer parameter hasn't been
   * fetched, the set value is used once initialization is triggered.
   */
  @Test
  public void lazyInitializationProducesCorrectResults() throws Throwable {
    design.setValue(dataId, 3);
    InitTester tester = design.getValue(testerId);
    // Setter is called during initialization, as usual.
    assertThat(tester.getSetterInvocations(), is(equalTo(1)));
    // The object has been initialized with the correct value.
    assertThat(tester.data, is(equalTo(3)));
    // The corresponding parameter value also agrees.
    assertThat(design.getValue(dataId), is(equalTo(3)));
  }
}
