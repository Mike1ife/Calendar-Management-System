package controller.command.calendar;

import static org.junit.Assert.assertTrue;

import controller.mock.MockCalendarManager;
import model.calendar.CalendarManagerInterface;
import org.junit.Before;
import org.junit.Test;
import view.CalendarTextView;
import view.CalendarViewInterface;

/**
 * Test class for EditCalendarCommand.
 */
public class EditCalendarCommandTest {
  private EditCalendarCommand command;
  private StringBuilder managerLog;
  private CalendarManagerInterface mockManager;
  private CalendarViewInterface view;

  /**
   * Create command, log, model, and view before every test.
   */
  @Before
  public void setUp() {
    command = new EditCalendarCommand();
    managerLog = new StringBuilder();
    mockManager = new MockCalendarManager(managerLog);
    StringBuilder viewOutput = new StringBuilder();
    view = new CalendarTextView(viewOutput);
  }

  @Test
  public void testCanHandle() {
    assertTrue(command.canHandle("edit calendar --name Test"));
    assertTrue(command.canHandle("EDIT CALENDAR --name Test"));
    assertTrue(command.canHandle("edit xyz calendar abc --name Test"));
  }

  @Test
  public void testEditCalendarName() {
    String cmd = "edit calendar --name OldName --property name NewName";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("editCalendar"));
    assertTrue(managerLog.toString().contains("OldName"));
    assertTrue(managerLog.toString().contains("name"));
    assertTrue(managerLog.toString().contains("NewName"));
  }

  @Test
  public void testEditCalendarTimezone() {
    String cmd = "edit calendar --name MyCalendar --property timezone America/Chicago";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("editCalendar"));
    assertTrue(managerLog.toString().contains("MyCalendar"));
    assertTrue(managerLog.toString().contains("timezone"));
    assertTrue(managerLog.toString().contains("America/Chicago"));
  }

  @Test
  public void testEditCalendarWithExtraWords() {
    String cmd = "edit xyz calendar abc --name MyCalendar def --property ghi name NewName";
    command.execute(cmd, mockManager, view);


    assertTrue(managerLog.toString().contains("editCalendar"));
    assertTrue(managerLog.toString().contains("MyCalendar"));
  }

  @Test
  public void testEditCalendarCaseInsensitive() {
    String cmd = "EDIT CALENDAR --NAME Test --PROPERTY NAME NewTest";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("editCalendar"));
  }

  @Test
  public void testEditCalendarNameProperty() {
    String cmd = "edit calendar --name Calendar1 --property name Calendar2";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("Calendar1"));
    assertTrue(managerLog.toString().contains("name"));
    assertTrue(managerLog.toString().contains("Calendar2"));
  }

  @Test
  public void testEditCalendarTimezoneProperty() {
    String cmd = "edit calendar --name Calendar1 --property timezone Europe/Paris";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("Calendar1"));
    assertTrue(managerLog.toString().contains("timezone"));
    assertTrue(managerLog.toString().contains("Europe/Paris"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingName() {
    String cmd = "edit calendar --property name NewName";
    command.execute(cmd, mockManager, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingProperty() {
    String cmd = "edit calendar --name MyCalendar NewValue";
    command.execute(cmd, mockManager, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingNewValue() {
    String cmd = "edit calendar --name MyCalendar --property name";
    command.execute(cmd, mockManager, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidProperty() {
    String cmd = "edit calendar --name MyCalendar --property invalid NewValue";
    command.execute(cmd, mockManager, view);
  }
}