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

import net.finkn.inputspec.tools.*;

import org.junit.Test;

/**
 * This class examines which values are generated by a Design Space as a
 * function of the parameter configurations.
 *
 * <h2>Only multi-range with inclusive limits work</h2>
 * Mixed limits result in an ArrayIndexOutOfBoundsException. Exclusive limits
 * simply degenerate to single range (with max missing as usual).
 * <p>
 * Note that the values that are listed as expected are
 * <strong>precisely</strong> those values that are expected. That is, x should
 * be produced <em>if and only if</em> it is among the expected values.
 * <p>
 * Note that all multi-ranges in these tests are matched.
 * {@link MultiRangeMismatchTest} examines what happens when they are not.
 *
 * @author Christoffer Fink
 * @see MultiRangeMismatchTest
 * @see SimpleSingleRangeNextTest
 */
public class SimpleMultiRangeNextTest {

  // 200 should be more than enough for relatively small ranges.
  // A smaller number will speed up tests, but it also increases the risk
  // of sporadic failures.
  private final int iterations = 200;

  private final GenTestCase tc = GenTestCase.getInstance();

  private final String intMin = "10,20,30";
  private final String intMax = "14,24,34";

  /**
   * When using a multi-range with inclusive limits, values from every range
   * are produced. As in the single range test, the max allowed value is
   * ignored.
   */
  @Test
  public void multiInclIncl() throws Throwable {
    test(pb()
        .inclMin(intMin)
        .inclMax(intMax))
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
    test(pb()
        .exclMin(intMin)
        .exclMax(intMax))
      .expected(11,12).run();
      // 13, 21, 22, 23, 31, 32, 33 are missing!
  }

  /** Using a multi-range with mixed limits yields an array index exception. */
  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void multiInclExcl() throws Throwable {
    test(pb()
        .inclMin(intMin)
        .exclMax(intMax))
      .expected(10,11,12, 20,21,22, 30,31,32).run();
      // Due to exceptions, it's impossible to know which values get generated.
  }

  /** Using a multi-range with mixed limits yields an array index exception. */
  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void multiExclIncl() throws Throwable {
    test(pb()
        .exclMin(intMin)
        .inclMax(intMax))
      .expected(11,12,13, 21,22,23, 31,32,33).run();
      // Due to exceptions, it's impossible to know which values get generated.
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void _multiExclIncl() throws Throwable {
    test(pb()
        .exclMin(intMin)
        .inclMax(intMax))
      .expected(11,12,13, 21,22,23, 31,32,33).run();
      // Due to exceptions, it's impossible to know which values get generated.
  }

  // TODO: Do these two tests belong in this class?
  // No, move them to AdvancedMultiRangeNextTest.

  // The behavior in this test depends on 3 different bugs(?).
  //
  // 1) DesignSpace.next() does not properly evaluate expressions.
  //    While X is defined to be in [10,20], the expression is evaluated as if
  //    X was 0.
  // 2) When defining multi-ranges containing expressions, the first
  //    three ranges are discarded. So only the last range counts.
  // 3) The maximum legal value is also discarded.
  //
  // So [19,1000] is first reduced to [19,30] by discarding the first three
  // ranges. Then, because 0+9 and 0+10 evaluate to 9 and 10, [19,30] is reduced
  // to [9,10] = {9,10}. The maximum of 9 and 10 is 10. So finally, the set of
  // legal values is {9,10} \ {10} = {9}.
  @Test
  public void multiRangesWithExpressionsBehaveStrangely() throws Throwable {
    ParamCfg x = pb()
      .id("X")
      .inclMin("10")
      .inclMax("20")
      .build();
    ParamCfg y = pb()
      .id("Y")
      // [30,100] ∪ [100,400] ∪ [400,1000] ∪ [19,30] = [19,1000]
      .inclMin("X+20, X*10, X*40, X+9")
      .inclMax("X+80, X*20, X*50, X+10")
      .build();

    DesignSpaceCfg space = DesignSpaceCfg.builder().param(x, y).build();
    Generator<Object> gen = Generator
      .fromDesignSpace(space.getDesignSpace(), y.getId())
      .limit(iterations*10);

    tc.gen(gen)
      .expected(9)
      .run();
  }

  // This is interesting. While exclusive-exclusive multi-range definitions
  // normally end up with only the first range, when expressions are involved,
  // exclusive-exclusive and inclusive-inclusive behave the same way.
  // Since limits are defined as expressions, InPUT4j doesn't enforce non-empty
  // ranges. However, the evaluation in this case is predictable, because X
  // just seems to resolve to 0. So defining an empty range is trivial.
  // The result is that the limits are swapped.
  @Test
  public void meep() throws Throwable {
    ParamCfg x = pb()
      .id("X")
      .inclMin("10")
      .inclMax("20")
      .build();
    ParamCfg y = pb()
      .id("Y")
      .exclMin("X+20, X*10, X*40, X+5")
      .exclMax("X+80, X*20, X*50, X+1")
      .build();

    DesignSpaceCfg space = DesignSpaceCfg.builder().param(x, y).build();
    Generator<Object> gen = Generator
      .fromDesignSpace(space.getDesignSpace(), y.getId())
      .limit(iterations*10);

    tc.gen(gen)
      .expected(1,2,3,4,5,6)
      .run();
  }

  private static ParamCfg.Builder pb() {
    return ParamCfg.builder();
  }
  private GenTestCase test(ParamCfg.Builder pb) throws Throwable {
    return tc.gen(Generator.fromParam(pb.build()).limit(iterations));
  }
}
