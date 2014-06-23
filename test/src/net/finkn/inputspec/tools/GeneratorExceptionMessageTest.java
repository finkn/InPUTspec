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
package net.finkn.inputspec.tools;

import static net.finkn.inputspec.tools.Unit.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class GeneratorExceptionMessageTest {
  private final String msg = "Custom static message.";
  private final String toMsg = "Custom dynamic message.";
  private final Generator<Integer> gen = Generator.fromSeq(1, 1, 2).limit(2);

  @Test
  public void isConstantShouldUseCustomStaticMessage() {
    Runnable runnable = () -> gen.msg(msg).limit(3).isConstant();
    assertExceptionMessageMatches(runnable, msg);
  }

  @Test
  public void isConstantShouldUseCustomDynamicMessage() {
    Runnable runnable = () -> gen.toMsg(x -> toMsg).limit(3).isConstant();
    assertExceptionMessageMatches(runnable, toMsg);
  }

  @Test
  public void isVariableShouldUseCustomStaticMessage() {
    Runnable runnable = () -> gen.msg(msg).isVariable();
    assertExceptionMessageMatches(runnable, msg);
  }

  @Test
  public void isVariableShouldUseCustomDynamicMessage() {
    Runnable runnable = () -> gen.toMsg(x -> toMsg).isVariable();
    assertExceptionMessageMatches(runnable, toMsg);
  }

  @Test
  public void generatesAllShouldUseCustomStaticMessage() {
    Runnable runnable = () -> gen.msg(msg).generatesAll(2);
    assertExceptionMessageMatches(runnable, msg);
  }

  @Test
  public void generatesAllShouldUseCustomDynamicMessage() {
    Runnable runnable = () -> gen.toMsg(x -> toMsg).generatesAll(2);
    assertExceptionMessageMatches(runnable, toMsg);
  }

  @Test
  public void generatesAnyShouldUseCustomStaticMessage() {
    Runnable runnable = () -> gen.msg(msg).generatesAny(2);
    assertExceptionMessageMatches(runnable, msg);
  }

  @Test
  public void generatesAnyShouldUseCustomDynamicMessage() {
    Runnable runnable = () -> gen.toMsg(x -> toMsg).generatesAny(2);
    assertExceptionMessageMatches(runnable, toMsg);
  }

  @Test
  public void generatesOnlyShouldUseCustomStaticMessage() {
    Runnable runnable = () -> gen.msg(msg).generatesOnly(2);
    assertExceptionMessageMatches(runnable, msg);
  }

  @Test
  public void generatesOnlyShouldUseCustomDynamicMessage() {
    Runnable runnable = () -> gen.toMsg(x -> toMsg).generatesOnly(2);
    assertExceptionMessageMatches(runnable, toMsg);
  }

  @Test
  public void generatesAllShouldUseDefaultMessageWhenStaticSetToNull() {
    Runnable runnable = () -> gen.msg(msg).msg(null).generatesAll(2);
    String exception = getExceptionMessage(runnable).get();
    assertFalse(exception.matches(msg));
  }

  @Test
  public void generatesAnyShouldUseDefaultMessageWhenDynamicSetToNull() {
    Runnable runnable = () -> gen.msg(msg).toMsg(null).generatesAny(2);
    String exception = getExceptionMessage(runnable).get();
    assertFalse(exception.matches(msg));
  }
}
