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

import java.util.*;
import java.util.function.*;

import se.miun.itm.input.model.InPUTException;
import se.miun.itm.input.model.design.IDesignSpace;

/**
 * Generator wrapper used for testing. Objects of this class aren't intended to
 * be generators per se. They are primarily meant to wrap some underlying
 * generator, making it easy to make assertions about the kinds of values it
 * generates in order to examine its behavior.
 * <p>
 * This class has testing-related state (number of iterations, custom messages)
 * so that many specialized use cases can be supported without causing the
 * number of overloaded methods to explode.
 * {@link net.finkn.inputspec.tools.Unit Unit} already has many methods for
 * making assertions about the values that are returned by some supplier or
 * stream, but this class is meant to be conveniently special-purpose.
 * <p>
 * All tests depend on an {@link #limit(int) iteration limit} and use
 * short-circuiting. See {@link net.finkn.inputspec.tools.Unit Unit} for
 * more information about what that means.
 *
 * <h3>Assertions</h3>
 * All the behavior-testing methods are based on assertions. Hence they will
 * either succeed silently or fail noisily (by throwing an exception).
 *
 * @author Christoffer Fink
 * @version 1.2.1
 * @see Sink
 */
public abstract class Generator<T> implements Supplier<T> {
  private int iterations = Unit.DEFAULT_ITERATIONS;
  private boolean iterationsSet = false;
  private Optional<Function<T, String>> toMsg = Optional.ofNullable(null);
  private long generated;

  protected Generator() {
  }

  /**
   * Subclasses override this method.
   */
  abstract protected T nextValue();

  /**
   * Final method for doing some bookkeeping and returning a value from the
   * wrapped generator. This method is final so that subclasses cannot get
   * around the bookkeeping.
   */
  @Override
  public final T get() {
    generated++;
    return nextValue();
  }

  /**
   * Returns the number of values that this generator was asked to generate.
   */
  public final long valuesGenerated() {
    return generated;
  }

  /**
   * Sets the iteration limit.
   * <p>
   * Note: This does not impose a limit on the number of values the generator
   * can return. Generators are not intended for generating values. Rather, they
   * are tools for making assertions about wrapped generators and examining
   * their behavior.
   *
   * @param iterations
   *          the maximum number of values that are generated when examining the
   *          generator
   */
  public final Generator<T> limit(int iterations) {
    this.iterations = iterations;
    this.iterationsSet = true;
    return this;
  }

  /**
   * Sets a custom message that is used when an assertion about the generator
   * fails. Set to {@code null} to reset to using a default message.
   *
   * @param msg
   *          a custom error message
   */
  public final Generator<T> msg(String msg) {
    return toMsg(x -> msg);
  }

  /**
   * Sets a dynamically generated custom message. When an assertion about the
   * generator fails, this function will be asked to generate a message using
   * the value that caused the error. Set to {@code null} to reset to using a
   * default message.
   *
   * @param toMsg
   *          a function that maps a value to an error message
   */
  public final Generator<T> toMsg(Function<T, String> toMsg) {
    this.toMsg = Optional.ofNullable(toMsg);
    return this;
  }

  /**
   * Asserts that the generator only produces identical values, whatever that
   * value may be.
   * <ul>
   * <li>Can produce false positives.</li>
   * <li>Cannot produce false negatives.</li>
   * <li>Fails fast.</li>
   * </ul>
   */
  public final void isConstant() {
    assertSufficientIterations(iterations, 2);
    T first = this.get();
    Function<T, String> toMsg = getToMsg(getDefaultConstancyMsg());
    Unit.assertAllMatch(iterations - 1, toMsg, this, x -> x == first);
  }

  /**
   * Asserts that the generator produces varying values, whatever those values
   * may be.
   * <ul>
   * <li>Can produce false negatives.</li>
   * <li>Cannot produce false positives.</li>
   * <li>Succeeds fast.</li>
   * </ul>
   */
  public final void isVariable() {
    assertSufficientIterations(iterations, 2);
    T first = this.get();
    Function<T, String> toMsg = x ->
      getToMsg(getDefaultVariabilityMsg()).apply(first);
    Unit.assertSomeMatch(iterations - 1, toMsg, this, x -> x != first);
  }

