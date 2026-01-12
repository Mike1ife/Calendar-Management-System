package swing;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import model.calendar.CalendarModelInterface;
import model.event.EventFactory;
import model.event.EventInterface;
import org.junit.Before;
import org.junit.Test;
import swing.controller.callbacks.EditEventOptionCallback;
import swing.mock.MockCalendarModel;
import swing.view.dialogs.event.data.EventEditData;

/**
 * Tests for {@link EditEventOptionCallback}, validating that editing an event—whether updating
 * a single occurrence, starting from a selected instance, or applying changes to the entire
 * series—invokes the correct sequence of calendar model operations. All interactions are logged
 * using {@link MockCalendarModel} and matched against expected outputs.
 */
public class EditEventOptionCallbackTest {
  private StringBuilder calendarLog;
  private CalendarModelInterface mockCalendar;

  /**
   * Initializes a new mock calendar model and fresh log buffer before each test case, ensuring
   * consistent isolation and accurate verification of method calls.
   */
  @Before
  public void setUp() {
    calendarLog = new StringBuilder();
    mockCalendar = new MockCalendarModel(calendarLog);
  }

  @Test
  public void testOnSaveSingleEvent1() {
    EventInterface dummy = EventFactory.createSingleEvent("dummy",
        LocalDateTime.parse("2025-11-13T11:00"), LocalDateTime.parse("2025-11-13T12:00"));
    EventEditData data = new EventEditData("new dummy", "2025-11-13T11:00",
        "2025-11-13T12:00", "dummy", "dummy", "dummy",
        false, "Mock", false, false
    );
    EditEventOptionCallback callback =
        new EditEventOptionCallback(mockCalendar, dummy);

    callback.onSave(data);
    String result =
        "editSingleEvent: dummy, subject, 2025-11-13T11:00, 2025-11-13T12:00, new dummy\n"
            + "editSingleEvent: new dummy, description, 2025-11-13T11:00, 2025-11-13T12:00, dummy\n"
            + "editSingleEvent: new dummy, location, 2025-11-13T11:00, 2025-11-13T12:00, dummy\n"
            + "editSingleEvent: new dummy, status, 2025-11-13T11:00, 2025-11-13T12:00, dummy\n";
    assertEquals(result, calendarLog.toString());
  }

  @Test
  public void testOnSaveSingleEvent2() {
    EventInterface dummy = EventFactory.createSingleEvent("dummy",
        LocalDateTime.parse("2025-11-13T11:00"), LocalDateTime.parse("2025-11-13T12:00"));
    EventEditData data = new EventEditData("dummy", "2025-11-13T12:00",
        "2025-11-13T12:30", "", "", "",
        false, "Mock", false, false
    );
    EditEventOptionCallback callback =
        new EditEventOptionCallback(mockCalendar, dummy);

    callback.onSave(data);
    String result =
        "editSingleEvent: dummy, start, 2025-11-13T11:00, 2025-11-13T12:00, 2025-11-13T12:00\n"
            + "editSingleEvent: dummy, end, 2025-11-13T12:00, 2025-11-13T13:00, 2025-11-13T12:30\n";
    assertEquals(result, calendarLog.toString());
  }

  @Test
  public void testOnSaveSingleEventStartFrom1() {
    EventInterface dummy = EventFactory.createSingleEvent("dummy",
        LocalDateTime.parse("2025-11-13T11:00"), LocalDateTime.parse("2025-11-13T12:00"));
    EventEditData data = new EventEditData("dummy", "2025-11-13T12:00",
        "2025-11-13T12:30", "", "", "",
        false, "Mock", true, false
    );
    EditEventOptionCallback callback =
        new EditEventOptionCallback(mockCalendar, dummy);

    callback.onSave(data);
    String result = "editEventStartFrom: dummy, start, 2025-11-13T11:00, 2025-11-13T12:00\n"
        + "editEventStartFrom: dummy, end, 2025-11-13T12:00, 2025-11-13T12:30\n";
    assertEquals(result, calendarLog.toString());
  }

  @Test
  public void testOnSaveSingleEventStartFrom2() {
    EventInterface dummy = EventFactory.createSingleEvent("dummy",
        LocalDateTime.parse("2025-11-13T11:00"), LocalDateTime.parse("2025-11-13T12:00"));
    EventEditData data = new EventEditData("new dummy", "2025-11-13T11:00",
        "2025-11-13T12:00", "dummy", "dummy", "public",
        false, "Mock", true, false
    );
    EditEventOptionCallback callback =
        new EditEventOptionCallback(mockCalendar, dummy);

    callback.onSave(data);
    String result = "editEventStartFrom: dummy, subject, 2025-11-13T11:00, new dummy\n"
        + "editEventStartFrom: new dummy, description, 2025-11-13T11:00, dummy\n"
        + "editEventStartFrom: new dummy, location, 2025-11-13T11:00, dummy\n"
        + "editEventStartFrom: new dummy, status, 2025-11-13T11:00, public\n";
    assertEquals(result, calendarLog.toString());
  }

  @Test
  public void testOnSaveSingleEventAllSeries1() {
    EventInterface dummy = EventFactory.createSingleEvent("dummy",
        LocalDateTime.parse("2025-11-13T11:00"), LocalDateTime.parse("2025-11-13T12:00"));
    EventEditData data = new EventEditData("dummy", "2025-11-13T12:00",
        "2025-11-13T12:30", "", "", "",
        false, "Mock", false, true
    );
    EditEventOptionCallback callback =
        new EditEventOptionCallback(mockCalendar, dummy);

    callback.onSave(data);
    String result = "editSeriesStartFrom: dummy, start, 2025-11-13T11:00, 2025-11-13T12:00\n"
        + "editSeriesStartFrom: dummy, end, 2025-11-13T12:00, 2025-11-13T12:30\n";
    assertEquals(result, calendarLog.toString());
  }

  @Test
  public void testOnSaveSingleEventAllSeries2() {
    EventInterface dummy = EventFactory.createSingleEvent("dummy",
        LocalDateTime.parse("2025-11-13T11:00"), LocalDateTime.parse("2025-11-13T12:00"));
    EventEditData data = new EventEditData("new dummy", "2025-11-13T11:00",
        "2025-11-13T12:00", "dummy", "dummy", "public",
        false, "Mock", false, true
    );
    EditEventOptionCallback callback =
        new EditEventOptionCallback(mockCalendar, dummy);

    callback.onSave(data);
    String result = "editSeriesStartFrom: dummy, subject, 2025-11-13T11:00, new dummy\n"
        + "editSeriesStartFrom: new dummy, description, 2025-11-13T11:00, dummy\n"
        + "editSeriesStartFrom: new dummy, location, 2025-11-13T11:00, dummy\n"
        + "editSeriesStartFrom: new dummy, status, 2025-11-13T11:00, public\n";
    assertEquals(result, calendarLog.toString());
  }

  @Test
  public void testOnCancel() {
    EventInterface dummy = EventFactory.createSingleEvent("dummy",
        LocalDateTime.parse("2025-11-13T11:00"), LocalDateTime.parse("2025-11-13T12:00"));
    EditEventOptionCallback callback =
        new EditEventOptionCallback(mockCalendar, dummy);
    callback.onCancel();
  }
}
