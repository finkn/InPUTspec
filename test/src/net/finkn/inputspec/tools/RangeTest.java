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

import static org.junit.Assert.*;
import static net.finkn.inputspec.tools.Unit.*;

import org.junit.Test;

public class RangeTest {
  private final Range range = Range.getInstance();

  @Test
  public void defaultRangeHasEmptyLimits() {
    assertRangeHasNoLimits(range);
  }

  @Test
  public void rangeWithLimitShouldReturnThatLimit() {
    String limit = "3";
    assertEquals(limit, range.withInclMin(limit).inclMin().get());
  }

  @Test
  public void rangeWithIntervalShouldReturnMatchingLimits() {
    Range modified = range.withInterval("]4,5]");
    assertEquals("4.0", modified.exclMin().get());
    assertEquals("5.0", modified.inclMax().get());
    // These were not set.
    assertNonePresent(modified.inclMin(), modified.exclMax());
  }

  @Test
  public void rangeWithEmptyIntervalShouldReturnEmptyLimits() {
    assertRangeHasNoLimits(range.withInterval("]*,*["));
  }

  @Test
  public void nullLimitsAreValidAndEmpty() {
    assertRangeHasNoLimits(range.withInclMin(null));
  }

  @Test
  public void nullIntervalsAreValidAndEmpty() {
    assertRangeHasNoLimits(range.withInterval(null));
  }

  @Test
  public void settingLimitDiscardsInterval() {
    Range modified = range.withInterval("[1,2]").withInclMin("3");
    assertNonePresent(modified.inclMax());
    assertEquals("3", modified.inclMin().get());
  }

  @Test
  public void settingIntervalDiscardsLimit() {
    Range modified = range.withInclMin("3");
    modified = modified.withInterval("]4,5]");
    assertNonePresent(modified.inclMin());
  }

  @Test
  public void settingLimitReturnsIndependentRange() {
    Range modified = range.withExclMax("3");
    assertNotSame(modified, range);
  }

  @Test
  public void settingIntervalReturnsIndependentRange() {
    Range modified = range.withInterval("[1,2]");
    assertNotSame(modified, range);
  }
}
