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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An individual mapping.
 * The code mapping configuration consists of multiple mapping elements.
 * This class represents a single such mapping.
 * <p>
 * Note that there is great potential for confusion. Each mapping has a type,
 * which is (usually) the fully qualified class name of the type of an SParam.
 * However, there are multiple kinds of mappings, such as a Wrapper. Do not
 * confuse the {@link MappingCfg.Type type} of a mapping with the
 * {@link #getType() type} attribute of the mapping. Furthermore, do not
 * confuse "type" and "MappingType".
 * <p>
 * This class is immutable.
 *
 * @version 1.0
 * @author Christoffer Fink
 */
// FIXME: Support both
// Mapping + (Wrapper|Complex) and
// MappingType + (Wrapper|Complex)
public class MappingCfg {
  /**
   * Separator used when joining together parameter IDs generate a constructor
   * attribute.
   */
  public static final String DELIM = " ";

  private final String id;
  private final String add;
  private final String constructor;
  private final String get;
  private final String set;
  private final String type;
  private final Type mappingType;
  private final Xml xml = Xml.getInstance().prefix(X.PREFIX);

  private MappingCfg(String id, String add, String con, String get, String set,
      String type, Type mt) {
    this.id = id;
    this.add = add;
    this.constructor = con;
    this.get = get;
    this.set = set;
    this.type = type;
    this.mappingType = mt;
  }

  public String getId() {
    return id;
  }

  public String getAdd() {
    return add;
  }

  public String getConstructor() {
    return constructor;
  }

  public String getGet() {
    return get;
  }

  public String getSet() {
    return set;
  }

  public String getType() {
    return type;
  }

  public Type getMappingType() {
    return mappingType;
  }

  public String xml() {
    return xml(0);
  }
  String xml(int level) {
    return xml.level(level).e(getTag(), getAttributes(), getChildren(level));
  }

  // TODO: This Mapping + (Wrapper|Complex) business is pretty awkward.
  private String getTag() {
    return mappingType.equals(Type.MAPPING_TYPE) ? X.MAPPING_TYPE : X.MAPPING;
  }
  private Map<String, Optional<? extends Object>> getAttributes() {
    Map<String, Optional<? extends Object>> attrib = new HashMap<>();
    attrib.put(X.ID, Optional.ofNullable(getId()));
    attrib.put(X.TYPE, Optional.ofNullable(getType()));
    attrib.put(X.ADD, Optional.ofNullable(getAdd()));
    attrib.put(X.CONSTRUCTOR, Optional.ofNullable(getConstructor()));
    attrib.put(X.GET, Optional.ofNullable(getGet()));
    attrib.put(X.SET, Optional.ofNullable(getSet()));
    return attrib;
  }
  private List<String> getChildren(int level) {
    if (mappingType.equals(Type.WRAPPER) || mappingType.equals(Type.COMPLEX)) {
      String tag = mappingType.equals(Type.COMPLEX) ? X.COMPLEX : X.WRAPPER;
      return Arrays.asList(xml.level(level + 1).e(tag));
    }
    return Collections.emptyList();
  }

  /** Returns a builder that can create instances of this class. */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder of mapping configurations.
   * As with the other configuration builders, each attribute can be set
   * directly to arbitrary strings. In addition, there are some convenience
   * methods that can infer some settings based on the arguments.
   * <p>
   * Both Mapping and MappingType can point at an actual class name, but only
   * a Mapping can point to a MappingType. So when the type is given as a
   * Class object, either a Mapping or MappingType could work. The builder
   * assumes that a Mapping is intended and therefore also takes a ParamCfg.
   * This makes it easy to create a direct mapping from a parameter ID to a
   * class. When the type is given as a MappingCfg, a Mapping is produced.
   * <p>
   * The only default assumption made by this builder is that it should
   * build a Mapping. All other information must be set manually.
   *
   * @see #infer(ParamCfg, Class)
   * @see #infer(ParamCfg, MappingCfg)
   * @author Christoffer Fink
   */
  public static class Builder {
    private String id;
    private String add;
    private String constructor;
    private String get;
    private String set;
    private String type;
    private Type mappingType = Type.MAPPING;

    /**
     * Returns a Builder with the id set to {@code id}.
     * @see #param(ParamCfg)
     */
    public Builder id(String id) {
      this.id = id;
      return this;
    }

    /** Returns a Builder with an updated add method. */
    public Builder add(String method) {
      this.add = method;
      return this;
    }

    /**
     * Returns a Builder with an updated {@code constructor} attribute.
     * The string lists parameter IDs separated by whitespace, such as "X Y Z".
     * @param parameters
     *    a string listing nested parameters to be initialized by constructor
     * @see #nested(ParamCfg...)
     * @see #nested(Stream)
     */
    public Builder constructor(String parameters) {
      this.constructor = parameters;
      return this;
    }

    /** Returns a Builder with an updated getter method. */
    public Builder get(String method) {
      this.get = method;
      return this;
    }

    /** Returns a Builder with an updated setter method. */
    public Builder set(String method) {
      this.set = method;
      return this;
    }

    /**
     * Returns a Builder with an updated type string.
     * @see #target(Class)
     * @see #target(MappingCfg)
     */
    public Builder type(String type) {
      this.type = type;
      return this;
    }

    /** Convenience method for setting the type based on a Class object. */
    public Builder target(Class<?> type) {
      return type(type.getName());
    }

    /** Convenience method for setting the type to point to a MappingType. */
    public Builder target(MappingCfg type) {
      return type(type.getId());
    }

    /** Convenience method for setting the ID to match the parameter. */
    public Builder param(ParamCfg param) {
      this.id = param.getId();
      return this;
    }

    /** Convenience method for setting a constructor string. */
    public Builder nested(ParamCfg ... parameters) {
      return nested(Stream.of(parameters));
    }

    /** Convenience method for setting a constructor string. */
    public Builder nested(Stream<ParamCfg> parameters) {
      String tmp = String.join(DELIM, parameters
        .map(ParamCfg::getId)
        .collect(Collectors.toList())
      );
      return constructor(tmp);
    }

    /**
     * Returns a builder that is set to produce a Mapping that maps the
     * given parameter to the given type.
     */
    public Builder infer(ParamCfg param, Class<?> type) {
      return this.mapping().param(param).target(type);
    }

    /**
     * Returns a builder that is set to produce a Mapping that maps the
     * given parameter to the given MappingType.
     */
    public Builder infer(ParamCfg param, MappingCfg type) {
      if (!type.getMappingType().equals(Type.MAPPING_TYPE)) {
        String msg = "Wrong mapping type. "
          + "Only MappingType mappings can be referenced.";
        throw new IllegalArgumentException(msg);
      }
      return this.mapping().param(param).target(type);
    }

    /** Configures the builder for building a regular Mapping. */
    public Builder mapping() {
      this.mappingType = Type.MAPPING;
      return this;
    }

    /** Configures the builder for building a MappingType mapping. */
    public Builder mappingType() {
      this.mappingType = Type.MAPPING_TYPE;
      return this;
    }

    /** Configures the builder for building a Complex mapping. */
    public Builder complex() {
      this.mappingType = Type.COMPLEX;
      return this;
    }

    /** Configures the builder for building a Wrapper mapping. */
    public Builder wrapper() {
      this.mappingType = Type.WRAPPER;
      return this;
    }

    /** Builds a mapping configuration based on the current builder state. */
    public MappingCfg build() {
      if (mappingType.equals(Type.COMPLEX)) {
        return new MappingCfg(id, add, null, null, null, type, mappingType);
      }
      return new MappingCfg(id, null, constructor, get, set, type, mappingType);
    }
  }

  /** The different kinds of mapping elements that make up a code mapping. */
  public enum Type {
    MAPPING, MAPPING_TYPE, WRAPPER, COMPLEX,
  }
}
