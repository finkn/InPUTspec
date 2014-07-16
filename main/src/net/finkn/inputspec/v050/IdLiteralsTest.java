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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import net.finkn.inputspec.tools.*;

import org.junit.Test;

import se.miun.itm.input.model.InPUTException;
import se.miun.itm.input.model.design.IDesignSpace;

/**
 * Tests which IDs are legal in InPUT4j.
 *
 * @author Christoffer Fink
 */
public class IdLiteralsTest {

  /**
   * This test seems to indicate that almost any non-empty string is legal.
   */
  @Test
  public void almostAnyIdIsLegal() throws Throwable {
    String[] ids = {
      "hello",
      "Hello",
      "_hello",
      "Hello_World",
      "Hello World",
      "Hello.World",
      "hello123",

      // Should really be illegal.
      "123",
      "123hello",
      "! -+ 123 , hello. ~?", // Now this is just crazy. :p
      "  ",
      "",
    };

    for (String id : ids) {
      assertLegal(id);
    }
  }

  /**
   * An ID that only consists of a newline is invalid, probably due to XML
   * parsing problems. A configuration with such an ID is legal, but the ID
   * cannot subsequently be used.
   */
  @Test(expected = AssertionError.class)
  public void newlineIsInvalid() throws Throwable {
    assertLegal("\n");
  }

  /**
   * A {@code null} ID is illegal.
   */
  @Test(expected = Exception.class)
  public void nullIsAnIllegalId() throws Throwable {
    assertLegal(null);
  }

  private void assertLegal(String id) throws InPUTException {
    ParamCfg param = ParamCfg.builder().id(id).build();
    IDesignSpace space = DesignSpaceCfg.builder()
      .param(param)
      .build()
      .getDesignSpace();
    assertThat(space.getSupportedParamIds(), hasItem(param.getId()));
    // Checking that the ID is in the set of supported IDs should probably be
    // enough. However, this demonstrates that InPUT4j can generate meaningful
    // values for the parameter. This should quash any doubt.
    Generator.fromDesignSpace(space, id).isVariable();
  }
}
