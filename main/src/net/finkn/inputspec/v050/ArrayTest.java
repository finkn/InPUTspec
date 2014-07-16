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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.finkn.inputspec.tools.DesignSpaceCfg;
import net.finkn.inputspec.tools.ParamCfg;

import org.junit.Before;
import org.junit.Test;

import se.miun.itm.input.model.design.IDesign;
import se.miun.itm.input.model.design.IDesignSpace;

/**
 * These tests demonstrate how InPUT handles certain aspects of arrays.
 *
 * @author Christoffer Fink
 */
public class ArrayTest {
  private final ParamCfg arrayParam = ParamCfg.builder()
    .type("integer[3]").inclMin("0").build();
  private final DesignSpaceCfg arraySpace = DesignSpaceCfg.builder()
    .param(arrayParam).build();
  private IDesignSpace space;
  private IDesign design;

  @Before
  public void setup() throws Throwable {
    space = arraySpace.getDesignSpace();
    design = space.nextDesign("Design");
  }

  /**
   * Values of individual array elements ("X.1") match the elements of the
   * array ("X").
   */
  @Test
  public void arrayElementsMatchValuesAtElementIds() throws Throwable {
    int[] x = design.getValue("X");
    for (int i = 0; i < x.length; i++) {
      String elementId = "X." + (i+1);
      int element = design.getValue(elementId);
      assertThat(x[i], is(equalTo(element)));
    }
  }

  /**
   * When setting the value of an array element ("X.1"), that change is
   * reflected in the array ("X").
   */
  @Test
  public void settingAnArrayElementUpdatesTheArray() throws Throwable {
    int[] x = design.getValue("X");
    int element = x[0] + 1;
    design.setValue("X.1", element);
    x = design.getValue("X");

    assertThat(x[0], is(equalTo(element)));
  }

  /**
   * When setting the value of an array ("X"), getting an element ("X.1")
   * produces an updated value.
   */
  @Test
  public void settingAnArrayUpdatesTheElements() throws Throwable {
    int oldX1 = design.getValue("X.1");
    int[] array = { oldX1 + 1, 2, 3, };
    design.setValue("X", array);
    int newX1 = design.getValue("X.1");

    assertThat(newX1, is(equalTo(array[0])));
  }

  /**
   * The range limitation applies when setting individual array elements.
   */
  @Test(expected = IllegalArgumentException.class)
  public void settingElementToOutOfRangeValueIsIllegal() throws Throwable {
    int value = -1;
    design.setValue("X.1", value);
  }

  /**
   * The range limitation applies when setting the whole array.
   */
  @Test(expected = IllegalArgumentException.class)
  public void settingArrayToOutOfRangeValuesIsIllegal() throws Throwable {
    int[] array = { -1, -2, -3, };
    design.setValue("X", array);
  }
}
