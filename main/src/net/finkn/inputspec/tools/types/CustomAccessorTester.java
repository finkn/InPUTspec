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
 * A simple class for testing custom accessors in the code mapping.
 * When an accessor is invoked, a counter is increased.
 * Here an "accessor" means either get or set method.
 * <p>
 * In contrast to {@link AccessorTester}, this class has only one pair of
 * accessor methods. This class only uses a custom naming scheme, which means
 * that data can't be initialized through a default accessor by accident.
 * 
 * @author Christoffer Fink
 */
public class CustomAccessorTester {
  private int getterInvocations = 0;
  private int setterInvocations = 0;
  // The data is public to provide a back door so that it can be
  // examined without invoking the accessors.
  public int data = 0;

  public CustomAccessorTester() { }

  public CustomAccessorTester(int data) {
    this.data = data;
  }

  public int customGetMethod() {
    getterInvocations++;
    return data;
  }
  public void customSetMethod(int data) {
    setterInvocations++;
    this.data = data;
  }

  public int getGetterInvocations() {
    return getterInvocations;
  }
  public int getSetterInvocations() {
    return setterInvocations;
  }
}