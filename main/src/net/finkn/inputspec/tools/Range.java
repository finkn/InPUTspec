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

/**
 * The range used to define a parameter. This class is immutable.
 *
 * @version 1.0
 * @author Christoffer Fink
 */
public class Range {

  /** An empty range. */
  public static final Range EMPTY = Range.getInstance();

  private final Optional<String> inclMin;
  private final Optional<String> exclMin;
  private final Optional<String> inclMax;
  private final Optional<String> exclMax;
  private final Interval interval;

  private Range(Optional<String> inclMin, Optional<String> exclMin,
      Optional<String> inclMax, Optional<String> exclMax) {
    this.interval = null;
    this.inclMin = inclMin;
    this.exclMin = exclMin;
    this.inclMax = inclMax;
    this.exclMax = exclMax;
  }

  private Range(Interval interval) {
    this.interval = interval;
    this.inclMin = getLimit(interval.getInclMin());
    this.exclMin = getLimit(interval.getExclMin());
    this.inclMax = getLimit(interval.getInclMax());
    this.exclMax = getLimit(interval.getExclMax());
  }

  /**
   * Get a new Range instance. This method is actually superfluous. Just start
   * with the {@link #EMPTY} range and add limits as necessary.
   * @return a new Range instance
   */
  public static Range getInstance() {
    Optional<String> empty = Optional.empty();
    return new Range(empty, empty, empty, empty);
  }

  // TODO: Simplify these to reduce redundancy.
  /**
   * Returns a new Range with this limit set.
   * Setting a limit explicitly discards any limits that were set implicitly
   * with an interval.
   * @param limit
   *          an arbitrary string that is supposed to represent a limit
   * @return a new Range instance with the specified limit
   */
  public Range withInclMin(String limit) {
    if (hasInterval()) {
      return EMPTY.withInclMin(limit);
    }
    return new Range(Optional.ofNullable(limit), exclMin, inclMax, exclMax);
  }

  /**
   * Returns a new Range with this limit set.
   * Setting a limit explicitly discards any limits that were set implicitly
   * with an interval.
   * @param limit
   *          an arbitrary string that is supposed to represent a limit
   * @return a new Range instance with the specified limit
   */
  public Range withExclMin(String limit) {
    if (hasInterval()) {
      return EMPTY.withExclMin(limit);
    }
    return new Range(inclMin, Optional.ofNullable(limit), inclMax, exclMax);
  }

  /**
   * Returns a new Range with this limit set.
   * Setting a limit explicitly discards any limits that were set implicitly
   * with an interval.
   * @param limit
   *          an arbitrary string that is supposed to represent a limit
   * @return a new Range instance with the specified limit
   */
  public Range withInclMax(String limit) {
    if (hasInterval()) {
      return EMPTY.withInclMax(limit);
    }
    return new Range(inclMin, exclMin, Optional.ofNullable(limit), exclMax);
  }

  /**
   * Returns a new Range with this limit set.
   * Setting a limit explicitly discards any limits that were set implicitly
   * with an interval.
   * @param limit
   *          an arbitrary string that is supposed to represent a limit
   * @return a new Range instance with the specified limit
   */
  public Range withExclMax(String limit) {
    if (hasInterval()) {
      return EMPTY.withExclMax(limit);
    }
    return new Range(inclMin, exclMin, inclMax, Optional.ofNullable(limit));
  }

  /**
   * Returns a new Range with limits set based on the interval.
   * Any limits that were set explicitly are discarded when setting an interval.
   * @param interval
   *          a string representing an interval
   * @return a new Range instance with the specified limits
   */
  public Range withInterval(String interval) {
    return new Range(Interval.valueOf(interval));
  }

  /** Returns this optional limit */
  public Optional<String> inclMin() {
    return inclMin;
  }

  /** Returns this optional limit */
  public Optional<String> exclMin() {
    return exclMin;
  }

  /** Returns this optional limit */
  public Optional<String> inclMax() {
    return inclMax;
  }

  /** Returns this optional limit */
  public Optional<String> exclMax() {
    return exclMax;
  }

  private boolean hasInterval() {
    return interval != null;
  }

  private static Optional<String> getLimit(Optional<Number> lim) {
    return Optional.ofNullable(lim.isPresent() ? lim.get().toString() : null);
  }
}
