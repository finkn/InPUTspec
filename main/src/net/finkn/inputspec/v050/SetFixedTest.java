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

import net.finkn.inputspec.tools.ParamCfg;
import net.finkn.inputspec.tools.DesignSpaceCfg;

import org.junit.Before;
import org.junit.Test;

import se.miun.itm.input.model.design.IDesignSpace;

/**
 * Tests the behavior of {@code setFixed} of design space in InPUT4j 0.5.
 *
 * @author Christoffer Fink
 */
public class SetFixedTest {

  private final ParamCfg param = ParamCfg.builder()
    .inclMin(1).inclMax(3).build();
  private final String id = param.getId();

  private IDesignSpace space;

  @Before
  public void setup() throws Throwable {
    this.space = DesignSpaceCfg.getInstance(param).getDesignSpace();
  }

  // TODO: link to a test showing how this affects design creation.
  /** Fixing a parameter to an out-of-range value is legal. */
  @Test
  public void parametersCanBeFixedToOutOfRangeValues() throws Throwable {
    space.setFixed(id, "5"); // 5 ∉ [1,3]
    assertThat(space.next(id), is(equalTo(5)));
  }

  /** Float values are truncated when fixing an integer parameter. */
  @Test
  public void floatValuesAreTruncatedForIntegerParameter() throws Throwable {
    space.setFixed(id, "1.5");
    assertThat(space.next(id), is(equalTo(1)));
  }

  /**
   * Parameters are fixed only in the sense of specifying the values that should
   * be generated, not in the sense that they are immutable.
   * Parameters can thus be fixed multiple times, and even to new values.
   */
  @Test
  public void parametersCanBeFixedMultipleTimes() throws Throwable {
    space.setFixed(id, "1");
    space.setFixed(id, "1");
    space.setFixed(id, "2");
    assertThat(space.next(id), is(equalTo(2)));
  }
}
