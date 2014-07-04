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
import static org.junit.Assert.*;

import java.util.Set;

import net.finkn.inputspec.tools.DesignSpaceCfg;
import net.finkn.inputspec.tools.ParamCfg;
import net.finkn.inputspec.tools.TestCleanup;

import org.junit.Test;

import se.miun.itm.input.model.design.IDesign;
import se.miun.itm.input.model.design.IDesignSpace;

/**
 * These tests examine in greater detail what designs and design spaces
 * include in their supported parameter ID sets. 
 *
 * @author Christoffer Fink
 */
public class SupportedParamIdsTest extends TestCleanup {
  private final ParamCfg arrayParam = ParamCfg.builder()
    .type("integer[1]").build();
  private final DesignSpaceCfg arraySpace = DesignSpaceCfg.builder()
    .param(arrayParam).build();

  /**
   * This test shows that the IDs of array elements (such as "X.1") are members
   * in the set of parameter IDs that <strong>designs</strong> support.
   */
  @Test
  public void designsIncludeArrayElementsInSupportedIds() throws Throwable {
    Set<String> ids = arraySpace.getDesignSpace()
      .nextDesign("Design").getSupportedParamIds();
    // Expecting {'X', 'X.1'}.
    assertThat(2, is(equalTo(ids.size())));
    assertThat(ids, hasItem(arrayParam.getId() + ".1"));
  }

  /**
   * This test demonstrates that the IDs of array elements (such as "X.1")
   * <strong>not</strong> members in the set of parameter IDs that
   * <strong>design spaces</strong> support.
   */
  @Test
  public void spacesDoNotIncludeArrayElementsInSupportedIds() throws Throwable {
    Set<String> ids = arraySpace.getDesignSpace().getSupportedParamIds();
    // Expecting {'X'}.
    assertThat(1, is(equalTo(ids.size())));
    assertThat(ids, hasItem(arrayParam.getId()));
  }

  /**
   * This test demonstrates that designs and design spaces do not necessarily
   * support the same set of parameter IDs.
   */
  @Test
  public void designsAndDesignSpacesSupportDifferentIds() throws Throwable {
    IDesignSpace space = DesignSpaceCfg.builder()
      .param(arrayParam).build().getDesignSpace();
    IDesign design = space.nextDesign("Design");
    assertNotEquals(space.getSupportedParamIds(), design.getSupportedParamIds());
  }
}
