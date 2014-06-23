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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class GeneratorTest {
  // ------------------------------------------------------------------------
  // ----- Assert variability tests. -----
  // ------------------------------------------------------------------------
  @Test(expected = IllegalArgumentException.class)
  public void assertVariabilityWithOneIterationIsIllegal() {
    Generator.fromSeq(1, 2, 3).limit(1).isVariable();
  }

  @Test(expected = AssertionError.class)
  public void assertVariabilityWithConstantValueShouldFail() {
    Generator.fromSeq(1).isVariable();
  }

  /** Demonstrates false negative. */
  @Test(expected = AssertionError.class)
  public void assertVariabilityFailsIfNotEnoughIterations() {
    Generator.fromSeq(1, 1, 1, 1, 2).limit(4).isVariable();
  }

  @Test
  public void assertVariabilityWithDifferentValuesShouldSucceed() {
    Generator.fromSeq(1, 1, 1, 1, 2).limit(5).isVariable();
  }

  // ------------------------------------------------------------------------
  // ----- Assert constancy tests. -----
  // ------------------------------------------------------------------------
  @Test(expected = IllegalArgumentException.class)
  public void assertConstancyWithOneIterationIsIllegal() {
    Generator.fromSeq(1).limit(1).isConstant();
  }

  @Test(expected = AssertionError.class)
  public void assertConstancyWithDifferentValuesShouldFail() {
    Generator.fromSeq(1, 1, 1, 1, 2).limit(5).isConstant();
  }

  @Test
  public void assertConstancyWithConstantValuesShouldSucceed() {
    Generator.fromSeq(1).isConstant();
  }

  /** Demonstrates false positive. */
  @Test
  public void assertConstancySucceedsIfNotEnoughIterations() {
    Generator.fromSeq(1, 1, 1, 1, 2).limit(4).isConstant();
  }

  // ------------------------------------------------------------------------
  // ----- Assert generated values match tests. -----
  // ------------------------------------------------------------------------
  @Test(expected = AssertionError.class)
  public void assertGeneratesAnyShouldFailIfNoneMatch() {
    Generator.fromSeq(6, 7, 8, 9).generatesAny(1, 2, 3, 4, 5);
  }

  @Test
  public void assertGeneratesAnyShouldSucceedIfAnyMatch() {
    Generator.fromSeq(2).generatesAny(1, 2, 3, 4, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void assertGeneratesAllWithTooFewIterationsIsIllegal() {
    List<Integer> expected = Arrays.asList(1, 2, 3);
    int len = expected.size() - 1;
    Generator.fromSeq(expected).limit(len).generatesAll(expected);
  }

  @Test(expected = AssertionError.class)
  public void assertGeneratesAllShouldFailIfAnyMissing() {
    Generator.fromSeq(1, 2, 3, 4).generatesAll(1, 2, 3, 4, 5);
  }

  /** Demonstrates false negative. */
  @Test(expected = AssertionError.class)
  public void assertGeneratesAllFailsIfNotEnoughIterations() {
    List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
    List<Integer> values = Arrays.asList(1, 1, 2, 2, 4, 4, 5, 5, 5, 3);
    int iterations = values.size() - 1;
    Generator.fromSeq(values).limit(iterations).generatesAll(expected);
  }

  @Test
  public void assertGeneratesAllShouldSucceedIfAllAreMatched() {
    Generator.fromSeq(3, 1, 2).generatesAll(1, 2);
  }

  @Test(expected = AssertionError.class)
  public void assertGeneratesOnlyShouldFailIfAnyMismatch() {
    Generator.fromSeq(1, 2, 3).generatesOnly(1, 2);
  }

  /** Demonstrates false positive. */
  @Test
  public void assertGeneratesOnlySucceedsIfNotEnoughIterations() {
    List<Integer> expected = Arrays.asList(1, 2, 3);
    Generator.fromSeq(1, 2, 3, 4).limit(2).generatesOnly(expected);
  }

  @Test
  public void assertGeneratesOnlyShouldSucceedIfNoMismatch() {
    Generator.fromSeq(3, 4, 3, 4, 5, 6, 5, 6).generatesOnly(1, 2, 3, 4, 5, 6,
        7, 8, 9, 10);
  }

  // ------------------------------------------------------------------------
  // ----- Generator from sequence tests. -----
  // ------------------------------------------------------------------------
  @Test
  public void finiteGeneratorFromSequenceShouldProduceTheSequence() {
    List<Integer> seq = Arrays.asList(0, 1, 2, 3);
    Generator<Integer> gen = Generator.finiteFromSeq(seq);
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < seq.size(); i++) {
      result.add(gen.get());
    }
    assertEquals(seq, result);
  }

  @Test(expected = IllegalStateException.class)
  public void finiteGeneratorFromSequenceShouldThrowExceptionWhenExhausted() {
    List<Integer> seq = Arrays.asList(0, 1, 2, 3);
    Generator<Integer> gen = Generator.finiteFromSeq(seq);
    for (int i = 0; i < seq.size() + 1; i++) {
      gen.get();
    }
  }

  @Test
  public void infiniteGeneratorFromSequenceShouldWrapAround() {
    List<Integer> seq = Arrays.asList(0, 1, 2, 3);
    List<Integer> expected = new ArrayList<>();
    expected.addAll(seq);
    expected.addAll(seq);
    expected.addAll(seq);
    Generator<Integer> gen = Generator.fromSeq(seq);
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < expected.size(); i++) {
      result.add(gen.get());
    }
    assertEquals(expected, result);
  }

  @Test
  public void generatorFromSupplierShouldReturnValuesFromSupplier() {
    Integer expected = 3;
    Generator<Integer> gen = Generator.fromSupplier(() -> expected);
    assertEquals(expected, gen.get());
  }

  @Test
  public void valuesGeneratedShouldMatchTheNumberOfCallsToGet() {
    Generator<Integer> gen = Generator.fromSeq(1, 2, 3);
    assertEquals(0, gen.valuesGenerated());
    gen.get();
    gen.get();
    assertEquals(2, gen.valuesGenerated());
  }
}
