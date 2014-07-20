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

import java.util.function.Predicate;

import org.junit.Test;

// FIXME: Cleanup.
// These are just the sink-specific tests moved from RangeTestHelper.
// For one, duplicate calls to accepts/rejects should be removed.

public class SinkTestCaseTest {

  private final Predicate<Object> pred = x -> Double.valueOf(x.toString()) > 3;

  private final SinkTestCase test = SinkTestCase.getInstance()
    .sink(Sink.fromPredicate(pred));

  // ----- Accepts -----
  @Test
  public void singleAcceptsTestShouldSucceedIfAllAccepted() {
    runTests(test.accepts(4));
  }

  @Test(expected = AssertionError.class)
  public void singleAcceptsTestShouldFailIfAnyRejected() {
    runTests(test.accepts(3));
  }

  @Test
  public void multipleAcceptsTestShouldSucceedIfAllAccepted() {
    runTests(test.accepts(4).accepts(5,4).accepts(6));
  }

  @Test(expected = AssertionError.class)
  public void multipleAcceptsTestShouldFailIfAnyRejected() {
    runTests(test.accepts(4).accepts(5,3).accepts(6));
  }

  // ----- Rejects -----
  @Test
  public void singleRejectsTestShouldSucceedIfAllRejected() {
    runTests(test.rejects(3));
  }

  @Test(expected = AssertionError.class)
  public void singleRejectsTestShouldFailIfAnyAccepted() {
    runTests(test.rejects(4));
  }

  @Test
  public void multipleRejectsTestShouldSucceedIfAllRejected() {
    runTests(test.rejects(3).rejects(2,3).rejects(1));
  }

  @Test(expected = AssertionError.class)
  public void multipleRejectsTestShouldFailIfAnyAccepted() {
    runTests(test.rejects(3).rejects(2,4).rejects(1));
  }

  // ----- Mixed -----
  @Test
  public void mixedTestsShouldPassIfAllPass() {
    runTests(test
      .rejects(1,2,3)
      .accepts(4,5,6)
      .rejects(3,2,1)
      .accepts(6,5,4)
    );
  }

  @Test(expected = AssertionError.class)
  public void mixedTestsShouldFailIfAnyAcceptTestFails() {
    runTests(test
      .rejects(1,2,3)
      .accepts(4,5,6)
      .rejects(3,2,1)
      .accepts(6,5,4,0)
    );
  }

  @Test(expected = AssertionError.class)
  public void mixedTestsShouldFailIfAnyRejectTestFails() {
    runTests(test
      .rejects(1,2,3)
      .accepts(4,5,6)
      .rejects(3,2,1,10)
      .accepts(6,5,4)
    );
  }

  private void runTests(SinkTestCase test) {
    test.run();
  }
}
