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

import net.finkn.inputspec.tools.types.Point;

import se.miun.itm.input.model.InPUTException;

/**
 * Helper for writing more concise tests.
 * Exports common configurations and small functions that are common to
 * multiple tests.
 */
public class Helper {

  private static final GenTestCase genTest = GenTestCase.getInstance();
  private static final SinkTestCase sinkTest = SinkTestCase.getInstance();

  public static final ParamCfg pointParam = ParamCfg.builder()
      .id("X").inclMin("10").inclMax("20").add()
      .id("Y").inclMin("15").inclMax("25").add()
      .structured()
      .id("Point")
      .build();

  public static final MappingCfg pointMapping = MappingCfg.builder()
      .infer(pointParam, Point.class)
      .build();

  public static ParamCfg.Builder pb() {
    return ParamCfg.builder();
  }

  public static SinkTestCase sinkTest(ParamCfg.Builder builder) throws InPUTException {
    return sinkTest.sink(Sink.fromParam(builder.build()));
  }

  public static GenTestCase genTest(ParamCfg.Builder builder) throws InPUTException {
    return genTest.gen(Generator.fromParam(builder.build()));
  }

  public static GenTestCase genTest(String id, ParamCfg ... cfgs) throws InPUTException {
    DesignSpaceCfg space = DesignSpaceCfg.builder().param(cfgs).build();
    return genTest.gen(Generator.fromDesignSpace(space.getDesignSpace(), id));
  }
}
