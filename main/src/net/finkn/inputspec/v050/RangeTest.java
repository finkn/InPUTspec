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

// TODO: Split these tests up into multiple classes.
public class RangeTest extends RangeTestHelper {

  public RangeTest(TestCase test) throws Throwable {
    super(test);
  }

  @Parameters
  public static Collection<TestCase[]> tests() throws InPUTException {
    return Arrays.asList(new TestCase[][] {
      // Examining the default range. An integer parameter should allow values
      // in [Integer.MIN_VALUE, Integer.MAX_VALUE].
      {
        t(pb())
          .accepts(Integer.MIN_VALUE, Integer.MAX_VALUE)
      },
      // Basic tests that verify the meaning of inclusive vs exclusive.
      {
        t(pb().inclMin("1").inclMax("3"))
          .accepts(1,2,3).rejects(0,4),
      },
      {
        t(pb().exclMin("1").exclMax("3"))
          .accepts(2).rejects(0,1,3,4),
      },

      // Float limits are truncated.
      {
        t(pb().exclMin("1.5").inclMax("3.5")) // Same as ]1,3].
          .accepts(2,3).rejects(1,4),
      },

      // Multi-range.
      // Only values in the intersection are accepted.
      {
        t(pb().inclMin("1,3").inclMax("4,6"))
          .accepts(3,4)               // [1,4] ∩ [3,6] = [3,4]
          .rejects(0,1,2,5,6,7),      // V \ [3,4]
      },
      {
        t(pb().inclMin("1,5").inclMax("2,6"))
          .rejects(0,1,2,3,4,5,6,7),  // V
      },
      // 1234
      // --
      //  --
      //   --
      {
        t(pb().inclMin("1,2,3").inclMax("2,3,4"))
          .rejects(0,1,2,3,4,5),      // V
      },
      // 12345
      // ---
      //  ---
      //   ---
      {
        t(pb().inclMin("1,2,3").inclMax("3,4,5"))
          .accepts(3)
          .rejects(0,1,2,4,5,6),      // V \ {3}
      },

      // Same goes for double. Only the intersection is legal.
      {
        t(pb().type("double")
            .inclMin("1,2,3")
            .inclMax("3,4,5"))
          .accepts(3.0)
          .rejects(0.0,1.0,2.0,4.0,5.0,6.0),
      },

      // Design behaves like DesignSpace. That is, only the first range counts.
      // The second range is illegal. Also, the limits are not exclusive!
      {
        t(pb().type("double")
            .exclMin("0.1,0.8")
            .exclMax("0.4,0.9"))
          .accepts(0.1,0.2,0.3,0.4)   // 0.1 and 0.4 do not belong here!
          .rejects(0.8,0.7,0.9),      // 0.7 does not belong here!
      },

      // Shows that floating point limits are never exclusive.
      {
        t(pb().type("double")
            .exclMin("0.1")
            .exclMax("0.3"))
          .accepts(0.1,0.2,0.3)
          .rejects(0.09,0.31),
      },
      {
        t(pb().type("double")
            .exclMin("0.1")
            .inclMax("0.3"))
          .accepts(0.1,0.2,0.3)
          .rejects(0.09,0.31),
      },
      {
        t(pb().type("double")
            .inclMin("0.1")
            .exclMax("0.3"))
          .accepts(0.1,0.2,0.3)
          .rejects(0.09,0.31),
      },

      /*
      // Mixed limits. Throws ArrayIndexOutOfBounds.
      {
        t(pb().type("double")
            .inclMin("0.1,0.2,0.3")
            .exclMax("0.3,0.4,0.5")),
      },
      //*/

      /*
      // Simple expression. (ILLEGAL)
      {
        t(pb().inclMin("1 + 2"))
          .accepts(3).rejects(2),
      },
      // Simple math library expressions. (ILLEGAL)
      {
        t(pb().inclMin("Math.PI"))
          .accepts(3).rejects(2),
      },
      {
        t(pb().inclMin("Math.sqrt(2)"))
          .accepts(1).rejects(0),
      },
      //*/
    });
  }
}
