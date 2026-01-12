package controller.command.calendar;

import static org.junit.Assert.assertTrue;

import controller.mock.MockCalendarManager;
import model.calendar.CalendarManagerInterface;
import org.junit.Before;
import org.junit.Test;
import view.CalendarTextView;
import view.CalendarViewInterface;

/**
 * Test class for UseCalendarCommand.
 */
public class UseCalendarCommandTest {
  private UseCalendarCommand command;
  private StringBuilder managerLog;
  private CalendarManagerInterface mockManager;
  private CalendarViewInterface view;

  /**
   * Create command, log, model, and view before every test.
   */
  @Before
  public void setUp() {
    command = new UseCalendarCommand();
    managerLog = new StringBuilder();
    mockManager = new MockCalendarManager(managerLog);
    StringBuilder viewOutput = new StringBuilder();
    view = new CalendarTextView(viewOutput);
  }

  @Test
  public void testCanHandle() {
    assertTrue(command.canHandle("use calendar --name Test"));
    assertTrue(command.canHandle("USE CALENDAR --name Test"));
    assertTrue(command.canHandle("use xyz calendar abc --name Test"));
  }

  @Test
  public void testUseCalendar() {
    String cmd = "use calendar --name MyCalendar";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("activateCalendar"));
    assertTrue(managerLog.toString().contains("MyCalendar"));
  }

  @Test
  public void testUseCalendarWithExtraWords() {
    String cmd = "use xyz calendar abc --name MyCalendar 123";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("activateCalendar"));
    assertTrue(managerLog.toString().contains("MyCalendar"));
  }

  @Test
  public void testUseCalendarCaseInsensitive() {
    String cmd = "USE CALENDAR --NAME Test";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("activateCalendar"));
    assertTrue(managerLog.toString().contains("Test"));
  }

  @Test
  public void testUseCalendarDifferentNames() {
    String cmd1 = "use calendar --name Work";
    command.execute(cmd1, mockManager, view);
    assertTrue(managerLog.toString().contains("Work"));

    String cmd2 = "use calendar --name Personal";
    command.execute(cmd2, mockManager, view);
    assertTrue(managerLog.toString().contains("Personal"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingName() {
    String cmd = "use calendar";
    command.execute(cmd, mockManager, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyName() {
    String cmd = "use calendar --name";
    command.execute(cmd, mockManager, view);
  }
}