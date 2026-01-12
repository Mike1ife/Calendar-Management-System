package controller.command.calendar;

import static org.junit.Assert.assertTrue;

import controller.mock.MockCalendarManager;
import model.calendar.CalendarManagerInterface;
import org.junit.Before;
import org.junit.Test;
import view.CalendarTextView;
import view.CalendarViewInterface;

/**
 * Test class for CopyEventCommand.
 */
public class CopyEventCommandTest {
  private CopyEventCommand command;
  private StringBuilder managerLog;
  private CalendarManagerInterface mockManager;
  private CalendarViewInterface view;

  /**
   * Create command, log, model, and view before every test.
   */
  @Before
  public void setUp() {
    command = new CopyEventCommand();
    managerLog = new StringBuilder();
    mockManager = new MockCalendarManager(managerLog);
    StringBuilder viewOutput = new StringBuilder();
    view = new CalendarTextView(viewOutput);
  }

  @Test
  public void testCanHandleCopyEvent() {
    assertTrue(command.canHandle("copy event \"Test\" on 2025-10-24T10:00"));
    assertTrue(command.canHandle("COPY EVENT \"Test\" on 2025-10-24T10:00"));
  }

  @Test
  public void testCanHandleCopyEvents() {
    assertTrue(command.canHandle("copy events on 2025-10-24"));
    assertTrue(command.canHandle("COPY EVENTS on 2025-10-24"));
  }

  @Test
  public void testCanHandleCopyEventsBetween() {
    assertTrue(command.canHandle("copy events between 2025-10-24 and 2025-10-30"));
    assertTrue(command.canHandle("COPY EVENTS between 2025-10-24 and 2025-10-30"));
  }

