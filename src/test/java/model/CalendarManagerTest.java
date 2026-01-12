package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.util.Set;
import model.calendar.CalendarExistException;
import model.calendar.CalendarManager;
import model.calendar.CalendarManagerInterface;
import model.calendar.CalendarModelInterface;
import model.calendar.CalendarNotFoundException;
import model.calendar.Weekday;
import model.event.EventExistException;
import model.event.EventNotFoundException;
import org.junit.Before;
import org.junit.Test;

/**
 * This class represents a test for CalendarManagerInterface.
 */
public class CalendarManagerTest {
  private CalendarManagerInterface calendarManager;

  /**
   * Create a CalendarManager before every test.
   */
  @Before
  public void setUp() {
    this.calendarManager = new CalendarManager();
  }

  @Test
  public void testAddCalendar() {
    this.calendarManager.addCalendar("Taipei", "Asia/Taipei");
    this.calendarManager.addCalendar("Tainan", "Asia/Taipei");
    this.calendarManager.addCalendar("New York", "America/New_York");
    this.calendarManager.addCalendar("Sydney", "Australia/Sydney");
  }

  @Test
  public void testAddCalendarException() {
    this.calendarManager.addCalendar("Taipei", "Asia/Taipei");
    assertThrows(CalendarExistException.class,
        () -> this.calendarManager.addCalendar("Taipei", "Asia/Taipei"));
    assertThrows(DateTimeException.class,
        () -> this.calendarManager.addCalendar("Random", "Invalid/Invalid"));
  }

  @Test
  public void testEditCalendar() {
    this.calendarManager.addCalendar("Mike's Calendar", "Asia/Taipei");
    this.calendarManager.activateCalendar("Mike's Calendar");
    CalendarModelInterface calendar = this.calendarManager.getActiveCalendar();

    calendar.createSingleEventWithTime("Taipei PDP", "2025-11-04T13:35",
        "2025-11-04T15:15");
    calendar.createSeriesEventWithOccurrence("Taipei DBMS", "2025-11-06T16:35",
        "2025-11-06T17:40", Set.of(Weekday.THURSDAY), 1);

    this.calendarManager.editCalendar("Mike's Calendar", "name",
        "Eric's Calendar");
    this.calendarManager.editCalendar("Eric's Calendar", "timezone",
        "America/New_York");

    String expected1 =
        "subject Taipei PDP starting on 2025-11-04 at 00:35, ending on 2025-11-04 at 02:15";
    assertEquals(expected1, calendar.getEventsOnDate("2025-11-04").trim());

    String expected2 =
        "subject Taipei PDP starting on 2025-11-04 at 00:35, ending on 2025-11-04 at 02:15\n"
            + "subject Taipei DBMS starting on 2025-11-06 at 03:35, ending on 2025-11-06 at 04:40";
    assertEquals(expected2,
        calendar.getEventsInRange("2025-11-01T13:35", "2025-11-10T17:40").trim());
  }

  @Test
  public void testEditCalendarException() {
    this.calendarManager.addCalendar("Taipei", "Asia/Taipei");
    this.calendarManager.addCalendar("Boston", "America/New_York");

    assertThrows(IllegalArgumentException.class,
        () -> this.calendarManager.editCalendar("Taipei", "subject",
            "Boston"));

    assertThrows(CalendarNotFoundException.class,
        () -> this.calendarManager.editCalendar("New York", "name",
            "Boston"));

    assertThrows(CalendarExistException.class,
        () -> this.calendarManager.editCalendar("Taipei", "name", "Boston"));

    assertThrows(DateTimeException.class,
        () -> this.calendarManager.editCalendar("Taipei", "timezone",
            "New York"));
  }

