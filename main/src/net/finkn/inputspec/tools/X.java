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

import se.miun.itm.input.util.Q;

/** Contains XML-related constants. */
public class X {
  public static final String PREFIX = Q.MY_NAMESPACE_PREFIX;
  public static final String INPUT_URL = "http://theinput.org";
  public static final String NS = "http://www.w3.org/2001/XMLSchema-instance";
  public static final String XML_XSI = "xmlns:xsi";
  public static final String SCHEMA = "xsi:schemaLocation";
  public static final String INPUT_XMLNS = "xmlns:" + PREFIX;
  // Tags
  public static final String NPARAM = Q.NPARAM;
  public static final String SPARAM = Q.SPARAM;
  public static final String SCHOICE = Q.SCHOICE;
  public static final String DESIGN_SPACE = Q.DESIGN_SPACE_ROOT;
  public static final String CODE_MAPPING = Q.MAPPINGS;
  // Attributes
  public static final String ID = Q.ID_ATTR;
  public static final String TYPE = Q.TYPE_ATTR;
  public static final String FIXED = Q.FIXED_ATTR;
  public static final String INCLMIN = Q.INCL_MIN;
  public static final String EXCLMIN = Q.EXCL_MIN;
  public static final String INCLMAX = Q.INCL_MAX;
  public static final String EXCLMAX = Q.EXCL_MAX;
  public static final String MAPPING_REF = Q.REF_ATTR;
  public static final String ADD = Q.ADD_ATTR;
  public static final String CONSTRUCTOR = Q.CONSTR_ATTR;
  public static final String GET = Q.GET_ATTR;
  public static final String SET = Q.SET_ATTR;
  public static final String COMPLEX = Q.COMPLEX;
  public static final String MAPPING = Q.MAPPING;
  public static final String MAPPING_TYPE = Q.MAPPING_TYPE;
  public static final String WRAPPER = Q.WRAPPER;

  public static final String SPACE_NS = Q.DESIGN_SPACE_NAMESPACE_ID;
  public static final String SPACE_SCHEMA = getSpaceSchema();
  public static final String CODE_MAPPING_NS = Q.NAMESPACE_ID + CODE_MAPPING;
  public static final String CODE_MAPPING_SCHEMA = getCodeMappingSchema();


  private static String getSpaceSchema() {
    return getSchema(SPACE_NS, DESIGN_SPACE);
  }
  private static String getCodeMappingSchema() {
    return getSchema(CODE_MAPPING_NS, CODE_MAPPING);
  }
  private static String getSchema(String ns, String root) {
    return String.format("%s %s/%s.xsd", ns, INPUT_URL, root);
  }
}
