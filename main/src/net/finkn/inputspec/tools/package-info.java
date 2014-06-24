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
/**
 * Contains tools intended for testing. Some are more or less general-purpose;
 * some are more or less special-purpose.
 * There are two major tools in this package, plus some supporting classes.
 * These are {@link net.finkn.inputspec.tools.Unit Unit} and various builders.
 *
 * <h2>Unit</h2>
 * {@link net.finkn.inputspec.tools.Unit Unit} contains more general-purpose
 * unit testing tools, such as custom assertions.
 *
 * <h2>Builders</h2>
 * Builders make it possible to build configurations in code and directly
 * create design spaces in the tests. In InPUT4, the only way to create a
 * design space is based on an XML document. There's no convenient way to build
 * such XML documents in code. So tests end up relying on external XML files.
 * <p>
 * The tests in InPUT4j solve the problem by using a single huge configuration
 * for testing. This has multiple drawbacks.
 * <p>
 * The tests in the DocTest project solve the problem by using many small files.
 * This solves some of the problems of the one-huge-configuration approach that
 * InPUT4j takes, but it introduces others.
 * <p>
 * The builders solve all of these problems. They make it easy to build
 * configurations for fast and specific tests. The information is right there in
 * the test in a readable form. The project does not get cluttered up with
 * a large number of XML files.
 *
 * <h3>Builder error checking</h3>
 * Because the purpose of this project is to probe InPUT4j to map its behavior,
 * tests cannot make any assumptions about how it should behave. In other
 * words, a configuration builder cannot know which configurations are valid;
 * finding that out is exactly what it will be used for. So builders cannot
 * impose rules on configurations. They have to allow strange configurations
 * so that InPUT4j's reaction can be observed. The only error checking they
 * can do is related to what makes sense to the builder itself.
 * <p>
 * Test writers who use the builders to simplify their tests need to keep this
 * in mind. Just because the builder is willing to produce a configuration
 * this does <strong>not</strong> mean that the configuration must therefore be
 * valid, or even legal!
 *
 * @author Christoffer Fink
 */
package net.finkn.inputspec.tools;
