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
 * Examines how InPUT4j handles single ranges defined with expressions.
 * <ul>
 *  <li>Expressions are evaluated as if references are resolved to 0.</li>
 *  <li>Plain references are illegal (min = "A").</li>
 *  <li>Simple expressions without references are illegal (min = "1 + 2").</li>
 *  <li>Simple math library use is illegal (min = "Math.PI").</li>
 *  <li>When expressions yield an empty range, the limits are swapped.*</li>
 * </ul>
 * <p>
 * *However, there is more to it than mere swapping.
 *
 * <h2>The theory of swapped limits</h2>
 * InPUT4j doesn't enforce non-empty ranges. So empty ranges can be defined
 * by setting limits to expressions that, when evaluated, will lead to an
 * empty range. That is, the minimum is greater than or equal to the maximum.
 * However, the result is not an empty range. Instead, InPUT4j swaps the
 * min and max limits. This behavior interacts with the max-value-exclusion bug
 * to give rise to surprising results.
 * <p>
 * Assume that a parameter has been defined relative to another parameter in
 * such a way that the expressions evaluate to the nonsensical interval [3,1].
 * Now min = 3 and max = 1. By swapping the limits we get the interval [1,3],
 * but 1 will not be generated, because it's the max.
 * <p>
 * Exclusive ranges are even more interesting, because they end up extending
 * the range to include the limits and more.
 * @see SimpleSingleRangeNextTest
 * @see SimpleMultiRangeNextTest#multiRangesWithExpressionsBehaveStrangely()
 */
public class AdvancedSingleRangeNextTest {

  private final int iterations = 100;
  private final GenTestCase testCase = GenTestCase.getInstance();

  private final ParamCfg dependee = ParamCfg.builder()
    .id("A").inclMin("10").inclMax("20").build();

  /** Next does not properly resolve references. */
  @Test
  public void referencesResolveToZero() throws Throwable {
    ParamCfg dependent = pb()
        .inclMin("A + 1")
        .inclMax("A + 1")
        .build();
    test(dependent.getId(), dependee, dependent)
      .expected(1).run();
  }

  /**
   * Empty ranges always behave the same, whether min is literally greater
   * than max, or, as in this test, min = max with exclusive limits.
   */
  @Test
  public void exclusiveIdenticalLimitsCountAsEmptyRange() throws Throwable {
    ParamCfg dependent = pb()
        .exclMin("A + 1")
        .exclMax("A + 1")
        .build();
    test(dependent.getId(), dependee, dependent)
      .expected(1,2).run();
  }

  /**
   * See
   * {@link SimpleMultiRangeNextTest#emptyRangesAreSwappedJustLikeSingleRange}.
   */
  @Test
  public void limitsAreSwappedForEmptyInclusiveRange() throws Throwable {
    ParamCfg dependent = pb()
        .inclMin("A + 5")
        .inclMax("A + 1")
        .build();
    test(dependent.getId(), dependee, dependent)
      .expected(2,3,4,5).run();
  }
  // We have exclusive max = 1, and exclusive min = 5. This means that we
  // can't go as "low" as 5, and we have to go even "lower". So the minimum
  // legal value must be 6. Similarly, we can't go as "hight" as 1. Instead,
  // we have to go one step "higher". So the maximum legal value is 0. Since
  // the maximum allowed value is excluded from the range, 0 will not be
  // generated. However, the limits are swapped at some point. So we end up
  // with the interval [1,6] instead of {}.
  /**
   * An empty exclusive range contains more legal values than the same
   * definition with inclusive limits.
   */
  @Test
  public void emptyExclusiveRangeIsExtendedWhenSwapped() throws Throwable {
    ParamCfg dependent = pb()
        .exclMin("A + 5")
        .exclMax("A + 1")
        .build();
    test(dependent.getId(), dependee, dependent)
      .expected(1,2,3,4,5,6).run();
    // The exclusive range is actually extended!
  }

  /**
   * While a plain reference (such as "A") is illegal, applying an identity
   * function (such as "A + 0") turns the plain reference into a legal
   * expression.
   */
  @Test
  public void plainReferenceCanBeSimulatedWithIdentity() throws Throwable {
    ParamCfg dependent = pb()
        .inclMin("A + 0")
        .inclMax("A * 1")
        .build();
    test(dependent.getId(), dependee, dependent)
      .expected(0).run();
  }

  /** Whitespace in expressions is ignored. */
  @Test
  public void whitespaceInExpressionsIsIgnored() throws Throwable {
    ParamCfg dependent = pb()
        .inclMin("A +   Math .sqrt (Math . log(  Math. exp ( 3 -1) )* 2) ")
        .inclMax("A+Math.sqrt(Math.log(Math.exp(3-1))*2)")
        .build();
    test(dependent.getId(), dependee, dependent)
      .expected(2).run();
  }

  // TODO: Do these tests belong in this class?
  // Does it make more sense to put them in a test that merely examines which
  // configurations can be successfully imported, or here, where they fit in
  // with the other range definitions.
  // If we use the latter criterion, then all illegal configuration options
  // would have to be placed with their legal siblings, and a LegalConfigTest
  // would be impossible.

  /** Expressions consisting only of a reference ("A") are illegal. */
  @Test(expected = RuntimeException.class)
  public void plainReferenceIsIllegal() throws Throwable {
    ParamCfg dependent = pb()
        .inclMin("A")
        .build();
    test(dependent.getId(), dependee, dependent)
      .expected(0).run();
  }

  /** Expressions without any reference ("1 + 2") are illegal. */
  @Test(expected = NumberFormatException.class)
  public void simpleExpressionIsIllegal() throws Throwable {
    test(pb().inclMin("1 + 2"));
  }

  /**
   * Expressions that use a math library function but lack a reference
   * ("Math.PI") are illegal.
   */
  @Test(expected = NumberFormatException.class)
  public void simpleMathLibraryExpressionIsIllegal() throws Throwable {
    test(pb().inclMin("Math.PI"));
  }

  private static ParamCfg.Builder pb() {
    return ParamCfg.builder();
  }
  private GenTestCase test(ParamCfg.Builder pb) throws Throwable {
    return testCase.gen(Generator.fromParam(pb.build()).limit(iterations));
  }
  // Move to Generator?
  private GenTestCase test(String id, ParamCfg ... params) throws Throwable {
    DesignSpaceCfg space = DesignSpaceCfg.builder().param(params).build();
    return testCase.gen(Generator
      .fromDesignSpace(space.getDesignSpace(), id).limit(iterations)
    );
  }
}
