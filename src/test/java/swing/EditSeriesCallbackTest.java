package swing;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import model.calendar.CalendarModelInterface;
import model.calendar.Weekday;
import model.event.EventFactory;
import model.event.EventInterface;
import model.event.EventReadOnlyInterface;
import model.event.SeriesInterface;
import org.junit.Before;
import org.junit.Test;
import swing.controller.callbacks.EditSeriesCallback;
import swing.mock.MockCalendarModel;
import swing.view.dialogs.event.data.SeriesData;

/**
 * Tests for {@link EditSeriesCallback}, verifying that editing a specific event within a series—
 * or editing an entire recurring series starting from a selected occurrence—results in the correct
 * sequence of model method calls.
 * All interactions with the calendar model are logged using {@link MockCalendarModel}, and the
 * resulting log output is compared against expected method-call sequences.
 */
public class EditSeriesCallbackTest {
  private StringBuilder calendarLog;
  private CalendarModelInterface mockCalendar;

  /**
   * Initializes a fresh mock calendar model and log buffer before each test case. This ensures
   * clean isolation between tests and accurate verification of callback behavior.
   */
  @Before
  public void setUp() {
    calendarLog = new StringBuilder();
    mockCalendar = new MockCalendarModel(calendarLog);
  }

  @Test
  public void testOnSaveSeriesSingle1() {
    SeriesInterface dummySeries =
        EventFactory.createSeriesWithOccurrence(Set.of(Weekday.TUESDAY, Weekday.FRIDAY), 5);
    List<EventInterface> dummyEvents =
        dummySeries.generateEvents("dummy",
            LocalDateTime.parse("2025-11-13T11:00"),
            LocalTime.parse("12:00"));

    EventReadOnlyInterface dummy = dummyEvents.get(0);
    SeriesData data = new SeriesData(
        "new dummy", "2025-11-14T11:00",
        "2025-11-14T12:00", "dummy", "dummy", "public",
        false, "Mock", false, false
    );

    EditSeriesCallback callback =
        new EditSeriesCallback(mockCalendar, dummy);

    callback.onSave(data);
    String result =
        "editSingleEvent: dummy, subject, 2025-11-14T11:00, 2025-11-14T12:00, new dummy\n"
            + "editSingleEvent: new dummy, description, 2025-11-14T11:00, 2025-11-14T12:00, dummy\n"
            + "editSingleEvent: new dummy, location, 2025-11-14T11:00, 2025-11-14T12:00, dummy\n"
            + "editSingleEvent: new dummy, status, 2025-11-14T11:00, 2025-11-14T12:00, public\n";
    assertEquals(result, calendarLog.toString());
  }

  @Test
  public void testOnSaveSeriesSingle2() {
    SeriesInterface dummySeries =
        EventFactory.createSeriesWithOccurrence(Set.of(Weekday.TUESDAY, Weekday.FRIDAY), 5);
    List<EventInterface> dummyEvents =
        dummySeries.generateEvents("dummy",
            LocalDateTime.parse("2025-11-13T11:00"),
            LocalTime.parse("12:00"));

    EventReadOnlyInterface dummy = dummyEvents.get(0);
    SeriesData data = new SeriesData(
        "new dummy", "2025-11-14T11:00",
        "2025-11-14T12:00", "dummy", "dummy", "public",
        false, "Mock", true, false
    );

    EditSeriesCallback callback =
        new EditSeriesCallback(mockCalendar, dummy);

    callback.onSave(data);
    String result =
        "editEventStartFrom: dummy, subject, 2025-11-14T11:00, new dummy\n"
            + "editEventStartFrom: new dummy, description, 2025-11-14T11:00, dummy\n"
            + "editEventStartFrom: new dummy, location, 2025-11-14T11:00, dummy\n"
            + "editEventStartFrom: new dummy, status, 2025-11-14T11:00, public\n";
    assertEquals(result, calendarLog.toString());
  }

  @Test
  public void testOnSaveSeriesSingle3() {
    SeriesInterface dummySeries =
        EventFactory.createSeriesWithOccurrence(Set.of(Weekday.TUESDAY, Weekday.FRIDAY), 5);
    List<EventInterface> dummyEvents =
        dummySeries.generateEvents("dummy",
            LocalDateTime.parse("2025-11-13T11:00"),
            LocalTime.parse("12:00"));

    EventReadOnlyInterface dummy = dummyEvents.get(0);
    SeriesData data = new SeriesData(
        "new dummy", "2025-11-14T11:00",
        "2025-11-14T12:00", "dummy", "dummy", "public",
        false, "Mock", false, true
    );

    EditSeriesCallback callback =
        new EditSeriesCallback(mockCalendar, dummy);

    callback.onSave(data);
    String result =
        "editSeriesStartFrom: dummy, subject, 2025-11-14T11:00, new dummy\n"
            + "editSeriesStartFrom: new dummy, description, 2025-11-14T11:00, dummy\n"
            + "editSeriesStartFrom: new dummy, location, 2025-11-14T11:00, dummy\n"
            + "editSeriesStartFrom: new dummy, status, 2025-11-14T11:00, public\n";
    assertEquals(result, calendarLog.toString());
  }

  @Test
  public void testOnCancel() {
    SeriesInterface dummySeries =
        EventFactory.createSeriesWithOccurrence(Set.of(Weekday.TUESDAY, Weekday.FRIDAY), 5);
    List<EventInterface> dummyEvents =
        dummySeries.generateEvents("dummy",
            LocalDateTime.parse("2025-11-13T11:00"),
            LocalTime.parse("12:00"));
    EventReadOnlyInterface dummy = dummyEvents.get(0);
    EditSeriesCallback callback =
        new EditSeriesCallback(mockCalendar, dummy);
    callback.onCancel();
  }
}
