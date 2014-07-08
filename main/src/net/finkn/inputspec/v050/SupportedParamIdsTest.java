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

import net.finkn.inputspec.tools.*;
import net.finkn.inputspec.tools.types.Point;

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
   * are <strong>not</strong> members in the set of parameter IDs that
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

  /**
   * This test shows that the IDs of nested parameters (such as "Point.X") are
   * members in the set of parameter IDs that design spaces as well as designs
   * support. That is, unlike arrays, these implicit IDs are supported, and
   * unlike arrays, they are supported both by design spaces and designs.
   */
  @Test
  public void designsAndDesignSpacesBothSupportNestedParams() throws Throwable {
    ParamCfg pointParam = ParamCfg.builder()
      .id("X").add()
      .id("Y").add()
      .structured()
      .id("Point")
      .build();
    MappingCfg pointMapping = MappingCfg.builder()
      .infer(pointParam, Point.class)
      .build();
    CodeMappingCfg codeMapping = CodeMappingCfg.builder()
      .mapping(pointMapping)
      .build();
    DesignSpaceCfg designSpace = DesignSpaceCfg.builder()
      .param(pointParam)
      .mapping(codeMapping)
      .build();

    IDesignSpace space = designSpace.getDesignSpace();
    assertThat(space.getSupportedParamIds(), hasItem("Point.X"));
    IDesign design = space.nextDesign("Design");
    assertEquals(space.getSupportedParamIds(), design.getSupportedParamIds());
  }
}
