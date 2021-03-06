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

import java.util.Set;

import net.finkn.inputspec.tools.DesignSpaceCfg;
import net.finkn.inputspec.tools.ParamCfg;

import org.junit.Before;
import org.junit.Test;

import se.miun.itm.input.model.design.IDesignSpace;

/**
 * These tests establish some general rules about how design spaces behave in
 * InPUT. Note that these are very basic tests. Corner cases are dealt with
 * elsewhere.
 *
 * @see net.finkn.inputspec.v050.SupportedParamIdsTest
 * @author Christoffer Fink
 */
public class BasicDesignSpaceTest {

  private final ParamCfg param = ParamCfg.getDefault();
  private final DesignSpaceCfg spaceCfg = DesignSpaceCfg.getInstance(param);
  private IDesignSpace space;

  @Before
  public void setup() throws Throwable {
    this.space = spaceCfg.getDesignSpace();
  }

  /**
   * This test shows that {@code getSupportedParamIds()} contains the IDs of
   * parameters defined in the design space, and nothing else.
   * @see net.finkn.inputspec.v050.SupportedParamIdsTest
   */
  @Test
  public void supportedParamIdsContainsIdOfTheParameter() throws Throwable {
    Set<String> ids = space.getSupportedParamIds();
    assertThat(1, is(equalTo(ids.size())));
    assertThat(ids, hasItem(param.getId()));
  }

  /**
   * This test shows that {@code next()} will return some non-null value when
   * called with a supported parameter ID.
   */
  @Test
  public void nextProducesSomeValueWhenUsingSupportedId() throws Throwable {
    assertThat(space.next(param.getId()), is(not(nullValue())));
  }

  /**
   * This test shows that {@code next()} will return {@code null} when called
   * with an unsupported parameter ID.
   */
  @Test
  public void nextProducesNullValueWhenUsingUnsupportedId() throws Throwable {
    String id = "Nonexistent";
    // Make sure this ID is not supported.
    assertThat(space.getSupportedParamIds(), not(hasItem(id)));
    assertThat(space.next(id), is(nullValue()));
  }
}
