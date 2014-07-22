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

import java.util.Arrays;
import java.util.Collection;

import net.finkn.inputspec.tools.RangeTestHelper;

import org.junit.runners.Parameterized.Parameters;

import se.miun.itm.input.model.InPUTException;

// TODO: Do not extend RangeTestHelper?
// Only exactly matching types are accepted.
public class TypeMismatchTest extends RangeTestHelper {

  public TypeMismatchTest(TestCase test) throws Throwable {
    super(test);
  }

  @Parameters
  public static Collection<TestCase[]> tests() throws InPUTException {
    return Arrays.asList(new TestCase[][] {
      { t(pb()).accepts(1), },
      { t(pb()).accepts((int) 1.0), },
      { t(pb()).accepts(Integer.valueOf(1)), },
      { t(pb()).accepts((Number) 1), },
      { t(pb()).rejects((short) 1), },
      { t(pb()).rejects(1L), },
      { t(pb()).rejects(1.0), },
      { t(pb()).rejects("1"), },

      { t(pb().type("double")).accepts(1.0), },
      { t(pb().type("double")).accepts((double) 1), },
      { t(pb().type("double")).rejects(1), },
      { t(pb().type("double")).rejects((float) 1.0), },
      { t(pb().type("double")).rejects("1.0"), },

      { t(pb().type("boolean")).accepts(true), },
      { t(pb().type("boolean")).accepts(false), },
      { t(pb().type("boolean")).accepts(Boolean.TRUE), },
      { t(pb().type("boolean")).rejects("true"), },
      { t(pb().type("boolean")).rejects(Boolean.TRUE.toString()), },
    });
  }
}
