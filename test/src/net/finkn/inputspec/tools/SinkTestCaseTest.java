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

import java.util.function.Predicate;

import org.junit.Test;

public class SinkTestCaseTest {

  private final Predicate<Object> pred = x -> Double.valueOf(x.toString()) > 3;

  private final SinkTestCase test = SinkTestCase.getInstance()
    .sink(Sink.fromPredicate(pred));

  @Test(expected = IllegalStateException.class)
  public void duplicateAcceptsShouldFail() {
    test.accepts(4,5,6).accepts(6,5,4);
  }

  @Test(expected = IllegalStateException.class)
  public void duplicateRejectsShouldFail() {
    test.rejects(1,2,3).rejects(3,2,1);
  }

  @Test(expected = IllegalStateException.class)
  public void duplicateSinkShouldFail() {
    test.sink(Sink.fromPredicate(x -> true));
  }

  @Test(expected = IllegalStateException.class)
  public void emptyTestShouldFail() {
    runTests(test); // No tests.
  }

  @Test(expected = IllegalStateException.class)
  public void testWithoutSinkShouldFail() {
    runTests(SinkTestCase.getInstance().accepts(4,5,6)); // No sink.
  }

  @Test
  public void testShouldHaveNoTestsUnlessAdded() {
    assertFalse(test.hasTests());
  }
  @Test
  public void testShouldHaveTestsOnceAdded() {
    assertTrue(test.rejects(1,2,3).hasTests());
  }

  // ----- Accepts -----
  @Test
  public void acceptsTestShouldSucceedIfAllAccepted() {
    runTests(test.accepts(4,5,6));
  }

  @Test(expected = AssertionError.class)
  public void acceptsTestShouldFailIfAnyRejected() {
    runTests(test.accepts(4,5,3));
  }

  // ----- Rejects -----
  @Test
  public void rejectsTestShouldSucceedIfAllRejected() {
    runTests(test.rejects(1,2,3));
  }

  @Test(expected = AssertionError.class)
  public void rejectsTestShouldFailIfAnyAccepted() {
    runTests(test.rejects(2,3,4));
  }

  // ----- Mixed -----
  @Test
  public void mixedTestsShouldPassIfAllPass() {
    runTests(test
      .rejects(1,2,3)
      .accepts(4,5,6)
    );
  }

  @Test(expected = AssertionError.class)
  public void mixedTestsShouldFailIfAnyAcceptTestFails() {
    runTests(test
      .rejects(1,2,3)
      .accepts(4,5,3)
    );
  }

  @Test(expected = AssertionError.class)
  public void mixedTestsShouldFailIfAnyRejectTestFails() {
    runTests(test
      .accepts(4,5,6)
      .rejects(2,3,4)
    );
  }

  private void runTests(SinkTestCase test) {
    test.run();
  }
}
