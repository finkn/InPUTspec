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

import static net.finkn.inputspec.tools.Helper.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import net.finkn.inputspec.tools.ParamCfg;

import org.junit.Test;

import se.miun.itm.input.model.design.IDesign;

/**
 * Tests how fixed numeric parameters work in InPUT4j 0.5.
 *
 * @author Christoffer Fink
 */
public class FixedNumericTest {

  /** When a parameter is fixed, only the fixed value is generated. */
  @Test
  public void fixedWorksAsExpected() throws Throwable {
    genTest(pb()
        .fixed("3"))
      .expected(3).run();
  }

  /** Floating point values are truncated when fixing an integer parameter. */
  @Test
  public void fixedValuesAreTruncatedForIntegerParameters() throws Throwable {
    genTest(pb()
        .fixed("3.9"))
      .expected(3).run();
  }

  /**
   * When a parameter has both a fixed value and a range, only the fixed value
   * is generated, even if that value is outside of the range.
   */
  @Test
  public void fixedTakesPrecedenceOverLimits() throws Throwable {
    genTest(pb()
        .inclMin("1")
        .inclMax("2")
        .fixed("3"))
      .expected(3).run();
  }

  /**
   * When a parameter is fixed to a value that is in the range that is also
   * defined, setting that parameter to the fixed value in a design is legal.
   * @see #setValueWithFixedOutOfRangeValueIsIllegal()
   */
  @Test
  public void setValueWithFixedWorksIfInRange() throws Throwable {
    ParamCfg param = pb()
      .inclMin("3")
      .inclMax("3")
      .fixed("3")
      .build();

    IDesign design = design(param);

    int value = design.getValue(param.getId());
    assertThat(value, is(equalTo(3)));
    design.setValue(param.getId(), value);
  }

  /**
   * When a parameter is fixed to a value that is outside the range, setting
   * the parameter to the value it already has is illegal.
   * @see #fixedTakesPrecedenceOverLimits()
   * @see #setValueWithFixedWorksIfInRange()
   */
  @Test
  public void setValueWithFixedOutOfRangeValueIsIllegal() throws Throwable {
    ParamCfg param = pb()
      .inclMin("1")
      .inclMax("2")
      .fixed("3")
      .build();

    IDesign design = design(param);

    int value = design.getValue(param.getId());
    assertThat(value, is(equalTo(3)));
    try {
      design.setValue(param.getId(), value);
      fail("Re-setting value was expected to fail.");
    } catch (IllegalArgumentException e) { // Expect to end up here.
    }
  }
}
