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
import java.util.stream.Stream;

/**
 * A Code Mapping configuration.
 * This class is immutable.
 *
 * @author Christoffer Fink
 */
public class CodeMappingCfg {
  /** The default ID used by the builder. */
  public static final String DEFAULT_ID = "TestMapping";

  private final String id;
  private final Collection<MappingCfg> mappings;

  private CodeMappingCfg(String id, Collection<MappingCfg> mappings) {
    this.id = id;
    this.mappings = new ArrayList<>(mappings);
  }

  public String getId() {
    return id;
  }

  public Stream<MappingCfg> getMappings() {
    return mappings.stream();
  }

  /** Returns a builder that can create instances of this class. */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder of Code Mapping configurations. Uses a default ID.
   * @version 1.0
   * @author Christoffer Fink
   */
  public static class Builder {
    private String id = DEFAULT_ID;
    private Collection<MappingCfg> mappings = new ArrayList<>();

    /** Returns a builder with the id set to {@code id}. */
    public Builder id(String id) {
      this.id = id;
      return this;
    }

    /** Returns a builder containing these mappings. */
    public Builder mapping(MappingCfg ... mappings) {
      for (MappingCfg mapping : mappings) {
        this.mappings.add(mapping);
      }
      return this;
    }

    /** Builds a configuration based on the current builder state. */
    public CodeMappingCfg build() {
      return new CodeMappingCfg(id, mappings);
    }
  }
}
