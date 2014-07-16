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

import static net.finkn.inputspec.tools.Unit.assertAllMatch;
import static net.finkn.inputspec.tools.Unit.assertNoneMatch;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import se.miun.itm.input.model.InPUTException;
import se.miun.itm.input.model.design.IDesign;

/**
 * Value sink wrapper used for testing. A sink either accepts or rejects a
 * value. This class is a counterpart to a
 * {@link net.finkn.inputspec.tools.Generator}. While a generator allows us to
 * make assertions about the kinds of values a component produces, a sink lets
 * us make assertions about the kinds of values a component consumes.
 *
 * @author Christoffer Fink
 * @version 1.0
 * @see Generator
 */
public abstract class Sink<T> implements Predicate<T> {

  protected Sink() {
  }

  /** Asserts that all the values are accepted by this sink. */
  @SuppressWarnings("unchecked")
  public Sink<T> accepts(T ... values) {
    Function<T, String> toMsg = x -> "Sink cannot accept " + x;
    assertAllMatch(toMsg, Stream.of(values), this);
    return this;
  }

  /** Asserts that all the values are rejected by this sink. */
  @SuppressWarnings("unchecked")
  public Sink<T> rejects(T ... values) {
    Function<T, String> toMsg = x -> "Sink does not reject " + x;
    assertNoneMatch(toMsg, Stream.of(values), this);
    return this;
  }

  /**
   * Creates a sink from an {@link net.finkn.inputspec.tools.Interval interval}
   * string. Primarily useful as a reference for testing. The sink will accept
   * a value if and only if that value is contained in the interval.
   */
  public static Sink<Number> fromInterval(String s) {
    Interval interval = Interval.valueOf(s);
    return fromPredicate(x -> interval.contains(x));
  }

  /**
   * Creates a sink based on an arbitrary predicate. The sink will accept a
   * given value if and only if it satisfies the predicate.
   */
  public static <T> Sink<T> fromPredicate(Predicate<T> pred) {
    return new Sink<T>() {
      @Override
      public boolean test(T t) {
        return pred.test(t);
      }
    };
  }

  /**
   * Creates a sink based on a parameter configuration. This is a convenient
   * shortcut to {@link #fromDesign(IDesign, String)}. A design is automatically
   * created using the parameter.
   *
   * @throws InPUTException if the configuration is illegal.
   */
  public static Sink<Object> fromParam(ParamCfg param) throws InPUTException {
      IDesign design = DesignSpaceCfg.builder()
        .param(param)
        .build()
        .getDesignSpace()
        .nextDesign("DesignSink");
      return fromDesign(design, param.getId());
  }

  /**
   * Creates a sink based on a design and a parameter ID. The parameter ID must
   * be supported by the design. The sink will accept a given value if and only
   * if the parameter can be successfully set to that value.
   *
   * @throws IllegalArgumentException if the parameter ID is not supported by
   *    the design.
   */
  public static Sink<Object> fromDesign(IDesign design, String paramId) {
    if (!design.getSupportedParamIds().contains(paramId)) {
      String id = design.getId();
      String msg = id + " does not support a parameter with ID " + paramId;
      throw new IllegalArgumentException(msg);
    }

    return fromPredicate(x -> {
      try {
        design.setValue(paramId, x);
        return true;
      } catch(Exception e) {
        return false;
      }
    });
  }
}
