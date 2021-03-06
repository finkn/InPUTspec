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
package net.finkn.inputspec.v050;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * A suite of all specification tests for version 0.5.
 * @author Christoffer Fink
 */
@RunWith(Suite.class)
@SuiteClasses({
    AccessorNamingTest.class,
    AccessorTest.class,
    AdvancedSetValueTest.class,
    AdvancedSingleRangeNextTest.class,
    AdvancedMultiRangeNextTest.class,
    ArrayTest.class,
    BasicDesignSpaceTest.class,
    BasicDesignTest.class,
    BooleanLiteralsTest.class,
    CodeMappingCachingTest.class,
    DesignCachingTest.class,
    DesignSpaceCachingTest.class,
    DuplicateIdTest.class,
    ExtendScopeTest.class,
    FixedNumericTest.class,
    IdLiteralsTest.class,
    IllegalConfigTest.class,
    LegalConfigTest.class,
    MultiRangeMismatchTest.class,
    NestedDependencyTest.class,
    ReadOnlyDesignTest.class,
    SetFixedTest.class,
    SetValueTest.class,
    SimpleMultiRangeNextTest.class,
    SimpleSingleRangeNextTest.class,
    SparamInitTest.class,
    SparamTest.class,
    SupportedParamIdsTest.class,
    TypeMismatchTest.class,
    ValueConsistencyTest.class,
})
public class Spec {
}
