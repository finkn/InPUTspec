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

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import se.miun.itm.input.model.InPUTException;

/**
 * Base class for writing tests that examine the values that are allowed for
 * some configuration.
 * An obvious use of this class is to observe the difference between inclusive
 * and exclusive limits. It's also useful for less obvious situations. For
 * example, it enables tests that check type enforcement when setting
 * parameters.
 * <p>
 * Extending classes just have to supply two things:
 * <ol>
 *   <li>
 *     A constructor that takes a {@link RangeTestHelper.TestCase}, which it
 *     forwards to {@code super}.
 *   </li>
 *   <li>A matching static @Parameters method.</li>
 * </ol>
 * <p>
 * Together with {@link RangeTestHelper.TestCase}, this class provides a
 * convenient and less verbose way to execute many
 * {@code Sink.fromParam(ParamCfg.builder()...build()).accepts(...).rejects(...)}
 * and similar tests.
 *
 * <h2>Example</h2>
 * One element returned by the @Parameters method might look like this.
 * <p>
 * <code>
 *   t(pb().interval("[1,2]")).accepts(1,2).rejects(0,3)
 * </code>
 *
 * @author Christoffer Fink
 * @version 1.0
 */
@RunWith(value = Parameterized.class)
@Deprecated
public class RangeTestHelper {

  private final TestCase test;

  public RangeTestHelper(TestCase test) {
    this.test = test;
  }

  @Test
  public void acceptTest() {
    test.runAcceptTests();
  }

  @Test
  public void rejectTest() {
    test.runRejectTests();
  }

  public static TestCase t(ParamCfg.Builder builder) throws InPUTException {
    return t(builder.build());
  }
  public static TestCase t(ParamCfg param) throws InPUTException {
    return new TestCase(param);
  }
  public static TestCase t(DesignSpaceCfg space, String id) throws InPUTException {
    return new TestCase(space, id);
  }
  public static TestCase t(Sink<Object> sink) {
    return new TestCase(sink);
  }

  public static ParamCfg.Builder pb() {
    return ParamCfg.builder();
  }
  public static DesignSpaceCfg.Builder ds() {
    return DesignSpaceCfg.builder();
  }

  /**
   * A test case for some range test. This class wraps a sink and accumulates
   * values to test for acceptance or rejection against the sink.
   * Note that it is possible to make multiple calls to add values for testing.
   * This can be useful, but it also makes debugging more problematic, since
   * it can be difficult to know which "batch" of values caused the test to
   * fail.
   */
  public static class TestCase {
    private final Sink<Object> sink;
    private final Collection<Runnable> acceptTests = new ArrayList<>();
    private final Collection<Runnable> rejectTests = new ArrayList<>();

    private TestCase(Sink<Object> sink) {
      this.sink = sink;
    }

    private TestCase(ParamCfg param) throws InPUTException {
      this.sink = Sink.fromParam(param);
    }

    private TestCase(DesignSpaceCfg space, String id) throws InPUTException {
      this.sink = Sink.fromDesign(space.getDesignSpace()
        .nextDesign("RangeTestDesign"), id);
    }

    public TestCase accepts(Object ... values) {
      acceptTests.add(() -> sink.accepts((Object[]) values));
      return this;
    }
    public TestCase rejects(Object ... values) {
      rejectTests.add(() -> sink.rejects((Object[]) values));
      return this;
    }

    private void runAcceptTests() {
      run(acceptTests);
    }
    private void runRejectTests() {
      run(rejectTests);
    }
    private void run(Collection<Runnable> tests) {
      for (Runnable test : tests) {
        test.run();
      }
    }
  }
}
