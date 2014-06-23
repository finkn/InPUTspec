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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Asserts and other helpers for InPUT unit testing.
 * <h2>Assertions</h2>
 * The majority of these tools come in the form of custom asserts.
 * Some of these are truly general-purpose, such as the
 * {@link #assertAllMatch(Function, Stream, Predicate) assertion that all items
 * in a stream satisfy some predicate}.
 * <p>
 * All the assert(All|Some|None)Match methods work on the same principle. They
 * look for a value that does not belong, and then use that value to build an
 * error message for an AssertionError.
 *
 * <h3>Iteration Limit</h3>
 * All the assert(All|Some|None)Match tests that take a Supplier rather than a
 * Stream have one thing in common: they can only have one definitive
 * outcome. That is, each test can definitely succeed XOR definitely
 * fail. The other outcome is a matter of degrees of confidence, depending on
 * how many values were examined.
 * <p>
 * For example, when testing whether
 * {@link #assertAllMatch(int, Supplier, Predicate) all values match}, the first
 * 10 values might match, but this does not prove that the 11th value will also
 * match. Hence we cannot conclude that all match. After examining n values, no
 * matter how large n is, there is always the chance that the (n+1)th value will
 * cause the test to fail. Assuming that all the values do indeed match, the
 * test would go on forever. So the test has to be abandoned at some (arbitrary)
 * point. Examining hundreds of values takes more time, but it gives the result
 * more weight. Examining a small number of values is fast, but it inspires
 * little confidence.
 * <p>
 * Speed and confidence can be balanced by adjusting the maximum number of
 * iterations. The default number of iterations is 20.
 *
 * <h3>Short-circuiting</h3>
 * All tests finish as soon as they can. That is, the test ends as soon as it
 * has definitively failed or succeeded.
 * <p>
 * For example, when checking that
 * {@link #assertSomeMatch(int, Supplier, Predicate) some values match}, the
 * test will <em>succeed</em> as soon as a matching value is found. Similarly,
 * when checking that
 * {@link #assertNoneMatch(int, Supplier, Predicate) no value matches}, the test
 * will <em>fail</em> as soon as a matching value is found.
 * <p>
 * This means that the iteration limit can be set quite high in some cases,
 * depending on the expected outcome, without an undue performance penalty. In
 * other words, it's possible to get both speed and confidence. As long as the
 * values look as expected, the test will finish quickly and will also be
 * definitive. However, should the test turn out to fail, it will of course take
 * a proportionately long time to do so.
 *
 * @see net.finkn.inputspec.tools.Generator
 * @author Christoffer Fink
 */
public class Unit {
  public static final int DEFAULT_ITERATIONS = 20;

  private Unit() {
  }

  public static <T> void assertStreamEmpty(Stream<T> stream) {
    long count = stream.count();
    if (count > 0) {
      throw new AssertionError("Stream not empty. (" + count + " items)");
    }
  }

  public static void assertRangeHasNoLimits(Range r) {
    String msg = "Range was not supposed to have any limits.";
    assertNonePresent(msg, r.inclMin(), r.exclMin(), r.inclMax(), r.exclMax());
  }

  public static void assertRangeHasLimits(Range r) {
    String msg = "Range was supposed to have at least one limit.";
    assertSomePresent(msg, r.inclMin(), r.exclMin(), r.inclMax(), r.exclMax());
  }

  @SafeVarargs
  public static <T> void assertAllPresent(Optional<T>... optionals) {
    assertAllPresent("Optional value unexpectedly missing.", optionals);
  }

  @SafeVarargs
  public static <T> void assertAllPresent(String msg, Optional<T>... optionals) {
    Stream<Optional<T>> stream = Stream.of(optionals);
    Predicate<Optional<T>> pred = x -> x.isPresent();
    Function<Optional<T>, String> toMsg = x -> msg;
    assertAllMatch(toMsg, stream, pred);
  }

  @SafeVarargs
  public static <T> void assertSomePresent(Optional<T>... optionals) {
    assertSomePresent("No optional value present.", optionals);
  }

  @SafeVarargs
  public static <T> void assertSomePresent(String msg, Optional<T>... optionals) {
    Stream<Optional<T>> stream = Stream.of(optionals);
    Predicate<Optional<T>> pred = x -> x.isPresent();
    Function<Optional<T>, String> toMsg = x -> msg;
    assertSomeMatch(toMsg, stream, pred);
  }

  @SafeVarargs
  public static <T> void assertNonePresent(Optional<T>... optionals) {
    assertNonePresent("Optional value unexpectedly present.", optionals);
  }

  @SafeVarargs
  public static <T> void assertNonePresent(String msg, Optional<T>... optionals) {
    Stream<Optional<T>> stream = Stream.of(optionals);
    Predicate<Optional<T>> pred = x -> x.isPresent();
    Function<Optional<T>, String> toMsg = x -> msg;
    assertNoneMatch(toMsg, stream, pred);
  }

  /**
   * Creates a Consumer that will throw an AssertionError when handed any value.
   * The error message is generated by the {@code toMsg} function based on
   * the value.
   * @param toMsg
   *          a Function that maps a value to an error message
   * @return a Consumer that will always throw an exception
   */
  public static <T> Consumer<T> getThrower(Function<T, String> toMsg) {
    return t -> {
      throw new AssertionError(toMsg.apply(t));
    };
  }

  /**
   * Creates a Runnable that will throw an AssertionError when run.
   * @param msg
   *          the error message for the exception
   * @return a Runnable that will always throw an exception
   */
  public static Runnable getThrower(String msg) {
    return () -> {
      throw new AssertionError(msg);
    };
  }

  /**
   * Asserts that an exception with the expected error message is thrown.
   *
   * @param runnable
   *          a Runnable that is expected to throw an exception
   * @param regex
   *          a regular expression which the message should match
   */
  public static void assertExceptionMessageMatches(Runnable runnable,
      String regex) {
    Function<Throwable, String> toMsg = e -> String.format(
        "'%s' did not match '%s'.", e.getMessage(), regex);
    assertExceptionMatches(toMsg, runnable, e -> e.getMessage().matches(regex));
  }

  /**
   * Asserts that the expected exception is thrown.
   *
   * @see #assertExceptionMatches(Function, Runnable, Predicate)
   * @param thrower
   *          a Runnable that is expected to throw an exception
   * @param catcher
   *          a Predicate that accepts or rejects the exception
   */
  public static void assertExceptionMatches(Runnable thrower,
      Predicate<Throwable> catcher) {
    assertExceptionMatches(e -> e + " did not match.", thrower, catcher);
  }

  /**
   * Asserts that the expected exception is thrown. The Runnable is expected to
   * throw an exception satisfying some arbitrary Predicate. If the exception
   * does not satisfy the predicate, the Function is used to generate a message
   * for the AssertionError, based on the actual exception that was caught.
   *
   * @param toMsg
   *          a Function that maps an exception to an error message
   * @param thrower
   *          a Runnable that is expected to throw an exception
   * @param catcher
   *          a Predicate that accepts or rejects the exception
   */
  public static void assertExceptionMatches(Function<Throwable, String> toMsg,
      Runnable thrower, Predicate<Throwable> catcher) {
    try {
      thrower.run();
      throw new IllegalArgumentException("thrower was supposed to throw!");
    } catch (Throwable e) {
      if (!catcher.test(e)) {
        throw new AssertionError(toMsg.apply(e));
      }
    }
  }

  /**
   * Returns the message for any exceptions thrown when running the Runnable.
   */
  public static Optional<String> getExceptionMessage(Runnable thrower) {
    try {
      thrower.run();
      return Optional.ofNullable(null);
    } catch (Throwable e) {
      return Optional.ofNullable(e.getMessage());
    }
  }

  public static <T> void assertAllMatch(Supplier<T> gen, Predicate<T> pred) {
    assertAllMatch(getDefaultAllMatchMsg(), gen, pred);
  }

  public static <T> void assertSomeMatch(Supplier<T> gen, Predicate<T> pred) {
    assertSomeMatch(getDefaultSomeMatchMsg(), gen, pred);
  }

  public static <T> void assertNoneMatch(Supplier<T> gen, Predicate<T> pred) {
    assertNoneMatch(getDefaultNoneMatchMsg(), gen, pred);
  }

  public static <T> void assertAllMatch(Function<T, String> toMsg,
      Supplier<T> gen, Predicate<T> pred) {
    assertAllMatch(DEFAULT_ITERATIONS, toMsg, gen, pred);
  }

  public static <T> void assertSomeMatch(Function<T, String> toMsg,
      Supplier<T> gen, Predicate<T> pred) {
    assertSomeMatch(DEFAULT_ITERATIONS, toMsg, gen, pred);
  }

  public static <T> void assertNoneMatch(Function<T, String> toMsg,
      Supplier<T> gen, Predicate<T> pred) {
    assertNoneMatch(DEFAULT_ITERATIONS, toMsg, gen, pred);
  }

  public static <T> void assertAllMatch(int iterations, Supplier<T> gen,
      Predicate<T> pred) {
    assertAllMatch(iterations, getDefaultAllMatchMsg(), gen, pred);
  }

  public static <T> void assertSomeMatch(int iterations, Supplier<T> gen,
      Predicate<T> pred) {
    assertSomeMatch(iterations, getDefaultSomeMatchMsg(), gen, pred);
  }

  public static <T> void assertNoneMatch(int iterations, Supplier<T> gen,
      Predicate<T> pred) {
    assertNoneMatch(iterations, getDefaultNoneMatchMsg(), gen, pred);
  }

  /**
   * Asserts that all values returned by the supplier satisfy the predicate.
   * At most {@code iterations} values will be examined. If it turns out that
   * a value does not match, then that value is handed to the {@code toMsg}
   * function, and the resulting message is used to throw an AssertionError.
   *
   * @param iterations
   *          the maximum number of values to examine before concluding the test
   * @param toMsg
   *          a Function mapping values to error messages
   * @param gen
   *          the Supplier of values to be examined
   * @param pred
   *          the Predicate that all values are expected to satisfy
   */
  public static <T> void assertAllMatch(int iterations,
      Function<T, String> toMsg, Supplier<T> gen, Predicate<T> pred) {
    assertAllMatch(toMsg, getLimitedStream(iterations, gen), pred);
  }

  /**
   * Asserts that some value returned by the supplier satisfies the predicate.
   * At most {@code iterations} values will be examined. If it turns out that
   * no value matches, then the {@code toMsg} function is expected to return
   * an error message, and the resulting message is used to throw an
   * AssertionError. Since no specific values failed to match, the function
   * is called with {@code null}.
   *
   * @param iterations
   *          the maximum number of values to examine before concluding the test
   * @param toMsg
   *          a Function mapping values to error messages
   * @param gen
   *          the Supplier of values to be examined
   * @param pred
   *          the Predicate that some values are expected to satisfy
   */
  public static <T> void assertSomeMatch(int iterations,
      Function<T, String> toMsg, Supplier<T> gen, Predicate<T> pred) {
    assertSomeMatch(toMsg, getLimitedStream(iterations, gen), pred);
  }

  /**
   * Asserts that no values returned by the supplier satisfy the predicate.
   * At most {@code iterations} values will be examined. If it turns out that
   * a value does match, then that value is handed to the {@code toMsg}
   * function, and the resulting message is used to throw an AssertionError.
   *
   * @param iterations
   *          the maximum number of values to examine before concluding the test
   * @param toMsg
   *          a Function mapping values to error messages
   * @param gen
   *          the Supplier of values to be examined
   * @param pred
   *          the Predicate that no values are expected to satisfy
   */
  public static <T> void assertNoneMatch(int iterations,
      Function<T, String> toMsg, Supplier<T> gen, Predicate<T> pred) {
    assertNoneMatch(toMsg, Stream.generate(gen).limit(iterations), pred);
  }

  /**
   * Asserts that all elements in the stream satisfy the predicate. This version
   * uses a default function for mapping values to error messages.
   *
   * @see #assertAllMatch(Function, Stream, Predicate)
   */
  public static <T> void assertAllMatch(Stream<T> stream, Predicate<T> pred) {
    assertAllMatch(getDefaultAllMatchMsg(), stream, pred);
  }

  /**
   * Asserts that all elements in the stream satisfy the predicate. If it turns
   * out that not all elements match, then the first offending value is handed
   * to the {@code toMsg} function, and the resulting message is used to throw
   * an AssertionError.
   *
   * @param toMsg
   *          a Function mapping values to error messages
   * @param stream
   *          a Stream of values
   * @param pred
   *          the Predicate that all elements are expected to satisfy
   */
  public static <T> void assertAllMatch(Function<T, String> toMsg,
      Stream<T> stream, Predicate<T> pred) {
    reportMatch(toMsg, stream, pred.negate());
  }

  /**
   * Asserts that some element in the stream satisfies the predicate. This
   * version uses a default function for mapping values to error messages.
   *
   * @see #assertSomeMatch(Function, Stream, Predicate)
   */
  public static <T> void assertSomeMatch(Stream<T> stream, Predicate<T> pred) {
    assertSomeMatch(getDefaultSomeMatchMsg(), stream, pred);
  }

  /**
   * Asserts that some element in the stream satisfies the predicate. If it
   * turns out that no elements match, then an AssertionError is thrown, using
   * the message returned by the {@code toMsg} function. Since no one offending
   * element can be identified in this case, toMsg is called with {@code null}.
   *
   * @param toMsg
   *          a Function mapping values to error messages
   * @param stream
   *          a Stream of values
   * @param pred
   *          the Predicate that some elements are expected to satisfy
   */
  public static <T> void assertSomeMatch(Function<T, String> toMsg,
      Stream<T> stream, Predicate<T> pred) {
    if (!stream.anyMatch(pred)) {
      throw new AssertionError(toMsg.apply(null));
    }
  }

  /**
   * Asserts that no elements in the stream satisfy the predicate. This version
   * uses a default function for mapping values to error messages.
   *
   * @see #assertNoneMatch(Function, Stream, Predicate)
   */
  public static <T> void assertNoneMatch(Stream<T> stream, Predicate<T> pred) {
    assertNoneMatch(getDefaultNoneMatchMsg(), stream, pred);
  }

  /**
   * Asserts that no elements in the stream satisfy the predicate. If it turns
   * out that some element does match, then the first offending value is handed
   * to the {@code toMsg} function, and the resulting message is used to throw
   * an AssertionError.
   *
   * @param toMsg
   *          a Function mapping values to error messages
   * @param stream
   *          a Stream of values
   * @param pred
   *          the Predicate that no elements are expected to satisfy
   */
  public static <T> void assertNoneMatch(Function<T, String> toMsg,
      Stream<T> stream, Predicate<T> pred) {
    reportMatch(toMsg, stream, pred);
  }

  private static <T> void reportMatch(Function<T, String> toMsg,
      Stream<T> stream, Predicate<T> pred) {
    stream.filter(pred).findFirst().ifPresent(getThrower(toMsg));
  }

  private static <T> Stream<T> getLimitedStream(int limit, Supplier<T> gen) {
    return Stream.generate(gen).limit(limit);
  }

  private static <T> Function<T, String> getDefaultAllMatchMsg() {
    return x -> x + " did not match.";
  }

  private static <T> Function<T, String> getDefaultSomeMatchMsg() {
    return x -> "No value matched.";
  }

  private static <T> Function<T, String> getDefaultNoneMatchMsg() {
    return x -> "Unexpected match: " + x;
  }
}
