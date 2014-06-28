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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Map;

public class Xml {
  private final static String DEFAULT_INDENT = "  ";

  private final Optional<String> prefix;
  private final String indentation = DEFAULT_INDENT;
  private final String indent;
  private final int level;

  private Xml(Optional<String> prefix, int level) {
    this.prefix = prefix;
    this.level = level;
    this.indent = getIndent(indentation, level);
  }

  public String e(String tag) {
    return e(tag, Collections.emptyMap(), Collections.emptyList());
  }

  public String e(String tag, Map<String, Optional<? extends Object>> attrib) {
    return e(tag, attrib, Collections.emptyList());
  }

  public String e(String tag, List<String> children) {
    return e(tag, Collections.emptyMap(), children);
  }

  public String e(String tag, Map<String, Optional<? extends Object>> attrib,
      List<String> children) {
    tag = getPrefixed(tag);
    StringBuilder sb = new StringBuilder();
    sb.append(getHead(tag, attrib));
    sb.append(getTail(tag, children));
    return sb.toString();
  }

  public static Xml getInstance() {
    return new Xml(Optional.ofNullable(null), 0);
  }

  public Xml prefix(String prefix) {
    return new Xml(Optional.ofNullable(prefix), level);
  }

  public Xml level(int level) {
    if (level < 0) {
      String msg = "Cannot set negative indentation level";
      throw new IllegalArgumentException(msg);
    }
    return new Xml(prefix, level);
  }

  private String getPrefixed(String tag) {
    return prefix.isPresent() ? prefix.get() + ":" + tag : tag;
  }

  private String getHead(String tag, 
      Map<String, Optional<? extends Object>> attrib) {
    return indent + "<" + tag + getAttributes(attrib);
  }

  private String getTail(String tag, List<String> children) {
    if (children.isEmpty()) {
      return " />";
    } else {
      return getTailWithChildren(tag, children);
    }
  }

  private String getTailWithChildren(String tag, List<String> children) {
    StringBuilder sb = new StringBuilder();
    sb.append(">\n");
    children.forEach(s -> sb.append(s).append("\n"));
    sb.append(indent).append("</").append(tag).append(">");
    return sb.toString();
  }

  private static <T> String getAttributes(Map<String,
      Optional<? extends T>> attrib) {
    StringBuilder sb = new StringBuilder();
    attrib.forEach((k, v) -> sb.append(getAttribute(k, v)));
    return sb.toString();
  }

  private static <T> String getAttribute(String name, Optional<T> value) {
    String fmt = " %s=\"%s\"";
    return value.isPresent() ? String.format(fmt, name, value.get()) : "";
  }

  private static String getIndent(String indentation, int level) {
    String tmp = "";
    for (int i = 0; i < level; i++) {
      tmp += indentation;
    }
    return tmp;
  }
}
