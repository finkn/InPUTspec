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

import net.finkn.inputspec.tools.DesignSpaceCfg;
import net.finkn.inputspec.tools.Generator;
import net.finkn.inputspec.tools.ParamCfg;

import org.junit.After;
import org.junit.Test;

import se.miun.itm.input.model.mapping.Mappings;
import se.miun.itm.input.model.param.ParamStore;

/**
 * These tests demonstrate how InPUT handles various boolean literals.
 * 
 * @author Christoffer Fink
 * @version 1.0
 */
public class BooleanLiteralsTest {
  // This counter is used to make sure that each DesignSpace that is created
  // gets a unique ID. This is required to prevent design spaces with the
  // same ID from being cached.
  private static long counter = 0;

  @After
  public void tearDown() {
    ParamStore.releaseAllParamStores();
    Mappings.releaseAllMappings();
  }

  /**
   * This test acts like a lemma. By using literals that are clearly invalid, it
   * demonstrates that invalid literals are ignored. Hence boolean parameters
   * that are fixed to such a literal aren't actually fixed. Now that this
   * behavior has been established, the other tests can check for this behavior
   * to find out which literals are valid.
   */
  @Test
  public void invalidBooleanLiteralsAreIgnored() throws Exception {
    assertBoolParamIsVariableWhenFixedTo("hello");
    assertBoolParamIsVariableWhenFixedTo("world");
    assertBoolParamIsVariableWhenFixedTo("Invalid boolean literal.");
  }

  /**
   * This test demonstrates that 'true' and 'false' are valid boolean literals
   * with the expected meanings.
   * 
   * @see #booleanLiteralsAreCaseSensitive()
   */
  @Test
  public void lowerCaseTrueAndFalseAreValidLiterals() throws Exception {
    assertAllValid("true", "false");
    // This is a stronger test. Not only are the values constant, but they
    // are specifically true and false, as expected.
    assertBoolParamIsTrueWhenFixedTo("true");
    assertBoolParamIsFalseWhenFixedTo("false");
  }

  /**
   * This test demonstrates that boolean literals are case sensitive. In
   * particular, while 'true' is valid, 'True' is not.
   * 
   * @see #booleanLiteralsAreCaseSensitive()
   */
  @Test
  public void booleanLiteralsAreCaseSensitive() throws Exception {
    assertAllInvalid("True", "TRUE", "trUE");
    assertAllInvalid("False", "FALSE", "falSE");
  }

  /**
   * This test demonstrates that numbers - including negative and positive
   * integer and floating point numbers - are valid {@code false} literals. Note
   * that '0' and '1' are equivalent. However, the floating point numbers cannot
   * start with a period. So the notation is restricted.
   * 
   * @see #floatingPointsBeginningWithAPeriodAreInvalidLiterals()
   */
  @Test
  public void numbersAreValidFalseLiterals() throws Exception {
    assertBoolParamsAreFalseWhenFixedTo("0", "1", "-1", "0.5", "-0.5", "0.123",
        "-0.123", "12345", "-12345");
  }

  /**
   * While '0.5' is a valid boolean literal, '.5' is not.
   * 
   * @see #numbersAreValidFalseLiterals()
   */
  @Test
  public void floatingPointsBeginningWithAPeriodAreInvalidLiterals()
      throws Exception {
    assertAllInvalid(".5", ".123");
  }

  /**
   * This test shows that some other plausible literals are invalid. These
   * literals are 'yes', 'no', 'on', 'off'.
   */
  @Test
  public void yesNoOnOffAreInvalidLiterals() throws Exception {
    assertAllInvalid("yes", "no", "on", "off");
  }

  /**
   * This test shows that the empty string ('') is an invalid literal.
   */
  @Test
  public void theEmptyStringIsAnInvalidLiteral() throws Exception {
    assertAllInvalid("");
  }

  private void assertBoolParamsAreFalseWhenFixedTo(String... literals)
      throws Exception {
    for (String literal : literals) {
      assertBoolParamIsFalseWhenFixedTo(literal);
    }
  }

  private void assertAllValid(String... literals) throws Exception {
    for (String literal : literals) {
      assertBoolParamIsConstantWhenFixedTo(literal);
    }
  }

  private void assertAllInvalid(String... literals) throws Exception {
    for (String literal : literals) {
      assertBoolParamIsVariableWhenFixedTo(literal);
    }
  }

  private void assertBoolParamIsTrueWhenFixedTo(String literal)
      throws Exception {
    String msg = literal + " not fixed to true.";
    getGenerator(literal).msg(msg).limit(50).generatesOnly(true);
  }

  private void assertBoolParamIsFalseWhenFixedTo(String literal)
      throws Exception {
    String msg = literal + " not fixed to false.";
    getGenerator(literal).msg(msg).limit(50).generatesOnly(false);
  }

  private void assertBoolParamIsVariableWhenFixedTo(String literal)
      throws Exception {
    String msg = literal + " unexpectedly valid.";
    getGenerator(literal).msg(msg).limit(500).isVariable();
  }

  private void assertBoolParamIsConstantWhenFixedTo(String literal)
      throws Exception {
    String msg = literal + " unexpectedly invalid.";
    getGenerator(literal).msg(msg).limit(500).isConstant();
  }

  private Generator<Object> getGenerator(String fixed) throws Exception {
    ParamCfg param = ParamCfg.builder()
      .id("X").type("boolean").fixed(fixed).build();
    String id = "DesignSpace" + counter;
    counter++;
    DesignSpaceCfg space = DesignSpaceCfg.builder().id(id).param(param).build();
    return Generator.fromDesignSpace(space.getDesignSpace(), "X");
  }
}