  @Test
  public void testActivateCalendar() {
    this.calendarManager.addCalendar("Taipei", "Asia/Taipei");
    this.calendarManager.addCalendar("Boston", "America/New_York");
    this.calendarManager.activateCalendar("Taipei");
    CalendarModelInterface taipei = this.calendarManager.getActiveCalendar();
    taipei.createAllDaySingleEvent("Taipei Event", "2025-11-04");
    String expected1 =
        "subject Taipei Event starting on 2025-11-04 at 08:00, ending on 2025-11-04 at 17:00";
    String result1 = taipei.getEventsOnDate("2025-11-04");
    assertEquals(expected1, result1.trim());
    this.calendarManager.activateCalendar("Boston");
    CalendarModelInterface boston = this.calendarManager.getActiveCalendar();
    boston.createAllDaySingleEvent("Boston Event", "2025-11-04");
    String expected2 =
        "subject Boston Event starting on 2025-11-04 at 08:00, ending on 2025-11-04 at 17:00";
    String result2 = boston.getEventsOnDate("2025-11-04");
    assertEquals(expected2, result2.trim());
  }

  @Test
  public void testActivateCalendarException() {
    this.calendarManager.addCalendar("Taipei", "Asia/Taipei");
    assertThrows(CalendarNotFoundException.class,
        () -> this.calendarManager.activateCalendar("Taichung"));
    assertThrows(NullPointerException.class,
        () -> this.calendarManager.getActiveCalendar());
  }

  @Test
  public void testCopyEvent() {
    this.calendarManager.addCalendar("Taipei", "Asia/Taipei");
    this.calendarManager.addCalendar("Boston", "America/New_York");

    this.calendarManager.activateCalendar("Taipei");
    CalendarModelInterface taipei = this.calendarManager.getActiveCalendar();
    taipei.createSingleEventWithTime("Taipei PDP", "2025-11-04T13:35",
        "2025-11-04T15:15");

    this.calendarManager.copyEvent("Taipei PDP", "2025-11-04T13:35",
        "Boston", "2025-11-03T10:00");

    this.calendarManager.activateCalendar("Boston");
    CalendarModelInterface boston = this.calendarManager.getActiveCalendar();
    String expected =
        "subject Taipei PDP starting on 2025-11-03 at 10:00, ending on 2025-11-03 at 11:40";
    assertEquals(expected, boston.getEventsOnDate("2025-11-03").trim());
  }

  @Test
  public void testCopyEventException() {
    this.calendarManager.addCalendar("Taipei", "Asia/Taipei");
    this.calendarManager.addCalendar("Boston", "America/New_York");

    assertThrows(NullPointerException.class,
        () -> this.calendarManager.copyEvent("Taipei PDP",
            "2025-11-04T13:35", "Boston",
            "2025-11-03T10:00"));

    this.calendarManager.activateCalendar("Taipei");
    CalendarModelInterface taipei = this.calendarManager.getActiveCalendar();
    taipei.createSingleEventWithTime("Taipei PDP", "2025-11-04T13:35",
        "2025-11-04T15:15");

    assertThrows(CalendarNotFoundException.class,
        () -> this.calendarManager.copyEvent("Taipei PDP",
            "2025-11-04T13:35", "Northeastern",
            "2025-11-03T10:00"));

    assertThrows(UnsupportedOperationException.class,
        () -> this.calendarManager.copyEvent("Taipei PDP",
            "2025-11-04T13:35", "Taipei",
            "2025-11-04T13:35"));

    assertThrows(DateTimeParseException.class,
        () -> this.calendarManager.copyEvent("Taipei PDP",
            "Invalid", "Boston",
            "2025-11-03T10:00"));

    assertThrows(DateTimeParseException.class,
        () -> this.calendarManager.copyEvent("Taipei PDP",
            "2025-11-04T13:35", "Boston",
            "Invalid"));

    assertThrows(EventNotFoundException.class,
        () -> this.calendarManager.copyEvent("Taipei DBMS",
            "2025-11-04T13:35", "Boston",
            "2025-11-03T10:00"));

    assertThrows(EventNotFoundException.class,
        () -> this.calendarManager.copyEvent("Taipei PDP",
            "2025-11-04T01:35", "Boston",
            "2025-11-03T10:00"));

    assertThrows(EventNotFoundException.class,
        () -> this.calendarManager.copyEvent("Taipei DBMS",
            "2025-11-04T01:35", "Boston",
            "2025-11-03T10:00"));

    this.calendarManager.activateCalendar("Boston");
    CalendarModelInterface boston = this.calendarManager.getActiveCalendar();
    boston.createSingleEventWithTime("Taipei PDP", "2025-11-04T13:35",
        "2025-11-04T15:15");

    assertThrows(EventExistException.class,
        () -> this.calendarManager.copyEvent("Taipei PDP", "2025-11-04T13:35",
            "Taipei", "2025-11-04T13:35"));
  }

