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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import net.finkn.inputspec.tools.*;
import net.finkn.inputspec.tools.types.AccessorTester;

import org.junit.Test;

import se.miun.itm.input.model.design.IDesign;

/**
 * Examines the relationship between a structured parameter and the
 * corresponding object in InPUT4j v0.5.
 * A nested parameter is kept in sync with the corresponding object field
 * if and only if it was not initialized by constructor. That is, changes to
 * the value of a parameter (through {@code setValue}) will not always affect
 * the parent object.
 * <p>
 * Note that these tests depend completely on the behavior demonstrated by
 * {@link AccessorTest}, and
 * {@link AccessorTest#constructorInitializationDisablesSetter} in particular.
 *
 * @author Christoffer Fink
 */
public class ValueConsistencyTest {

  private final AccessorTest.TestParameters test =
    AccessorTest.TestParameters.builder()
    .inner(SparamTest.DATA_PARAM)
    .outer(SparamTest.TESTER_PARAM)
    .type(AccessorTester.class)
    .build();

  /**
   * If a parameter was <strong>not</strong> initialized by the constructor,
   * then {@code setValue} keeps the object up to date.
   */
  @Test
  public void objectIsUpdatedWhenSetterIsEnabled() throws Throwable {
    testConsistency(test.withoutConstructor);
  }

  /**
   * If a parameter was initialized by the constructor, then {@code setValue}
   * does <strong>not</strong> keep the object up to date.
   */
  @Test(expected = AssertionError.class)
  public void objectIsNotUpdatedWhenSetterIsDisabled() throws Throwable {
    testConsistency(test.withConstructor);
  }

  // Test whether field value and parameter value agree.
  private void testConsistency(CodeMappingCfg mapping) throws Throwable {
    IDesign design = Helper.design(mapping, test.testerParam);

    AccessorTester tester = design.getValue(test.testerParam.getId());
    int newValue = 1;
    // Update parameter value.
    design.setValue(test.dataId, newValue);
    // Get updated parameter value.
    int currentValue = design.getValue(test.dataId);

    // The value returned by getValue is up-to-date,
    assertThat(currentValue, is(equalTo(newValue)));
    // but what about the value of the object field?
    assertThat(tester.getData(), is(equalTo(currentValue)));
  }
}
