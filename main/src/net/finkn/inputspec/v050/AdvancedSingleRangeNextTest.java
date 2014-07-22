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
package net.finkn.inputspec.v050;

import net.finkn.inputspec.tools.Generator;
import net.finkn.inputspec.tools.ParamCfg;
import net.finkn.inputspec.tools.GenTestCase;

import org.junit.Test;

// Tests involving single-range definitions with expressions.
public class AdvancedSingleRangeNextTest {

  private final int iterations = 100;
  private final GenTestCase testCase = GenTestCase.getInstance();

  // TODO: Do these two tests belong in this class?
  // Does it make more sense to put them in a test that merely examines which
  // configurations can be successfully imported, or here, where they fit in
  // with the other range definitions.
  // If we use the latter criterion, then all illegal configuration options
  // would have to be placed with their legal siblings, and a LegalConfigTest
  // would be impossible.
  @Test(expected = NumberFormatException.class)
  public void simpleExpressionIsIllegal() throws Throwable {
    test(pb().inclMin("1 + 2"));
  }

  @Test(expected = NumberFormatException.class)
  public void simpleMathLibraryExpression() throws Throwable {
    test(pb().inclMin("Math.PI"));
  }

  private static ParamCfg.Builder pb() {
    return ParamCfg.builder();
  }
  private GenTestCase test(ParamCfg.Builder pb) throws Throwable {
    return testCase.gen(Generator.fromParam(pb.build()).limit(iterations));
  }
}
