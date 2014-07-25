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

import org.junit.Test;

public class ConfigValidatorTest {
  private final ConfigValidator validator = ConfigValidator.getInstance();

  @Test(expected = AssertionError.class)
  public void parameterWithMissingIdIsIllegal() {
    check(ParamCfg.builder().id(null).build());
  }

  @Test(expected = AssertionError.class)
  public void designSpaceWithMissingIdIsIllegal() {
    check(DesignSpaceCfg.builder().id(null).build());
  }

  @Test(expected = AssertionError.class)
  public void parameterWithDuplicateLimitsIsIllegal() {
    ParamCfg param = ParamCfg.builder()
      .inclMin(1)
      .exclMin(2)
      .build();
    check(DesignSpaceCfg.builder().param(param).build());
  }

  @Test(expected = AssertionError.class)
  public void arbitraryStringIsAnIllagalConfig() {
    check("This is not entirely valid XML.");
  }

  @Test
  public void designSpaceWithDefaultParameterShouldBeLegal() {
    check(DesignSpaceCfg.builder().param(ParamCfg.getDefault()).build());
  }

  private void check(ParamCfg cfg) {
    validator.validParamConfig(cfg);
  }
  private void check(DesignSpaceCfg cfg) {
    validator.validDesignSpaceConfig(cfg);
  }
  private void check(String cfg) {
    validator.validDesignSpaceConfig(cfg);
  }
}
