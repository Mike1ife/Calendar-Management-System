package controller.command.event;

import static org.junit.Assert.assertTrue;

import controller.command.evet.CreateEventCommand;
import controller.mock.MockCalendarModel;
import model.calendar.CalendarModelInterface;
import org.junit.Before;
import org.junit.Test;
import view.CalendarTextView;
import view.CalendarViewInterface;

/**
 * Test for create event command.
 */
public class CreateEventCommandTest {
  private CreateEventCommand command;
  private StringBuilder modelLog;
  private CalendarModelInterface mockModel;
  private StringBuilder viewOutput;
  private CalendarViewInterface view;

  /**
   * Create command, log, model, and view before every test.
   */
  @Before
  public void setUp() {
    command = new CreateEventCommand();
    modelLog = new StringBuilder();
    mockModel = new MockCalendarModel(modelLog);
    viewOutput = new StringBuilder();
    view = new CalendarTextView(viewOutput);
  }

  @Test
  public void testCanHandle() {
    assertTrue(command.canHandle("create event \"Test\""));
    assertTrue(command.canHandle("CREATE EVENT \"Test\""));
    assertTrue(command.canHandle("create something event \"Test\""));
  }

  @Test
  public void testCreateSingleEventWithTime() {
    String cmd = "create event \"Meeting\" from 2025-10-24T10:00 to 2025-10-24T11:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("createSingleEventWithTime"));
    assertTrue(modelLog.toString().contains("Meeting"));
    assertTrue(modelLog.toString().contains("2025-10-24T10:00"));
    assertTrue(viewOutput.toString().contains("Success"));
  }

  @Test
  public void testCreateSingleEventWithTimeAndExtraWords() {
    String cmd = "create xyz 123 event abc \"Meeting\" from junk 2025-10-24T10:00 to stuff "
        + "2025-10-24T11:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("createSingleEventWithTime"));
    assertTrue(modelLog.toString().contains("Meeting"));
  }

