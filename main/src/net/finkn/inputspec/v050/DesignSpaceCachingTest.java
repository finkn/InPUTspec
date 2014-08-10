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

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import net.finkn.inputspec.tools.DesignSpaceCfg;
import net.finkn.inputspec.tools.ParamCfg;

import org.junit.Test;

import se.miun.itm.input.model.design.IDesignSpace;

/**
 * Demonstrates caching of design spaces in InPUT4j v0.5.
 * These tests probably shouldn't contribute to the InPUT specification,
 * since they demonstrate what seem to be implementation-specific details.
 * It is doubtful that these are details that are relevant for other
 * implementations. However, the tools that are available within this project
 * make it convenient to do these tests here.
 * They could be moved in the future.
 *
 * @author Christoffer Fink
 */
public class DesignSpaceCachingTest {

  private final String PARAM_ID = "Parameter";

  /**
   * InPUT4j v0.5 reuses design spaces when creating new design spaces with the
   * same ID, even when the configurations are different.
   */
  @Test
  public void designSpacesAreCachedById() throws Throwable {
    String spaceId = "DesignSpace";

    IDesignSpace floatSpace = getFloatSpace(spaceId);
    IDesignSpace boolSpace = getBoolSpace(spaceId);

    // Both are expected to be Float because of caching.
    assertThat(boolSpace.next(PARAM_ID), isA(Float.class));
    assertThat(floatSpace.next(PARAM_ID), isA(Float.class));
  }

  /** Using different IDs is sufficient to avoid caching. */
  @Test
  public void designSpacesWithDifferentIdsAreIndependent() throws Throwable {
    IDesignSpace floatSpace = getFloatSpace("FloatSpace");
    IDesignSpace boolSpace = getBoolSpace("BoolSpace");

    // One is Boolean, the other is Float.
    assertThat(boolSpace.next(PARAM_ID), isA(Boolean.class));
    assertThat(floatSpace.next(PARAM_ID), isA(Float.class));
  }

  private IDesignSpace getFloatSpace(String id) throws Throwable {
    return getSpace(id, getParam("float"));
  }
  private IDesignSpace getBoolSpace(String id) throws Throwable {
    return getSpace(id, getParam("boolean"));
  }
  private ParamCfg getParam(String type) {
    return ParamCfg.builder()
      .id(PARAM_ID)
      .type(type)
      .build();
  }
  private IDesignSpace getSpace(String id, ParamCfg param) throws Throwable {
    return DesignSpaceCfg.builder()
      .id(id)
      .param(param)
      .build()
      .getDesignSpace();
  }
}
