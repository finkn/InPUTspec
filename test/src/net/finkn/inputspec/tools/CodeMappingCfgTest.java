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

import static net.finkn.inputspec.tools.Unit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class CodeMappingCfgTest {
  private final CodeMappingCfg.Builder builder = CodeMappingCfg.builder();

  @Test
  public void defaultBuilderShouldCreateCodeMapping() {
    assertThat(builder.build(), is(not(nullValue())));
  }

  @Test
  public void defaultBuilderShouldUseDefaultId() {
    CodeMappingCfg mapping = builder.build();
    assertThat(mapping.getId(), is(equalTo(CodeMappingCfg.DEFAULT_ID)));
  }

  @Test
  public void defaultBuilderShouldNotHaveMappings() {
    CodeMappingCfg mapping = builder.build();
    assertStreamEmpty(mapping.getMappings());
  }

  @Test
  public void nullIdShouldBeLegal() {
    assertThat(builder.id(null).build(), is(not(nullValue())));
  }

  @Test
  public void builderShouldUseCustomId() {
    String id = "My custom ID";
    CodeMappingCfg mapping = builder.id(id).build();
    assertThat(mapping.getId(), is(equalTo(id)));
  }
  @Test
  public void builderShouldUseAddedMappings() {
    MappingCfg mapping = MappingCfg.builder()
      .id("IntegerParam")
      .target(Integer.class)
      .build();
    MappingCfg wrapper = MappingCfg.builder()
      .id("WrapperType")
      .wrapper()
      .target(Double.class)
      .build();
    CodeMappingCfg codeMapping = builder.mapping(mapping, wrapper).build();

    List<MappingCfg> mappings = codeMapping.getMappings()
      .collect(Collectors.toList());

    assertThat(2, is(equalTo(mappings.size())));
    assertThat(mappings, hasItem(mapping));
    assertThat(mappings, hasItem(wrapper));
  }
}
