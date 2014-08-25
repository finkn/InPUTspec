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

import net.finkn.inputspec.tools.X;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A parameter configuration. This class is immutable.
 *
 * @version 1.0
 * @author Christoffer Fink
 */
public class ParamCfg {
  /** Default parameter ID used by the Builder. */
  public static final String DEFAULT_ID = "X";
  /** Default parameter type used by the Builder. */
  public static final String DEFAULT_TYPE = "integer";

  private static final ParamCfg defaultParam = builder().build();

  // TODO: Use Optionals?
  private final String id;
  private final String type;
  private final String fixed;
  private final Range range;
  private final Collection<ParamCfg> nested;
  private final ParamType paramType;
  private final Xml xml = Xml.getInstance().prefix(X.PREFIX);

  private ParamCfg(String id, String type, String fixed, Range range,
      Collection<ParamCfg> nested, ParamType paramType) {
    this.id = id;
    this.type = type;
    this.fixed = fixed;
    this.range = range;
    this.nested = new ArrayList<>(nested);
    this.paramType = paramType;

    if (range == null) {
      throw new IllegalArgumentException("null Range is illegal.");
    }
  }

  public String getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public String getFixed() {
    return fixed;
  }

  public Range getRange() {
    return range;
  }

  public Stream<ParamCfg> getNested() {
    return nested.stream();
  }

  /** Returns a builder that can create instances of this class. */
  public static Builder builder() {
    return new Builder();
  }

  /** Shortcut for {@code builder().build()}. **/
  public static ParamCfg getDefault() {
    return defaultParam;
  }

  public String xml() {
    return xml(0);
  }

  String xml(int level) {
    return xml.level(level).e(getTag(), getAttributes(), getChildren(level));
  }

  private String getTag() {
    if (paramType.equals(ParamType.CHOICE)) {
      return X.SCHOICE;
    }
    return paramType.equals(ParamType.NUMERIC) ? X.NPARAM : X.SPARAM;
  }
  private Map<String, Optional<? extends Object>> getAttributes() {
    Map<String, Optional<? extends Object>> attrib = new HashMap<>();
    attrib.put(X.ID, Optional.ofNullable(getId()));
    attrib.put(X.TYPE, Optional.ofNullable(getType()));
    attrib.put(X.FIXED, Optional.ofNullable(getFixed()));
    attrib.put(X.INCLMIN, range.inclMin());
    attrib.put(X.EXCLMIN, range.exclMin());
    attrib.put(X.INCLMAX, range.inclMax());
    attrib.put(X.EXCLMAX, range.exclMax());
    return attrib;
  }
  private List<String> getChildren(int level) {
    return getNested().map(x -> x.xml(level + 1)).collect(Collectors.toList());
  }

  /**
   * Builder of parameter configurations.
   * The defaults will create the simplest possible parameter. The default
   * parameter is an integer NParam with id "X" and no limits. The
   * builder makes some unsafe assumptions in order to simplify testing as much
   * as possible. In other words, it will not hold the user's hand and attempt
   * to prevent invalid configurations.
   * <p>
   * When creating a structured parameter, any range is ignored. When creating
   * a numeric parameter, any nested parameters are ignored.
   * <p>
   * There are three ways to add a nested parameter to the builder:
   * <ol>
   *   <li>{@code builder.add(param1, param2, ..., paramN)}</li>
   *   <li>{@code builder.add()}</li>
   *   <li>{@code builder.nest()}</li>
   * </ol>
   * {@link #add(ParamCfg...)} adds a given parameter. {@link #add()} constructs
   * a new parameter using the current state of the builder, and then adds
   * that parameter to the collection of nested parameters. {@link #nest()} is
   * the most advanced version. It works like {@link #add()} in that it
   * constructs a new parameter and adds it to the collection of nested
   * parameters. However, the new parameter absorbs the currently nested
   * parameters. This means that the new parameter replaces the old collection.
   * This seems like the natural way to build a hierarchy of nested parameters.
   * As a rule of thumb, {@link #add()} is suitable for numeric parameters,
   * while {@link #nest()} is suitable for structured parameters.
   * <p>
   * Note: the builder should be used under the assumption that it is immutable.
   * That is, the builder that is returned by a method invocation should be
   * assumed to be a new instance. The recommended way to use the builder is to
   * chain multiple calls:
   * {@code builder.id("X").inclMin("5").build()}
   *
   * @version 1.0
   * @author Christoffer Fink 
   */
  public static class Builder {
    private final static List<ParamCfg> EMPTY_LIST = Collections.emptyList();
    private String id = ParamCfg.DEFAULT_ID;
    private String type;
    private String fixed;
    private final List<ParamCfg> nested = new ArrayList<>();
    private ParamType paramType = ParamType.NUMERIC;
    private Range range = Range.EMPTY;

