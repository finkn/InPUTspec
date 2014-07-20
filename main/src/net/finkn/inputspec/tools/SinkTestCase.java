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

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;

// FIXME: Should probably remove support for duplicate tests.
public class SinkTestCase {
  private static final SinkTestCase instance = new SinkTestCase(
    null, Collections.emptyList(), Collections.emptyList());

  private final Sink<Object> sink;
  private final Collection<Runnable> acceptTests;
  private final Collection<Runnable> rejectTests;

  private SinkTestCase(Sink<Object> sink,
      Collection<Runnable> accept, Collection<Runnable> reject) {
    this.sink = sink;
    this.acceptTests = accept;
    this.rejectTests = reject;
  }

  public static SinkTestCase getInstance() {
    return instance;
  }

  public SinkTestCase accepts(Object ... values) {
    Collection<Runnable> tmp = new ArrayList<>(acceptTests);
    tmp.add(() -> sink.accepts((Object[]) values));
    return new SinkTestCase(sink, Collections.unmodifiableCollection(tmp), rejectTests);
  }
  public SinkTestCase rejects(Object ... values) {
    Collection<Runnable> tmp = new ArrayList<>(rejectTests);
    tmp.add(() -> sink.rejects((Object[]) values));
    return new SinkTestCase(sink, acceptTests, Collections.unmodifiableCollection(tmp));
  }

  public SinkTestCase sink(Sink<Object> sink) {
    return new SinkTestCase(sink, acceptTests, rejectTests);
  }

  public SinkTestCase run() {
    runAcceptTests();
    runRejectTests();
    return this;
  }
  SinkTestCase runAcceptTests() {
    return run(acceptTests);
  }
  SinkTestCase runRejectTests() {
    return run(rejectTests);
  }
  private SinkTestCase run(Collection<Runnable> tests) {
    for (Runnable test : tests) {
      test.run();
    }
    return this;
  }
}
