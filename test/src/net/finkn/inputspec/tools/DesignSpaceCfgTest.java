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

import static net.finkn.inputspec.tools.Unit.assertStreamEmpty;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import se.miun.itm.input.model.design.IDesignSpace;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;

public class DesignSpaceCfgTest {
  private DesignSpaceCfg.Builder builder = DesignSpaceCfg.builder();

  @Test
  public void nullIdShouldBeIgnored() {
    assertFalse(builder.id(null).build().getId().isPresent());
  }

  @Test
  public void nullRefShouldBeIgnored() {
    assertFalse(builder.ref(null).build().getMappingRef().isPresent());
  }

  @Test
  public void nullMappingShouldBeIgnored() {
    assertFalse(builder.mapping(null).build().getMapping().isPresent());
  }

  @Test
  public void defaultBuilderShouldCreateDesignSpace() {
    assertNotNull(builder.build());
  }

  @Test
  public void defaultBuilderShouldCreateDesignSpaceWithDefaultId() {
    Optional<String> id = builder.build().getId();
    assertTrue(id.isPresent());
    assertThat(id.get(), startsWith(DesignSpaceCfg.DEFAULT_ID));
  }

  @Test
  public void builderShouldCreateDesignSpacesWithSemiUniqueIds() {
    String id1 = builder.build().getId().get();
    String id2 = builder.build().getId().get();
    assertThat(id1, is(not(equalTo(id2))));
  }

  @Test
  public void defaultBuilderShouldCreateDesignSpaceWithoutMapping() {
    assertFalse(builder.build().getMapping().isPresent());
  }

  @Test
  public void defaultBuilderShouldCreateDesignSpaceWithoutMappingRef() {
    assertFalse(builder.build().getMappingRef().isPresent());
  }

  @Test
  public void defaultBuilderShouldCreateDesignSpaceWithNoParameters() {
    assertStreamEmpty(builder.build().getParameters());
  }

  @Test
  public void builderShouldUseCustomId() {
    String id = "Crab Battle!";
    assertEquals(id, builder.id(id).build().getId().get());
  }

  @Test
  public void builderShouldUseCodeMappingRef() {
    String ref = "code mapping reference";
    assertEquals(ref, builder.ref(ref).build().getMappingRef().get());
  }

  @Test
  public void builderShouldUseCodeMapping() {
    CodeMappingCfg mapping = CodeMappingCfg.builder().build();
    DesignSpaceCfg space = builder.mapping(mapping).build();
    assertThat(mapping, is(equalTo(space.getMapping().get())));
  }

  @Test
  public void builderShouldUseAddedParameters() {
    ParamCfg first = ParamCfg.builder().id("First").build();
    ParamCfg second = ParamCfg.builder().id("Second").build();
    ParamCfg third = ParamCfg.builder().id("Third").build();
    builder.param(first);
    builder.param(second, third);

    List<ParamCfg> params;
    params = builder.build().getParameters().collect(Collectors.toList());

    assertEquals(3, params.size());
    assertThat(params, hasItem(first));
    assertThat(params, hasItem(second));
    assertThat(params, hasItem(third));
  }

  @Test
  public void addingParametersToBuilderShouldNotAffectPreviouslyBuiltConfig() {
    ParamCfg first = ParamCfg.builder().id("First").build();
    ParamCfg second = ParamCfg.builder().id("Second").build();
    DesignSpaceCfg one = builder.param(first).build();
    DesignSpaceCfg two = builder.param(second).build();
    assertThat(1L, is(equalTo(one.getParameters().count())));
    assertThat(2L, is(equalTo(two.getParameters().count())));
  }

  @Test
  public void designSpaceShouldHaveMatchingId() throws Throwable {
    String id = "DesignSpaceId";
    IDesignSpace space = builder.id(id).build().getDesignSpace();
    assertEquals(id, space.getId());
  }

  @Test
  public void designSpaceShouldSupportParameterIds() throws Throwable {
    ParamCfg param = ParamCfg.builder().build();
    IDesignSpace space = builder.param(param).build().getDesignSpace();
    assertTrue(space.getSupportedParamIds().contains(param.getId()));
  }

  @Test(expected = NullPointerException.class)
  public void addingNullParameterShouldFail() {
    builder.param(ParamCfg.builder().build(), null);
  }

  // These two are basically the same. They make sure the order doesn't matter.
  @Test(expected = IllegalStateException.class)
  public void settingRefAndThenMappingShouldFail() {
    CodeMappingCfg mapping = CodeMappingCfg.builder().build();
    builder.ref("code mapping reference").mapping(mapping);
  }

  @Test(expected = IllegalStateException.class)
  public void settingMappingAndThenRefShouldFail() {
    CodeMappingCfg mapping = CodeMappingCfg.builder().build();
    builder.mapping(mapping).ref("code mapping reference");
  }

  // Setting both mapping and ref is OK as long as one has been "unset" first.
  @Test
  public void settingMappingAndRefAfterNullShouldSucceed() {
    CodeMappingCfg mapping = CodeMappingCfg.builder().build();
    builder.mapping(mapping).mapping(null).ref("code mapping reference");
  }
}