  @Test
  public void testCreateAllDayEvent() {
    String cmd = "create event \"Birthday\" on 2025-10-24";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("createAllDaySingleEvent"));
    assertTrue(modelLog.toString().contains("Birthday"));
    assertTrue(modelLog.toString().contains("2025-10-24"));
  }

  @Test
  public void testCreateAllDayEventWithExtraWords() {
    String cmd = "create xyz event abc \"Birthday\" on junk 2025-10-24";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("createAllDaySingleEvent"));
    assertTrue(modelLog.toString().contains("Birthday"));
  }

  @Test
  public void testCreateSeriesEventWithOccurrences() {
    String cmd = "create event \"Class\" from 2025-10-24T10:00 to 2025-10-24T11:00 repeats "
        + "MWF for 10 times";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("createSeriesEventWithOccurrence"));
    assertTrue(modelLog.toString().contains("Class"));
    assertTrue(modelLog.toString().contains("10"));
  }

  @Test
  public void testCreateSeriesEventWithOccurrencesExtraWords() {
    String cmd = "create xyz event \"Class\" from 2025-10-24T10:00 to 2025-10-24T11:00 repeats "
        + "junk MWF for abc 10 times";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("createSeriesEventWithOccurrence"));
  }

  @Test
  public void testCreateSeriesEventUntilDate() {
    String cmd = "create event \"Gym\" from 2025-10-24T10:00 to 2025-10-24T11:00 repeats TR "
        + "until 2025-12-31";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("createSeriesEventWithEndDate"));
    assertTrue(modelLog.toString().contains("Gym"));
    assertTrue(modelLog.toString().contains("2025-12-31"));
  }

  @Test
  public void testCreateSeriesEventUntilDateExtraWords() {
    String cmd = "create event \"Gym\" from 2025-10-24T10:00 to 2025-10-24T11:00 repeats abc TR "
        + "until xyz 2025-12-31";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("createSeriesEventWithEndDate"));
  }

  @Test
  public void testCreateAllDaySeriesWithOccurrences() {
    String cmd = "create event \"Holiday\" on 2025-10-24 repeats MWF for 5 times";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("createAllDaySeriesEventWithOccurrence"));
    assertTrue(modelLog.toString().contains("Holiday"));
  }

  @Test
  public void testCreateAllDaySeriesWithOccurrencesExtraWords() {
    String cmd = "create xyz event \"Holiday\" on abc 2025-10-24 repeats junk MWF for def 5 times";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("createAllDaySeriesEventWithOccurrence"));
  }

  @Test
  public void testCreateAllDaySeriesUntilDate() {
    String cmd = "create event \"Weekend\" on 2025-10-24 repeats SU until 2025-12-31";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("createAllDaySeriesEventWithEndDate"));
    assertTrue(modelLog.toString().contains("Weekend"));
  }

  @Test
  public void testCreateAllDaySeriesUntilDateExtraWords() {
    String cmd = "create xyz event \"Weekend\" on abc 2025-10-24 repeats def SU "
        + "until ghi 2025-12-31";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("createAllDaySeriesEventWithEndDate"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCommandNoSubject() {
    String cmd = "create event from 2025-10-24T10:00 to 2025-10-24T11:00";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCommandNoDateTime() {
    String cmd = "create event \"Meeting\"";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidSeriesNoWeekdays() {
    String cmd = "create event \"Class\" from 2025-10-24T10:00 to 2025-10-24T11:00 "
        + "repeats";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidSeriesNoOccurrencesOrEndDate() {
    String cmd = "create event \"Class\" from 2025-10-24T10:00 to 2025-10-24T11:00 repeats MWF";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidAllDaySeriesNoOccurrencesOrEndDate() {
    String cmd = "create event \"Holiday\" on 2025-10-24 repeats MWF";
    command.execute(cmd, mockModel, view);
  }

  @Test
  public void testSingleEventWithTimeMatchesFirst() {
    String cmd = "create event \"Test\" from 2025-10-24T10:00 to 2025-10-24T11:00";
    command.execute(cmd, mockModel, view);
    assertTrue(modelLog.toString().contains("createSingleEventWithTime"));
  }

  @Test
  public void testAllDayEventMatchesWhenNoTime() {
    String cmd = "create event \"Test\" on 2025-10-24";
    command.execute(cmd, mockModel, view);
    assertTrue(modelLog.toString().contains("createAllDaySingleEvent"));
  }

  @Test
  public void testCreateEventWithQuotedSubject() {
    String cmd = "create event \"Team Meeting\" from 2025-10-24T10:00 to 2025-10-24T11:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("createSingleEventWithTime"));
    assertTrue(modelLog.toString().contains("Team Meeting"));
    assertTrue(viewOutput.toString().contains("Success"));
  }

  @Test
  public void testCreateEventWithQuotedSubjectAndExtraWords() {
    String cmd = "create event abc \"Team Meeting\" from 2025-10-24T10:00 to 2025-10-24T11:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("createSingleEventWithTime"));
    assertTrue(modelLog.toString().contains("Team Meeting"));
  }

  @Test
  public void testCreateAllDayEventWithQuotedSubjectExtraWords() {
    String cmd = "create event xyz \"Birthday Party\" on 2025-10-24";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("createAllDaySingleEvent"));
    assertTrue(modelLog.toString().contains("Birthday Party"));
  }

  @Test
  public void testCreateAllDaySeriesWithQuotedSubject() {
    String cmd = "create event \"Holiday Break\" on 2025-10-24 repeats MWF for 5 times";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("createAllDaySeriesEventWithOccurrence"));
    assertTrue(modelLog.toString().contains("Holiday Break"));
  }

  @Test
  public void testSingleWordSubjectTakesPrecedencePosition() {
    String cmd = "create event Meeting abc on 2025-10-24";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("createAllDaySingleEvent"));
    assertTrue(modelLog.toString().contains("Meeting"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventOnWithoutSubject() {
    String cmd = "create event on 2025-10-24";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCommandNoDateTimeOrEndDate() {
    String cmd = "create event \"Meeting\" repeats MWF for 5 times";
    command.execute(cmd, mockModel, view);
  }
}