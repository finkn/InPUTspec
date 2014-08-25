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
import static net.finkn.inputspec.tools.Helper.*;

import net.finkn.inputspec.tools.ParamCfg;
import net.finkn.inputspec.tools.DesignSpaceCfg;

import org.junit.Before;
import org.junit.Test;

import se.miun.itm.input.model.InPUTException;
import se.miun.itm.input.model.design.IDesignSpace;

/**
 * Tests the behavior of {@code setFixed} of design space in InPUT4j 0.5.
 *
 * @author Christoffer Fink
 */
public class SetFixedTest {

  private final ParamCfg param = pb().interval("[1,3]").build();
  private final String id = param.getId();

  private IDesignSpace space;

  @Before
  public void setup() throws Throwable {
    this.space = space(param);
  }

  // TODO: link to a test showing how this affects design creation.
  /**
   * Fixing a parameter to an out-of-range value is legal.
   * This is consistent with the behavior demonstrated in
   * {@link FixedNumericTest#fixedTakesPrecedenceOverLimits}.
   */
  @Test
  public void parametersCanBeFixedToOutOfRangeValues() throws Throwable {
    space.setFixed(id, "5"); // 5 ∉ [1,3]
    assertThat(space.next(id), is(equalTo(5)));
  }

  /** Float values are truncated when fixing an integer parameter. */
  @Test
  public void floatValuesAreTruncatedForIntegerParameter() throws Throwable {
    space.setFixed(id, "1.5");
    assertThat(space.next(id), is(equalTo(1)));
  }

  /**
   * Parameters are fixed only in the sense of specifying the values that should
   * be generated, not in the sense that they are immutable.
   * Parameters can thus be fixed multiple times, and even to new values.
   */
  @Test
  public void parametersCanBeFixedMultipleTimes() throws Throwable {
    space.setFixed(id, "1");
    space.setFixed(id, "1");
    space.setFixed(id, "2");
    assertThat(space.next(id), is(equalTo(2)));
  }

  /**
   * Setting a fixed parameter to a new value is illegal.
   * As {@link FixedNumericTest#settingFixedToNewValueIsIllegal} shows,
   * a parameter that was fixed in the configuration produces the same result.
   */
  @Test(expected = InPUTException.class)
  public void setValueOnFixedParamIsIllegalIfValuesDiffer() throws Throwable {
    space.setFixed(id, "1");
    space.nextDesign("Design").setValue(id, 2);
  }

  /** Setting a fixed parameter to the same value is legal. */
  @Test
  public void setValueOnFixedParamIsLegalIfValuesMatch() throws Throwable {
    space.setFixed(id, "1");
    space.nextDesign("Design").setValue(id, 1);
  }

  /**
   * Fixing a parameter to {@code null} makes the parameter unfixed.
   * In contrast to {@link #setFixedToNullUnfixesParameter}, this test
   * unfixes a parameter that was originally unfixed in the configuration, but
   * was subsequently fixed by {@code setFixed}.
   */
  @Test
  public void setFixedToNullCancelsPreviousFix() throws Throwable {
    space.setFixed(id, "1");
    space.setFixed(id, null);
    space.nextDesign("Design").setValue(id, 2);
  }

  /**
   * Fixing a parameter to {@code null} makes the parameter unfixed.
   * In contrast to {@link #setFixedToNullCancelsPreviousFix}, this test
   * unfixes a parameter that was defined as fixed in the configuration.
   * The effect is the same in both cases.
   */
  @Test
  public void setFixedToNullUnfixesParameter() throws Throwable {
    ParamCfg fixed = pb().fixed(1).build();
    String id = fixed.getId();
    space = space(fixed);
    space.setFixed(id, null);
    space.nextDesign("Design").setValue(id, 2);
  }
}
