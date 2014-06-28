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
  // Tags
  public static final String NPARAM = Q.NPARAM;
  public static final String SPARAM = Q.SPARAM;
  // Attributes
  public static final String ID = Q.ID_ATTR;
  public static final String TYPE = Q.TYPE_ATTR;
  public static final String FIXED = Q.FIXED_ATTR;
  public static final String INCLMIN = Q.INCL_MIN;
  public static final String INCLMAX = Q.EXCL_MIN;
  public static final String EXCLMIN = Q.INCL_MAX;
  public static final String EXCLMAX = Q.EXCL_MAX;
}