  @Test
  public void testCopyEventsOnDate() {
    this.calendarManager.addCalendar("Taipei", "Asia/Taipei");
    this.calendarManager.addCalendar("Boston", "America/New_York");
    this.calendarManager.activateCalendar("Taipei");
    CalendarModelInterface taipei = this.calendarManager.getActiveCalendar();

    taipei.createSingleEventWithTime("Taipei M1", "2025-11-05T14:30",
        "2025-11-06T15:00");
    taipei.createSingleEventWithTime("Taipei M2", "2025-11-06T14:30",
        "2025-11-07T15:00");
    taipei.createAllDaySeriesEventWithOccurrence("Taipei M3 Series", "2025-11-01",
        Set.of(Weekday.THURSDAY), 6);

    this.calendarManager.copyEventsOnDate("2025-11-06", "Boston",
        "2025-12-06");

    this.calendarManager.activateCalendar("Boston");
    CalendarModelInterface boston = this.calendarManager.getActiveCalendar();
    String expected =
        "subject Taipei M1 starting on 2025-12-05 at 01:30, ending on 2025-12-06 at 02:00\n"
            + "subject Taipei M3 Series starting on 2025-12-05 at 19:00, "
            + "ending on 2025-12-06 at 04:00\n"
            + "subject Taipei M2 starting on 2025-12-06 at 01:30, ending on 2025-12-07 at 02:00";
    assertEquals(expected, boston.getEventsOnDate("2025-12-06").trim());
  }

  @Test
  public void testCopyEventsOnDateException() {
    this.calendarManager.addCalendar("Taipei", "Asia/Taipei");
    this.calendarManager.addCalendar("Boston", "America/New_York");

    assertThrows(NullPointerException.class,
        () -> this.calendarManager.copyEventsOnDate("2025-11-06", "Boston",
            "2025-12-06"));

    this.calendarManager.activateCalendar("Taipei");
    CalendarModelInterface taipei = this.calendarManager.getActiveCalendar();
    taipei.createSingleEventWithTime("Taipei M1", "2025-11-05T14:30",
        "2025-11-06T15:00");

    assertThrows(CalendarNotFoundException.class,
        () -> this.calendarManager.copyEventsOnDate("2025-11-06", "New York",
            "2025-12-06"));

    assertThrows(UnsupportedOperationException.class,
        () -> this.calendarManager.copyEventsOnDate("2025-11-06", "Taipei",
            "2025-12-06"));

    assertThrows(DateTimeParseException.class,
        () -> this.calendarManager.copyEventsOnDate("Invalid", "Boston",
            "2025-12-06"));

    assertThrows(DateTimeParseException.class,
        () -> this.calendarManager.copyEventsOnDate("2025-11-06", "Boston",
            "Invalid"));

    assertThrows(EventNotFoundException.class,
        () -> this.calendarManager.copyEventsOnDate("2025-11-04", "Boston",
            "2025-12-06"));

    assertThrows(EventNotFoundException.class,
        () -> this.calendarManager.copyEventsOnDate("2025-11-07", "Boston",
            "2025-12-06"));
  }

