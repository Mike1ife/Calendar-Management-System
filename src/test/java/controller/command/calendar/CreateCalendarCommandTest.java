package controller.command.calendar;

import static org.junit.Assert.assertTrue;

import controller.mock.MockCalendarManager;
import model.calendar.CalendarManagerInterface;
import org.junit.Before;
import org.junit.Test;
import view.CalendarTextView;
import view.CalendarViewInterface;

/**
 * Test class for CreateCalendarCommand.
 */
public class CreateCalendarCommandTest {
  private CreateCalendarCommand command;
  private StringBuilder managerLog;
  private CalendarManagerInterface mockManager;
  private CalendarViewInterface view;

  /**
   * Create command, log, model, and view before every test.
   */
  @Before
  public void setUp() {
    command = new CreateCalendarCommand();
    managerLog = new StringBuilder();
    mockManager = new MockCalendarManager(managerLog);
    StringBuilder viewOutput = new StringBuilder();
    view = new CalendarTextView(viewOutput);
  }

  @Test
  public void testCanHandle() {
    assertTrue(command.canHandle("create calendar --name Test"));
    assertTrue(command.canHandle("CREATE CALENDAR --name Test"));
    assertTrue(command.canHandle("create xyz calendar abc --name Test"));
  }

  @Test
  public void testCreateCalendar() {
    String cmd = "create calendar --name MyCalendar --timezone America/New_York";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("addCalendar"));
    assertTrue(managerLog.toString().contains("MyCalendar"));
    assertTrue(managerLog.toString().contains("America/New_York"));
  }

  @Test
  public void testCreateCalendarWithExtraWords() {
    String cmd = "create xyz calendar abc --name MyCalendar def --timezone America/New_York";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("addCalendar"));
    assertTrue(managerLog.toString().contains("MyCalendar"));
  }

  @Test
  public void testCreateCalendarDifferentTimezones() {
    String cmd1 = "create calendar --name Tokyo --timezone Asia/Tokyo";
    command.execute(cmd1, mockManager, view);
    assertTrue(managerLog.toString().contains("Asia/Tokyo"));

    String cmd2 = "create calendar --name London --timezone Europe/London";
    command.execute(cmd2, mockManager, view);
    assertTrue(managerLog.toString().contains("Europe/London"));

    String cmd3 = "create calendar --name Sydney --timezone Australia/Sydney";
    command.execute(cmd3, mockManager, view);
    assertTrue(managerLog.toString().contains("Australia/Sydney"));
  }

  @Test
  public void testCreateCalendarCaseInsensitive() {
    String cmd = "CREATE CALENDAR --NAME Test --TIMEZONE America/New_York";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("addCalendar"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingName() {
    String cmd = "create calendar --timezone America/New_York";
    command.execute(cmd, mockManager, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingTimezone() {
    String cmd = "create calendar --name MyCalendar";
    command.execute(cmd, mockManager, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTimezoneFormat() {
    String cmd = "create calendar --name MyCalendar --timezone InvalidFormat";
    command.execute(cmd, mockManager, view);
  }

  @Test
  public void testCreateCalendarMultipleWords() {
    String cmd = "create calendar --name Work_Calendar --timezone America/Los_Angeles";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("Work_Calendar"));
    assertTrue(managerLog.toString().contains("America/Los_Angeles"));
  }
}