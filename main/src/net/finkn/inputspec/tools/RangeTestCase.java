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

import se.miun.itm.input.model.InPUTException;
import se.miun.itm.input.model.design.IDesignSpace;

/**
 * A buildable test case for performing various range tests.
 * This class wraps both a {@link GenTestCase} and {@link SinkTestCase}.
 * The main use case for this class is to examine a parameter definition and
 * testing which values are produced and accepted for that parameter. So
 * it's simply a combination of both kinds of tests. It saves repeating the
 * same configuration in multiple different tests, and it highlights that
 * the set of legal values is expected to coincide with the set of generated
 * values.
 *
 * @author Christoffer Fink
 * @version 0.8
 */
public class RangeTestCase {

  private static final RangeTestCase instance = new RangeTestCase(
      GenTestCase.getInstance(), SinkTestCase.getInstance());

  private final GenTestCase genTest;
  private final SinkTestCase sinkTest;

  private RangeTestCase(GenTestCase genTest, SinkTestCase sinkTest) {
    this.genTest = genTest;
    this.sinkTest = sinkTest;
  }

  public static RangeTestCase getInstance() {
    return instance;
  }

  /** Shortcut to {@link #sink(Sink)} and {@link #gen(Generator)}. */
  public RangeTestCase param(ParamCfg param) throws InPUTException {
    return param(param.getId(), param);
  }
  /** Shortcut to {@link #sink(Sink)} and {@link #gen(Generator)}. */
  public RangeTestCase param(String id, ParamCfg ... params) throws InPUTException {
    DesignSpaceCfg space = DesignSpaceCfg.builder().param(params).build();
    return space(space, id);
  }
  /** Shortcut to {@link #sink(Sink)} and {@link #gen(Generator)}. */
  public RangeTestCase space(DesignSpaceCfg spaceCfg, String paramId)
      throws InPUTException {
    IDesignSpace space = spaceCfg.getDesignSpace();
    return gen(Generator.fromDesignSpace(space, paramId))
      .sink(Sink.fromDesign(space.nextDesign("RangeTestCaseDesign"), paramId));
  }

  public RangeTestCase accepts(Object ... values) {
    return new RangeTestCase(genTest, sinkTest.accepts(values));
  }
  public RangeTestCase rejects(Object ... values) {
    return new RangeTestCase(genTest, sinkTest.rejects(values));
  }
  public RangeTestCase all(Object ... values) {
    return new RangeTestCase(genTest.all(values), sinkTest);
  }
  public RangeTestCase only(Object ... values) {
    return new RangeTestCase(genTest.only(values), sinkTest);
  }
  public RangeTestCase any(Object ... values) {
    return new RangeTestCase(genTest.any(values), sinkTest);
  }
  public RangeTestCase none(Object ... values) {
    return new RangeTestCase(genTest.none(values), sinkTest);
  }
  public RangeTestCase expected(Object ... values) {
    return new RangeTestCase(genTest.expected(values), sinkTest.accepts(values));
  }
  public RangeTestCase sink(Sink<Object> sink) {
    return new RangeTestCase(genTest, sinkTest.sink(sink));
  }
  public RangeTestCase gen(Generator<Object> gen) {
    return new RangeTestCase(genTest.gen(gen), sinkTest);
  }

  public RangeTestCase run() {
    if (!(sinkTest.hasTests() || genTest.hasTests())) {
      throw new IllegalStateException("Refusing to run empty test.");
    }
    if (sinkTest.hasTests()) {
      sinkTest.run();
    }
    if (genTest.hasTests()) {
      genTest.run();
    }
    return this;
  }
}
