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

import static net.finkn.inputspec.tools.Helper.pb;
import static net.finkn.inputspec.tools.Helper.sinkTest;

import java.util.Arrays;
import java.util.Collection;

import net.finkn.inputspec.tools.SinkTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import se.miun.itm.input.model.InPUTException;

/**
 * Shows that only exact type matches are accepted in v0.5.
 * That is, for an integer parameter, neither short nor long values are
 * legal. Similarly, a double parameter cannot be set to either a float
 * nor any integer value.
 * <p>
 * Note that only numeric parameters are currently being tested.
 *
 * @author Christoffer Fink
 */
@RunWith(value = Parameterized.class)
public class TypeMismatchTest {

  private final SinkTestCase test;

  public TypeMismatchTest(SinkTestCase test) throws Throwable {
    this.test = test;
  }

  @Test
  public void test() {
    test.run();
  }

  @Parameters
  public static Collection<SinkTestCase[]> tests() throws InPUTException {
    return Arrays.asList(new SinkTestCase[][] {
      { sinkTest(pb()).accepts(1), },
      { sinkTest(pb()).accepts((int) 1.0), },
      { sinkTest(pb()).accepts(Integer.valueOf(1)), },
      { sinkTest(pb()).accepts((Number) 1), },
      { sinkTest(pb()).rejects((short) 1), },
      { sinkTest(pb()).rejects(1L), },
      { sinkTest(pb()).rejects(1.0), },
      { sinkTest(pb()).rejects("1"), },

      { sinkTest(pb().type("double")).accepts(1.0), },
      { sinkTest(pb().type("double")).accepts((double) 1), },
      { sinkTest(pb().type("double")).rejects(1), },
      { sinkTest(pb().type("double")).rejects((float) 1.0), },
      { sinkTest(pb().type("double")).rejects("1.0"), },

      { sinkTest(pb().type("boolean")).accepts(true), },
      { sinkTest(pb().type("boolean")).accepts(false), },
      { sinkTest(pb().type("boolean")).accepts(Boolean.TRUE), },
      { sinkTest(pb().type("boolean")).rejects("true"), },
      { sinkTest(pb().type("boolean")).rejects(Boolean.TRUE.toString()), },
    });
  }
}
