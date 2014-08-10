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
import net.finkn.inputspec.tools.*;

import org.junit.Test;

import se.miun.itm.input.model.InPUTException;
import se.miun.itm.input.model.design.IDesignSpace;

/**
 * Demonstrates caching of code mappings in InPUT4j v0.5.
 * These tests probably shouldn't contribute to the InPUT specification,
 * since they demonstrate what seem to be implementation-specific details.
 * It is doubtful that these are details that are relevant for other
 * implementations. However, the tools that are available within this project
 * make it convenient to do these tests here.
 * They could be moved in the future.
 *
 * @author Christoffer Fink
 * @see DesignSpaceCachingTest
 */
public class CodeMappingCachingTest {

  private final String paramId = "Parameter";
  private final ParamCfg intParam = ParamCfg.builder()
    .type("integer")
    .nest()
    .structured()
    .id(paramId)
    .build();
  private final ParamCfg floatParam = ParamCfg.builder()
    .type("float")
    .nest()
    .structured()
    .id(paramId)
    .build();
  private final MappingCfg intMapping = MappingCfg.builder()
    .infer(intParam, Integer.class)
    .nested(intParam.getNested())
    .build();
  private final MappingCfg floatMapping = MappingCfg.builder()
    .infer(floatParam, Float.class)
    .nested(floatParam.getNested())
    .build();

  /**
   * InPUT4j v0.5 reuses code mappings when creating new code mappings with the
   * same ID, even when the configurations are different.
   */
  @Test(expected = InPUTException.class)
  public void codeMappingsAreCachedById() throws Throwable {
    getIntSpace("Mapping");
    // Both have ID "Mapping", so the int mapping will be reused here.
    IDesignSpace space = getFloatSpace("Mapping");

    // The int Mapping doesn't match the design space. Hence the exception.
    space.next(paramId);
  }

  /** Using different IDs is sufficient to avoid caching. */
  @Test
  public void codeMappingsWithDifferentIdsAreIndependent() throws Throwable {
    IDesignSpace intSpace = getIntSpace("IntMapping");
    IDesignSpace floatSpace = getFloatSpace("FloatMapping");

    assertThat(intSpace.next(paramId), isA(Integer.class));
    assertThat(floatSpace.next(paramId), isA(Float.class));
  }

  private IDesignSpace getIntSpace(String mappingId) throws Throwable {
    return getSpace(intParam, getMapping(mappingId, intMapping));
  }
  private IDesignSpace getFloatSpace(String mappingId) throws Throwable {
    return getSpace(floatParam, getMapping(mappingId, floatMapping));
  }
  private CodeMappingCfg getMapping(String id, MappingCfg mapping) {
    return CodeMappingCfg.builder()
      .id(id)
      .mapping(mapping)
      .build();
  }
  private IDesignSpace getSpace(ParamCfg param, CodeMappingCfg mapping) throws Throwable {
    return DesignSpaceCfg.builder()
      .param(param)
      .mapping(mapping)
      .build()
      .getDesignSpace();
  }
}
