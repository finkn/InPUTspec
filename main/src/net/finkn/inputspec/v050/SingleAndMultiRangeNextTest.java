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

import net.finkn.inputspec.tools.Generator;
import net.finkn.inputspec.tools.ParamCfg;

import org.junit.Test;

/**
 * This class examines which values are generated by a Design Space as a
 * function of the parameter configurations.
 *
 * <h2>Max is excluded</h2>
 * When generating values for an integer parameter, the maximum value is never
 * generated. If x is the largest legal value for a given parameter, then x
 * will never be generated.
 * <p>
 * This is consistently the case, with one exception. If the set of legal values
 * is {x}, then x will be generated.
 *
 * <h2>Only multi-range with inclusive limits work</h2>
 * Mixed limits result in an ArrayIndexOutOfBoundsException. Exclusive limits
 * simply degenerate to single range (with max missing as usual).
 *
 * <p>
 * Note that the values that are listed as expected are
 * <strong>precisely</strong> those values that are expected. That is, x should
 * be produced <em>if and only if</em> it is among the expected values.
 *
 * <p>
 * Note that all multi-ranges in these tests are matched.
 * {@link MultiRangeMismatchTest} examines what happens when they are not.
 *
 * @author Christoffer Fink
 * @see MultiRangeMismatchTest
 */
public class SingleAndMultiRangeNextTest {

  private final TestCase test = TestCase.instance;

  private final String singleMin = "10";
  private final String singleMax = "14";
  private final String multiMin = "10,20,30";
  private final String multiMax = "14,24,34";

  /** When using a singleton inclusive range, the max value is included. */
  @Test
  public void singleInclInclWithSingleValue() throws Throwable {
    test.param(pb()
        .inclMin("1")
        .inclMax("1"))
      .expected(1).run();
  }

  /** When using a singleton exclusive range, the max value is included. */
  @Test
  public void singleExclExclWithSingleValue() throws Throwable {
    test.param(pb()
        .exclMin("0")
        .exclMax("2"))
      .expected(1).run();
  }

  /** When using a singleton mixed range, the max value is included. */
  @Test
  public void singleInclExclWithSingleValue() throws Throwable {
    test.param(pb()
        .inclMin("1")
        .exclMax("2"))
      .expected(1).run();
  }

  /** When using a singleton mixed range, the max value is included. */
  @Test
  public void singleExclInclWithSingleValue() throws Throwable {
    test.param(pb()
        .exclMin("0")
        .inclMax("1"))
      .expected(1).run();
  }


  /** When using a single range, the max allowed value is ignored. */
  @Test
  public void singleInclIncl() throws Throwable {
    test.param(pb()
        .inclMin(singleMin)
        .inclMax(singleMax))
      .expected(10,11,12,13).run(); // 14 is missing.
  }

  /** When using a single range, the max allowed value is ignored. */
  @Test
  public void singleExclExcl() throws Throwable {
    test.param(pb()
        .exclMin(singleMin)
        .exclMax(singleMax))
      .expected(11,12).run();       // 13 is missing.
  }

  /** When using a single range, the max allowed value is ignored. */
  @Test
  public void singleInclExcl() throws Throwable {
    test.param(pb()
        .inclMin(singleMin)
        .exclMax(singleMax))
      .expected(10,11,12).run();    // 13 is missing.
  }

  /** When using a single range, the max allowed value is ignored. */
  @Test
  public void singleExclIncl() throws Throwable {
    test.param(pb()
        .exclMin(singleMin)
        .inclMax(singleMax))
      .expected(11,12,13).run();    // 14 is missing.
  }

  /**
   * When using a multi-range with inclusive limits, values from every range
   * are produced. As in the single range test, the max allowed value is
   * ignored.
   */
  @Test
  public void multiInclIncl() throws Throwable {
    test.param(pb()
        .inclMin(multiMin)
        .inclMax(multiMax))
      .expected(10,11,12,13, 20,21,22,23, 30,31,32,33).run();
      // 14, 24, 34 are missing.
  }

  /**
   * When using a multi-range with exclusive limits, only values from the first
   * range are produced. As in the single range tests, the max allowed value
   * is ignored.
   */
  @Test
  public void multiExclExcl() throws Throwable {
    test.param(pb()
        .exclMin(multiMin)
        .exclMax(multiMax))
      .expected(11,12).run();
      // 13, 21, 22, 23, 31, 32, 33 are missing!
  }

  /** Using a multi-range with mixed limits yields an array index exception. */
  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void multiInclExcl() throws Throwable {
    test.param(pb()
        .inclMin(multiMin)
        .exclMax(multiMax))
      .expected(10,11,12, 20,21,22, 30,31,32).run();
      // Due to exceptions, it's impossible to know which values get generated.
  }

  /** Using a multi-range with mixed limits yields an array index exception. */
  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void multiExclIncl() throws Throwable {
    test.param(pb()
        .exclMin(multiMin)
        .inclMax(multiMax))
      .expected(11,12,13, 21,22,23, 31,32,33).run();
      // Due to exceptions, it's impossible to know which values get generated.
  }

  private static ParamCfg.Builder pb() {
    return ParamCfg.builder();
  }

  // TODO: Should probably make this public and reusable in other tests.
  private static class TestCase {
    private static final TestCase instance = new TestCase(null, null);

    private final ParamCfg param;
    private final Object[] expected;

    private TestCase(ParamCfg param, Object[] expected) {
      this.param = param;
      this.expected = expected;
    }

    private TestCase param(ParamCfg.Builder builder) {
      return new TestCase(builder.build(), this.expected);
    }
    private TestCase expected(Object ... expected) {
      return new TestCase(this.param, expected);
    }

    private TestCase run() throws Throwable {
      Generator<Object> gen = Generator.fromParam(param);
      // Generates all of the expected but nothing else.
      // This test fails slowly but succeeds reasonably quickly.
      gen.limit(1000).generatesOnly(expected);
      gen.limit(10000).generatesAll(expected); // Shortcuts, so 10,000 is fine.
      return this;
    }
  }
}
