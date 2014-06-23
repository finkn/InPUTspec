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
 * Compact representation of parameter limits for use in testing. A String
 * representing an interval is parsed and translated into individual
 * inclusive/exclusive minimum/maximum limits. This removes the burden of
 * handling individual limits. Additionally, Intervals can be used to check that
 * values are valid.
 * <p>
 * The supported notation is mostly standard mathematical notation. Only square
 * brackets are allowed. Infinity is expressed using an asterisk. This class
 * does not care whether infinite limits are inclusive or exclusive. However,
 * for mathematical consistency, it is highly recommended to always make them
 * exclusive, such as ]*,3] or ]1,*[. They are just more sensible that way.
 * <p>
 * This class is not meant for production. For one, it doesn't handle BigDecimal
 * limits, so it wouldn't be able to handle all parameter configurations.
 * 
 * @version 1.0
 * @author Christoffer Fink
 */
public class Interval {
  private final Optional<Number> inclMin;
  private final Optional<Number> exclMin;
  private final Optional<Number> inclMax;
  private final Optional<Number> exclMax;
  private final String spec;

  private Interval(String imin, String emin, String imax, String emax,
      String spec) {
    this.inclMin = getOptional(imin);
    this.exclMin = getOptional(emin);
    this.inclMax = getOptional(imax);
    this.exclMax = getOptional(emax);
    this.spec = spec;
  }

  /**
   * Parses an interval string representation such as {@code "[1,2]"}, and
   * returns an appropriate Interval object.
   */
  public static Interval valueOf(String spec) {
    String[] limits = parseInterval(spec);
    return new Interval(limits[0], limits[1], limits[2], limits[3], spec);
  }

  /**
   * Checks whether this interval contains a given number.
   * @param value the value to test for membership
   * @return {@code true} if the value is contained in the interval
   */
  public boolean contains(Number value) {
    return lowerLimitMatches(value) && upperLimitMatches(value);
  }

  public Optional<Number> getInclMin() {
    return inclMin;
  }

  public Optional<Number> getExclMin() {
    return exclMin;
  }

  public Optional<Number> getInclMax() {
    return inclMax;
  }

  public Optional<Number> getExclMax() {
    return exclMax;
  }

  @Override
  public String toString() {
    return spec;
  }

  private static String[] parseInterval(String interval) {
    String[] endpoints = new String[4];
    if (interval == null) {
      return endpoints;
    }
    char start = interval.charAt(0);
    char end = interval.charAt(interval.length() - 1);
    interval = interval.substring(1, interval.length() - 1);
    String[] ends = interval.split(", *");
    String min = ends[0];
    String max = ends[1];

    if (start == '[') {
      endpoints[0] = min;
    }
    if (start == ']') {
      endpoints[1] = min;
    }
    if (end == ']') {
      endpoints[2] = max;
    }
    if (end == '[') {
      endpoints[3] = max;
    }

    for (int i = 0; i < endpoints.length; i++) {
      if ("*".equals(endpoints[i])) {
        endpoints[i] = null;
      }
    }

    return endpoints;
  }

  private static Optional<Number> getOptional(String limit) {
    return Optional.ofNullable(limit != null ? Double.valueOf(limit) : null);
  }

  private boolean lowerLimitMatches(Number value) {
    if (inclMin.isPresent()) {
      return value.doubleValue() >= inclMin.get().doubleValue();
    } else if (exclMin.isPresent()) {
      return value.doubleValue() > exclMin.get().doubleValue();
    } else {
      return true;
    }
  }

  private boolean upperLimitMatches(Number value) {
    if (inclMax.isPresent()) {
      return value.doubleValue() <= inclMax.get().doubleValue();
    } else if (exclMax.isPresent()) {
      return value.doubleValue() < exclMax.get().doubleValue();
    } else {
      return true;
    }
  }
}