  @SafeVarargs
  public final void generatesAll(T... expected) {
    generatesAll(getTree(expected));
  }

  @SafeVarargs
  public final void generatesAny(T... expected) {
    generatesAny(getTree(expected));
  }

  @SafeVarargs
  public final void generatesOnly(T... expected) {
    generatesOnly(getTree(expected));
  }

  @SafeVarargs
  public final void generatesNone(T... expected) {
    generatesNone(getTree(expected));
  }

  public final void generatesAll(Collection<T> expected) {
    generatesAll(new TreeSet<>(expected));
  }

  public final void generatesAny(Collection<T> expected) {
    generatesAny(new TreeSet<>(expected));
  }

  public final void generatesOnly(Collection<T> expected) {
    generatesOnly(new TreeSet<>(expected));
  }

  public final void generatesNone(Collection<T> expected) {
    generatesNone(new TreeSet<>(expected));
  }

  /**
   * Asserts that the generator eventually produces all the expected values.
   * All overloaded versions of this method end up here.
   * <ul>
   * <li>Can produce false negatives.</li>
   * <li>Cannot produce false positives.</li>
   * <li>Succeeds fast.</li>
   * </ul>
   * Note that the iteration limit must be at least as large as the number of
   * distinct values that are expected. Otherwise, the test would be impossible
   * to pass.
   */
  public final void generatesAll(SortedSet<T> expected) {
    if (expected.isEmpty()) {
      return;
    }
    Collection<T> remaining = new TreeSet<>(expected);
    int it = iterations;
    if (!iterationsSet) {
      it = it * expected.size();
    }
    assertSufficientIterations(it, expected.size());
    Function<T, String> toMsg = getToMsg(x -> "Did not generate all. Missing: "
        + remaining);
    Predicate<T> pred = x -> {
      remaining.remove(x);
      return remaining.isEmpty();
    };
    Unit.assertSomeMatch(it, toMsg, this, pred);
  }

  /**
   * Asserts that the generator produces any of the expected values.
   * All overloaded versions of this method end up here.
   * <ul>
   * <li>Can produce false negatives.</li>
   * <li>Cannot produce false positives.</li>
   * <li>Succeeds fast.</li>
   * </ul>
   */
  public final void generatesAny(SortedSet<T> expected) {
    if (expected.isEmpty()) {
      return;
    }
    Function<T, String> toMsg = getToMsg(x -> "Did not generate any of "
        + expected);
    Unit.assertSomeMatch(iterations, toMsg, this, x -> expected.contains(x));
  }

  /**
   * Asserts that the generator only produces the expected values.
   * All overloaded versions of this method end up here.
   * <ul>
   * <li>Can produce false positives.</li>
   * <li>Cannot produce false negatives.</li>
   * <li>Fails fast.</li>
   * </ul>
   */
  public final void generatesOnly(SortedSet<T> expected) {
    Function<T, String> toMsg = getToMsg(x -> "Unexpected value " + x
        + " not in " + expected);
    Unit.assertAllMatch(iterations, toMsg, this, x -> expected.contains(x));
  }

  /**
   * Asserts that the generator produces none of the given values.
   * All overloaded versions of this method end up here.
   * <ul>
   * <li>Can produce false positives.</li>
   * <li>Cannot produce false negatives.</li>
   * <li>Fails fast.</li>
   * </ul>
   * @since 1.2
   */
  public final void generatesNone(SortedSet<T> prohibited) {
    Function<T, String> toMsg = getToMsg(x -> "Prohibited value " + x);
    Unit.assertNoneMatch(iterations, toMsg, this, x -> prohibited.contains(x));
  }

  private static void assertSufficientIterations(int iterations, int min) {
    if (iterations < min) {
      String msg = iterations + " < " + min + " iterations";
      throw new IllegalArgumentException(msg);
    }
  }

