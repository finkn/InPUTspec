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
 * A tool for examining the relationship between parameter initialization
 * and object instantiation.
 * The class keeps track of the number of instances as well as invocations
 * of the accessors.
 * 
 * @author Christoffer Fink
 */
public class InitTester {

  public static final String SETTER = "customSetMethod";
  public static final String GETTER = "customGetMethod";

  private static int globalSetterCount = 0;
  private static int globalGetterCount = 0;
  private static int instanceCount = 0;

  private int getterInvocations = 0;
  private int setterInvocations = 0;
  // The data is public to provide a back door so that it can be
  // examined without invoking the accessors.
  public int data = 0;

  public InitTester() {
    instanceCount++;
  }

  public InitTester(int data) {
    this.data = data;
    instanceCount++;
  }

  public int customGetMethod() {
    getterInvocations++;
    globalGetterCount++;
    return data;
  }
  public void customSetMethod(int data) {
    setterInvocations++;
    globalSetterCount++;
    this.data = data;
  }

  public int getGetterInvocations() {
    return getterInvocations;
  }
  public int getSetterInvocations() {
    return setterInvocations;
  }

  public static int getGlobalGetterCount() {
    return globalGetterCount;
  }
  public static int getGlobalSetterCount() {
    return globalSetterCount;
  }

  public static int getInstanceCount() {
    return instanceCount;
  }

  public static void resetGlobal() {
    globalSetterCount = 0;
    globalGetterCount = 0;
  }
}
