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
package net.finkn.inputspec.tools;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class GenTestCaseTest {

  private GenTestCase test = GenTestCase.getInstance()
    .gen(Generator.fromSeq(1, 3, 5, 7, 9));

  // Note: test...run() may seem more natural, but starting with runTest(...
  // is a small guarantee that the test is actually executed.
  // A test will succeed vacuously if run() is never called.

  @Test(expected = IllegalStateException.class)
  public void emptyTestShouldFail() {
    runTest(test); // No tests added.
  }

  @Test(expected = IllegalStateException.class)
  public void testWithoutGeneratorShouldFail() {
    runTest(GenTestCase.getInstance().all(1,3,5)); // No generator set.
  }

  @Test(expected = IllegalStateException.class)
  public void duplicateAllShouldFail() {
    test.all(1,2,3).all(3,2,1);
  }
  @Test(expected = IllegalStateException.class)
  public void duplicateOnlyShouldFail() {
    test.only(1,2,3).only(3,2,1);
  }
  @Test(expected = IllegalStateException.class)
  public void duplicateAnyShouldFail() {
    test.any(1,2,3).any(3,2,1);
  }
  @Test(expected = IllegalStateException.class)
  public void duplicateNoneShouldFail() {
    test.none(1,2,3).none(3,2,1);
  }
  @Test(expected = IllegalStateException.class)
  public void duplicateGenShouldFail() {
    test.gen(Generator.fromSeq());
  }
  @Test(expected = IllegalStateException.class)
  public void duplicateIntervalsShouldFail() {
    test.intervals("[1,3]").intervals("[5,7]");
  }

  @Test
  public void testShouldHaveNoTestsUnlessAdded() {
    assertFalse(test.hasTests());
  }
  @Test
  public void testShouldHaveTestsOnceAdded() {
    assertTrue(test.all(1,3,5).hasTests());
  }

  // ----- All -----
  @Test
  public void testAllSuccess() {
    runTest(test.all(1,3,5));
  }
  @Test(expected = AssertionError.class)
  public void testAllFailure() {
    runTest(test.all(1,3,0));
  }

  // ----- Any -----
  @Test
  public void testAnySuccess() {
    runTest(test.any(0,2,3));
  }
  @Test(expected = AssertionError.class)
  public void testAnyFailure() {
    runTest(test.any(0,2,4));
  }

  // ----- Only -----
  @Test
  public void testOnlySuccess() {
    runTest(test.only(1,2,3,4,5,6,7,8,9));
  }
  @Test(expected = AssertionError.class)
  public void testOnlyFailure() {
    runTest(test.only(1,5,9));
  }

  // ----- None -----
  @Test
  public void testNoneSuccess() {
    runTest(test.none(0,2,4));
  }
  @Test(expected = AssertionError.class)
  public void testNoneFailure() {
    runTest(test.none(0,2,3));
  }

  // ----- Expected -----
  @Test
  public void testExpectedSuccess() {
    runTest(test.expected(1,3,5,7,9));
  }
  @Test(expected = AssertionError.class)
  public void testExpectedFailureBecauseNotAll() {
    runTest(test.expected(0,1,3,5,7,9));
  }
  @Test(expected = AssertionError.class)
  public void testExpectedFailureBecauseNotOnly() {
    runTest(test.expected(1,3,5));
  }

  // ----- Intervals -----
  @Test
  public void testIntervalsSuccessWithInteger() {
    runTest(test.intervals("[1,3]", "[5,9]"));
  }
  @Test(expected = AssertionError.class)
  public void testIntervalsFailureWithDouble() {
    runTest(test.intervals("[1,3]", "[6,8]"));
  }
  @Test
  public void testIntervalsSuccessWithDouble() {
    runTest(GenTestCase.getInstance()
      .gen(Generator.fromSeq(1.2, 2.5, 0.4, 4.7))
      .intervals("[0.4,1.2]", "]1.9,4.8["));
  }
  @Test(expected = AssertionError.class)
  public void testIntervalsFailureWithDoubleBecauseExclusiveLimitViolated() {
    runTest(GenTestCase.getInstance()
      .gen(Generator.fromSeq(1.2, 2.5, 0.4, 4.7))
      .intervals("]0.4,1.2]", "]1.9,4.8["));
  }

  private void runTest(GenTestCase test) {
    test.run();
  }
}
