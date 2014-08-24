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

import static net.finkn.inputspec.tools.Helper.design;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import net.finkn.inputspec.tools.ParamCfg;

import org.junit.Before;
import org.junit.Test;

import se.miun.itm.input.model.design.IDesign;

/**
 * Examines the effect of extending a design in InPUT4j v0.5.
 * Note that extending a design does not add the supported parameter IDs
 * of the extending design to the extended design.
 *
 * @see ReadOnlyDesignTest
 * @author Christoffer Fink
 */
public class ExtendScopeTest {

  private final ParamCfg param1 = ParamCfg.builder().id("X1").build();
  private final ParamCfg param2 = ParamCfg.builder().id("X2").build();
  private final ParamCfg param3 = ParamCfg.builder().id("X3").build();
  private final String id1 = param1.getId();
  private final String id2 = param2.getId();
  private IDesign design1;
  private IDesign design2;

  @Before
  public void setup() throws Throwable {
    design1 = design(param1);
    design2 = design(param2);
    // Make sure the wrong parameters are unsupported prior to extension.
    assertThat(design1.getValue(id2), is(nullValue()));
    assertThat(design2.getValue(id1), is(nullValue()));
  }

  /** A design can access the parameters of the extending design. */
  @Test
  public void canAccessParameterOfExtendingDesign() throws Throwable {
    design1.extendScope(design2);
    assertThat(design1.getValue(id2), is(not(nullValue())));
  }

  /** Extending a design does not alter its set of supported IDs. */
  @Test
  public void extendScopeDoesNotUpdateSupportedIds() throws Throwable {
    design1.extendScope(design2);
    // TODO: Prime candidate for a custom matcher.
    assertThat(design1.getSupportedParamIds(), not(hasItem(id2)));
  }

  /** Changes to a parameter are reflected in both designs. */
  @Test
  public void settingValueUpdatesBothDesigns() throws Throwable {
    design1.extendScope(design2);
    int old = design2.getValue(id2);
    int value = old + 1;
    design1.setValue(id2, value);
    assertThat(design1.getValue(id2), is(equalTo(value)));
    assertThat(design2.getValue(id2), is(equalTo(value)));
  }

  /**
   * Extending A by B and then B by A is legal, but getValue no longer works
   * as expected. Rather than returning {@code null} when given an invalid
   * ID, the call results in a StackOverflowError instead.
   */
  @Test(expected = StackOverflowError.class)
  public void circularExtensionsAreLegalButInvalid() throws Throwable {
    design1.extendScope(design2);
    design2.extendScope(design1);
    design1.getValue("BANKAI"); // Should normally be null.
  }

  /**
   * Extending B by C and A by B has the expected effect.
   * All the parameters from the three designs can be accessed through A.
   */
  @Test
  public void serialExtensionIsValid() throws Throwable {
    IDesign design3 = design(param3);
    design2.extendScope(design3);
    design1.extendScope(design2);
    assertThat(design1.getValue(param3.getId()), is(not(nullValue())));
  }

  /**
   * Extending A by B and then B by C, that is, retroactively extending the
   * extending design, has the same effect as extending B by C first, and then
   * A by B, as shown in {@link #serialExtensionIsValid}.
   */
  @Test
  public void serialDelayedExtensionIsValid() throws Throwable {
    IDesign design3 = design(param3);
    design1.extendScope(design2);
    design2.extendScope(design3);
    assertThat(design1.getValue(param3.getId()), is(not(nullValue())));
  }

  /**
   * Extending A by both B and C has the expected effect.
   * All the parameters from the three designs can be accessed through A.
   */
  @Test
  public void parallelExtensionIsValid() throws Throwable {
    IDesign design3 = design(param3);
    design1.extendScope(design2);
    design1.extendScope(design3);
    assertThat(design1.getValue(param2.getId()), is(not(nullValue())));
    assertThat(design1.getValue(param3.getId()), is(not(nullValue())));
  }

  /**
   * Extending a design with a design such that one or more parameter IDs
   * occur in both designs is legal.
   */
  @Test
  public void intersectingDesignsAreLegal() throws Throwable {
    design(param1).extendScope(design(param1));
  }

  /** Extending a design with itself is legal. */
  @Test
  public void extendingSelfIsLegal() throws Throwable {
    design1.extendScope(design1);
  }

  /**
   * When a parameter ID occurs in the intersection of two designs, then
   * the parameter in the extending design is ignored.
   */
  @Test
  public void originalParameterTakesPrecedence() throws Throwable {
    ParamCfg x1 = ParamCfg.builder().fixed(1).build();
    ParamCfg x2 = ParamCfg.builder().fixed(2).build();

    test(design(x1), design(x2), 1);
    test(design(x2), design(x1), 2);
  }

  private void test(IDesign d1, IDesign d2, int val) throws Throwable {
    d1.extendScope(d2);
    assertThat(d1.getValue("X"), is(equalTo(val)));
  }
}
