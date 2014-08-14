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
import static org.junit.Assert.fail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.finkn.inputspec.tools.*;
import net.finkn.inputspec.tools.types.Empty;

import org.junit.Test;

import se.miun.itm.input.model.InPUTException;
import se.miun.itm.input.model.design.IDesign;
import se.miun.itm.input.util.Q;

/**
 * Tests how InPUT4j infers setter names based on parameter name.
 * This test shows that InPUT4j doesn't assume Java naming conventions when
 * inferring setter names from parameter names. For example, it doesn't use
 * camel-case.
 * <p>
 * Note that only setters are examined. Since getters are never invoked, that
 * behavior is inaccessible.
 * <p>
 * Note that this test depends on the format of InPUT4j exception messages.
 *
 * @author Christoffer Fink
 */
public class AccessorNamingTest {

  private final String regex = ".*: There is no setter method by name '(.*)'.*";
  private final Pattern pattern = Pattern.compile(regex);

  private final String parentId = "Outer";

  private static String expectedSetterFor(String id) {
    return Q.SETTER_PREFIX + id;
  }

  private final String[] ids = {
    "inner", "Inner", "INNER", "InNeR", "iNnEr",
  };

  /**
   * Inferred setter name is a simple concatenation of a prefix and the
   * parameter name.
   */
  @Test
  public void inferredSetterNameIsSimpleConcatenation() throws Throwable {
    for (String id : ids) {
      assertThat(inferredSetterFor(id), is(equalTo(expectedSetterFor(id))));
    }
  }

  private String inferredSetterFor(String id) throws Throwable {
    IDesign design = getDesign(id);
    try {
      design.getValue(parentId);
      fail("Test was expecting an exception!");
    } catch (InPUTException e) {
      return extractNameFrom(e.getMessage());
    }
    return null; // Look what you made me do. Happy now?
  }

  private IDesign getDesign(String id) throws Throwable {
    ParamCfg innerParam = ParamCfg.builder().id(id).build();
    ParamCfg outerParam = ParamCfg.builder()
      .id(parentId)
      .structured()
      .add(innerParam)
      .build();

    MappingCfg outerMapping = MappingCfg.builder()
      .infer(outerParam, Empty.class)
      .build();
    MappingCfg innerMapping = MappingCfg.builder()
      .param(outerParam, innerParam)
      .build();

    CodeMappingCfg codeMapping = CodeMappingCfg
      .getInstance(outerMapping, innerMapping);

    return Helper.design(codeMapping, outerParam, innerParam);
  }

  private String extractNameFrom(String message) {
    Matcher matcher = pattern.matcher(message);
    // Note that this check has the side-effect of forcing matching.
    // Without it, the extraction will fail.
    if (!matcher.matches()) {
      fail("Wrong regex pattern!");
    }
    return matcher.group(1);
  }
}
