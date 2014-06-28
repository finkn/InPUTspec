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

import static net.finkn.inputspec.tools.Unit.*;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class XmlTest {
  private final String tag = "tag name";
  private final String prefix = "prefix";
  private final String fullTag = prefix + ":" + tag;
  private final Xml xml = Xml.getInstance().prefix(prefix);

  @Test
  public void factoryMethodShouldReturnInstance() {
    assertNotNull(Xml.getInstance());
  }

  @Test
  public void settingPrefixShouldReturnNewInstance() {
    Xml without = Xml.getInstance();
    Xml with = without.prefix(prefix);
    assertNotSame(with, without);
  }

  @Test
  public void settingLevelShouldReturnNewInstance() {
    Xml without = Xml.getInstance();
    Xml with = without.level(1);
    assertNotSame(with, without);
  }

  @Test
  public void defaultIndentationLevelShouldBeZero() {
    assertFalse(isIndented(xml.e(tag)));
  }

  @Test
  public void prefixShouldNotBeUsedUnlessSet() {
    Xml xml = Xml.getInstance();
    String result = xml.e(tag);
    String expected = "<" + tag + " />";
    assertEquals(expected, result);
  }

  @Test
  public void elementTagShouldMatch() {
    String result = xml.e(tag);
    String expected = "<" + fullTag + " />";
    assertEquals(expected, result);
  }

  @Test
  public void multipleAttributesShouldAllBeIncluded() {
    HashMap<String, Optional<? extends Object>> attrib = new HashMap<>();
    attrib.put("fixed", Optional.of("123"));
    attrib.put("inclMax", Optional.of("321"));
    attrib.put("type", Optional.of("integer[2][3]"));

    String result = xml.e(tag, attrib);
    String attribPair = "\\w+=\"\\S+\"";
    String regex = String.format("<%s %2$s %2$s %2$s />", fullTag, attribPair);
    assertTrue(result + " did not match " + regex, result.matches(regex));
    attrib.forEach((k, v) -> {
      assertTrue(result.contains(k));
      assertTrue(result.contains(v.get().toString()));
    });
  }

  @Test
  public void childrenShouldBeIncluded() {
    String child1 = "hello";
    String child2 = "world";
    String result = xml.e(tag, Arrays.asList(child1, child2));
    String format = "<%1$s>\n%2$s\n%3$s\n</%1$s>";
    String regex = String.format(format, fullTag, child1, child2);
    assertTrue(result + " did not match " + regex, result.matches(regex));
  }

  @Test
  public void attributesAndChildrenShouldBeIncluded() {
    String name = "name";
    String value = "value";
    String child = "child";
    HashMap<String, Optional<? extends Object>> attrib = new HashMap<>();
    attrib.put(name, Optional.of(value));
    String result = xml.e(tag, attrib, Arrays.asList(child));

    String format = "<%s %s=\"%s\">\n%s\n</%s>";
    String regex = String.format(format, fullTag, name, value, child, fullTag);
    assertTrue(result + " did not match " + regex, result.matches(regex));
  }

  @Test
  public void shouldUseClosedOpeningTagWhenListOfChildrenIsEmpty() {
    String result = xml.e(tag, Arrays.asList());
    String expected = "<" + fullTag + " />";
    assertEquals(expected, result);
  }

  @Test
  public void settingLevelShouldControlIndentation() {
    String result = xml.level(1).e(tag);
    assertTrue(isIndented(result));
  }

  @Test
  public void prefixAndLevelSettingsShouldCoexist() {
    String result = xml.level(1).prefix(prefix).e(tag);
    assertTrue(isIndented(result));
    assertStringContainsAll(result, prefix);
  }

  @Test(expected = IllegalArgumentException.class)
  public void settingNegativeLevelShouldBeIllegal() {
    xml.level(-1);
  }

  private boolean isIndented(String s) {
    return s.matches("^\\s.*");
  }
}
