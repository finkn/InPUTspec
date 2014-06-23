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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class IntervalContainmentTest {
  private final TestCase testCase;

  public IntervalContainmentTest(TestCase testCase) {
    this.testCase = testCase;
  }

  @Test
  public void test() {
    testCase.run();
  }

  @Parameters
  public static Collection<TestCase[]> parameters() {
    return Arrays.asList(new TestCase[][] {
        { new TestCase("[1,3]").in(1, 2, 3).out(0, 4), },
        { new TestCase("]1,3]").in(2, 3).out(0, 1, 4), },
        { new TestCase("[1,3[").in(1, 2).out(0, 3, 4), },
        { new TestCase("]1,3[").in(2).out(0, 1, 3, 4), },

        { new TestCase("[*,3]").in(-1, 3).out(4, 5), },
        { new TestCase("[1,*]").in(1, 2, 3).out(-1, 0), },
        { new TestCase("[*,3[").in(-1, 2).out(3, 4, 5), },
        { new TestCase("]1,*]").in(2, 3).out(-1, 0, 1), },
        { new TestCase("[*,*]").in(1, 2, 3), },
        { new TestCase("]*,*[").in(1, 2, 3), },

        { new TestCase("[.005,.01]")
            .in(.005, .0051, .009, .01)
            .out(.0049, .011), },
        { new TestCase("].005,.01]")
            .in(.0051, .009, .01)
            .out(.005, .0049, .011), },
        { new TestCase("[.005,.01[")
            .in(.005, .0051, .009)
            .out(.0049, .011, .01), },
        { new TestCase("].005,.01[")
            .in(.0051, .009)
            .out(.0049, .005, .01, .011), }, });
  }

  private static class TestCase {
    private final Interval interval;
    private Collection<Number> contained = Collections.emptyList();
    private Collection<Number> notContained = Collections.emptyList();

    public TestCase(String interval) {
      this.interval = Interval.valueOf(interval);
    }

    public TestCase in(Number... n) {
      this.contained = Arrays.asList(n);
      return this;
    }

    public TestCase out(Number... n) {
      this.notContained = Arrays.asList(n);
      return this;
    }

    public void run() {
      assertTrue(contained.stream().allMatch(x -> interval.contains(x)));
      assertFalse(notContained.stream().anyMatch(x -> interval.contains(x)));
    }
  }
}
