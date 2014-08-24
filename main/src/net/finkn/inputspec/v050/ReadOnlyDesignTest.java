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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import net.finkn.inputspec.tools.Helper;
import net.finkn.inputspec.tools.ParamCfg;

import org.junit.Before;
import org.junit.Test;

import se.miun.itm.input.model.InPUTException;
import se.miun.itm.input.model.design.IDesign;

/**
 * Examines what it means for a design to be read-only in InPUT4j v0.5.
 * In particular, these tests demonstrate how {@code setReadOnly} and
 * {@code extendScope} interact.
 *
 * @see ExtendScopeTest
 * @author Christoffer Fink
 */
public class ReadOnlyDesignTest {

  private final ParamCfg param1 = ParamCfg.builder().id("X1").build();
  private final ParamCfg param2 = ParamCfg.builder().id("X2").build();
  private final String id1 = param1.getId();
  private final String id2 = param2.getId();
  private IDesign design1;
  private IDesign design2;

  @Before
  public void setup() throws Throwable {
    design1 = Helper.design(param1);
    design2 = Helper.design(param2);
  }

  /** Calling {@code setValue} after {@code setReadOnly} is illegal. */
  @Test(expected = InPUTException.class)
  public void setValueOnReadOnlyDesignIsIllegal() throws Throwable {
    design1.setReadOnly();
    design1.setValue(id1, 3);
  }

  /** Extending a read-only design is legal. */
  @Test
  public void extendScopeOnReadOnlyDesignIsLegal() throws Throwable {
    design1.setReadOnly();
    design1.extendScope(design2);
  }

  /**
   * In a very real sense, extending a design violates its read-only status.
   * If a call to {@code getValue} returns different values at different
   * points in time, then surely the design must be considered to have been
   * modified?
   */
  @Test
  public void extendingReadOnlyModifiesDesign() throws Throwable {
    design1.setReadOnly();
    Integer old = design1.getValue(id2);
    design1.extendScope(design2);
    Integer current = design1.getValue(id2);
    assertThat(current, is(not(equalTo(old))));
  }

  /**
   * An extending design acts as a back-door, allowing parameters that are
   * part of the extended design (due to the extension) to be modified.
   */
  @Test
  public void extendedReadOnlyCanBeModified() throws Throwable {
    design1.extendScope(design2);
    design1.setReadOnly();
    Integer old = design1.getValue(id2);
    design2.setValue(id2, old + 1);
    Integer current = design1.getValue(id2);
    assertThat(current, is(not(equalTo(old))));
  }

  /**
   * An extended design acts as a back-door, allowing parameters in the
   * read-only extending design to be modified.
   */
  @Test
  public void extendingReadOnlyCanBeModified() throws Throwable {
    design1.extendScope(design2);
    design2.setReadOnly();
    Integer old = design2.getValue(id2);
    design1.setValue(id2, old + 1);
    Integer current = design2.getValue(id2);
    assertThat(current, is(not(equalTo(old))));
  }
}
