package view;

import static org.junit.Assert.assertEquals;

import controller.mock.FakeAppendable;
import org.junit.Test;

/**
 * Test class for CalendarTextView.
 */
public class CalendarTextViewTest {

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullOutput() {
    new CalendarTextView(null);
  }

  @Test(expected = IllegalStateException.class)
  public void testDisplayPromptThrowsIoException() {
    Appendable fakeAppendable = new FakeAppendable();
    CalendarViewInterface view = new CalendarTextView(fakeAppendable);

    view.displayPrompt("Test message");
  }

  @Test(expected = IllegalStateException.class)
  public void testDisplayErrorThrowsIoException() {
    Appendable fakeAppendable = new FakeAppendable();
    CalendarViewInterface view = new CalendarTextView(fakeAppendable);

    view.displayError("Error message");
  }

  @Test(expected = IllegalStateException.class)
  public void testDisplaySuccessThrowsIoException() {
    Appendable fakeAppendable = new FakeAppendable();
    CalendarViewInterface view = new CalendarTextView(fakeAppendable);

    view.displaySuccess("Success message");
  }

  @Test(expected = IllegalStateException.class)
  public void testDisplayExitThrowsIoException() {
    Appendable fakeAppendable = new FakeAppendable();
    CalendarViewInterface view = new CalendarTextView(fakeAppendable);

    view.displayExit();
  }

  @Test(expected = IllegalStateException.class)
  public void testDisplayExportResultThrowsIoException() {
    Appendable fakeAppendable = new FakeAppendable();
    CalendarViewInterface view = new CalendarTextView(fakeAppendable);

    view.displayExportResult("/path/to/file.csv");
  }

  @Test
  public void testDisplayPromptWithValidAppendable() {
    StringBuilder output = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(output);

    view.displayPrompt("Hello");

    assertEquals("Hello", output.toString().trim());
  }

  @Test
  public void testDisplayErrorWithValidAppendable() {
    StringBuilder output = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(output);

    view.displayError("Something went wrong");

    assertEquals("Error: Something went wrong", output.toString().trim());
  }

  @Test
  public void testDisplaySuccessWithValidAppendable() {
    StringBuilder output = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(output);

    view.displaySuccess("Operation completed");

    assertEquals("Success: Operation completed", output.toString().trim());
  }
}