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

import static net.finkn.inputspec.tools.Helper.sinkTest;
import static net.finkn.inputspec.tools.Helper.pb;

import org.junit.Test;

/**
 * Tests which values a design accepts for a parameter, depending on how that
 * parameter was defined.
 * <p>
 * Ideally, the next tests (examining which values are generated for a
 * parameter) and these tests should be combined. At the moment this is not
 * feasible. Designs and design spaces don't always agree on which values
 * are legal. Once they agree, the tests should be merged.
 *
 * @author Christoffer Fink
 */
public class SetValueTest {
  /**
   * The default range for an integer parameter includes the largest and
   * smallest possible integer values.
   */
  @Test
  public void integerWithoutLimitsAllowsMaxAndMinInteger() throws Throwable {
    sinkTest(pb())
      .accepts(Integer.MIN_VALUE, Integer.MAX_VALUE)
      .run();
  }

  /** Limits are included for inclusive-inclusive integer ranges. */
  @Test
  public void limitsAreAcceptedWhenUsingInclusiveRange() throws Throwable {
    sinkTest(pb()
        .inclMin(1)
        .inclMax(3))
      .accepts(1,2,3)
      .rejects(0,4)
      .run();
  }

  /** Limits are excluded for exclusive-exclusive integer ranges. */
  @Test
  public void limitsAreRejectedWhenUsingExclusiveIntegerRange() throws Throwable {
    sinkTest(pb()
        .exclMin(1)
        .exclMax(3))
      .accepts(2)
      .rejects(0,1,3,4)
      .run();
  }

  /** Floating point limits for an integer parameter are truncated. */
  @Test
  public void floatLimitsAreTruncatedForIntegerParameters() throws Throwable {
    sinkTest(pb()
        .exclMin(1.5)
        .inclMax(3.5)) // Same as ]1,3].
      .accepts(2,3)
      .rejects(1,4)
      .run();
  }

  /** Only values in the intersection of all the ranges are accepted. */
  @Test
  public void valuesAreRejectedWhenNotInIntersectionOfTwoRanges() throws Throwable {
    sinkTest(pb()
        .inclMin("1,3")
        .inclMax("4,6"))
      .accepts(3,4)               // [1,4] ∩ [3,6] = [3,4]
      .rejects(0,1,2,5,6,7)       // V \ [3,4]
      .run();
  }

  /** @see #valuesAreRejectedWhenNotInIntersectionOfTwoRanges() */
  @Test
  public void noValuesAreAcceptedWhenNoRangesIntersect() throws Throwable {
    sinkTest(pb()
        .inclMin("1,5")
        .inclMax("2,6"))
      .rejects(0,1,2,3,4,5,6,7)   // V
      .run();
  }

  // 1234
  // --
  //  --
  //   --
  /** @see #valuesAreRejectedWhenNotInIntersectionOfTwoRanges() */
  @Test
  public void noValuesAreExceptedEvenWhenSomeRangesIntersect() throws Throwable {
    sinkTest(pb()
        .inclMin("1,2,3")
        .inclMax("2,3,4"))
      .rejects(0,1,2,3,4,5)       // V
      .run();
  }

  // 12345
  // ---
  //  ---
  //   ---
  /** @see #valuesAreRejectedWhenNotInIntersectionOfTwoRanges() */
  @Test
  public void onlyValuesInIntersectionOfAllRangesAreAccepted() throws Throwable {
    sinkTest(pb()
        .inclMin("1,2,3")
        .inclMax("3,4,5"))
      .accepts(3)
      .rejects(0,1,2,4,5,6)       // V \ {3}
      .run();
  }

  /** @see #valuesAreRejectedWhenNotInIntersectionOfTwoRanges() */
  @Test
  public void doubleMultiRangesWorkLikeIntegerMultiRanges() throws Throwable {
    sinkTest(pb()
        .type("double")
        .inclMin("1,2,3")
        .inclMax("3,4,5"))
      .accepts(3.0)
      .rejects(0.0,1.0,2.0,4.0,5.0,6.0)
      .run();
  }

  /**
   * When defining exclusive multi-ranges, only the first range is used.
   * All other ranges are ignored.
   */
  @Test
  public void exclusiveMultiRangesAreInvalid() throws Throwable {
    sinkTest(pb()
        .type("double")
        .exclMin("0.1,0.8")
        .exclMax("0.4,0.9"))
      .accepts(0.1,0.2,0.3,0.4)   // 0.1 and 0.4 do not belong here!
      .rejects(0.8,0.7,0.9)       // 0.7 does not belong here!
      .run();
  }

  /** Exclusive limits are accepted for double parameters */
  @Test
  public void excludedLimitsAreAccepted() throws Throwable {
    sinkTest(pb()
        .type("double")
        .exclMin(0.1)
        .exclMax(0.3))
      .accepts(0.1,0.2,0.3)
      .rejects(0.09,0.31)
      .run();
  }

  /** Exclusive limits are accepted for double parameters */
  @Test
  public void excludedMinIsAcceptedInMixedRange() throws Throwable {
    sinkTest(pb()
        .type("double")
        .exclMin(0.1)
        .inclMax(0.3))
      .accepts(0.1,0.2,0.3)
      .rejects(0.09,0.31)
      .run();
  }

  /** Exclusive limits are accepted for double parameters */
  @Test
  public void excludedMaxIsAcceptedInMixedRange() throws Throwable {
    sinkTest(pb()
        .type("double")
        .inclMin(0.1)
        .exclMax(0.3))
      .accepts(0.1,0.2,0.3)
      .rejects(0.09,0.31)
      .run();
  }
}
