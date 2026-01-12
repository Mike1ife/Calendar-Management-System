package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import controller.mock.FakeAppendable;
import controller.mock.MockCalendarModel;
import java.io.StringReader;
import model.calendar.CalendarModelInterface;
import org.junit.Test;
import view.CalendarTextView;
import view.CalendarViewInterface;

/**
 * Calendar Controller Test.
 */
public class CalendarControllerTest {
  @Test
  public void testControllerWithMockModel() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "create event \"Meeting\" from 2025-10-24T10:00 to 2025-10-24T11:00\nexit\n";
    Readable readable = new StringReader(input);

    CalendarController controller = new CalendarController(mockModel, view, readable);
    controller.go();

    assertTrue(modelLog.toString().contains("createSingleEventWithTime"));
    assertTrue(modelLog.toString().contains("Meeting"));
    assertTrue(modelLog.toString().contains("2025-10-24T10:00"));
    assertTrue(modelLog.toString().contains("2025-10-24T11:00"));

    assertTrue(viewOutput.toString().contains("Success"));
    assertTrue(viewOutput.toString().contains("Meeting"));
  }

  @Test
  public void testControllerWithMultipleCommands() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "create event \"Meeting\" from 2025-10-24T10:00 to 2025-10-24T11:00\n"
        + "print events on 2025-10-24\n"
        + "exit\n";
    Readable readable = new StringReader(input);

    CalendarController controller = new CalendarController(mockModel, view, readable);
    controller.go();

    assertTrue(modelLog.toString().contains("createSingleEventWithTime"));
    assertTrue(modelLog.toString().contains("getEventsOnDate"));
  }

  @Test
  public void testControllerWithInvalidCommand() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "invalid command\nexit\n";
    Readable readable = new StringReader(input);

    CalendarController controller = new CalendarController(mockModel, view, readable);
    controller.go();

    assertTrue(viewOutput.toString().contains("Error"));
    assertTrue(viewOutput.toString().contains("Unknown command"));
  }

  @Test
  public void testControllerWithAllEventCommands() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "create event \"Meeting\" from 2025-10-24T10:00 to 2025-10-24T11:00\n"
        + "create event \"Birthday\" on 2025-10-25\n"
        +
        "create event \"Class\" from 2025-10-27T09:00 to 2025-10-27T10:00 repeats MWF for 5 times\n"
        +
        "edit event Subject \"Meeting\" from 2025-10-24T10:00 to 2025-10-24T11:00 with Important "
        + "Meeting\n"
        + "edit events Location \"Class\" from 2025-10-27T09:00 with Room 101\n"
        + "edit series Description \"Class\" from 2025-10-27T09:00 with Updated Description\n"
        + "print events on 2025-10-24\n"
        + "print events from 2025-10-24T08:00 to 2025-10-26T20:00\n"
        + "show status on 2025-10-24T10:30\n"
        + "export cal calendar.csv\n"
        + "exit\n";
    Readable readable = new StringReader(input);

    CalendarController controller = new CalendarController(mockModel, view, readable);
    controller.go();

    assertTrue(modelLog.toString().contains("createSingleEventWithTime"));
    assertTrue(modelLog.toString().contains("createAllDaySingleEvent"));
    assertTrue(modelLog.toString().contains("createSeriesEventWithOccurrence"));
    assertTrue(modelLog.toString().contains("editSingleEvent"));
    assertTrue(modelLog.toString().contains("editEventStartFrom"));
    assertTrue(modelLog.toString().contains("editSeriesStartFrom"));
    assertTrue(modelLog.toString().contains("getEventsOnDate"));
    assertTrue(modelLog.toString().contains("getEventsInRange"));
    assertTrue(modelLog.toString().contains("isBusy"));
    assertTrue(modelLog.toString().contains("getAllEvents"));
  }

  @Test
  public void testControllerWithEmptyInput() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "\n\n\nexit\n";
    Readable readable = new StringReader(input);

    CalendarController controller = new CalendarController(mockModel, view, readable);
    controller.go();

    assertEquals("", modelLog.toString());
  }

  @Test
  public void testControllerWithEmptyLines() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "\n  \n\t\n\nexit\n";
    Readable readable = new StringReader(input);

    CalendarController controller = new CalendarController(mockModel, view, readable);
    controller.go();

    assertTrue(viewOutput.toString().contains("Enter command:"));
    assertTrue(viewOutput.toString().contains("Exiting"));
  }

  @Test
  public void testControllerExitCommand() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "exit\n";
    Readable readable = new StringReader(input);

    CalendarController controller = new CalendarController(mockModel, view, readable);
    controller.go();

    assertTrue(viewOutput.toString().contains("Exiting"));
  }

  @Test
  public void testControllerExitCommandCaseInsensitive() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "EXIT\n";
    Readable readable = new StringReader(input);

    CalendarController controller = new CalendarController(mockModel, view, readable);
    controller.go();

    assertTrue(viewOutput.toString().contains("Exiting"));
  }

  @Test
  public void testControllerWithModelException() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input =
        "edit event InvalidProperty \"Test\" from 2025-10-24T10:00 to 2025-10-24T11:00 with "
            + "Value\nexit\n";
    Readable readable = new StringReader(input);

    CalendarController controller = new CalendarController(mockModel, view, readable);
    controller.go();

    assertTrue(viewOutput.toString().contains("Error"));
  }

  @Test
  public void testControllerContinuesAfterError() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "invalid command\n"
        + "create event \"Meeting\" from 2025-10-24T10:00 to 2025-10-24T11:00\n"
        + "exit\n";
    Readable readable = new StringReader(input);

    CalendarController controller = new CalendarController(mockModel, view, readable);
    controller.go();

    assertTrue(viewOutput.toString().contains("Error"));
    assertTrue(modelLog.toString().contains("createSingleEventWithTime"));
  }

  @Test
  public void testControllerNoMoreInput() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "";
    Readable readable = new StringReader(input);

    CalendarController controller = new CalendarController(mockModel, view, readable);
    controller.go();

    assertTrue(viewOutput.toString().contains("Enter command:"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testControllerConstructorWithNullView() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);
    Readable readable = new StringReader("");

    new CalendarController(mockModel, null, readable);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testControllerConstructorWithNullInput() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);
    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    new CalendarController(mockModel, view, null);
  }

  @Test
  public void testControllerWithIoException() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);

    Appendable fakeAppendable = new FakeAppendable();
    CalendarViewInterface view = new CalendarTextView(fakeAppendable);

    String input = "create event \"Meeting\" from 2025-10-24T10:00 to 2025-10-24T11:00\nexit\n";
    Readable readable = new StringReader(input);

    CalendarController controller = new CalendarController(mockModel, view, readable);

    try {
      controller.go();
      fail("Expected IllegalStateException when view fails to write");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("Failed to write to output"));
    }
  }

  @Test
  public void testProcessCommandWithCreateEvent() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "create event \"Test\" from 2025-10-24T10:00 to 2025-10-24T11:00\nexit\n";
    Readable readable = new StringReader(input);

    CalendarController controller = new CalendarController(mockModel, view, readable);
    controller.go();

    assertTrue(modelLog.toString().contains("createSingleEventWithTime"));
  }

  @Test
  public void testProcessCommandWithEditEvent() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input =
        "edit event Subject \"Meeting\" from 2025-10-24T10:00 to 2025-10-24T11:00 with "
            + "NewMeeting\nexit\n";
    Readable readable = new StringReader(input);

    CalendarController controller = new CalendarController(mockModel, view, readable);
    controller.go();

    assertTrue(modelLog.toString().contains("editSingleEvent"));
  }

  @Test
  public void testProcessCommandWithPrintEvents() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "print events on 2025-10-24\nexit\n";
    Readable readable = new StringReader(input);

    CalendarController controller = new CalendarController(mockModel, view, readable);
    controller.go();

    assertTrue(modelLog.toString().contains("getEventsOnDate"));
  }

  @Test
  public void testProcessCommandWithShowStatus() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "show status on 2025-10-24T10:00\nexit\n";
    Readable readable = new StringReader(input);

    CalendarController controller = new CalendarController(mockModel, view, readable);
    controller.go();

    assertTrue(modelLog.toString().contains("isBusy"));
  }

  @Test
  public void testProcessCommandWithExport() {
    StringBuilder modelLog = new StringBuilder();
    CalendarModelInterface mockModel = new MockCalendarModel(modelLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "export cal calendar.csv\nexit\n";
    Readable readable = new StringReader(input);

    CalendarController controller = new CalendarController(mockModel, view, readable);
    controller.go();

    assertTrue(modelLog.toString().contains("getAllEvents"));
  }
}