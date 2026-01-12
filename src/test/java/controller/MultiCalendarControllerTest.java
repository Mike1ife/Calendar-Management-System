package controller;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import controller.mock.FakeAppendable;
import controller.mock.MockCalendarManager;
import java.io.StringReader;
import model.calendar.CalendarManagerInterface;
import org.junit.Test;
import view.CalendarTextView;
import view.CalendarViewInterface;

/**
 * Tests for MultiCalendarController.
 */
public class MultiCalendarControllerTest {

  @Test
  public void testCreateCalendarCommand() {
    StringBuilder managerLog = new StringBuilder();
    CalendarManagerInterface mockManager = new MockCalendarManager(managerLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "create calendar --name Work --timezone America/New_York\nexit\n";
    Readable readable = new StringReader(input);

    MultiCalendarController controller = new MultiCalendarController(mockManager, view, readable);
    controller.go();

    assertTrue(managerLog.toString().contains("addCalendar"));
    assertTrue(managerLog.toString().contains("Work"));
    assertTrue(managerLog.toString().contains("America/New_York"));
  }

  @Test
  public void testEditCalendarCommand() {
    StringBuilder managerLog = new StringBuilder();
    CalendarManagerInterface mockManager = new MockCalendarManager(managerLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "edit calendar --name OldName --property name NewName\nexit\n";
    Readable readable = new StringReader(input);

    MultiCalendarController controller = new MultiCalendarController(mockManager, view, readable);
    controller.go();

    assertTrue(managerLog.toString().contains("editCalendar"));
    assertTrue(managerLog.toString().contains("OldName"));
    assertTrue(managerLog.toString().contains("name"));
  }

  @Test
  public void testUseCalendarCommand() {
    StringBuilder managerLog = new StringBuilder();
    CalendarManagerInterface mockManager = new MockCalendarManager(managerLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "use calendar --name Work\nexit\n";
    Readable readable = new StringReader(input);

    MultiCalendarController controller = new MultiCalendarController(mockManager, view, readable);
    controller.go();

    assertTrue(managerLog.toString().contains("activateCalendar"));
    assertTrue(managerLog.toString().contains("Work"));
  }

  @Test
  public void testCopyEventCommand() {
    StringBuilder managerLog = new StringBuilder();
    CalendarManagerInterface mockManager = new MockCalendarManager(managerLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input =
        "copy event \"Meeting\" on 2025-10-24T10:00 --target Work to 2025-10-25T14:00\nexit\n";
    Readable readable = new StringReader(input);

    MultiCalendarController controller = new MultiCalendarController(mockManager, view, readable);
    controller.go();

    assertTrue(managerLog.toString().contains("copyEvent"));
    assertTrue(managerLog.toString().contains("Meeting"));
  }

  @Test
  public void testCopyEventsOnDateCommand() {
    StringBuilder managerLog = new StringBuilder();
    CalendarManagerInterface mockManager = new MockCalendarManager(managerLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "copy events on 2025-10-24 --target Work to 2025-10-25\nexit\n";
    Readable readable = new StringReader(input);

    MultiCalendarController controller = new MultiCalendarController(mockManager, view, readable);
    controller.go();

    assertTrue(managerLog.toString().contains("copyEventsOnDate"));
  }

  @Test
  public void testCopyEventsBetweenCommand() {
    StringBuilder managerLog = new StringBuilder();
    CalendarManagerInterface mockManager = new MockCalendarManager(managerLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input =
        "copy events between 2025-10-24 and 2025-10-30 --target Work to 2025-11-01\nexit\n";
    Readable readable = new StringReader(input);

    MultiCalendarController controller = new MultiCalendarController(mockManager, view, readable);
    controller.go();

    assertTrue(managerLog.toString().contains("copyEventsBetween"));
  }

  @Test
  public void testEventCommandWithoutActiveCalendar() {
    StringBuilder managerLog = new StringBuilder();
    CalendarManagerInterface mockManager = new MockCalendarManager(managerLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "create event \"Meeting\" from 2025-10-24T10:00 to 2025-10-24T11:00\nexit\n";
    Readable readable = new StringReader(input);

    MultiCalendarController controller = new MultiCalendarController(mockManager, view, readable);
    controller.go();

    assertTrue(viewOutput.toString().contains("Error"));
    assertTrue(viewOutput.toString().contains("No calendar in use"));
  }

  @Test
  public void testMixedCalendarAndEventCommands() {
    StringBuilder managerLog = new StringBuilder();
    CalendarManagerInterface mockManager = new MockCalendarManager(managerLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "create calendar --name Work --timezone America/New_York\n"
        + "use calendar --name Work\n"
        + "create event \"Meeting\" from 2025-10-24T10:00 to 2025-10-24T11:00\n"
        + "edit calendar --name Work --property name Personal\n"
        + "exit\n";
    Readable readable = new StringReader(input);

    MultiCalendarController controller = new MultiCalendarController(mockManager, view, readable);
    controller.go();

    assertTrue(managerLog.toString().contains("addCalendar"));
    assertTrue(managerLog.toString().contains("activateCalendar"));
    assertTrue(managerLog.toString().contains("getActiveCalendar"));
    assertTrue(managerLog.toString().contains("editCalendar"));
  }

  @Test
  public void testCalendarCommandRouting() {
    StringBuilder managerLog = new StringBuilder();
    CalendarManagerInterface mockManager = new MockCalendarManager(managerLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "create calendar --name Test --timezone America/New_York\n"
        + "edit calendar --name Test --property name Updated\n"
        + "use calendar --name Updated\n"
        + "copy event \"Event\" on 2025-10-24T10:00 --target Other to 2025-10-25T10:00\n"
        + "exit\n";
    Readable readable = new StringReader(input);

    MultiCalendarController controller = new MultiCalendarController(mockManager, view, readable);
    controller.go();

    assertTrue(managerLog.toString().contains("addCalendar"));
    assertTrue(managerLog.toString().contains("editCalendar"));
    assertTrue(managerLog.toString().contains("activateCalendar"));
    assertTrue(managerLog.toString().contains("copyEvent"));
  }

  @Test
  public void testInvalidCalendarCommand() {
    StringBuilder managerLog = new StringBuilder();
    CalendarManagerInterface mockManager = new MockCalendarManager(managerLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "invalid calendar command\nexit\n";
    Readable readable = new StringReader(input);

    MultiCalendarController controller = new MultiCalendarController(mockManager, view, readable);
    controller.go();

    assertTrue(viewOutput.toString().contains("Error"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullManager() {
    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);
    Readable readable = new StringReader("");

    new MultiCalendarController(null, view, readable);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullView() {
    StringBuilder managerLog = new StringBuilder();
    CalendarManagerInterface mockManager = new MockCalendarManager(managerLog);
    Readable readable = new StringReader("");

    new MultiCalendarController(mockManager, null, readable);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullInput() {
    StringBuilder managerLog = new StringBuilder();
    CalendarManagerInterface mockManager = new MockCalendarManager(managerLog);
    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    new MultiCalendarController(mockManager, view, null);
  }

  @Test
  public void testWithIoException() {
    StringBuilder managerLog = new StringBuilder();
    CalendarManagerInterface mockManager = new MockCalendarManager(managerLog);

    Appendable fakeAppendable = new FakeAppendable();
    CalendarViewInterface view = new CalendarTextView(fakeAppendable);

    String input = "create calendar --name Test --timezone America/New_York\nexit\n";
    Readable readable = new StringReader(input);

    MultiCalendarController controller = new MultiCalendarController(mockManager, view, readable);

    try {
      controller.go();
      fail("Expected IllegalStateException when view fails to write");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("Failed to write to output"));
    }
  }

  @Test
  public void testProcessCommandRoutingToCalendarManager() {
    StringBuilder managerLog = new StringBuilder();
    CalendarManagerInterface mockManager = new MockCalendarManager(managerLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "create calendar --name Test --timezone America/New_York\nexit\n";
    Readable readable = new StringReader(input);

    MultiCalendarController controller = new MultiCalendarController(mockManager, view, readable);
    controller.go();

    assertTrue(managerLog.toString().contains("addCalendar"));
  }

  @Test
  public void testProcessCommandRoutingToEventManager() {
    StringBuilder managerLog = new StringBuilder();
    CalendarManagerInterface mockManager = new MockCalendarManager(managerLog);

    StringBuilder viewOutput = new StringBuilder();
    CalendarViewInterface view = new CalendarTextView(viewOutput);

    String input = "use calendar --name Test\n"
        + "create event \"Meeting\" from 2025-10-24T10:00 to 2025-10-24T11:00\n"
        + "exit\n";
    Readable readable = new StringReader(input);

    MultiCalendarController controller = new MultiCalendarController(mockManager, view, readable);
    controller.go();

    assertTrue(managerLog.toString().contains("getActiveCalendar"));
  }
}