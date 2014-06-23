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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class IntervalParsingTest {
  private final Double inclMin;
  private final Double exclMin;
  private final Double inclMax;
  private final Double exclMax;
  private final Interval interval;

  public IntervalParsingTest(String interval, String imin, String emin,
      String imax, String emax) {
    this.inclMin = imin != null ? new Double(imin) : null;
    this.exclMin = emin != null ? new Double(emin) : null;
    this.inclMax = imax != null ? new Double(imax) : null;
    this.exclMax = emax != null ? new Double(emax) : null;
    this.interval = Interval.valueOf(interval);
  }

  @Test
  public void testIntervalParsing() {
    assertMatches(interval.getInclMin(), inclMin);
    assertMatches(interval.getExclMin(), exclMin);
    assertMatches(interval.getInclMax(), inclMax);
    assertMatches(interval.getExclMax(), exclMax);
  }

  private void assertMatches(Optional<Number> value, Double expected) {
    if (expected == null) {
      assertFalse(value.isPresent());
    } else {
      assertEquals(expected, value.get());
    }
  }

  @Parameters
  public static Collection<String[]> parameters() {
    return Arrays.asList(new String[][] {
        { "[1, 5]", "1", null, "5", null },
        { "[1, 5[", "1", null, null, "5" },
        { "]1, 5[", null, "1", null, "5" },
        { "[1, *]", "1", null, null, null },
        { "[*, 5]", null, null, "5", null },
        { "[*, *]", null, null, null, null },
        { "]1, *]", null, "1", null, null },
        { "[*, 5[", null, null, null, "5" },
        { "]*, *[", null, null, null, null }, });
  }
}
