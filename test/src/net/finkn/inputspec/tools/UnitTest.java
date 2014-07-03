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

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.Test;

public class UnitTest {
  private static final Predicate<Integer> neg = x -> false;
  private static final Predicate<Integer> pos = neg.negate();
  private static final Supplier<Integer> num = () -> 1;

  @Test
  public void assertAllMatchShouldSucceedIfAllValuesMatch() {
    Generator<Integer> gen = Generator.fromSeq(1, 2, 3);
    assertAllMatch(gen, x -> x > 0);
  }

  @Test(expected = AssertionError.class)
  public void assertAllMatchShouldFailIfAnyDoesNotMatch() {
    Generator<Integer> gen = Generator.fromSeq(1, 2, 3, 4, 5, 0);
    assertAllMatch(gen, x -> x > 0);
  }

  @Test
  public void assertSomeMatchShouldSucceedIfAtLeastOneValueMatches() {
    Generator<Integer> gen = Generator.fromSeq(0, 1, 2, 3);
    assertSomeMatch(gen, x -> x > 0);
  }

  @Test(expected = AssertionError.class)
  public void assertSomeMatchShouldFailIfNoneMatch() {
    Generator<Integer> gen = Generator.fromSeq(0, 0, -1, -1);
    assertSomeMatch(gen, x -> x > 0);
  }

  @Test
  public void assertNoneMatchShouldSucceedIfNoValuesMatch() {
    Generator<Integer> gen = Generator.fromSeq(0, 0, -1, -1);
    assertNoneMatch(gen, x -> x > 0);
  }

  @Test(expected = AssertionError.class)
  public void assertNoneMatchShouldFailIfAnyDoNotMatch() {
    Generator<Integer> gen = Generator.fromSeq(0, 1, 2, 3);
    assertNoneMatch(gen, x -> x > 0);
  }

  @Test
  public void assertXMatchesShouldUseCustomMessage() {
    String msg = "Custom exception message.";
    assertExceptionMessageMatches(() -> assertAllMatch(x -> msg, num, neg), msg);
    assertExceptionMessageMatches(() -> assertSomeMatch(x -> msg, num, neg),
        msg);
    assertExceptionMessageMatches(() -> assertNoneMatch(x -> msg, num, pos),
        msg);
  }

  @Test
  public void assertAllPresentShouldSucceedIfNoValuesMissing() {
    Optional<String> optional1 = Optional.of("Hello");
    Optional<String> optional2 = Optional.of("World");
    assertAllPresent(optional1, optional2);
  }

  @Test(expected = AssertionError.class)
  public void assertAllPresentShouldFailIfAnyValuesMissing() {
    Optional<String> optional1 = Optional.of("Hello");
    Optional<String> optional2 = Optional.ofNullable(null);
    assertAllPresent(optional1, optional2);
  }

  @Test
  public void assertSomePresentShouldSucceedIfAtLeastOnePresent() {
    Optional<String> optional1 = Optional.of("Hello");
    Optional<String> optional2 = Optional.ofNullable(null);
    assertSomePresent(optional1, optional2);
  }

  @Test(expected = AssertionError.class)
  public void assertSomePresentShouldFailIfNoValuesPresent() {
    Optional<String> optional1 = Optional.ofNullable(null);
    Optional<String> optional2 = Optional.ofNullable(null);
    assertSomePresent(optional1, optional2);
  }

  @Test
  public void assertNonePresentShouldSucceedIfNoValuesPresent() {
    Optional<String> optional1 = Optional.ofNullable(null);
    Optional<String> optional2 = Optional.ofNullable(null);
    assertNonePresent(optional1, optional2);
  }

  @Test(expected = AssertionError.class)
  public void assertNonePresentShouldFailIfAnyValuesPresent() {
    Optional<String> optional1 = Optional.of("Hello");
    Optional<String> optional2 = Optional.ofNullable(null);
    assertNonePresent(optional1, optional2);
  }

  // The Range limit assertions are too simple to be tested here.
  // They are also exercised in RangeTest.

  @Test
  public void assertStreamEmptyShouldSucceedIfNoItemsInStream() {
    assertStreamEmpty(Stream.of());
  }

  @Test(expected = AssertionError.class)
  public void assertStreamEmptyShouldFailIfAnyItemsInStream() {
    assertStreamEmpty(Stream.of(1));
  }

  @Test
  public void assertStringContainsAllShouldSucceedIfAllMatch() {
    String s = "Hello World";
    assertStringContainsAll(s, "Hello", "World", "lo Wo", "");
  }

  @Test(expected = AssertionError.class)
  public void assertStringContainsAllShouldFailIfAnyDoesNotMatch() {
    String s = "Hello World";
    assertStringContainsAll(s, "Hello", "World", "onetwothree DEATH!");
  }

  @Test
  public void assertStringContainsNoneShouldSucceedIfNoneMatch() {
    String s = "Hello World";
    assertStringContainsNone(s, "undead", "parrot", "is the", "only");
  }

  @Test(expected = AssertionError.class)
  public void assertStringContainsNoneShouldFailIfAnyMatch() {
    String s = "Hello World";
    assertStringContainsNone(s, "diabetic", "cannibal", "in the", "World");
  }
}
