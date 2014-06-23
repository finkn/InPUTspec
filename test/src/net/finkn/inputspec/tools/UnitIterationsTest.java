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

import java.util.function.Predicate;

import org.junit.Test;

public class UnitIterationsTest {
  private final Generator<Integer> gen = Generator.fromSupplier(() -> 0);
  private final int limit = 3;
  private static final Predicate<Integer> pos = x -> true;
  private static final Predicate<Integer> neg = x -> false;

  @Test
  public void assertAllMatchFailsWhenReachingIterationLimit() {
    try {
      assertAllMatch(limit, gen, pos);
    } catch (AssertionError e) {
      fail("The assertion was supposed to succeed.");
    }
    assertEquals(limit, gen.valuesGenerated());
  }

  @Test
  public void assertAllMatchShortCircuits() {
    try {
      assertAllMatch(limit, gen, neg);
      fail("The assertion was supposed to fail.");
    } catch (AssertionError e) {
    }
    assertEquals(1, gen.valuesGenerated());
  }

  @Test
  public void assertSomeMatchFailsWhenReachingIterationLimit() {
    try {
      assertSomeMatch(limit, gen, pos);
    } catch (AssertionError e) {
      fail("The assertion was supposed to succeed.");
    }
    assertEquals(1, gen.valuesGenerated());
  }

  @Test
  public void assertSomeMatchShortCircuits() {
    try {
      assertSomeMatch(limit, gen, neg);
      fail("The assertion was supposed to fail.");
    } catch (AssertionError e) {
    }
    assertEquals(limit, gen.valuesGenerated());
  }

  @Test
  public void assertNoneMatchFailsWhenReachingIterationLimit() {
    try {
      assertNoneMatch(limit, gen, neg);
    } catch (AssertionError e) {
      fail("The assertion was supposed to succeed.");
    }
    assertEquals(limit, gen.valuesGenerated());
  }

  @Test
  public void assertNoneMatchShortCircuits() {
    try {
      assertNoneMatch(limit, gen, pos);
      fail("The assertion was supposed to fail.");
    } catch (AssertionError e) {
    }
    assertEquals(1, gen.valuesGenerated());
  }
}
