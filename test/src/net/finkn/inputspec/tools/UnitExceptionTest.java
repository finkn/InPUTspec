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

import static net.finkn.inputspec.tools.Unit.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Exception-related tests.
 * This class keeps {@link UnitTest} from getting too cluttered.
 *
 * @see UnitTest
 * @author Christoffer Fink
 */
public class UnitExceptionTest {
  @Test
  public void getExceptionShouldReturnEmptyOptionalIfNothingThrown() {
    assertFalse(getException(() -> { }).isPresent());
  }

  @Test
  public void getExceptionShouldReturnTheExceptionThatWasThrown() {
    AssertionError e = new AssertionError("My exception");
    Runnable r = () -> { throw e; };
    assertThat(getException(r).get(), is(equalTo(e)));
  }

  @Test
  public void getThrowerShouldReturnConsumerThatUsesTheGivenFunction() {
    try {
      getThrower(x -> "" + x).accept(3);
      fail("Expected to get an exception to examine.");
    } catch (AssertionError e) {
      assertEquals("3", e.getMessage());
    }
  }

  @Test
  public void getThrowerShouldReturnRunnableThatThrowsExceptionWithTheMsg() {
    try {
      getThrower("3").run();
      fail("Expected to get an exception to examine.");
    } catch (AssertionError e) {
      assertEquals("3", e.getMessage());
    }
  }

  @Test
  public void assertExceptionMessageMatchesShouldSucceedIfRegexMatches() {
    String msg = "Custom exception message.";
    String regex = ".*exception.*\\.$";
    assertExceptionMessageMatches(getThrower(msg), regex);
  }

  @Test(expected = AssertionError.class)
  public void assertExceptionMessageMatchesShouldFailIfRegexDoesNotMatch() {
    String msg = "Custom exception message.";
    String regex = "^exception.*\\.$";
    assertExceptionMessageMatches(getThrower(msg), regex);
  }

  @Test
  public void getExceptionMessageShouldReturnTheExceptionMessage() {
    String msg = "Custom exception message.";
    String result = getExceptionMessage(getThrower(msg)).get();
    assertEquals(msg, result);
  }

  @Test(expected = IllegalArgumentException.class)
  public void assertExceptionMatchesShouldFailUnlessAnExceptionIsThrown() {
    // Since this catcher always accepts, we can be sure that the failure
    // bypasses the actual matching test.
    assertExceptionMatches(() -> {}, x -> true);
  }

  @Test
  public void getExceptionMessageShouldReturnEmptyOptionalWhenNothingThrown() {
    assertFalse(getExceptionMessage(() -> {}).isPresent());
  }

  @Test
  public void assertThrowsExceptionShouldSucceedIfRunnableThrowsException() {
    assertThrowsException(getThrower("Whatever"));
  }

  @Test(expected = AssertionError.class)
  public void assertThrowExceptionsShouldFailUnlessRunnableThrowsException() {
    assertThrowsException(() -> {});
  }

  @Test(expected = AssertionError.class)
  public void assertThrowsNothingShouldFailIfRunnableThrowsException() {
    assertThrowsNothing(getThrower("Whatever"));
  }

  @Test
  public void assertThrowNothingShouldSucceedUnlessRunnableThrowsException() {
    assertThrowsNothing(() -> {});
  }
}
