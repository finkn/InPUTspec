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

import net.finkn.inputspec.tools.*;

import org.junit.Before;
import org.junit.Test;

import se.miun.itm.input.model.InPUTException;
import se.miun.itm.input.model.design.IDesign;

/**
 * These tests establish some general rules about how designs behave in InPUT.
 * Note that these are very basic tests. Corner cases are dealt with elsewhere.
 * 
 * @see net.finkn.inputspec.v050.SupportedParamIdsTest
 * @author Christoffer Fink
 */
public class BasicDesignTest {

  private final String badId = "Nonexistent";
  private final ParamCfg param = ParamCfg.builder().build();
  private final String id = param.getId();
  private IDesign design;

  @Before
  public void setup() throws Throwable {
    this.design = Helper.design(param);
    assertThat(badId, is(not(equalTo(id))));
  }

  /**
   * This test shows that {@code getSupportedParamIds()} contains the IDs of
   * parameters defined in the design space, and nothing else.
   * @see net.finkn.inputspec.v050.SupportedParamIdsTest
   */
  @Test
  public void supportedParamIdsContainsIdOfTheParameter() throws Throwable {
    Set<String> ids = design.getSupportedParamIds();
    assertThat(1, is(equalTo(ids.size())));
    assertThat(ids, hasItem(id));
  }

  /**
   * This test shows that {@code getValue()} will return some non-null value
   * when called with a supported parameter ID.
   */
  @Test
  public void getValueReturnsSomeValueForSupportedId() throws Throwable {
    assertThat(design.getValue(id), is(not(nullValue())));
  }

  /**
   * This test shows that {@code getValue()} will return {@code null} when
   * called with an unsupported parameter ID.
   */
  @Test
  public void getValueReturnsNullForUnsupportedId() throws Throwable {
    assertThat(design.getValue(badId), is(nullValue()));
  }

  /**
   * This test demonstrates that {@code setValue()} changes the value that is
   * associated with that particular (supported) parameter ID.
   */
  @Test
  public void setValueChangesTheValueOfTheSupportedId() throws Throwable {
    Integer originalValue = design.getValue(id);
    design.setValue(id, originalValue + 1);
    Integer currentValue = design.getValue(id);
    assertThat(currentValue, is(not(equalTo(originalValue))));
  }

  /**
   * This test demonstrates that {@code setValue()} throws an InPUTException
   * when trying to set an unsupported parameter.
   */
  @Test(expected = InPUTException.class)
  public void setValueThrowsAnExceptionWhenIdIsUnsupported() throws Throwable {
    design.setValue(badId, 3);
  }
}
