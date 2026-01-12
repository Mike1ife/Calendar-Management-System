package swing;

import static org.junit.Assert.assertEquals;

import model.calendar.CalendarManagerInterface;
import org.junit.Before;
import org.junit.Test;
import swing.controller.callbacks.CreateEventCallback;
import swing.mock.MockCalendarManager;
import swing.view.dialogs.event.data.EventData;

/**
 * Tests for {@link CreateEventCallback}, validating that saving various types of events—
 * single events, all-day events, recurring events, and series with end dates—produces the
 * correct sequence of interactions with the calendar manager. All interactions are recorded
 * using a {@link MockCalendarManager}, and expected log output is compared against the
 * aggregated result.
 */
public class CreateEventCallbackTest {
  private StringBuilder calendarManagerLog;
  private CalendarManagerInterface mockCalendarManager;

  /**
   * Sets up a fresh mock calendar manager and log buffer before each test execution.
   * This ensures test isolation and accurate method-call tracing.
   */
  @Before
  public void setUp() {
    calendarManagerLog = new StringBuilder();
    mockCalendarManager = new MockCalendarManager(calendarManagerLog);
  }

  @Test
  public void testOnSave1() {
    EventData data =
        new EventData(
            "Squash", "2025-11-23T11:00", "2025-11-23T12:00",
            "Squash Square", "Double", "private",
            false, false, null, null, null,
            "NEU");

    CreateEventCallback callback = new CreateEventCallback(mockCalendarManager);
    callback.onSave(data);

    String result = "activateCalendar: NEU\n"
        + "getActiveCalendar\n"
        + "createSingleEventWithTime: Squash, 2025-11-23T11:00, 2025-11-23T12:00\n"
        + "getActiveCalendar\n"
        + "editSingleEvent: Squash, location, 2025-11-23T11:00, 2025-11-23T12:00, Squash Square\n"
        + "getActiveCalendar\n"
        + "editSingleEvent: Squash, description, 2025-11-23T11:00, 2025-11-23T12:00, Double\n"
        + "getActiveCalendar\n"
        + "editSingleEvent: Squash, status, 2025-11-23T11:00, 2025-11-23T12:00, private\n";
    assertEquals(result, calendarManagerLog.toString());
  }

  @Test
  public void testOnSave2() {
    EventData data =
        new EventData(
            "Squash", "2025-11-23T08:00", "2025-11-23T17:00",
            "", "", "",
            true, false, null, null, null,
            "NEU");

    CreateEventCallback callback = new CreateEventCallback(mockCalendarManager);
    callback.onSave(data);

    String result = "activateCalendar: NEU\n"
        + "getActiveCalendar\n"
        + "createAllDaySingleEvent: Squash, 2025-11-23\n";
    assertEquals(result, calendarManagerLog.toString());
  }

  @Test
  public void testOnSave3() {
    EventData data =
        new EventData(
            "PDP", "2025-11-23T13:35", "2025-11-23T15:15",
            "SN", "Course", "private",
            false, true, "TF", 5, null,
            "NEU");
    CreateEventCallback callback = new CreateEventCallback(mockCalendarManager);
    callback.onSave(data);

    String result = "activateCalendar: NEU\n"
        + "getActiveCalendar\n"
        + "createSeriesEventWithOccurrence: PDP, 2025-11-23T13:35, 2025-11-23T15:15, "
        + "[TUESDAY, FRIDAY], 5\n"
        + "getActiveCalendar\n"
        + "editSeriesStartFrom: PDP, location, 2025-11-25T13:35, SN\n"
        + "getActiveCalendar\n"
        + "editSeriesStartFrom: PDP, description, 2025-11-25T13:35, Course\n"
        + "getActiveCalendar\n"
        + "editSeriesStartFrom: PDP, status, 2025-11-25T13:35, private\n";
    assertEquals(result, calendarManagerLog.toString());
  }

  @Test
  public void testOnSave4() {
    EventData data =
        new EventData(
            "PDP", "2025-11-23T13:35", "2025-11-23T15:15",
            "", "", "",
            false, true, "TF", null, "2025-12-16",
            "NEU");
    CreateEventCallback callback = new CreateEventCallback(mockCalendarManager);
    callback.onSave(data);

    String result = "activateCalendar: NEU\n"
        + "getActiveCalendar\n"
        + "createSeriesEventWithEndDate: PDP, 2025-11-23T13:35, 2025-11-23T15:15, "
        + "[TUESDAY, FRIDAY], 2025-12-16\n";
    assertEquals(result, calendarManagerLog.toString());
  }

  @Test
  public void testOnSave5() {
    EventData data =
        new EventData(
            "PDP", "2025-11-23T13:35", "2025-11-23T15:15",
            "", "", "",
            false, true, "TF", null, "2025-10-16",
            "NEU");
    CreateEventCallback callback = new CreateEventCallback(mockCalendarManager);
    callback.onSave(data);

    String result = "activateCalendar: NEU\n"
        + "getActiveCalendar\n"
        + "createSeriesEventWithEndDate: PDP, 2025-11-23T13:35, 2025-11-23T15:15, "
        + "[TUESDAY, FRIDAY], 2025-10-16\n";
    assertEquals(result, calendarManagerLog.toString());
  }

  @Test
  public void testOnSave6() {
    EventData data =
        new EventData(
            "PDP", "2025-11-23T08:00", "2025-11-23T17:00",
            "SN", "Course", "private",
            true, true, "TF", null, "2025-12-15",
            "NEU");
    CreateEventCallback callback = new CreateEventCallback(mockCalendarManager);
    callback.onSave(data);

    String result = "activateCalendar: NEU\n"
        + "getActiveCalendar\n"
        + "createAllDaySeriesEventWithEndDate: PDP, 2025-11-23, [TUESDAY, FRIDAY], 2025-12-15\n"
        + "getActiveCalendar\n"
        + "editSeriesStartFrom: PDP, location, 2025-11-25T08:00, SN\n"
        + "getActiveCalendar\n"
        + "editSeriesStartFrom: PDP, description, 2025-11-25T08:00, Course\n"
        + "getActiveCalendar\n"
        + "editSeriesStartFrom: PDP, status, 2025-11-25T08:00, private\n";
    assertEquals(result, calendarManagerLog.toString());
  }

  @Test
  public void testOnSave7() {
    EventData data =
        new EventData(
            "Squash", "2025-11-23T08:00", "2025-11-23T17:00",
            "Squash Square", "Double", "private",
            true, true, "TF", 5, null,
            "NEU");

    CreateEventCallback callback = new CreateEventCallback(mockCalendarManager);
    callback.onSave(data);

    String result = "activateCalendar: NEU\n"
        + "getActiveCalendar\n"
        + "createAllDaySeriesEventWithOccurrence: Squash, 2025-11-23, [TUESDAY, FRIDAY], 5\n"
        + "getActiveCalendar\n"
        + "editSeriesStartFrom: Squash, location, 2025-11-25T08:00, Squash Square\n"
        + "getActiveCalendar\n"
        + "editSeriesStartFrom: Squash, description, 2025-11-25T08:00, Double\n"
        + "getActiveCalendar\n"
        + "editSeriesStartFrom: Squash, status, 2025-11-25T08:00, private\n";
    assertEquals(result, calendarManagerLog.toString());
  }

  @Test
  public void testOnCancel() {
    CreateEventCallback callback = new CreateEventCallback(mockCalendarManager);
    callback.onCancel();
  }
}
