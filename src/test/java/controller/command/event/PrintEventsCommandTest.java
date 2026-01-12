package controller.command.event;

import static org.junit.Assert.assertTrue;

import controller.command.evet.PrintEventsCommand;
import controller.mock.MockCalendarModel;
import model.calendar.CalendarModelInterface;
import org.junit.Before;
import org.junit.Test;
import view.CalendarTextView;
import view.CalendarViewInterface;

/**
 * Test class for PrintEventsCommand.
 */
public class PrintEventsCommandTest {
  private PrintEventsCommand command;
  private StringBuilder modelLog;
  private CalendarModelInterface mockModel;
  private StringBuilder viewOutput;
  private CalendarViewInterface view;

  /**
   * Create command, log, model, and view before every test.
   */
  @Before
  public void setUp() {
    command = new PrintEventsCommand();
    modelLog = new StringBuilder();
    mockModel = new MockCalendarModel(modelLog);
    viewOutput = new StringBuilder();
    view = new CalendarTextView(viewOutput);
  }

  @Test
  public void testCanHandle() {
    assertTrue(command.canHandle("print events on 2025-10-24"));
    assertTrue(command.canHandle("PRINT EVENTS on 2025-10-24"));
    assertTrue(command.canHandle("print xyz events abc on 2025-10-24"));
  }

  @Test
  public void testPrintEventsOn() {
    String cmd = "print events on 2025-10-24";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("getEventsOnDate"));
    assertTrue(modelLog.toString().contains("2025-10-24"));
  }

  @Test
  public void testPrintEventsOnCaseInsensitive() {
    String cmd = "PRINT EVENTS ON 2025-10-24";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("getEventsOnDate"));
    assertTrue(modelLog.toString().contains("2025-10-24"));
  }

  @Test
  public void testPrintEventsOnWithExtraWords() {
    String cmd = "print xyz events abc on def 2025-10-24";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("getEventsOnDate"));
    assertTrue(modelLog.toString().contains("2025-10-24"));
  }

  @Test
  public void testPrintEventsOnDifferentDates() {
    String cmd1 = "print events on 2025-01-01";
    command.execute(cmd1, mockModel, view);
    assertTrue(modelLog.toString().contains("2025-01-01"));

    modelLog.setLength(0);

    String cmd2 = "print events on 2025-12-31";
    command.execute(cmd2, mockModel, view);
    assertTrue(modelLog.toString().contains("2025-12-31"));
  }

  @Test
  public void testPrintEventsFrom() {
    String cmd = "print events from 2025-10-24T10:00 to 2025-10-24T15:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("getEventsInRange"));
    assertTrue(modelLog.toString().contains("2025-10-24T10:00"));
    assertTrue(modelLog.toString().contains("2025-10-24T15:00"));
  }

  @Test
  public void testPrintEventsFromCaseInsensitive() {
    String cmd = "PRINT EVENTS FROM 2025-10-24T10:00 TO 2025-10-24T15:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("getEventsInRange"));
  }

  @Test
  public void testPrintEventsFromWithExtraWords() {
    String cmd = "print xyz events abc from def 2025-10-24T10:00 ghi to jkl 2025-10-24T15:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("getEventsInRange"));
    assertTrue(modelLog.toString().contains("2025-10-24T10:00"));
    assertTrue(modelLog.toString().contains("2025-10-24T15:00"));
  }

  @Test
  public void testPrintEventsFromDifferentTimes() {
    String cmd = "print events from 2025-10-24T08:00 to 2025-10-24T17:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("2025-10-24T08:00"));
    assertTrue(modelLog.toString().contains("2025-10-24T17:00"));
  }

  @Test
  public void testPrintEventsFromAcrossDays() {
    String cmd = "print events from 2025-10-24T10:00 to 2025-10-26T15:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("getEventsInRange"));
    assertTrue(modelLog.toString().contains("2025-10-24T10:00"));
    assertTrue(modelLog.toString().contains("2025-10-26T15:00"));
  }

  @Test
  public void testPrintEventsOnDisplaysOutput() {
    String cmd = "print events on 2025-10-24";
    command.execute(cmd, mockModel, view);

    assertTrue(viewOutput.toString().contains("Events on 2025-10-24"));
  }

  @Test
  public void testPrintEventsFromDisplaysOutput() {
    String cmd = "print events from 2025-10-24T10:00 to 2025-10-24T15:00";
    command.execute(cmd, mockModel, view);

    assertTrue(viewOutput.toString().contains("Events from 2025-10-24T10:00 to 2025-10-24T15:00"));
  }

  @Test
  public void testPrintEventsOnTakesPrecedenceOverFrom() {
    String cmd = "print events on 2025-10-25 from 2025-10-25T10:00 to 2025-10-25T15:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("getEventsInRange"));
    assertTrue(modelLog.toString().contains("2025-10-25"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPrintCommand() {
    String cmd = "print events";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintEventsWithoutOnOrFrom() {
    String cmd = "print events 2025-10-24";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintEventsOnMissingDate() {
    String cmd = "print events on";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintEventsOnInvalidDateFormat() {
    String cmd = "print events on invalid-date";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintEventsFromMissingStartTime() {
    String cmd = "print events from to 2025-10-24T15:00";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintEventsFromMissingEndTime() {
    String cmd = "print events from 2025-10-24T10:00 to";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintEventsFromMissingTo() {
    String cmd = "print events from 2025-10-24T10:00";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintEventsFromInvalidStartTimeFormat() {
    String cmd = "print events from invalid-time to 2025-10-24T15:00";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintEventsFromInvalidEndTimeFormat() {
    String cmd = "print events from 2025-10-24T10:00 to invalid-time";
    command.execute(cmd, mockModel, view);
  }

  @Test
  public void testPrintEventsOnMultipleTimes() {
    String cmd1 = "print events on 2025-10-24";
    command.execute(cmd1, mockModel, view);
    assertTrue(modelLog.toString().contains("getEventsOnDate"));

    modelLog.setLength(0);
    viewOutput.setLength(0);

    String cmd2 = "print events on 2025-10-25";
    command.execute(cmd2, mockModel, view);
    assertTrue(modelLog.toString().contains("getEventsOnDate"));
    assertTrue(modelLog.toString().contains("2025-10-25"));
  }

  @Test
  public void testPrintEventsFromMultipleTimes() {
    String cmd1 = "print events from 2025-10-24T10:00 to 2025-10-24T12:00";
    command.execute(cmd1, mockModel, view);
    assertTrue(modelLog.toString().contains("getEventsInRange"));

    modelLog.setLength(0);
    viewOutput.setLength(0);

    String cmd2 = "print events from 2025-10-25T14:00 to 2025-10-25T16:00";
    command.execute(cmd2, mockModel, view);
    assertTrue(modelLog.toString().contains("getEventsInRange"));
    assertTrue(modelLog.toString().contains("2025-10-25T14:00"));
  }

  @Test
  public void testPrintEventsFromWithMixedCase() {
    String cmd = "PrInT eVeNtS fRoM 2025-10-24T10:00 tO 2025-10-24T15:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("getEventsInRange"));
  }

  @Test
  public void testPrintEventsOnWithMixedCase() {
    String cmd = "PrInT eVeNtS oN 2025-10-24";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("getEventsOnDate"));
  }
}