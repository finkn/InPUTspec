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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import net.finkn.inputspec.tools.*;
import net.finkn.inputspec.tools.types.AccessorTester;
import net.finkn.inputspec.tools.types.InitTester;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import se.miun.itm.input.model.design.IDesign;

/**
 * Examines under which circumstances InPUT4j invokes accessors and when it
 * does not.
 * The same tests are run with multiple configurations. In particular, two
 * different classes are used, one of which has no accessors that match the
 * default names. We can therefore be confident that InPUT4j indeed attempts
 * to use custom names when set.
 * <p>
 * The tests examine a counter to show that getters are not invoked, but
 * one configuration also shows this by setting a ridiculous getter name,
 * which does not provoke any adverse reaction.
 *
 * @author Christoffer Fink
 */
@RunWith(value = Parameterized.class)
public class AccessorTest {

  private final String testerId;
  private final String dataId; // Absolute ID.
  private final ParamCfg testerParam;
  private final CodeMappingCfg withConstructorMapping;
  private final CodeMappingCfg withoutConstructorMapping;
  private final IDesign design;

  public AccessorTest(TestParameters test) throws Throwable {
    this.dataId = test.dataId;
    this.testerParam = test.testerParam;
    this.withConstructorMapping = test.withConstructor;
    this.withoutConstructorMapping = test.withoutConstructor;

    this.testerId = testerParam.getId();
    // Most of the tests use the same design. Only one test uses the
    // other mapping, so it can create its own design.
    this.design = Helper.design(withoutConstructorMapping, testerParam);
  }

  /** Setter is invoked while initializing the outer parameter. */
  @Test
  public void initializationInvokesSetterIfEnabled() throws Throwable {
    InitTester tester = design.getValue(testerId);
    assertThat(tester.getSetterInvocations(), is(equalTo(1)));
  }

  /** Fetching outer parameter only invokes setter the first time. */
  @Test
  public void setterIsOnlyInvokedByTheFirstFetch() throws Throwable {
    int setterBaseline = InitTester.getGlobalSetterCount();
    design.getValue(testerId);
    design.getValue(testerId);
    design.getValue(testerId);
    int setterCount = InitTester.getGlobalSetterCount();
    assertThat(setterCount, is(equalTo(setterBaseline + 1)));
  }

  /**
   * Accessors are never invoked if the nested parameter was set by
   * the constructor. Note that getters are never invoked either way.
   */
  @Test
  public void constructorInitializationDisablesSetter() throws Throwable {
    // Use the mapping with a constructor for this test.
    IDesign design = Helper.design(withConstructorMapping, testerParam);

    InitTester tester = design.getValue(testerId);
    design.setValue(dataId, 1);
    // Other tests show that getters aren't invoked. Just checking that
    // this is still the case when constructor initialization is used.
    design.getValue(dataId);

    // No accessors have been invoked.
    assertThat(tester.getSetterInvocations(), is(equalTo(0)));
    assertThat(tester.getGetterInvocations(), is(equalTo(0)));
  }

  /** Getter is not invoked when getting the corresponding value. */
  @Test
  public void customGetterIsNotInvokedWhenGettingValue() throws Throwable {
    // Fetch outer first to force initialization.
    // Otherwise, getter might not be invoked anyway.
    InitTester tester = design.getValue(testerId);
    design.getValue(dataId);
    assertThat(tester.getGetterInvocations(), is(equalTo(0)));
  }

  /**
   * {@code setValue} only invokes setter if the outer parameter has been
   * been fetched (initialized).
   */
  @Test
  public void setterOnlyInvokedIfOuterParamHasBeenFetched() throws Throwable {
    // This interacts with lazy initialization.
    InitTester.resetGlobal();
    design.setValue(dataId, 1);
    design.setValue(dataId, 2);
    assertThat(InitTester.getGlobalSetterCount(), is(equalTo(0)));
  }

  /**
   * Setter is invoked when setting inner parameter using {@code setValue},
   * provided that the outer parameter has first been fetched and that the 
   * inner parameter was not initialized using the constructor.
   * @see #constructorInitializationDisablesSetter
   * @see #setterOnlyInvokedIfOuterParamHasBeenFetched
   */
  @Test
  public void setterIsInvokedWhenSettingValue() throws Throwable {
    InitTester tester = design.getValue(testerId);
    int baseline = tester.getSetterInvocations();
    design.setValue(dataId, 2);
    design.setValue(dataId, 3);
    design.setValue(dataId, 4);
    // Expecting 3 more invocations than whatever the baseline is.
    assertThat(tester.getSetterInvocations(), is(equalTo(baseline + 3)));
  }

