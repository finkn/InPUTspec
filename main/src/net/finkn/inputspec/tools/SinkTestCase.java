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

  public SinkTestCase accepts(Object ... values) {
    failIfPresent(accepts, "Already added an accepts tests.");
    return new SinkTestCase(sink, Optional.of(values), rejects);
  }

  public SinkTestCase rejects(Object ... values) {
    failIfPresent(rejects, "Already added a rejects tests.");
    return new SinkTestCase(sink, accepts, Optional.of(values));
  }

  public SinkTestCase sink(Sink<Object> sink) {
    failIfPresent(this.sink, "Already set a sink.");
    return new SinkTestCase(Optional.of(sink), accepts, rejects);
  }

  public boolean hasTests() {
    return accepts.isPresent() || rejects.isPresent();
  }

  public SinkTestCase run() {
    if (!sink.isPresent()) {
      throw new IllegalStateException("Sink missing!");
    }
    if (!(accepts.isPresent() || rejects.isPresent())) {
      throw new IllegalStateException("Refusing to run empty test.");
    }
    runAcceptTest();
    runRejectTest();
    return this;
  }
  SinkTestCase runAcceptTest() {
    sink.get().accepts(accepts.orElse(dummy));
    return this;
  }
  SinkTestCase runRejectTest() {
    sink.get().rejects(rejects.orElse(dummy));
    return this;
  }

  private void failIfPresent(Optional<?> opt, String msg) {
    if (opt.isPresent()) {
      throw new IllegalStateException(msg);
    }
  }
}
