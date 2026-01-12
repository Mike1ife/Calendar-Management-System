package controller.command.event;

import static org.junit.Assert.assertTrue;

import controller.command.evet.EditEventCommand;
import controller.mock.MockCalendarModel;
import model.calendar.CalendarModelInterface;
import org.junit.Before;
import org.junit.Test;
import view.CalendarTextView;
import view.CalendarViewInterface;

/**
 * Test class for EditEventCommand.
 */
public class EditEventCommandTest {
  private EditEventCommand command;
  private StringBuilder modelLog;
  private CalendarModelInterface mockModel;
  private CalendarViewInterface view;

  /**
   * Create command, log, model, and view before every test.
   */
  @Before
  public void setUp() {
    command = new EditEventCommand();
    modelLog = new StringBuilder();
    mockModel = new MockCalendarModel(modelLog);
    StringBuilder viewOutput = new StringBuilder();
    view = new CalendarTextView(viewOutput);
  }

  @Test
  public void testCanHandleEditEvent() {
    assertTrue(command.canHandle("edit event Subject \"Test\""));
    assertTrue(command.canHandle("EDIT EVENT Subject \"Test\""));
  }

  @Test
  public void testCanHandleEditEvents() {
    assertTrue(command.canHandle("edit events Location \"Test\""));
    assertTrue(command.canHandle("EDIT EVENTS Location \"Test\""));
  }

  @Test
  public void testCanHandleEditSeries() {
    assertTrue(command.canHandle("edit series Description \"Test\""));
    assertTrue(command.canHandle("EDIT SERIES Description \"Test\""));
  }

  @Test
  public void testEditSingleEventSubject() {
    String cmd = "edit event Subject \"Meeting\" from 2025-10-24T10:00 to "
        + "2025-10-24T11:00 with NewMeeting";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSingleEvent"));
    assertTrue(modelLog.toString().contains("Meeting"));
    assertTrue(modelLog.toString().contains("Subject"));
    assertTrue(modelLog.toString().contains("NewMeeting"));
  }

  @Test
  public void testEditSingleEventStartDate() {
    String cmd = "edit event StartDate \"Meeting\" from 2025-10-24T10:00 to "
        + "2025-10-24T11:00 with 2025-10-25";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSingleEvent"));
    assertTrue(modelLog.toString().contains("StartDate"));
  }

