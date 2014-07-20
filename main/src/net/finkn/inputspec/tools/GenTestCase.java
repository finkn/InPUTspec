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

import java.util.Optional;

public class GenTestCase {

  private static final GenTestCase instance = new GenTestCase(Optional.empty(),
    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
  private static final Object[] dummy = {};

  private final Optional<Generator<Object>> gen;
  private final Optional<Object[]> all;
  private final Optional<Object[]> only;
  private final Optional<Object[]> any;
  private final Optional<Object[]> none;

  private GenTestCase(Optional<Generator<Object>> gen,
      Optional<Object[]> all, Optional<Object[]> only,
      Optional<Object[]> any, Optional<Object[]> none) {
    this.gen = gen;
    this.all = all;
    this.only = only;
    this.any = any;
    this.none = none;
  }

  public static GenTestCase getInstance() {
    return instance;
  }

  public GenTestCase expected(Object ... values) {
    return all(values).only(values);
  }
  public GenTestCase all(Object ... values) {
    failIfPresent(all, "Already added an 'all' test.");
    return new GenTestCase(gen, Optional.of(values), only, any, none);
  }
  public GenTestCase only(Object ... values) {
    failIfPresent(only, "Already added an 'only' test.");
    return new GenTestCase(gen, all, Optional.of(values), any, none);
  }
  public GenTestCase any(Object ... values) {
    failIfPresent(any, "Already added an 'any' test.");
    return new GenTestCase(gen, all, only, Optional.of(values), none);
  }
  public GenTestCase none(Object ... values) {
    failIfPresent(none, "Already added a 'none' test.");
    return new GenTestCase(gen, all, only, any, Optional.of(values));
  }
  public GenTestCase gen(Generator<Object> gen) {
    failIfPresent(this.gen, "Already set a generator.");
    return new GenTestCase(Optional.of(gen), all, only, any, none);
  }

  public GenTestCase run() {
    if (!gen.isPresent()) {
      throw new IllegalStateException("Generator missing!");
    }
    failUnlessTestsPresent();
    return this
      .runGeneratesAllTests()
      .runGeneratesOnlyTests()
      .runGeneratesAnyTests()
      .runGeneratesNoneTests();
  }

  GenTestCase runGeneratesAllTests() {
    gen.get().generatesAll(all.orElse(dummy));
    return this;
  }
  GenTestCase runGeneratesOnlyTests() {
    if (only.isPresent()) {
      gen.get().generatesOnly(only.get());
    }
    return this;
  }
  GenTestCase runGeneratesAnyTests() {
    gen.get().generatesAny(any.orElse(dummy));
    return this;
  }
  GenTestCase runGeneratesNoneTests() {
    gen.get().generatesNone(none.orElse(dummy));
    return this;
  }

  private void failIfPresent(Optional<?> opt, String msg) {
    if (opt.isPresent()) {
      throw new IllegalStateException(msg);
    }
  }

  private void failUnlessTestsPresent() {
    String msg = "Refusing to run empty test.";
    if (!(all.isPresent() || only.isPresent()
      || any.isPresent() || none.isPresent())) {
      throw new IllegalStateException(msg);
    }
  }
}