  @Test
  public void testCopySingleEvent() {
    String cmd =
        "copy event \"Meeting\" on 2025-10-24T10:00 --target TargetCal to 2025-10-25T14:00";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("copyEvent"));
    assertTrue(managerLog.toString().contains("Meeting"));
    assertTrue(managerLog.toString().contains("2025-10-24T10:00"));
    assertTrue(managerLog.toString().contains("TargetCal"));
    assertTrue(managerLog.toString().contains("2025-10-25T14:00"));
  }

  @Test
  public void testCopySingleEventWithExtraWords() {
    String cmd =
        "copy xyz event abc \"Meeting\" on def 2025-10-24T10:00 ghi --target jkl TargetCal to mno "
            + "2025-10-25T14:00";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("copyEvent"));
    assertTrue(managerLog.toString().contains("Meeting"));
  }

  @Test
  public void testCopyEventsOnDate() {
    String cmd = "copy events on 2025-10-24 --target TargetCal to 2025-10-25";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("copyEventsOnDate"));
    assertTrue(managerLog.toString().contains("2025-10-24"));
    assertTrue(managerLog.toString().contains("TargetCal"));
    assertTrue(managerLog.toString().contains("2025-10-25"));
  }

  @Test
  public void testCopyEventsOnDateWithExtraWords() {
    String cmd =
        "copy xyz events abc on def 2025-10-24 ghi --target jkl TargetCal to mno 2025-10-25";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("copyEventsOnDate"));
  }

  @Test
  public void testCopyEventsBetween() {
    String cmd = "copy events between 2025-10-24 and 2025-10-30 --target TargetCal to 2025-11-01";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("copyEventsBetween"));
    assertTrue(managerLog.toString().contains("2025-10-24"));
    assertTrue(managerLog.toString().contains("2025-10-30"));
    assertTrue(managerLog.toString().contains("TargetCal"));
    assertTrue(managerLog.toString().contains("2025-11-01"));
  }

  @Test
  public void testCopyEventsBetweenWithExtraWords() {
    String cmd =
        "copy xyz events abc between def 2025-10-24 ghi and jkl 2025-10-30 mno --target pqr "
            + "TargetCal to stu 2025-11-01";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("copyEventsBetween"));
  }

  @Test
  public void testCopySingleEventDifferentTimezones() {
    String cmd = "copy event \"Event1\" on 2025-10-24T10:00 --target Cal2 to 2025-10-24T15:00";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("Event1"));
    assertTrue(managerLog.toString().contains("Cal2"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopySingleEventMissingSubject() {
    String cmd = "copy event on 2025-10-24T10:00 --target TargetCal to 2025-10-25T14:00";
    command.execute(cmd, mockManager, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopySingleEventMissingSourceDateTime() {
    String cmd = "copy event \"Meeting\" --target TargetCal to 2025-10-25T14:00";
    command.execute(cmd, mockManager, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopySingleEventMissingTarget() {
    String cmd = "copy event \"Meeting\" on 2025-10-24T10:00 to 2025-10-25T14:00";
    command.execute(cmd, mockManager, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopySingleEventMissingTargetDateTime() {
    String cmd = "copy event \"Meeting\" on 2025-10-24T10:00 --target TargetCal";
    command.execute(cmd, mockManager, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsOnDateMissingSourceDate() {
    String cmd = "copy events on --target TargetCal to 2025-10-25";
    command.execute(cmd, mockManager, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsOnDateMissingTarget() {
    String cmd = "copy events on 2025-10-24 to 2025-10-25";
    command.execute(cmd, mockManager, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsOnDateMissingTargetDate() {
    String cmd = "copy events on 2025-10-24 --target TargetCal";
    command.execute(cmd, mockManager, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsBetweenMissingStartDate() {
    String cmd = "copy events between and 2025-10-30 --target TargetCal to 2025-11-01";
    command.execute(cmd, mockManager, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsBetweenMissingEndDate() {
    String cmd = "copy events between 2025-10-24 and --target TargetCal to 2025-11-01";
    command.execute(cmd, mockManager, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsBetweenMissingTarget() {
    String cmd = "copy events between 2025-10-24 and 2025-10-30 to 2025-11-01";
    command.execute(cmd, mockManager, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsBetweenMissingTargetDate() {
    String cmd = "copy events between 2025-10-24 and 2025-10-30 --target TargetCal";
    command.execute(cmd, mockManager, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCopyCommandFormat() {
    String cmd = "copy invalid format";
    command.execute(cmd, mockManager, view);
  }

  @Test
  public void testCopySingleEventWithQuotedSubject() {
    String cmd = "copy event \"Team Meeting\" on 2025-10-24T10:00 --target TargetCal "
        + "to 2025-10-25T14:00";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("copyEvent"));
    assertTrue(managerLog.toString().contains("Team Meeting"));
  }

  @Test
  public void testCopySingleEventWithQuotedSubjectExtraWords() {
    String cmd = "copy event abc \"Team Meeting\" on def 2025-10-24T10:00 --target TargetCal "
        + "to 2025-10-25T14:00";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("copyEvent"));
    assertTrue(managerLog.toString().contains("Team Meeting"));
  }

  @Test
  public void testCopySingleWordSubjectWithExtraWords() {
    String cmd = "copy event Meeting abc on 2025-10-24T10:00 --target TargetCal "
        + "to 2025-10-25T14:00";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("copyEvent"));
    assertTrue(managerLog.toString().contains("Meeting"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventOnWithoutSubject() {
    String cmd = "copy event on 2025-10-24T10:00 --target TargetCal to 2025-10-25T14:00";
    command.execute(cmd, mockManager, view);
  }

  @Test
  public void testCopyQuotedSubjectWithSpecialCharacters() {
    String cmd = "copy event \"Meeting@Room-A!\" on 2025-10-24T10:00 --target TargetCal "
        + "to 2025-10-25T14:00";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("copyEvent"));
    assertTrue(managerLog.toString().contains("Meeting@Room-A!"));
  }

  @Test
  public void testCopyQuotedSubjectWithNumbers() {
    String cmd = "copy event \"Meeting 123\" on 2025-10-24T10:00 --target TargetCal "
        + "to 2025-10-25T14:00";
    command.execute(cmd, mockManager, view);

    assertTrue(managerLog.toString().contains("copyEvent"));
    assertTrue(managerLog.toString().contains("Meeting 123"));
  }
}