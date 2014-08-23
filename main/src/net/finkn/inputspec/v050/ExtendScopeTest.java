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

import static net.finkn.inputspec.tools.Helper.design;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import net.finkn.inputspec.tools.ParamCfg;

import org.junit.Before;
import org.junit.Test;

import se.miun.itm.input.model.design.IDesign;

/**
 * Examines the effect of extending a design in InPUT4j v0.5.
 * Note that extending a design does not add the supported parameter IDs
 * of the extending design to the extended design.
 *
 * @author Christoffer Fink
 */
public class ExtendScopeTest {
  private final ParamCfg param1 = ParamCfg.builder().id("X1").build();
  private final ParamCfg param2 = ParamCfg.builder().id("X2").build();
  private final String id1 = param1.getId();
  private final String id2 = param2.getId();
  private IDesign design1;
  private IDesign design2;

  @Before
  public void setup() throws Throwable {
    design1 = design(param1);
    design2 = design(param2);
    // Make sure the wrong parameters are unsupported prior to extension.
    assertThat(design1.getValue(id2), is(nullValue()));
    assertThat(design2.getValue(id1), is(nullValue()));
    design1.extendScope(design2);
  }

  /** A design can access the parameters of the extending design. */
  @Test
  public void canAccessParameterOfExtendingDesign() throws Throwable {
    assertThat(design1.getValue(id2), is(not(nullValue())));
  }

  /** Extending a design does not alter its set of supported IDs. */
  @Test
  public void extendScopeDoesNotUpdateSupportedIds() throws Throwable {
    // TODO: Prime candidate for a custom matcher.
    assertThat(design1.getSupportedParamIds(), not(hasItem(id2)));
  }

  /** Changes to a parameter are reflected in both designs. */
  @Test
  public void settingValueUpdatesBothDesigns() throws Throwable {
    int old = design2.getValue(id2);
    int value = old + 1;
    design1.setValue(id2, value);
    assertThat(design1.getValue(id2), is(equalTo(value)));
    assertThat(design2.getValue(id2), is(equalTo(value)));
  }
}
