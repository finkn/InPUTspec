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

import net.finkn.inputspec.tools.*;

import org.junit.Test;

/**
 * Misc tests demonstrating illegal configurations in InPUT4j v0.5.
 * It is unclear whether a separate class for such tests is appropriate.
 *
 * @author Christoffer Fink
 * @see LegalConfigTest
 */
public class IllegalConfigTest {
  private final ConfigValidator validator = ConfigValidator.getInstance();

  @Test(expected = AssertionError.class)
  public void inclusiveAndExclusiveLimitIsIllegal() {
    ParamCfg param = ParamCfg.builder()
      .inclMin("1")
      .exclMin("3")
      .build();
    validator.validParamConfig(param);
  }

  @Test(expected = AssertionError.class)
  public void circularDependenciesAreIllegal() {
    ParamCfg a = ParamCfg.builder()
      .id("A")
      .inclMin("B + 1")
      .build();
    ParamCfg b = ParamCfg.builder()
      .id("B")
      .inclMin("C + 1")
      .build();
    ParamCfg c = ParamCfg.builder()
      .id("C")
      .inclMin("A + 1")
      .build();
    validator.validParamConfig(a, b, c);
  }
}
