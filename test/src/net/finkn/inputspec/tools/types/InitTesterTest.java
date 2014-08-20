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
package net.finkn.inputspec.tools.types;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class InitTesterTest {
  private final InitTester tester = new InitTester(1);

  @Test
  public void fieldShouldMatchConstructorArg() {
    assertThat(tester.data, is(equalTo(1)));
    InitTester tester = new InitTester(2);
    assertThat(tester.data, is(equalTo(2)));
  }

  @Test
  public void getterShouldReturnData() {
    assertThat(tester.customGetMethod(), is(equalTo(tester.data)));
    tester.data = 2;
    assertThat(tester.customGetMethod(), is(equalTo(tester.data)));
  }

  @Test
  public void setterShouldSetData() {
    tester.customSetMethod(2);
    assertThat(tester.data, is(equalTo(2)));
  }

  @Test
  public void invokingCustomAccessorsShouldIncreaseCounters() {
    // Should start off at 0 invocations.
    assertThat(tester.getGetterInvocations(), is(equalTo(0)));
    assertThat(tester.getSetterInvocations(), is(equalTo(0)));
    // Get data.
    assertThat(tester.customGetMethod(), is(equalTo(1)));
    // Number of invocations should have increased.
    assertThat(tester.getGetterInvocations(), is(equalTo(1)));
    // Set data.
    tester.customSetMethod(3);
    tester.customSetMethod(2);
    // Number of invocations should have increased.
    assertThat(tester.getSetterInvocations(), is(equalTo(2)));
  }

  @Test
  public void testGlobalCounters() {
    InitTester.resetGlobal();
    assertThat(InitTester.getGlobalSetterCount(), is(equalTo(0)));
    assertThat(InitTester.getGlobalGetterCount(), is(equalTo(0)));

    tester.customSetMethod(3);
    tester.customSetMethod(5);
    tester.customGetMethod();
    tester.customGetMethod();

    assertThat(InitTester.getGlobalSetterCount(), is(equalTo(2)));
    assertThat(InitTester.getGlobalGetterCount(), is(equalTo(2)));

    InitTester.resetGlobal();
    assertThat(InitTester.getGlobalSetterCount(), is(equalTo(0)));
    assertThat(InitTester.getGlobalGetterCount(), is(equalTo(0)));
  }

  @Test
  public void instanceCountShouldIncreaseWhenCreatingInstances() {
    int count = InitTester.getInstanceCount();
    new InitTester();
    new InitTester(3);
    assertThat(InitTester.getInstanceCount(), is(equalTo(count + 2)));
  }
}
