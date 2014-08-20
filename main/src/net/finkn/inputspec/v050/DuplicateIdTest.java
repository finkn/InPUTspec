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
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Set;

import net.finkn.inputspec.tools.CodeMappingCfg;
import net.finkn.inputspec.tools.ParamCfg;
import net.finkn.inputspec.tools.types.Point;

import org.junit.Test;

import se.miun.itm.input.model.design.IDesign;
import se.miun.itm.input.model.design.IDesignSpace;

/**
 * Tests the effects of duplicate IDs in InPUT4j v0.5.
 * A "duplicate" ID may either literally be a duplicate or just ambiguous.
 * That is, it may be a relative or an absolute duplicate.
 * <p>
 * All the individual IDs in these tests are legal. The collisions are
 * also legal, with
 * {@link #absoluteOuterAfterAbsoluteInnerIsIllegal one exception}.
 *
 * @author Christoffer Fink
 */
public class DuplicateIdTest {

  // The outer and inner X-parameters can be identified by their values.
  private final int outer = 1;
  private final int inner = 2;

  // Multi-dimensional array.
  private final ParamCfg array1 = pb()
    .id("A")
    .type("integer[2][2]")
    .fixed(outer)
    .build();
  // Single-dimensional array with a legal name that will collide with the
  // first element (sub-array) of array1.
  private final ParamCfg array2 = pb()
    .id("A.1")
    .type("integer[2]")
    .fixed(inner)
    .build();

  // Plain numerics with the same ID but fixed to different values.
  private final ParamCfg x1 = pb().fixed(1).build();
  private final ParamCfg x2 = pb().fixed(3).build();

  // Intended to collide with the "X" in a "Point" parameter.
  // "X" matches the relative name of "Point.X" while "Point.X", quite
  // obviously, matches the absolute name.
  private final ParamCfg x = pb().id("X").fixed(outer).build();
  private final ParamCfg pointX = pb().id("Point.X").fixed(outer).build();

  private final ParamCfg point = pb()
    .id("X").fixed(inner).add()
    .id("Y").fixed(null).add() // Unfixed.
    .id("Point")
    .structured()
    .build();

  private final CodeMappingCfg mapping = codeMapping(point, Point.class);

  /**
   * When two parameters have exactly the same absolute (and relative)
   * IDs, then the parameter that occurs "last" takes precedence.
   */
  @Test
  public void firstIgnoredForOuterParameters() throws Throwable {
    IDesign design = design(x1, x2);
    assertThat(x1.getId(), is(equalTo(x2.getId())));
    assertThat(design.getSupportedParamIds().size(), is(equalTo(1)));
    assertThat(design.getValue(x1.getId()), is(equalTo(3)));
  }

  /**
   * When an array parameter with an ID that matches an element in another
   * array parameter occurs "laster" in the configuration, then its elements
   * are assigned IDs relative to a different parent ID.
   */
  @Test
  public void duplicateElementsAddNewIds() throws Throwable {
    // Expecting to derive these IDs from the first array:
    // X = { A, A.1, A.2, A.1.1, A.1.2, A.2.1, A.2.2 }. So |X| = 7.
    // Expecting to derive these IDs from the second array:
    // Y = { A.1, A.1.1, A.1.2 }. So |Y| = 3.
    // Then ∀y ∈ Y (y ∈ X), and we should expect |X ∪ Y| = 7.
    // Instead, it looks like the elements of the "A.1" parameter are
    // relative to a parent ID of "A.A.1". So we get |X ∪ Y| = 9.

    IDesign design = design(array1, array2);
    Set<String> ids = design.getSupportedParamIds();
    // Surprising number of IDs.
    assertThat(ids.size(), is(equalTo(9)));
    // Surprising ID.
    assertThat(ids, hasItem("A.A.1.1"));
    assertThat(design.getValue("A.1.1"), is(equalTo(outer)));
  }

  /**
   * When an array parameter with an ID that matches an element in another
   * array parameter occurs "first" in the configuration, then it is ignored.
   */
  @Test
  public void duplicateElementsAreIgnored() throws Throwable {
    // Adding the "A.1" parameter first produces less surprising results.
    IDesign design = design(array2, array1);
    Set<String> ids = design.getSupportedParamIds();
    assertThat(ids.size(), is(equalTo(7)));
    assertThat(ids, not(hasItem("A.A.1.1")));
    assertThat(design.getValue("A.1.1"), is(equalTo(outer)));
  }

  /**
   * When an outer parameter has an absolute ID that matches the absolute ID
   * of an inner parameter, and the outer parameter occurs "first", then the
   * latter parameter takes precedence. Note that, unlike the
   * {@link #lastIgnoredForAbsoluteOuterAndRelativeInner} test, this is not
   * a matter of first or last taking precedence, as a general rule. Rather,
   * when the outer parameter occurs "before" the inner, then, in that
   * particular case, "last" takes precedence. The other case fails.
   * In other words, this is a test of which order is legal.
   * @see #absoluteOuterAfterAbsoluteInnerIsIllegal
   * @see #absoluteOuterAfterAbsoluteInnerIsIllegal2
   */
  @Test
  public void firstIgnoredForAbsoluteOuterAndInner() throws Throwable {
    IDesign design = design(mapping, pointX, point);
    // One parameter is "missing".
    assertThat(design.getSupportedParamIds().size(), is(equalTo(3)));
    // The nested parameter, which was added last, takes precedence.
    assertThat(design.getValue(pointX.getId()), is(equalTo(inner)));
  }

  /**
   * An absolute outer ID that clashes with an absolute inner ID is
   * illegal if occurring "later" in the configuration. This test has the
   * virtue of being concise. It has the flaw that it is obscure.
   * @see #absoluteOuterAfterAbsoluteInnerIsIllegal2
   */
  @Test(expected = NullPointerException.class)
  public void absoluteOuterAfterAbsoluteInnerIsIllegal() throws Throwable {
    design(mapping, point, pointX);
  }

  /**
   * An absolute outer ID that clashes with an absolute inner ID is
   * illegal if occurring "later" in the configuration. This test demonstrates
   * exactly the same result as
   * {@link #absoluteOuterAfterAbsoluteInnerIsIllegal}, but with more detail.
   */
  @Test
  public void absoluteOuterAfterAbsoluteInnerIsIllegal2() throws Throwable {
    IDesignSpace space = space(mapping, point, pointX);

    assertThat(space.getSupportedParamIds().size(), is(equalTo(3)));
    // The outer parameter, which was added last, takes precedence.
    // Hence there is no inner "Point.X" to use for initialization.
    assertThat(space.next(pointX.getId()), is(equalTo(outer)));
    try {
      space.nextDesign("Design");
      fail("Creating design was expected to fail!");
    } catch (NullPointerException e) {
    }
  }

  /**
   * When an outer parameter has an absolute ID that matches the relative ID
   * of an inner parameter, then the parameter that occurs "first" takes
   * precedence.
   */
  @Test
  public void lastIgnoredForAbsoluteOuterAndRelativeInner() throws Throwable {
    innerMatches(x, point, outer); // X first, then Point.
    innerMatches(point, x, inner); // Point first, then X.
  }

  private void innerMatches(ParamCfg a, ParamCfg b, int val) throws Throwable {
    IDesign design = design(mapping, a, b);
    // These are always the same.
    assertThat(design.getSupportedParamIds().size(), is(equalTo(4)));
    assertThat(design.getValue("X"), is(equalTo(outer)));
    // The value of the inner parameter varies.
    assertThat(design.getValue("Point.X"), is(equalTo(val)));
  }
}