  @Test
  public void testCopyEventsBetween() {
    this.calendarManager.addCalendar("Taipei", "Asia/Taipei");
    this.calendarManager.addCalendar("Boston", "America/New_York");

    this.calendarManager.activateCalendar("Boston");
    CalendarModelInterface boston = this.calendarManager.getActiveCalendar();
    boston.createAllDaySingleEvent("Study", "2025-11-06");
    boston.createSeriesEventWithOccurrence("PDP", "2025-10-27T13:35",
        "2025-10-27T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        6);
    boston.createSeriesEventWithEndDate("DBMS", "2025-11-01T16:35",
        "2025-11-01T17:40", Set.of(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.THURSDAY),
        "2025-11-14");

    this.calendarManager.copyEventsBetween("2025-11-02", "2025-11-09",
        "Taipei", "2025-12-02");

    this.calendarManager.activateCalendar("Taipei");
    CalendarModelInterface taipei = this.calendarManager.getActiveCalendar();

    // PDP and DBMS should remain its recurring weekdays
    String expected1 =
        "subject PDP starting on 2025-12-02 at 02:35, ending on 2025-12-02 at 04:15\n"
            + "subject DBMS starting on 2025-12-03 at 05:35, ending on 2025-12-03 at 06:40\n"
            + "subject DBMS starting on 2025-12-04 at 05:35, ending on 2025-12-04 at 06:40\n"
            + "subject PDP starting on 2025-12-05 at 02:35, ending on 2025-12-05 at 04:15\n"
            + "subject Study starting on 2025-12-06 at 21:00, ending on 2025-12-07 at 06:00\n"
            + "subject DBMS starting on 2025-12-08 at 05:35, ending on 2025-12-08 at 06:40";
    assertEquals(expected1, taipei.getEventsInRange("2025-12-01T08:00", "2025-12-30T17:00").trim());

    // Test they are indeed in a series
    taipei.editSeriesStartFrom("PDP", "subject", "2025-12-05T02:35",
        "Programming Design Paradigm");
    String expected2 =
        "subject Programming Design Paradigm starting on 2025-12-02 at 02:35, "
            + "ending on 2025-12-02 at 04:15\n"
            + "subject DBMS starting on 2025-12-03 at 05:35, ending on 2025-12-03 at 06:40\n"
            + "subject DBMS starting on 2025-12-04 at 05:35, ending on 2025-12-04 at 06:40\n"
            + "subject Programming Design Paradigm starting on 2025-12-05 at 02:35, "
            + "ending on 2025-12-05 at 04:15\n"
            + "subject Study starting on 2025-12-06 at 21:00, ending on 2025-12-07 at 06:00\n"
            + "subject DBMS starting on 2025-12-08 at 05:35, ending on 2025-12-08 at 06:40";
    assertEquals(expected2, taipei.getEventsInRange("2025-12-01T08:00", "2025-12-30T17:00").trim());

    // Test we didn't modify original series events
    String expected3 =
        "subject PDP starting on 2025-10-28 at 13:35, ending on 2025-10-28 at 15:15\n"
            + "subject PDP starting on 2025-10-31 at 13:35, ending on 2025-10-31 at 15:15\n"
            + "subject DBMS starting on 2025-11-03 at 16:35, ending on 2025-11-03 at 17:40\n"
            + "subject PDP starting on 2025-11-04 at 13:35, ending on 2025-11-04 at 15:15\n"
            + "subject DBMS starting on 2025-11-05 at 16:35, ending on 2025-11-05 at 17:40\n"
            + "subject Study starting on 2025-11-06 at 08:00, ending on 2025-11-06 at 17:00\n"
            + "subject DBMS starting on 2025-11-06 at 16:35, ending on 2025-11-06 at 17:40\n"
            + "subject PDP starting on 2025-11-07 at 13:35, ending on 2025-11-07 at 15:15\n"
            + "subject DBMS starting on 2025-11-10 at 16:35, ending on 2025-11-10 at 17:40\n"
            + "subject PDP starting on 2025-11-11 at 13:35, ending on 2025-11-11 at 15:15\n"
            + "subject DBMS starting on 2025-11-12 at 16:35, ending on 2025-11-12 at 17:40\n"
            + "subject DBMS starting on 2025-11-13 at 16:35, ending on 2025-11-13 at 17:40\n"
            + "subject PDP starting on 2025-11-14 at 13:35, ending on 2025-11-14 at 15:15";
    assertEquals(expected3, boston.getEventsInRange("2025-10-01T08:00", "2025-12-30T17:00").trim());
  }

  @Test
  public void testCopyEventsBetweenException() {
    this.calendarManager.addCalendar("Taipei", "Asia/Taipei");
    this.calendarManager.addCalendar("Boston", "America/New_York");

    assertThrows(NullPointerException.class,
        () -> this.calendarManager.copyEventsBetween("2025-11-02", "2025-11-09",
            "Taipei", "2025-12-02"));

    this.calendarManager.activateCalendar("Boston");
    CalendarModelInterface boston = this.calendarManager.getActiveCalendar();
    boston.createAllDaySingleEvent("Study", "2025-11-06");
    boston.createAllDaySeriesEventWithOccurrence("Basketball", "2025-10-27",
        Set.of(Weekday.TUESDAY, Weekday.FRIDAY), 6);
    boston.createSeriesEventWithOccurrence("Football", "2025-10-27T20:00",
        "2025-10-27T23:00", Set.of(Weekday.TUESDAY, Weekday.SATURDAY),
        6);

    assertThrows(CalendarNotFoundException.class,
        () -> this.calendarManager.copyEventsBetween("2025-11-02", "2025-11-09",
            "Beijing", "2025-12-02"));

    assertThrows(UnsupportedOperationException.class,
        () -> this.calendarManager.copyEventsBetween("2025-11-02", "2025-11-09",
            "Taipei", "2025-12-02"));

    assertThrows(DateTimeParseException.class,
        () -> this.calendarManager.copyEventsBetween("2025-11-02", "invalid",
            "Taipei", "2025-12-02"));

    assertThrows(UnsupportedOperationException.class, () ->
        this.calendarManager.copyEventsBetween("2025-11-02", "2025-11-09",
            "Taipei", "2025-12-02"));

    assertThrows(EventNotFoundException.class, () ->
        this.calendarManager.copyEventsBetween("2026-11-02", "2026-11-09",
            "Taipei", "2025-12-02"));

    this.calendarManager.activateCalendar("Taipei");
    CalendarModelInterface taipei = this.calendarManager.getActiveCalendar();
    taipei.createSingleEventWithTime("Study", "2025-12-06T21:00",
        "2025-12-07T06:00");

    taipei.createSingleEventWithTime("Football", "2025-12-09T09:00",
        "2025-12-09T12:00");

    this.calendarManager.activateCalendar("Boston");
    assertThrows(EventExistException.class,
        () -> this.calendarManager.copyEventsBetween("2025-11-06",
            "2025-11-06", "Taipei", "2025-12-06"));

    assertThrows(EventExistException.class,
        () -> this.calendarManager.copyEventsBetween("2025-11-08",
            "2025-11-08", "Taipei", "2025-12-08"));
  }

  @Test
  public void testGetAllCalendarNames() {
    assertThrows(CalendarNotFoundException.class, () -> this.calendarManager.getAllCalendarNames());
    this.calendarManager.addCalendar("Taipei", "Asia/Taipei");
    this.calendarManager.addCalendar("Boston", "America/New_York");
    assertTrue(this.calendarManager.getAllCalendarNames().contains("Taipei"));
    assertTrue(this.calendarManager.getAllCalendarNames().contains("Boston"));
  }

  @Test
  public void getCalendarTimezone() {
    this.calendarManager.addCalendar("Taipei", "Asia/Taipei");
    this.calendarManager.addCalendar("Boston", "America/New_York");

    assertEquals("Asia/Taipei", this.calendarManager.getCalendarTimezone("Taipei").toString());
    assertEquals("America/New_York", this.calendarManager.getCalendarTimezone("Boston").toString());

    assertThrows(CalendarNotFoundException.class,
        () -> this.calendarManager.getCalendarTimezone("NEU"));
  }
}
