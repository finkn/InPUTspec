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

import static org.junit.Assert.assertEquals;
import net.finkn.inputspec.tools.*;

import org.junit.Test;

import se.miun.itm.input.model.design.IDesignSpace;

/**
 * These tests demonstrate how InPUT handles certain aspects of multi-ranges.
 *
 * @author Christoffer Fink
 */
public class MultiRangeTest extends TestCleanup {

  /**
   * Design spaces will generate values taken from any of the ranges when
   * multiple ones are defined. This is a very basic test.
   */
  @Test
  public void allRangesAreUsedToGenerateValues() throws Throwable {
    IDesignSpace space = DesignSpaceCfg.builder()
      .param(ParamCfg.builder()
        .inclMin("1,5")
        .inclMax("1,5")
        .build())
      .build()
      .getDesignSpace();

    Generator.fromDesignSpace(space, "X").generatesOnly(1, 5); // Overkill.
    Generator.fromDesignSpace(space, "X").generatesAll(1, 5);
  }

  /**
   * If a multi-range is defined using an unmatched upper limit (that is, a
   * min limit is missing), an exception is thrown when using the configuration
   * to create a design space. Because the configuration results in a crash, a
   * missing min limit is illegal.
   */
  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void multirangeWithMissingMinIsIllegal() throws Throwable {
    DesignSpaceCfg.builder()
      .param(ParamCfg.builder()
        .inclMin("1,5")
        .inclMax("1,5,10")
        .build())
      .build()
      .getDesignSpace();
  }

  /**
   * If a multi-range is defined using an unmatched upper limit (that is, a max
   * limit is missing), generating a value for the parameter may or may not
   * succeed, depending on chance. In other words, a design space can be created
   * based on such a configuration, but generating a value fails randomly.
   */
  @Test
  public void multirangeWithMissingMaxFailsRandomly() throws Throwable {
    // 1 in 5 ranges is complete, so we expect a 20% success rate.
    assertSuccessRate(ParamCfg.builder()
      .inclMin("1,2,3,4,5")
      .inclMax("1")
      .build(), 20);

    TestCleanup.cleanup();

    // 4 in 5 ranges are complete, so we expect a 80% success rate.
    assertSuccessRate(ParamCfg.builder()
      .inclMin("1,2,3,4,5")
      .inclMax("1,2,3,4")
      .build(), 80);
  }

  private void assertSuccessRate(ParamCfg param, int percent) throws Throwable {
    int values = 200;
    double epsilon = 0.1;
    double rate = getSuccessRate(param, values);
    double expected = ((double) percent) / 100;
    assertEquals(expected, rate, epsilon);
  }

  private double getSuccessRate(ParamCfg param, int values) throws Throwable {
    IDesignSpace space = DesignSpaceCfg.builder()
      .param(param)
      .build()
      .getDesignSpace();
    return ((double) countSuccess(space, param.getId(), values)) / values;
  }

  private int countSuccess(IDesignSpace space, String id, int values) {
    int count = 0;
    for (int i = 0; i < values; i++) {
      try {
        space.next(id);
        count++;
      } catch(Exception e) { }
    }
    return count;
  }
}
