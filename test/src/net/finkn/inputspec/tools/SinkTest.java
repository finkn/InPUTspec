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

import se.miun.itm.input.model.design.IDesign;

public class SinkTest {
  private final Predicate<Integer> pred = x -> x > 3;

  @Test
  public void acceptsShouldSucceedIfAllAreAccepted() {
    Sink.fromPredicate(pred).accepts(4, 5, 6);
  }

  @Test(expected = AssertionError.class)
  public void acceptsShouldFailIfAnyAreRejected() {
    Sink.fromPredicate(pred).accepts(4, 5, 1);
  }

  @Test
  public void rejectsShouldSucceedIfAllAreRejected() {
    Sink.fromPredicate(pred).rejects(1, 2, 3);
  }

  @Test(expected = AssertionError.class)
  public void rejectsShouldFailIfAllAnyAreAccepted() {
    Sink.fromPredicate(pred).rejects(1, 2, 4);
  }

  @Test
  public void sinkFromIntervalBehaviorShouldMatchInterval() {
    Sink.fromInterval("[1,2]")
      .rejects(0, 0.5, 2.5, 3)
      .accepts(1, 1.5, 2);
  }

  @Test
  public void sinkFromParam() throws Throwable {
    Sink.fromParam(ParamCfg.builder().interval("[1,2]").build())
      .rejects(0, 3)
      .accepts(1, 2);
  }

  // Same as sinkFromParam, but the interval is different. The two tests
  // taken together show that there are no caching problems.
  @Test
  public void paramIsNotCached() throws Throwable {
    Sink.fromParam(ParamCfg.builder().interval("[3,4]").build())
      .rejects(2, 5)
      .accepts(3, 4);
  }

  // Since fromParam uses fromDesign, this test is technically superfluous,
  // but maybe the implementation changes in the future.
  @Test
  public void sinkFromDesign() throws Throwable {
    ParamCfg param = ParamCfg.builder().interval("[1,2]").build();
    DesignSpaceCfg spaceCfg = DesignSpaceCfg.builder()
      .param(param)
      .build();
    IDesign design = spaceCfg.getDesignSpace().nextDesign("Design");
    Sink.fromDesign(design, param.getId())
      .rejects(0, 3)
      .accepts(1, 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void sinkFromDesignAndUnsupportedIdShouldFail() throws Throwable {
    ParamCfg param = ParamCfg.builder().build();
    IDesign design = DesignSpaceCfg.builder()
      .param(param)
      .build()
      .getDesignSpace()
      .nextDesign("Design");
    Sink.fromDesign(design, param.getId() + "A");
  }
}
