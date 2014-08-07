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
import net.finkn.inputspec.tools.ParamCfg;

import org.junit.Test;

import se.miun.itm.input.model.design.IDesign;

/**
 * Tests which values a design accepts for a parameter, depending on how that
 * parameter was defined.
 * These tests involve dependencies on other parameters.
 *
 * @author Christoffer Fink
 */
public class AdvancedSetValueTest {

  private final ParamCfg dependee = ParamCfg.builder()
    .id("A")
    .inclMin("1")
    .inclMax("3")
    .build();

  /**
   * When defined relative to another parameter, legal values depend on the
   * <strong>current</strong> value of the referenced parameter.
   */
  @Test
  public void valuesAreLegalRelativeToCurrentValueOfReferencedParam() throws Throwable {
    ParamCfg dependent = pb()
      .inclMin("A + 2")
      .exclMax("A + 4") // [A+2,A+4[ = { A+2, A+3 }
      .build();

    IDesign design = design(dependent, dependee);
    int a = design.getValue("A");

    // Set A to a different value.
    a = a == 1 ? 2 : 1;
    design.setValue("A", a);

    sinkTest(design, dependent.getId())
      .accepts(a + 2, a + 3)
      .rejects(a, a + 4)
      .run();
  }
}