  @Test
  public void testEditSingleEventStartTime() {
    String cmd = "edit event StartTime \"Meeting\" from 2025-10-24T10:00 to "
        + "2025-10-24T11:00 with 11:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSingleEvent"));
    assertTrue(modelLog.toString().contains("StartTime"));
  }

  @Test
  public void testEditSingleEventEndDate() {
    String cmd = "edit event EndDate \"Meeting\" from 2025-10-24T10:00 to "
        + "2025-10-24T11:00 with 2025-10-25";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSingleEvent"));
    assertTrue(modelLog.toString().contains("EndDate"));
  }

  @Test
  public void testEditSingleEventEndTime() {
    String cmd = "edit event EndTime \"Meeting\" from 2025-10-24T10:00 to "
        + "2025-10-24T11:00 with 12:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSingleEvent"));
    assertTrue(modelLog.toString().contains("EndTime"));
  }

  @Test
  public void testEditSingleEventLocation() {
    String cmd = "edit event Location \"Meeting\" from 2025-10-24T10:00 to "
        + "2025-10-24T11:00 with Conference Room";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSingleEvent"));
    assertTrue(modelLog.toString().contains("Location"));
    assertTrue(modelLog.toString().contains("Conference Room"));
  }

  @Test
  public void testEditSingleEventDescription() {
    String cmd = "edit event Description \"Meeting\" from 2025-10-24T10:00 to "
        + "2025-10-24T11:00 with New Description";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSingleEvent"));
    assertTrue(modelLog.toString().contains("Description"));
    assertTrue(modelLog.toString().contains("New Description"));
  }

  @Test
  public void testEditSingleEventIsPublic() {
    String cmd = "edit event IsPublic \"Meeting\" from 2025-10-24T10:00 to "
        + "2025-10-24T11:00 with true";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSingleEvent"));
    assertTrue(modelLog.toString().contains("IsPublic"));
  }

  @Test
  public void testEditSingleEventWithExtraWords() {
    String cmd = "edit xyz event abc Subject def \"Meeting\" from junk 2025-10-24T10:00 "
        + "to garbage 2025-10-24T11:00 with stuff NewMeeting";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSingleEvent"));
    assertTrue(modelLog.toString().contains("Meeting"));
    assertTrue(modelLog.toString().contains("Subject"));
  }

  @Test
  public void testEditEventsFromDateSubject() {
    String cmd = "edit events Subject \"Meeting\" from 2025-10-24T10:00 with Updated Meeting";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editEventStartFrom"));
    assertTrue(modelLog.toString().contains("Subject"));
    assertTrue(modelLog.toString().contains("Updated Meeting"));
  }

  @Test
  public void testEditEventsFromDateStartDate() {
    String cmd = "edit events StartDate \"Meeting\" from 2025-10-24T10:00 with 2025-10-25";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editEventStartFrom"));
    assertTrue(modelLog.toString().contains("StartDate"));
  }

  @Test
  public void testEditEventsFromDateStartTime() {
    String cmd = "edit events StartTime \"Meeting\" from 2025-10-24T10:00 with 11:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editEventStartFrom"));
    assertTrue(modelLog.toString().contains("StartTime"));
  }

  @Test
  public void testEditEventsFromDateEndDate() {
    String cmd = "edit events EndDate \"Meeting\" from 2025-10-24T10:00 with 2025-10-25";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editEventStartFrom"));
    assertTrue(modelLog.toString().contains("EndDate"));
  }

  @Test
  public void testEditEventsFromDateEndTime() {
    String cmd = "edit events EndTime \"Meeting\" from 2025-10-24T10:00 with 12:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editEventStartFrom"));
    assertTrue(modelLog.toString().contains("EndTime"));
  }

  @Test
  public void testEditEventsFromDateLocation() {
    String cmd = "edit events Location \"Meeting\" from 2025-10-24T10:00 with Conference Room A";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editEventStartFrom"));
    assertTrue(modelLog.toString().contains("Location"));
    assertTrue(modelLog.toString().contains("Conference Room A"));
  }

  @Test
  public void testEditEventsFromDateDescription() {
    String cmd = "edit events Description \"Meeting\" from 2025-10-24T10:00 with New Description";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editEventStartFrom"));
    assertTrue(modelLog.toString().contains("Description"));
  }

  @Test
  public void testEditEventsFromDateIsPublic() {
    String cmd = "edit events IsPublic \"Meeting\" from 2025-10-24T10:00 with false";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editEventStartFrom"));
    assertTrue(modelLog.toString().contains("IsPublic"));
  }

  @Test
  public void testEditEventsFromDateWithExtraWords() {
    String cmd = "edit xyz events abc Location def \"Meeting\" from ghi 2025-10-24T10:00 "
        + "with jkl Conference Room";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editEventStartFrom"));
    assertTrue(modelLog.toString().contains("Location"));
  }

  @Test
  public void testEditSeriesSubject() {
    String cmd = "edit series Subject \"Meeting\" from 2025-10-24T10:00 with Updated Series";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSeriesStartFrom"));
    assertTrue(modelLog.toString().contains("Subject"));
    assertTrue(modelLog.toString().contains("Updated Series"));
  }

  @Test
  public void testEditSeriesStartDate() {
    String cmd = "edit series StartDate \"Meeting\" from 2025-10-24T10:00 with 2025-10-25";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSeriesStartFrom"));
    assertTrue(modelLog.toString().contains("StartDate"));
  }

  @Test
  public void testEditSeriesStartTime() {
    String cmd = "edit series StartTime \"Meeting\" from 2025-10-24T10:00 with 11:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSeriesStartFrom"));
    assertTrue(modelLog.toString().contains("StartTime"));
  }

  @Test
  public void testEditSeriesEndDate() {
    String cmd = "edit series EndDate \"Meeting\" from 2025-10-24T10:00 with 2025-10-25";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSeriesStartFrom"));
    assertTrue(modelLog.toString().contains("EndDate"));
  }

  @Test
  public void testEditSeriesEndTime() {
    String cmd = "edit series EndTime \"Meeting\" from 2025-10-24T10:00 with 12:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSeriesStartFrom"));
    assertTrue(modelLog.toString().contains("EndTime"));
  }

  @Test
  public void testEditSeriesLocation() {
    String cmd = "edit series Location \"Meeting\" from 2025-10-24T10:00 with Room B";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSeriesStartFrom"));
    assertTrue(modelLog.toString().contains("Location"));
  }

  @Test
  public void testEditSeriesDescription() {
    String cmd = "edit series Description \"Meeting\" from 2025-10-24T10:00 "
        + "with Updated Description";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSeriesStartFrom"));
    assertTrue(modelLog.toString().contains("Description"));
  }

  @Test
  public void testEditSeriesIsPublic() {
    String cmd = "edit series IsPublic \"Meeting\" from 2025-10-24T10:00 with true";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSeriesStartFrom"));
    assertTrue(modelLog.toString().contains("IsPublic"));
  }

  @Test
  public void testEditSeriesWithExtraWords() {
    String cmd = "edit xyz series abc Description def \"Meeting\" from ghi 2025-10-24T10:00 "
        + "with jkl Updated Description";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSeriesStartFrom"));
    assertTrue(modelLog.toString().contains("Description"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPropertyName() {
    String cmd = "edit event InvalidProperty \"Test\" from 2025-10-24T10:00 to "
        + "2025-10-24T11:00 with Value";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingSubject() {
    String cmd = "edit event Subject from 2025-10-24T10:00 to 2025-10-24T11:00 with Value";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingFromTime() {
    String cmd = "edit event Subject \"Meeting\" to 2025-10-24T11:00 with Value";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingToTime() {
    String cmd = "edit event Subject \"Meeting\" from 2025-10-24T10:00 with Value";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingWithValue() {
    String cmd = "edit event Subject \"Meeting\" from 2025-10-24T10:00 to 2025-10-24T11:00";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventsFromDateMissingProperty() {
    String cmd = "edit events \"Meeting\" from 2025-10-24T10:00 with Value";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventsFromDateMissingSubject() {
    String cmd = "edit events Location from 2025-10-24T10:00 with Value";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventsFromDateMissingFromTime() {
    String cmd = "edit events Location \"Meeting\" with Value";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventsFromDateMissingWithValue() {
    String cmd = "edit events Location \"Meeting\" from 2025-10-24T10:00";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditSeriesMissingProperty() {
    String cmd = "edit series \"Meeting\" from 2025-10-24T10:00 with Value";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditSeriesMissingSubject() {
    String cmd = "edit series Description from 2025-10-24T10:00 with Value";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditSeriesMissingFromTime() {
    String cmd = "edit series Description \"Meeting\" with Value";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditSeriesMissingWithValue() {
    String cmd = "edit series Description \"Meeting\" from 2025-10-24T10:00";
    command.execute(cmd, mockModel, view);
  }

  @Test
  public void testEditSingleEventWithQuotedSubject() {
    String cmd = "edit event Subject \"Team Meeting\" from 2025-10-24T10:00 to "
        + "2025-10-24T11:00 with NewMeeting";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSingleEvent"));
    assertTrue(modelLog.toString().contains("Team Meeting"));
    assertTrue(modelLog.toString().contains("Subject"));
  }

  @Test
  public void testEditSingleEventWithQuotedSubjectExtraWords() {
    String cmd = "edit event Subject abc \"Team Meeting\" from 2025-10-24T10:00 to "
        + "2025-10-24T11:00 with NewMeeting";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSingleEvent"));
    assertTrue(modelLog.toString().contains("Team Meeting"));
  }

  @Test
  public void testEditEventsFromDateWithQuotedSubject() {
    String cmd = "edit events Subject \"Team Meeting\" from 2025-10-24T10:00 with UpdatedMeeting";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editEventStartFrom"));
    assertTrue(modelLog.toString().contains("Team Meeting"));
  }

  @Test
  public void testEditEventsFromDateWithQuotedSubjectExtraWords() {
    String cmd = "edit events Subject xyz \"Team Meeting\" from 2025-10-24T10:00 "
        + "with UpdatedMeeting";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editEventStartFrom"));
    assertTrue(modelLog.toString().contains("Team Meeting"));
  }

  @Test
  public void testEditSeriesWithQuotedSubject() {
    String cmd = "edit series Subject \"Team Meeting\" from 2025-10-24T10:00 with UpdatedSeries";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSeriesStartFrom"));
    assertTrue(modelLog.toString().contains("Team Meeting"));
  }

  @Test
  public void testEditSeriesWithQuotedSubjectExtraWords() {
    String cmd = "edit series Subject abc \"Team Meeting\" from 2025-10-24T10:00 "
        + "with UpdatedSeries";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSeriesStartFrom"));
    assertTrue(modelLog.toString().contains("Team Meeting"));
  }

  @Test
  public void testEditSingleWordSubjectTakesPrecedence() {
    String cmd = "edit event Subject Meeting abc from 2025-10-24T10:00 to 2025-10-24T11:00 "
        + "with NewMeeting";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSingleEvent"));
    assertTrue(modelLog.toString().contains("Meeting"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventSubjectFromWithoutSubject() {
    String cmd = "edit event Subject from 2025-10-24T10:00 to 2025-10-24T11:00 with Value";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventSubjectWithWithoutSubject() {
    String cmd = "edit event Subject with Value";
    command.execute(cmd, mockModel, view);
  }

  @Test
  public void testEditQuotedSubjectWithSpecialCharacters() {
    String cmd = "edit event Subject \"Meeting@Room-A\" from 2025-10-24T10:00 to 2025-10-24T11:00 "
        + "with NewMeeting";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("editSingleEvent"));
    assertTrue(modelLog.toString().contains("Meeting@Room-A"));
  }
}