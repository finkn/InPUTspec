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

import net.finkn.inputspec.tools.X;

import org.junit.Test;

public class MappingCfgTest {
  private final MappingCfg.Builder builder = MappingCfg.builder();
  private final MappingCfg.Builder ignoreTest = MappingCfg.builder()
      .id("id").add("add").constructor("X").get("get").set("set").type("type");

  @Test
  public void defaultBuilderShouldCreateMapping() {
    assertNotNull(builder.build());
  }

  @Test
  public void defaultBuilderShouldNotUseAnyDefaults() {
    MappingCfg m = builder.build();
    assertThat(m.getAdd(), is(nullValue()));
    assertThat(m.getConstructor(), is(nullValue()));
    assertThat(m.getGet(), is(nullValue()));
    assertThat(m.getSet(), is(nullValue()));
    assertThat(m.getType(), is(nullValue()));
    assertThat(m.getId(), is(nullValue()));
  }

  @Test
  public void mappingTypeShouldIgnoreAdd() {
    MappingCfg m = ignoreTest.mappingType().build();

    assertThat(m.getAdd(), is(nullValue()));

    assertThat(m.getConstructor(), is(not(nullValue())));
    assertThat(m.getGet(), is(not(nullValue())));
    assertThat(m.getSet(), is(not(nullValue())));
    assertThat(m.getType(), is(not(nullValue())));
    assertThat(m.getId(), is(not(nullValue())));
  }

  @Test
  public void mappingShouldIgnoreAdd() {
    MappingCfg m = ignoreTest.mapping().build();

    assertThat(m.getAdd(), is(nullValue()));

    assertThat(m.getConstructor(), is(not(nullValue())));
    assertThat(m.getGet(), is(not(nullValue())));
    assertThat(m.getSet(), is(not(nullValue())));
    assertThat(m.getType(), is(not(nullValue())));
    assertThat(m.getId(), is(not(nullValue())));
  }

  @Test
  public void wrapperShouldIgnoreAdd() {
    MappingCfg m = ignoreTest.wrapper().build();

    assertThat(m.getAdd(), is(nullValue()));

    assertThat(m.getConstructor(), is(not(nullValue())));
    assertThat(m.getGet(), is(not(nullValue())));
    assertThat(m.getSet(), is(not(nullValue())));
    assertThat(m.getType(), is(not(nullValue())));
    assertThat(m.getId(), is(not(nullValue())));
  }

  @Test
  public void complexShouldIgnoreGetSetAndConstructor() {
    MappingCfg m = ignoreTest.complex().build();

    assertThat(m.getConstructor(), is(nullValue()));
    assertThat(m.getGet(), is(nullValue()));
    assertThat(m.getSet(), is(nullValue()));

    assertThat(m.getAdd(), is(not(nullValue())));
    assertThat(m.getType(), is(not(nullValue())));
    assertThat(m.getId(), is(not(nullValue())));
  }

  @Test
  public void nullIdShouldBeLegal() {
    assertNotNull(builder.id(null).build());
  }

  @Test
  public void nullTypeShouldBeLegal() {
    assertNotNull(builder.type(null).build());
  }

  @Test
  public void nullGetShouldBeLegal() {
    assertNotNull(builder.get(null).build());
  }

  @Test
  public void nullSetShouldBeLegal() {
    assertNotNull(builder.set(null).build());
  }

  @Test
  public void nullConstructorShouldBeLegal() {
    assertNotNull(builder.constructor(null).build());
  }

  @Test
  public void nullAddShouldBeLegal() {
    assertNotNull(builder.add(null).build());
  }

  @Test
  public void nestedWithParametersShouldJoinParamIds() {
    ParamCfg.Builder pb = ParamCfg.builder();
    ParamCfg first = pb.id("FirstParam").build();
    ParamCfg second = pb.id("SecondParam").build();

    MappingCfg mapping = builder.nested(first, second).build();
    String constructor = mapping.getConstructor();
    String expected = "FirstParam SecondParam";

    assertThat(constructor, is(equalTo(expected)));
  }

  @Test
  public void nestedWithStreamShouldJoinParamIds() {
    ParamCfg.Builder pb = ParamCfg.builder();
    ParamCfg first = pb.id("FirstParam").build();
    ParamCfg second = pb.id("SecondParam").build();
    ParamCfg param = pb
      .structured()
      .add(first, second)
      .build();

    MappingCfg mapping = builder.nested(param.getNested()).build();
    String constructor = mapping.getConstructor();
    String expected = "FirstParam SecondParam";

    assertThat(constructor, is(equalTo(expected)));
  }

  @Test
  public void paramShouldSetIdToParamId() {
    ParamCfg param = ParamCfg.builder().build();
    MappingCfg mapping = builder.param(param).build();
    String id = mapping.getId();
    assertThat(id, is(equalTo(param.getId())));
  }

  @Test
  public void targetShouldSetTypeToFullClassName() {
    MappingCfg mapping = builder.target(Integer.class).build();
    String type = mapping.getType();
    String expected = "java.lang.Integer";
    assertThat(type, is(equalTo(expected)));
  }

  @Test
  public void inferWithParamAndClassShouldSetMappingWithMatchingIdAndType() {
    ParamCfg param = ParamCfg.builder().build();
    String expectedId = param.getId();
    Class<Integer> typeClass = Integer.class;
    String expectedType = typeClass.getName();
    MappingCfg.Type expectedMappingType = MappingCfg.Type.MAPPING;

    MappingCfg mapping = builder.infer(param, typeClass).build();
    assertThat(mapping.getMappingType(), is(equalTo(expectedMappingType)));
    assertThat(mapping.getId(), is(equalTo(expectedId)));
    assertThat(mapping.getType(), is(equalTo(expectedType)));
  }

  @Test
  public void inferWithParamAndMappingShouldSetMappingWithMatchingIdAndType() {
    ParamCfg param = ParamCfg.builder().build();
    MappingCfg mappingType = MappingCfg.builder()
      .mappingType()
      .id("CustomType")
      .type("java.lang.Integer")
      .build();
    String expectedId = param.getId();
    String expectedType = mappingType.getId();
    MappingCfg.Type expectedMappingType = MappingCfg.Type.MAPPING;

    MappingCfg mapping = builder.infer(param, mappingType).build();
    assertThat(mapping.getMappingType(), is(equalTo(expectedMappingType)));
    assertThat(mapping.getId(), is(equalTo(expectedId)));
    assertThat(mapping.getType(), is(equalTo(expectedType)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void inferWithTheWrongMappingTypeShouldFail() {
    ParamCfg param = ParamCfg.builder().build();
    MappingCfg mapping = MappingCfg.builder().complex().build();
    builder.infer(param, mapping);
  }

  @Test
  public void testMappingXml() {
    ParamCfg param = ParamCfg.builder().build();
    MappingCfg mapping = builder.infer(param, Integer.class).build();
    String xml = mapping.xml();
    assertStringContainsAll(xml, X.MAPPING, X.TYPE, X.ID, param.getId());
    assertStringContainsNone(xml, X.GET, X.SET, X.ADD, X.WRAPPER);
  }

  @Test
  public void testWrapperXml() {
    ParamCfg param = ParamCfg.builder().build();
    MappingCfg mapping = builder
      .wrapper()
      .param(param)
      .target(Integer.class)
      .build();
    String xml = mapping.xml();
    assertStringContainsAll(xml, X.WRAPPER, X.MAPPING, param.getId());
    assertStringContainsNone(xml, X.GET, X.SET, X.ADD);
  }
}
