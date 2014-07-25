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

import java.io.ByteArrayInputStream;
import se.miun.itm.input.model.InPUTException;
import se.miun.itm.input.model.design.DesignSpace;

/**
 * A tool for checking whether a configuration is legal.
 * This is a very early and basic version. A more advanced version might
 * make a good candidate for a stand-alone tool.
 *
 * @author Christoffer Fink
 */
public class ConfigValidator {
  private ConfigValidator() {
  }

  public void validParamConfig(ParamCfg ... param) {
    validDesignSpaceConfig(DesignSpaceCfg.builder().param(param).build());
  }
  public void validDesignSpaceConfig(DesignSpaceCfg space) {
    validDesignSpaceConfig(space.xml());
  }
  public void validDesignSpaceConfig(String spaceXml) {
    try {
      new DesignSpace(new ByteArrayInputStream(spaceXml.getBytes()));
    } catch (Throwable e) {
      throw new AssertionError("Illegal config. Error: " + e.getMessage());
    }
  }

  public static ConfigValidator getInstance() {
    return new ConfigValidator();
  }
}
