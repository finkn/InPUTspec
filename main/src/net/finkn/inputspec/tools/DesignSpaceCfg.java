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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A Design Space configuration.
 * This class is immutable.
 *
 * @author Christoffer Fink
 */
public class DesignSpaceCfg {
  public static final String DEFAULT_ID = "TestSpace";

  private final Optional<String> id;
  private final Optional<String> mappingRef;
  private final Optional<MappingCfg> mapping;
  private final Collection<ParamCfg> parameters;

  private DesignSpaceCfg(String id, String ref, MappingCfg mapping,
      Collection<ParamCfg> params) {
    this.id = Optional.ofNullable(id);
    this.mappingRef = Optional.ofNullable(ref);
    this.mapping = Optional.ofNullable(mapping);
    this.parameters = new ArrayList<>(params);
  }

  public Optional<String> getId() {
    return id;
  }

  public Optional<MappingCfg> getMapping() {
    return mapping;
  }

  public Optional<String> getMappingRef() {
    return mappingRef;
  }

  public Stream<ParamCfg> getParameters() {
    return parameters.stream();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String id = DEFAULT_ID;
    private String ref;
    private MappingCfg mapping;
    private Collection<ParamCfg> params = new ArrayList<>();

    private Builder() {
    }

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder ref(String ref) {
      this.ref = ref;
      return this;
    }

    public Builder mapping(MappingCfg mapping) {
      this.mapping = mapping;
      return this;
    }

    public Builder param(ParamCfg param) {
      params.add(param);
      return this;
    }

    public Builder param(ParamCfg... parameters) {
      for (ParamCfg param : parameters) {
        params.add(param);
      }
      return this;
    }

    public DesignSpaceCfg build() {
      return new DesignSpaceCfg(id, ref, mapping, params);
    }
  }
}
