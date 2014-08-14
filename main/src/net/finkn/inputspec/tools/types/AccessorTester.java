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
package net.finkn.inputspec.tools.types;

/**
 * Like {@link InitTester}, but this class also has default accessors.
 * That is, it has accessors with those names that InPUT4j would infer
 * if the inner parameter corresponding to the "data" field in this
 * class were called "Data".
 * 
 * @author Christoffer Fink
 * @see net.finkn.inputspec.v050.AccessorNamingTest
 */
public class AccessorTester extends InitTester {

  public AccessorTester() {
  }

  public AccessorTester(int data) {
    super(data);
  }

  public int getData() {
    return super.customGetMethod();
  }
  public void setData(int data) {
    super.customSetMethod(data);
  }
}
