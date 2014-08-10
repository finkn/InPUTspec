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

import java.util.Optional;

/**
 * A buildable test case for testing a {@link Sink}.
 * While primarily intended to examine values accepted by the {@code setValue}
 * method of a design to see what effects various parameter
 * configurations have, this class can be used with any sink.
 * <p>
 * The expected behavior of the sink can be set by adding values that
 * are expected to be accepted or rejected by the sink.
 * Setting the same kind of test multiple times (multiple calls to
 * {@link #accepts}, for example) is not allowed.
 * Executing the test case is not allowed if no expectations have been set.
 * <p>
 * This class is immutable.
 *
 * @author Christoffer Fink
 */
public class SinkTestCase {
  private static final SinkTestCase instance = new SinkTestCase(
    Optional.empty(), Optional.empty(), Optional.empty());

  private static final Object[] dummy = {};

  private final Optional<Sink<Object>> sink;
  private final Optional<Object[]> accepts;
  private final Optional<Object[]> rejects;

  private SinkTestCase(Optional<Sink<Object>> sink,
      Optional<Object[]> accepts, Optional<Object[]> rejects) {
    this.sink = sink;
    this.accepts = accepts;
    this.rejects = rejects;
  }

  public static SinkTestCase getInstance() {
    return instance;
  }

  /** All these values should be accepted by the sink. */
  public SinkTestCase accepts(Object ... values) {
    failIfPresent(accepts, "Already added an accepts tests.");
    return new SinkTestCase(sink, Optional.of(values), rejects);
  }

  /** All these values should be rejected by the sink. */
  public SinkTestCase rejects(Object ... values) {
    failIfPresent(rejects, "Already added a rejects tests.");
    return new SinkTestCase(sink, accepts, Optional.of(values));
  }

  /** Set the sink. */
  public SinkTestCase sink(Sink<Object> sink) {
    failIfPresent(this.sink, "Already set a sink.");
    return new SinkTestCase(Optional.of(sink), accepts, rejects);
  }

  /** Check whether any tests have been added. */
  public boolean hasTests() {
    return accepts.isPresent() || rejects.isPresent();
  }

  /**
   * Execute the test.
   * @throws IllegalStateException if there are no tests to run
   */
  public SinkTestCase run() {
    if (!sink.isPresent()) {
      throw new IllegalStateException("Sink missing!");
    }
    if (!(accepts.isPresent() || rejects.isPresent())) {
      throw new IllegalStateException("Refusing to run empty test.");
    }
    sink.get().accepts(accepts.orElse(dummy));
    sink.get().rejects(rejects.orElse(dummy));
    return this;
  }

  private void failIfPresent(Optional<?> opt, String msg) {
    if (opt.isPresent()) {
      throw new IllegalStateException(msg);
    }
  }
}
