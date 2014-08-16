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
import net.finkn.inputspec.tools.*;
import net.finkn.inputspec.tools.types.AccessorTester;
import net.finkn.inputspec.tools.types.InitTester;

import org.junit.Test;

import se.miun.itm.input.model.design.IDesign;

/**
 * Examines how structured parameters are handled in InPUT4j v0.5.
 * Certain details are dealt with in more specialized test classes.
 *
 * @author Christoffer Fink
 * @see AccessorTest
 */
public class SparamTest {

  public static final ParamCfg DATA_PARAM = ParamCfg.builder()
    .id("Data")
    .build();

  public static final ParamCfg TESTER_PARAM = ParamCfg.builder()
    .id("Tester")
    .structured()
    .add(DATA_PARAM)
    .build();

  private final MappingCfg dataMapping = MappingCfg.builder()
    .param(TESTER_PARAM, DATA_PARAM)
    .set(InitTester.SETTER)
    .get(InitTester.GETTER)
    .build();

  private final MappingCfg testerMapping = MappingCfg.builder()
    .infer(TESTER_PARAM, AccessorTester.class)
    .build();

  private final CodeMappingCfg mapping = CodeMappingCfg
    .getInstance(testerMapping, dataMapping);


  private final String testerId = TESTER_PARAM.getId();
  // Absolute ID.
  private final String dataId = dataMapping.getId();

  /**
   * Fetching a structured parameter multiple times always returns
   * the same instance.
   */
  @Test
  public void gettingAnObjectReturnsSameInstance() throws Throwable {
    IDesign design = Helper.design(mapping, TESTER_PARAM);
    String id = TESTER_PARAM.getId();
    assertThat(design.getValue(id), is(sameInstance(design.getValue(id))));
  }

  /** Modifying an object does not change the corresponding parameter value. */
  @Test
  public void modifyingObjectDoesNotUpdateParameter() throws Throwable {
    IDesign design = Helper.design(mapping, TESTER_PARAM);

    AccessorTester tester = design.getValue(testerId);
    tester.setData(3);

    // This looks slightly worrying.
    tester = design.getValue(testerId);
    assertThat(design.getValue(dataId), is(not(equalTo(tester.getData()))));
  }
}
