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

public class ParamCfgTest {
  private final static String INTERVAL = "[1,2]";
  private final ParamCfg.Builder builder = ParamCfg.builder();

  @Test
  public void getDefaultShouldReturnDefaultParamCfg() {
    ParamCfg p1 = ParamCfg.getDefault();
    ParamCfg p2 = builder.build();
    assertThat(p1.xml(), is(equalTo(p2.xml())));
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
  public void nullFixedShouldBeLegal() {
    assertNotNull(builder.fixed(null).build());
  }

  @Test
  public void defaultBuilderShouldCreateParam() {
    assertNotNull(builder.build());
  }

  @Test
  public void defaultBuilderShouldCreateParamWithDefaultId() {
    ParamCfg param = builder.build();
    assertEquals(ParamCfg.DEFAULT_ID, param.getId());
  }

  @Test
  public void defaultBuilderShouldCreateParamWithDefaultType() {
    ParamCfg param = builder.build();
    assertEquals(ParamCfg.DEFAULT_TYPE, param.getType());
  }

  @Test
  public void defaultBuilderShouldCreateUnfixedParam() {
    ParamCfg param = builder.build();
    assertNull(param.getFixed());
  }

  @Test
  public void defaultBuilderShouldCreateParamWithoutLimits() {
    Range range = builder.build().getRange();
    assertRangeHasNoLimits(range);
  }

  @Test
  public void defaultBuilderShouldCreateParamWithoutNestedParams() {
    ParamCfg param = builder.build();
    assertStreamEmpty(param.getNested());
  }

  @Test
  public void builderShouldUseCustomId() {
    String id = "custom param ID";
    ParamCfg param = builder.id(id).build();
    assertEquals(id, param.getId());
  }

  @Test
  public void builderShouldUseCustomType() {
    String type = "custom type";
    ParamCfg param = builder.type(type).build();
    assertEquals(type, param.getType());
  }

  @Test
  public void builderShouldUseFixedValue() {
    String fixed = "custom fixed value";
    ParamCfg param = builder.fixed(fixed).build();
    assertEquals(fixed, param.getFixed());
  }

  @Test
  public void builderShouldUseInterval() {
    ParamCfg param = builder.interval(INTERVAL).build();
    assertEquals("1.0", param.getRange().inclMin().get());
    assertEquals("2.0", param.getRange().inclMax().get());
  }

  @Test
  public void builderShouldUseLimits() {
    ParamCfg param = builder.exclMin("1").exclMax("2").build();
    assertEquals("1", param.getRange().exclMin().get());
    assertEquals("2", param.getRange().exclMax().get());
  }

  @Test
  public void builderShouldUseNestedParamsWhenBuildingStructured() {
    ParamCfg nestedParam = ParamCfg.builder().id("nested").build();
    ParamCfg param = ParamCfg.builder().structured().add(nestedParam).build();
    List<ParamCfg> nested = param.getNested().collect(Collectors.toList());
    assertEquals(1, nested.size());
    assertThat(nested, hasItem(nestedParam));
  }

  @Test
  public void builderShouldIgnoreNestedParamsWhenBuildingNumeric() {
    ParamCfg nestedParam = ParamCfg.builder().id("nested").build();
    // Numeric is the default. Setting it explicitly anyway.
    ParamCfg param = ParamCfg.builder().numeric().add(nestedParam).build();
    assertStreamEmpty(param.getNested());
  }

  @Test
  public void builderShouldIgnoreLimitsWhenBuildingStructuredParam() {
    ParamCfg param = builder.inclMin("1").structured().build();
    assertRangeHasNoLimits(param.getRange());
  }

  @Test
  public void builderShouldIgnoreIntervalWhenBuildingStructuredParam() {
    ParamCfg param = builder.interval(INTERVAL).structured().build();
    assertRangeHasNoLimits(param.getRange());
  }

  // This is a nonsense configuration, but the builder has to allow it.
  @Test
  public void buildingParamWithMultipleLimitsShouldBeAllowed() {
    ParamCfg param = builder.inclMin("1").exclMin("2").build();
    assertRangeHasLimits(param.getRange());
  }

  @Test
  public void builderShouldUseCustomTypeForSParam() {
    String type = "Square";
    ParamCfg param = builder.type(type).structured().build();
    assertEquals(type, param.getType());
  }

  @Test
  public void builderShouldNotUseDefaultTypeForSParam() {
    ParamCfg param = builder.structured().build();
    assertNull(param.getType());
  }

  // Because the same builder is used, both first and second should be added
  // to the nested parameters of two. However, one was built after only first
  // was added, and adding second should only affect two, not one. So one
  // should only have one nested parameter.
  @Test
  public void addingNestedToBuilderShouldNotAffectPreviouslyBuiltParameter() {
    ParamCfg first = builder.id("First").build();
    ParamCfg second = builder.id("Second").build();
    ParamCfg one = builder.structured().id("OneNested").add(first).build();
    ParamCfg two = builder.structured().id("TwoNested").add(second).build();
    assertThat(1L, is(equalTo(one.getNested().count())));
    assertThat(2L, is(equalTo(two.getNested().count())));
  }

  @Test
  public void addShouldAddTheParameterConfigurationToNested() {
    ParamCfg param = builder.id("X").add().id("Y").add()
      .structured().id("Z").build();
    assertThat(2L, is(equalTo(param.getNested().count())));
    assertTrue(param.getNested().anyMatch(p -> p.getId().equals("X")));
    assertTrue(param.getNested().anyMatch(p -> p.getId().equals("Y")));
  }

  @Test
  public void nestShouldReplaceNestedParameters() {
    ParamCfg param = builder.id("X").add().id("Y").add()
      .structured().id("Point").nest()
      .numeric().id("R").add()
      .structured().id("Circle").build();

    assertThat(2L, is(equalTo(param.getNested().count())));
    assertTrue(param.getNested().anyMatch(p -> p.getId().equals("R")));

    param = param.getNested()
      .filter(p -> p.getId().equals("Point"))
      .findFirst().get();

    assertThat(2L, is(equalTo(param.getNested().count())));
    assertTrue(param.getNested().anyMatch(p -> p.getId().equals("X")));
    assertTrue(param.getNested().anyMatch(p -> p.getId().equals("Y")));
  }

  @Test
  public void xmlShouldIncludeAllAttributesAndChildren() {
    ParamCfg p1 = ParamCfg.builder()
      .id("X").interval("[1,3[").fixed("2").build();
    ParamCfg p2 = ParamCfg.builder()
      .id("Y").interval("]1,3]").type("float").build();
    ParamCfg p3 = ParamCfg.builder()
      .id("Z").structured().add(p1, p2).build();
    String xml;

    xml = p1.xml();
    assertStringContainsAll(xml, X.ID, X.TYPE, X.FIXED, X.INCLMIN, X.EXCLMAX,
      X.NPARAM, ParamCfg.DEFAULT_TYPE, "X", "1", "3", "2");
    assertStringContainsNone(xml, X.EXCLMIN, X.INCLMAX, X.SPARAM);

    xml = p2.xml();
    assertStringContainsAll(xml, X.ID, X.TYPE, X.INCLMAX, X.EXCLMIN,
      X.NPARAM, "Y", "1", "3", "float");
    assertStringContainsNone(xml, X.FIXED, X.INCLMIN, X.EXCLMAX, X.SPARAM);

    xml = p3.xml();
    assertStringContainsAll(xml, X.ID, X.TYPE, X.FIXED, X.INCLMIN, X.EXCLMIN,
      X.INCLMAX, X.EXCLMAX, X.NPARAM, X.SPARAM, "X", "Y", "Z");
  }
}