  @Parameters
  public static Collection<TestParameters[]> parameters() {
    ParamCfg dataParam = ParamCfg.builder()
      .id("Data")
      .build();

    ParamCfg testerParam = ParamCfg.builder()
      .id("Tester")
      .structured()
      .add(dataParam)
      .build();

    return Arrays.asList(new TestParameters[][] {
      // Implicit accessors.
      {
        tb(dataParam, testerParam)
          .type(AccessorTester.class)
          .build(),
      },
      // Explicit accessors with the names InPUT would infer.
      {
        tb(dataParam, testerParam)
          .type(AccessorTester.class)
          .setter(AccessorTester.SETTER)
          .getter(AccessorTester.GETTER)
          .build(),
      },
      // Explicit custom accessors.
      {
        tb(dataParam, testerParam)
          .type(AccessorTester.class)
          .setter(InitTester.SETTER)
          .getter(InitTester.GETTER)
          .build(),
      },
      // Explicit custom accessors.
      // This class does not have accessors with the inferred names,
      // so we now know that at least custom setters can be set.
      {
        tb(dataParam, testerParam)
          .type(InitTester.class)
          .setter(InitTester.SETTER)
          .getter(InitTester.GETTER)
          .build(),
      },
      // Further evidence that getters are never invoked.
      {
        tb(dataParam, testerParam)
          .type(InitTester.class)
          .setter(InitTester.SETTER)
          .getter("I don't think this would be a sensible name for a getter!")
          .build(),
      },
    });
  }

  private static TestParameters.Builder tb(ParamCfg inner, ParamCfg outer) {
    return TestParameters.builder().inner(inner).outer(outer);
  }

  public static class TestParameters {
    public final String dataId; // Absolute ID.
    public final ParamCfg testerParam;
    public final CodeMappingCfg withConstructor;
    public final CodeMappingCfg withoutConstructor;

    private TestParameters(String dataId, ParamCfg testerParam,
        CodeMappingCfg with, CodeMappingCfg without) {
      this.dataId = dataId;
      this.testerParam = testerParam;
      this.withConstructor = with;
      this.withoutConstructor = without;
    }

    public static TestParameters.Builder builder() {
      return new Builder();
    }

    public static class Builder {
      private ParamCfg inner;
      private ParamCfg outer;
      private Class<?> type;
      private String setter = null;
      private String getter = null;

      private boolean built = false;

      public Builder inner(ParamCfg p) {
        this.inner = p;
        return this;
      }
      public Builder outer(ParamCfg p) {
        this.outer = p;
        return this;
      }
      public Builder setter(String s) {
        this.setter = s;
        return this;
      }
      public Builder getter(String s) {
        this.getter = s;
        return this;
      }
      public Builder type(Class<?> t) {
        this.type = t;
        return this;
      }

      public TestParameters build() {
        // Prevent the same builder from being used for multiple tests. This
        // is a precaution to avoid contamination by old builder state.
        if (built) {
          String msg = "Can only use a builder once.";
          throw new IllegalStateException(msg);
        }
        built = true;

        MappingCfg innerMapping = innerMapping();
        CodeMappingCfg with = CodeMappingCfg
          .getInstance(innerMapping, outerWithConstructor());
        CodeMappingCfg without = CodeMappingCfg
          .getInstance(innerMapping, outerWithoutConstructor());
        String innerId = innerMapping.getId();
        return new TestParameters(innerId, outer, with, without);
      }

      private MappingCfg outerWithConstructor() {
        return partialMapping().nested(outer.getNested()).build();
      }
      private MappingCfg outerWithoutConstructor() {
        return partialMapping().build();
      }
      private MappingCfg innerMapping() {
        return MappingCfg.builder()
          .param(outer, inner)
          .set(setter)
          .get(getter)
          .build();
      }
      private MappingCfg.Builder partialMapping() {
        return MappingCfg.builder().infer(outer, type);
      }
    }
  }
}
