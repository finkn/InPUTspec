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

import static net.finkn.inputspec.tools.Helper.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.finkn.inputspec.tools.*;
import net.finkn.inputspec.tools.types.Point;

import org.junit.Test;

import se.miun.itm.input.model.design.IDesign;

/**
 * Examines dependencies involving nested parameters in InPUT4j v0.5.
 *
 * @author Christoffer Fink
 * @see AccessorTest
 * @see ValueConsistencyTest
 */
public class NestedDependencyTest {

  /** Referencing siblings by their relative names is illegal. */
  @Test(expected = NumberFormatException.class)
  public void innerParametersCannotReferenceRelativeSiblings() throws Throwable {
    ParamCfg point = pb()
      .id("X").interval("[1,1]").add()
      .id("Y").inclMin("X+1").inclMax("X+1").add()
      .id("Point").structured()
      .build();
    getDesign(point);
  }

  /** Outer parameters can reference the inner parameters of others. */
  @Test
  public void outerCanReferenceInner() throws Throwable {
    ParamCfg outer = pb()
      .id("A")
      .inclMin("Point.X+1").inclMax("Point.X+1")
      .build();
    ParamCfg point = pb()
      .id("X").interval("[2,2]").add()
      .id("Y").interval("[4,4]").add()
      .id("Point").structured()
      .build();

    CodeMappingCfg mapping = codeMapping(point, Point.class);
    IDesign design = design(mapping, point, outer);
    assertThat(design.getValue("A"), is(equalTo(2+1)));
    assertThat(design.getValue("Point.X"), is(equalTo(2)));
    assertThat(design.getValue("Point.Y"), is(equalTo(4)));
  }

  /** Inner parameters can reference outer parameters. */
  @Test
  public void innerCanReferenceOuter() throws Throwable {
    ParamCfg outer = pb()
      .id("A")
      .interval("[1,1]").add()
      .build();
    ParamCfg point = pb()
      .id("X").interval("[2,2]").add()
      .id("Y").inclMin("A+3").inclMax("A+3").add()
      .id("Point").structured()
      .build();

    CodeMappingCfg mapping = codeMapping(point, Point.class);
    IDesign design = design(mapping, point, outer);
    assertThat(design.getValue("A"), is(equalTo(1)));
    assertThat(design.getValue("Point.X"), is(equalTo(2)));
    assertThat(design.getValue("Point.Y"), is(equalTo(4)));
  }

  /**
   * Inner parameters can reference siblings by their absolute IDs.
   * Contrast this with
   * {@link #innerParametersCannotReferenceRelativeSiblings}.
   */
  @Test
  public void innerParametersCanReferenceAbsoluteSiblings() throws Throwable {
    ParamCfg point = pb()
      .id("X").interval("[2,2]").add()
      .id("Y").inclMin("Point.X+1").inclMax("Point.X+1").add()
      .id("Point").structured()
      .build();

    IDesign design = getDesign(point);
    assertThat(design.getValue("Point.X"), is(equalTo(2)));
    assertThat(design.getValue("Point.Y"), is(equalTo(3)));
  }

  /** Inner parameters can reference cousins using absolute IDs. */
  @Test
  public void innerParametersCanReferenceAbsoluteCousins() throws Throwable {
    ParamCfg point1 = pb()
      .id("X").interval("[1,1]").add()
      .id("Y").interval("[3,3]").add()
      .id("Point1").structured()
      .build();
    ParamCfg point2 = pb()
      .id("X").inclMin("Point1.X+1").inclMax("Point1.X+1").add()
      .id("Y").inclMin("Point1.Y+1").inclMax("Point1.Y+1").add()
      .id("Point2").structured()
      .build();

    MappingCfg p1Mapping = mapping(point1, Point.class);
    MappingCfg p2Mapping = mapping(point2, Point.class);
    CodeMappingCfg mapping = CodeMappingCfg.getInstance(p1Mapping, p2Mapping);

    IDesign design = design(mapping, point1, point2);
    assertThat(design.getValue("Point2.X"), is(equalTo(2)));
    assertThat(design.getValue("Point2.Y"), is(equalTo(4)));
  }

  // Dependency graph: B -> C -> A
  /** Chained but non-circular dependencies between cousins are invalid. */
  @Test
  public void chainedDependenciesAreInvalid() throws Throwable {
    ParamCfg first = pb()
      .id("A").interval("[2,2]").add()
      .id("B").inclMin("Second.C + 2").inclMax("Second.C + 2").add()
      .id("First").structured()
      .build();
    ParamCfg second = pb()
      .id("C").inclMin("First.A + 1").inclMax("First.A + 1").add()
      .id("D").interval("[1,1]").add()
      .id("Second").structured()
      .build();

    MappingCfg p1Mapping = mapping(first, Point.class);
    MappingCfg p2Mapping = mapping(second, Point.class);
    CodeMappingCfg mapping = CodeMappingCfg.getInstance(p1Mapping, p2Mapping);

    IDesign design = design(mapping, first, second);
    assertThat(design.getValue("First.A"), is(equalTo(2)));
    // Should expect
    // B = A + 1 + 2
    //   = 2 + 1 + 2 = 5
    assertThat(design.getValue("First.B"), is(equalTo(2)));
    assertThat(design.getValue("Second.C"), is(equalTo(3)));
    assertThat(design.getValue("Second.D"), is(equalTo(1)));
  }

  private IDesign getDesign(ParamCfg point) throws Throwable {
    return design(codeMapping(point, Point.class), point);
  }
}