    private Builder() {
    }

    /** Returns a Builder with the id set to {@code id}. */
    public Builder id(String id) {
      this.id = id;
      return this;
    }

    /** Returns a Builder with the type set to {@code type}. */
    public Builder type(String type) {
      this.type = type;
      return this;
    }

    /** Returns a Builder with a fixed value of {@code value}. */
    public Builder fixed(Object value) {
      this.fixed = getObjectString(value);
      return this;
    }

    /** Returns a Builder with an updated range. */
    public Builder inclMin(Object limit) {
      this.range = range.withInclMin(getObjectString(limit));
      return this;
    }

    /** Returns a Builder with an updated range. */
    public Builder exclMin(Object limit) {
      this.range = range.withExclMin(getObjectString(limit));
      return this;
    }

    /** Returns a Builder with an updated range. */
    public Builder inclMax(Object limit) {
      this.range = range.withInclMax(getObjectString(limit));
      return this;
    }

    /** Returns a Builder with an updated range. */
    public Builder exclMax(Object limit) {
      this.range = range.withExclMax(getObjectString(limit));
      return this;
    }

    /** Returns a Builder with an updated range. */
    public Builder interval(String interval) {
      this.range = range.withInterval(interval);
      return this;
    }

    /** Configures the builder for building numeric parameters. */
    public Builder numeric() {
      paramType = ParamType.NUMERIC;
      return this;
    }

    /** Configures the builder for building structured parameters. */
    public Builder structured() {
      paramType = ParamType.STRUCTURED;
      return this;
    }

    /** Configures the builder for building choice parameters. */
    public Builder choice() {
      paramType = ParamType.CHOICE;
      return this;
    }

    /**
     * Resets everything except for nested parameters. This behavior allows a
     * parameter to accumulate nested parameters (using {@code #add()}) without
     * forcing the user to manually reset or override each attribute.
     */
    public Builder resetAttributes() {
      this.range = Range.getInstance();
      return id(DEFAULT_ID).type(null).interval(null);
    }

    /**
     * Adds these parameters to the nested parameters.
     *
     * @see #add()
     */
    public Builder add(ParamCfg... params) {
      for (ParamCfg param : params) {
        nested.add(param);
      }
      return this;
    }

    /**
     * Adds the current parameter configuration to the nested parameters.
     * {@code builder.add()} has the same effect as
     * {@code builder.add(builder.build())}.
     *
     * @see #nest()
     */
    public Builder add() {
      return add(build());
    }

    /**
     * Adds the current parameter configuration to the nested parameters,
     * replacing any existing nested parameters.
     * This method is useful for building a structured parameter from the inside
     * out.
     *
     * @see #add()
     */
    public Builder nest() {
      ParamCfg param = build();
      nested.clear();
      nested.add(param);
      return this;
    }

    /** Creates a ParamCfg based on the current builder state. */
    public ParamCfg build() {
      if (paramType == ParamType.NUMERIC) {
        String npType = getNParamType(type);
        return new ParamCfg(id, npType, fixed, range, EMPTY_LIST, paramType);
      } else {
        return new ParamCfg(id, type, fixed, Range.EMPTY, nested, paramType);
      }
    }

    private static String getObjectString(Object obj) {
      return obj != null ? obj.toString() : null;
    }

    private static String getNParamType(String type) {
      return type != null ? type : DEFAULT_TYPE;
    }
  }

  private enum ParamType {
    NUMERIC, STRUCTURED, CHOICE,
  }
}
