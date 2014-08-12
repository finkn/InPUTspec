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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.finkn.inputspec.tools.*;

import org.junit.Before;
import org.junit.Test;

import se.miun.itm.input.InPUTConfig;
import se.miun.itm.input.model.InPUTException;
import se.miun.itm.input.model.design.IDesignSpace;
import se.miun.itm.input.util.Q;

/**
 * Demonstrates designs are not cached in InPUT4j v0.5.
 * InPUTConfig recognizes a "cacheDesigns" parameter. However, the effect
 * of enabling this feature is not literally caching. A more apt parameter
 * name might be "saveDesigns". In any case, this form of caching is not
 * like the caching that is always enabled for design spaces and code
 * mappings, where configurations are cached by ID and reused even when
 * the configurations are different. Such behavior would of course be
 * insensible for designs.
 *
 * @author Christoffer Fink
 * @see CodeMappingCachingTest
 * @see DesignSpaceCachingTest
 */
public class DesignCachingTest {

  private final ParamCfg constantParam = ParamCfg.builder().fixed(3).build();
  private final ParamCfg variableParam = ParamCfg.getDefault();
  private final String designId = "Design";

  @Before
  public void setup() throws Throwable {
    InPUTConfig.setValue(Q.CACHE_DESIGNS, true);
    assertThat(InPUTConfig.getValue(Q.CACHE_DESIGNS), is(true));
  }

  @Test(expected = AssertionError.class)
  public void fixedParameterShouldLookLikeCaching() throws Throwable {
    test(constantParam);
  }

  @Test
  public void designsAreNotCachedById() throws Throwable {
    test(variableParam);
  }

  private void test(ParamCfg param) throws Throwable {
    IDesignSpace space = DesignSpaceCfg.getInstance(param).getDesignSpace();

    Generator.fromSupplier(() -> {
        try {
          return space.nextDesign(designId).getValue(param.getId());
        } catch (InPUTException e) {
          return null;
        }
      })
      .isVariable();
  }
}