  /**
   * Returns a sorted set with the elements.
   * We want a set because duplicates are irrelevant. We want a tree for fast
   * lookup. When comparing the generator against a large number of values,
   * using the right data structure makes a huge difference.
   */
  private static <T> SortedSet<T> getTree(T[] elements) {
    SortedSet<T> result = new TreeSet<>();
    for (T element : elements) {
      result.add(element);
    }
    return result;
  }

  private Function<T, String> getToMsg(Function<T, String> other) {
    return toMsg.orElse(other);
  }

  private Function<T, String> getDefaultConstancyMsg() {
    return x -> "Unexpected variation in generator: " + x;
  }

  private Function<T, String> getDefaultVariabilityMsg() {
    return x -> "No variation in generator. Only got: " + x;
  }

  @SafeVarargs
  public static <T> Generator<T> finiteFromSeq(T... elements) {
    return finiteFromSeq(Arrays.asList(elements));
  }

  /**
   * Creates a finite generator that will return the values in the collection.
   * Once all the values have been returned, the generator will throw a runtime
   * exception when asked to generate more values.
   */
  public static <T> Generator<T> finiteFromSeq(Collection<T> elements) {
    return SequenceGenerator.getFinite(elements);
  }

  @SafeVarargs
  public static <T> Generator<T> fromSeq(T... elements) {
    return fromSeq(Arrays.asList(elements));
  }

  /**
   * Creates an infinite generator that will return the values in the
   * collection. Once all the values have been returned, the generator will
   * start over from the first value and repeat the sequence.
   */
  public static <T> Generator<T> fromSeq(Collection<T> elements) {
    return SequenceGenerator.getInfinite(elements);
  }

  /**
   * Creates a generator that returns the values generated for the given
   * parameter ID by the given design space.
   *
   * @param space
   *          a design space
   * @param paramId
   *          a parameter ID that is supported by the design space
   */
  public static Generator<Object> fromDesignSpace(IDesignSpace space,
      String paramId) {
    return fromSupplier(() -> {
      try {
        return space.next(paramId);
      } catch (InPUTException e) {
        throw new RuntimeException(e);
      }
    });
  }

  /**
   * Convenient shortcut to {@link #fromDesignSpace(IDesignSpace, String)}.
   * @throws InPUTException if a design space cannot be created
   * @since 1.1
   */
  public static Generator<Object> fromParam(ParamCfg param)
      throws InPUTException {
    IDesignSpace space = DesignSpaceCfg.builder()
      .param(param)
      .build()
      .getDesignSpace();
    return fromDesignSpace(space, param.getId());
  }

  /**
   * Creates a generator that takes values from some arbitrary Supplier.
   */
  public static <T> Generator<T> fromSupplier(Supplier<T> supplier) {
    return new SupplierGenerator<>(supplier);
  }

  private static class SupplierGenerator<T> extends Generator<T> {
    private final Supplier<T> supplier;

    public SupplierGenerator(Supplier<T> supplier) {
      this.supplier = supplier;
    }

    @Override
    public T nextValue() {
      return supplier.get();
    }
  }

  private static class SequenceGenerator<T> extends Generator<T> {
    private final LinkedList<T> queue;
    private final boolean infinite;

    public static <T> SequenceGenerator<T> getInfinite(Collection<T> elements) {
      return new SequenceGenerator<T>(elements, true);
    }

    public static <T> SequenceGenerator<T> getFinite(Collection<T> elements) {
      return new SequenceGenerator<T>(elements, false);
    }

    private SequenceGenerator(Collection<T> elements, boolean infinite) {
      this.infinite = infinite;
      this.queue = new LinkedList<T>(elements);
    }

    @Override
    public T nextValue() {
      T result = queue.pollFirst();
      if (result == null) {
        throw new IllegalStateException("Generator out of values.");
      }
      if (infinite) {
        queue.addLast(result);
      }
      return result;
    }
  }
}
