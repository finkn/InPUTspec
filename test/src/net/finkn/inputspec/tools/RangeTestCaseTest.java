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

import org.junit.Test;

public class RangeTestCaseTest {

  private final ParamCfg param = ParamCfg.builder()
    .inclMin(0)
    .inclMax(10)
    .build();

  private final RangeTestCase test = RangeTestCase.getInstance();

  @Test(expected = IllegalStateException.class)
  public void testWithoutTestsShouldFail() {
    runTests(test);
  }

  // Note that these tests depend on the accuracy of certain assumptions about
  // the behavior of InPUT4j.
  @Test
  public void testWithParamShouldSucceedWhenValuesMatch() throws Throwable {
    runTests(test.param(param)
      .accepts(0,1,9,10)
      .rejects(-1,-2,11)
      .none(-1,11)
      .any(1,2,3,4,5)
    );
  }

  @Test(expected = AssertionError.class)
  public void testWithParamShouldFailWhenThereIsMismatch() throws Throwable {
    runTests(test.param(param)
      .accepts(0,1,0,10,20)
      .rejects(-1,-2,11)
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

    runTests(test.space(space, dependent.getId())
      .accepts(4,5,6,7,8)
      .rejects(2,3,9,10)
    );
  }

  private void runTests(RangeTestCase test) {
    test.run();
  }
}
