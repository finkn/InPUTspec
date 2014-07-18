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

import static net.finkn.inputspec.tools.Unit.*;
import static net.finkn.inputspec.tools.RangeTestHelper.t;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

public class RangeTestHelperTest {
  private final ParamCfg param = ParamCfg.builder()
    .inclMin(0)
    .inclMax(10)
    .build();

  private final Predicate<Object> pred = x -> Double.valueOf(x.toString()) > 3;
  private final Sink<Object> sink = Sink.fromPredicate(pred);
  private final RangeTestHelper.TestCase test = t(sink);

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

  // ----- From Parameter -----
  @Test
  public void testWithParamShouldSucceedWhenValuesMatch() throws Throwable {
    runTests(t(param)
      .accepts(0,1).accepts(5)
      .rejects(-1,-2).rejects(11)
      .accepts(9,10)
    );
  }

  @Test(expected = AssertionError.class)
  public void testWithParamShouldFailWhenThereIsMismatch() throws Throwable {
    runTests(t(param)
      .accepts(0,1).accepts(20)
      .rejects(-1,-2).rejects(11)
      .accepts(9,10)
    );
  }

  @Test
  public void testWithDependentParam() throws Throwable {
    ParamCfg dependee = ParamCfg.builder()
      .id("X")
      .interval("[2,2]")
      .build();
    ParamCfg dependent = ParamCfg.builder()
      .id("Y")
      .inclMin("X * 2")
      .inclMax("X * 4") // [4,8]
      .build();

    DesignSpaceCfg space = DesignSpaceCfg.builder()
      .param(dependee, dependent)
      .build();

    runTests(t(space, dependent.getId())
      .accepts(4,5,6,7,8)
      .rejects(2,3,9,10)
    );
  }

  private void runTests(RangeTestHelper.TestCase test) {
    RangeTestHelper helper = new RangeTestHelper(test);
    helper.acceptTest();
    helper.rejectTest();
  }
}
