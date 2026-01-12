package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;
import model.calendar.Calendar;
import model.calendar.CalendarModelInterface;
import model.calendar.CalendarStatus;
import model.calendar.Weekday;
import model.event.EventExistException;
import model.event.EventNotFoundException;
import model.event.EventReadOnlyInterface;
import org.junit.Before;
import org.junit.Test;

/**
 * This class represents test for CalendarModelInterface.
 */
public class CalendarTest {
  private CalendarModelInterface calendar;

  /**
   * Create CalendarModel before each test.
   */
  @Before
  public void setUp() {
    this.calendar = new Calendar();
  }

  @Test
  public void testGetEventsNoEvents() {
    assertEquals("No events scheduled on this date",
        this.calendar.getEventsOnDate("2025-11-01"));
    assertEquals("No events scheduled between this range",
        this.calendar.getEventsInRange("2025-11-01T11:00", "2025-11-10T11:00"));
  }

  @Test
  public void testGetEventsOnDateBoundaryConditions() {
    // Event: 2025-11-05
    this.calendar.createSingleEventWithTime("Event1", "2025-11-05T10:00", "2025-11-05T12:00");
    // Also test not printing location is empty
    this.calendar.editSingleEvent(List.of("Event1", "location",
        "2025-11-05T10:00", "2025-11-05T12:00", ""));

    // Before start → should not include
    assertEquals("No events scheduled on this date",
        this.calendar.getEventsOnDate("2025-11-04"));

    // After end → should not include
    assertEquals("No events scheduled on this date",
        this.calendar.getEventsOnDate("2025-11-06"));

    // Equal to start → should include
    String resultStart = this.calendar.getEventsOnDate("2025-11-05");
    String expected1 =
        "subject Event1 starting on 2025-11-05 at 10:00, ending on 2025-11-05 at 12:00";
    assertEquals(expected1, resultStart.trim());

    // Event spanning multiple days: 2025-11-05 → 2025-11-07
    this.calendar.createSingleEventWithTime("Event2", "2025-11-05T10:00", "2025-11-07T12:00");

    // Equal to end → should include
    String resultEnd = this.calendar.getEventsOnDate("2025-11-07");
    String expected2 =
        "subject Event2 starting on 2025-11-05 at 10:00, ending on 2025-11-07 at 12:00";
    assertEquals(expected2, resultEnd.trim());

    // Between start and end → should include
    String resultMiddle = this.calendar.getEventsOnDate("2025-11-06");
    String expected3 =
        "subject Event2 starting on 2025-11-05 at 10:00, ending on 2025-11-07 at 12:00";
    assertEquals(expected3, resultMiddle.trim());
  }


  @Test
  public void testCreateSingleEvent() {
    this.calendar.createSingleEventWithTime("Hw4 Deadline", "2025-10-29T21:00",
        "2025-10-31T21:00");
    this.calendar.createAllDaySingleEvent("Hw4 Self-eval", "2025-10-31");
    String expected1 =
        "subject Hw4 Deadline starting on 2025-10-29 at 21:00, ending on 2025-10-31 at 21:00";
    String result1 = this.calendar.getEventsOnDate("2025-10-29");
    assertEquals(expected1, result1.trim());
    String expected2 =
        "subject Hw4 Deadline starting on 2025-10-29 at 21:00, ending on 2025-10-31 at 21:00\n"
            +
            "subject Hw4 Self-eval starting on 2025-10-31 at 08:00, ending on 2025-10-31 at 17:00";
    String result2 = this.calendar.getEventsOnDate("2025-10-31");
    assertEquals(expected2, result2.trim());
  }

  @Test
  public void testSingleEventUniqueness() {
    this.calendar.createSingleEventWithTime("Meeting", "2025-11-01T10:00",
        "2025-11-01T11:00");
    assertThrows(EventExistException.class,
        () -> this.calendar.createSingleEventWithTime("Meeting",
            "2025-11-01T10:00",
            "2025-11-01T11:00"));
    this.calendar.createSingleEventWithTime("Workshop", "2025-11-01T10:00",
        "2025-11-01T11:00");
    this.calendar.createSingleEventWithTime("Meeting", "2025-11-01T12:00",
        "2025-11-01T13:00");
    this.calendar.createSingleEventWithTime("Meeting", "2025-11-02T10:00",
        "2025-11-02T11:30");
  }

  @Test
  public void testCreateSingleEventException() {
    this.calendar.createSingleEventWithTime("Hw4 Deadline", "2025-10-29T21:00",
        "2025-10-31T21:00");
    assertThrows(EventExistException.class,
        () -> this.calendar.createSingleEventWithTime("Hw4 Deadline",
            "2025-10-29T21:00",
            "2025-10-31T21:00"));
    assertThrows(UnsupportedOperationException.class,
        () -> this.calendar.createSingleEventWithTime("Hw4 Deadline",
            "2025-10-29T21:00",
            "2025-10-29T20:00"));
    this.calendar.createAllDaySingleEvent("Hw4 Self-eval", "2025-10-31");
    assertThrows(EventExistException.class,
        () -> this.calendar.createAllDaySingleEvent("Hw4 Self-eval",
            "2025-10-31"));
  }

  @Test
  public void testCreateSeriesWithOccurrence() {
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15",
        Set.of(Weekday.TUESDAY, Weekday.FRIDAY), 4);
    String expected1 =
        "subject PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 15:15\n"
            + "subject PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 15:15\n"
            + "subject PDP starting on 2025-10-14 at 13:35, ending on 2025-10-14 at 15:15\n"
            + "subject PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 15:15";
    String result1 = this.calendar.getEventsInRange("2025-10-07T08:00", "2025-10-19T21:00");
    assertEquals(expected1, result1.trim());
    this.calendar.createAllDaySeriesEventWithOccurrence("Intern", "2025-10-20",
        Set.of(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.THURSDAY), 6);
    String expected2 =
        "subject Intern starting on 2025-10-22 at 08:00, ending on 2025-10-22 at 17:00\n"
            + "subject Intern starting on 2025-10-23 at 08:00, ending on 2025-10-23 at 17:00\n"
            + "subject Intern starting on 2025-10-27 at 08:00, ending on 2025-10-27 at 17:00\n"
            + "subject Intern starting on 2025-10-29 at 08:00, ending on 2025-10-29 at 17:00";
    String result2 = this.calendar.getEventsInRange("2025-10-20T17:01", "2025-10-30T07:59");
    assertEquals(expected2, result2.trim());
  }

  @Test
  public void testCreateSeriesWithEndDate() {
    this.calendar.createSeriesEventWithEndDate("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15",
        Set.of(Weekday.TUESDAY, Weekday.FRIDAY), "2025-10-18");
    String expected1 =
        "subject PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 15:15\n"
            + "subject PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 15:15\n"
            + "subject PDP starting on 2025-10-14 at 13:35, ending on 2025-10-14 at 15:15\n"
            + "subject PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 15:15";
    String result1 = this.calendar.getEventsInRange("2025-10-07T08:00", "2025-10-19T21:00");
    assertEquals(expected1, result1.trim());
    this.calendar.createAllDaySeriesEventWithEndDate("Intern", "2025-10-20",
        Set.of(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.THURSDAY), "2025-11-01");
    String expected2 =
        "subject Intern starting on 2025-10-22 at 08:00, ending on 2025-10-22 at 17:00\n"
            + "subject Intern starting on 2025-10-23 at 08:00, ending on 2025-10-23 at 17:00\n"
            + "subject Intern starting on 2025-10-27 at 08:00, ending on 2025-10-27 at 17:00\n"
            + "subject Intern starting on 2025-10-29 at 08:00, ending on 2025-10-29 at 17:00";
    String result2 = this.calendar.getEventsInRange("2025-10-20T17:01", "2025-10-30T07:59");
    assertEquals(expected2, result2.trim());
  }

  @Test
  public void testCreateSeriesException1() {
    this.calendar.createSingleEventWithTime("PDP", "2025-10-10T13:35",
        "2025-10-10T15:15");
    this.calendar.createAllDaySingleEvent("All Day PDP", "2025-10-10");
    assertThrows(EventExistException.class,
        () -> this.calendar.createSeriesEventWithOccurrence("PDP",
            "2025-10-07T13:35",
            "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
            4));
    assertThrows(EventExistException.class,
        () -> this.calendar.createSeriesEventWithEndDate("PDP",
            "2025-10-07T13:35", "2025-10-07T15:15",
            Set.of(Weekday.TUESDAY, Weekday.FRIDAY), "2025-11-01"));
    assertThrows(EventExistException.class,
        () -> this.calendar.createAllDaySeriesEventWithOccurrence("All Day PDP",
            "2025-10-07", Set.of(Weekday.TUESDAY, Weekday.FRIDAY), 4));
    assertThrows(EventExistException.class,
        () -> this.calendar.createAllDaySeriesEventWithEndDate("All Day PDP",
            "2025-10-07", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
            "2025-11-01"));
    String expected =
        "subject All Day PDP starting on 2025-10-10 at 08:00, ending on 2025-10-10 at 17:00\n"
            + "subject PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 15:15";
    assertEquals(expected,
        this.calendar.getEventsInRange("2025-10-07T13:35", "2025-10-10T15:15").trim());
  }

  @Test
  public void testCreateSeriesException2() {
    assertThrows(UnsupportedOperationException.class,
        () -> this.calendar.createSeriesEventWithOccurrence("PDP",
            "2025-10-07T13:35", "2025-10-08T15:15",
            Set.of(Weekday.TUESDAY, Weekday.FRIDAY), 4));
    assertThrows(UnsupportedOperationException.class,
        () -> this.calendar.createSeriesEventWithEndDate("PDP",
            "2025-10-07T13:35", "2025-10-08T15:15",
            Set.of(Weekday.TUESDAY, Weekday.FRIDAY), "2025-11-01"));
  }

  @Test
  public void testEditSingleEvent() {
    this.calendar.createSingleEventWithTime("Meeting", "2025-11-01T10:00",
        "2025-11-01T11:00");
    assertEquals(
        "subject Meeting starting on 2025-11-01 at 10:00, ending on 2025-11-01 at 11:00",
        this.calendar.getEventsOnDate("2025-11-01").trim());
    this.calendar.editSingleEvent(List.of("Meeting", "subject",
        "2025-11-01T10:00", "2025-11-01T11:00",
        "Project Proposal"));
    assertEquals(
        "subject Project Proposal starting on 2025-11-01 at 10:00, "
            + "ending on 2025-11-01 at 11:00",
        this.calendar.getEventsOnDate("2025-11-01").trim());
    this.calendar.editSingleEvent(List.of("Project Proposal", "start",
        "2025-11-01T10:00", "2025-11-01T11:00",
        "2025-11-01T10:30"));
    assertEquals(
        "subject Project Proposal starting on 2025-11-01 at 10:30, "
            + "ending on 2025-11-01 at 11:30", this.calendar.getEventsOnDate("2025-11-01").trim());
    this.calendar.editSingleEvent(List.of("Project Proposal", "end",
        "2025-11-01T10:30", "2025-11-01T11:30",
        "2025-11-01T12:00"));
    assertEquals(
        "subject Project Proposal starting on 2025-11-01 at 10:30, "
            + "ending on 2025-11-01 at 12:00", this.calendar.getEventsOnDate("2025-11-01").trim());
    this.calendar.editSingleEvent(List.of("Project Proposal", "location",
        "2025-11-01T10:30", "2025-11-01T12:00", "Snell Library"));
    assertEquals(
        "subject Project Proposal starting on 2025-11-01 at 10:30, "
            + "ending on 2025-11-01 at 12:00 at Snell Library",
        this.calendar.getEventsOnDate("2025-11-01").trim());
    this.calendar.editSingleEvent(List.of("Project Proposal", "end",
        "2025-11-01T10:30", "2025-11-01T12:00",
        "2025-11-02T13:00"));
    assertEquals(
        "subject Project Proposal starting on 2025-11-01 at 10:30, "
            + "ending on 2025-11-02 at 13:00 at Snell Library",
        this.calendar.getEventsOnDate("2025-11-01").trim());
  }

  @Test
  public void testEditSingleEventException() {
    this.calendar.createSingleEventWithTime("Meeting", "2025-11-01T10:00",
        "2025-11-01T11:00");

    assertThrows(IllegalArgumentException.class,
        () -> this.calendar.editSingleEvent(List.of("invalid", "invalid", "invalid", "invalid")));

    assertThrows(IllegalArgumentException.class,
        () -> this.calendar.editSingleEvent(
            List.of("invalid", "invalid", "invalid", "invalid", "invalid", "invalid")));

    assertThrows(IllegalArgumentException.class,
        () -> this.calendar.editSingleEvent(List.of("Meeting", "invalid",
            "2025-11-01T10:00", "2025-11-01T12:00",
            "Project Proposal")));

    assertThrows(EventNotFoundException.class,
        () -> this.calendar.editSingleEvent(List.of("Meeting", "subject",
            "2025-11-01T10:00", "2025-11-01T12:00",
            "Project Proposal")));
    assertThrows(UnsupportedOperationException.class,
        () -> this.calendar.editSingleEvent(List.of("Meeting", "end",
            "2025-11-01T10:00", "2025-11-01T11:00",
            "2025-11-01T09:00")));
    this.calendar.createSingleEventWithTime("Project Proposal",
        "2025-11-01T10:00", "2025-11-01T11:00");
    assertThrows(EventExistException.class,
        () -> this.calendar.editSingleEvent(List.of("Meeting", "subject",
            "2025-11-01T10:00", "2025-11-01T11:00",
            "Project Proposal")));
  }

  @Test
  public void testEditEventsStartFrom() {
    this.calendar.createSeriesEventWithEndDate("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        "2025-10-17");
    this.calendar.editEventStartFrom("PDP", "subject", "2025-10-07T13:35",
        "First Half PDP");
    this.calendar.editEventStartFrom("First Half PDP", "subject",
        "2025-10-14T13:35", "Second Half PDP");
    String expected =
        "subject First Half PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 15:15\n"
            + "subject First Half PDP starting on 2025-10-10 at 13:35, "
            + "ending on 2025-10-10 at 15:15\n"
            + "subject Second Half PDP starting on 2025-10-14 at 13:35, "
            + "ending on 2025-10-14 at 15:15\n"
            + "subject Second Half PDP starting on 2025-10-17 at 13:35, "
            + "ending on 2025-10-17 at 15:15";
    String result = this.calendar.getEventsInRange("2025-10-07T13:35", "2025-10-17T15:15");
    assertEquals(expected, result.trim());
  }

  @Test
  public void testEditEventsStartFromException() {
    this.calendar.createSeriesEventWithEndDate("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        "2025-10-17");
    assertThrows(EventNotFoundException.class,
        () -> this.calendar.editEventStartFrom("PDP", "subject",
            "2025-10-08T13:35", "First Half PDP"));
    this.calendar.createSingleEventWithTime("NEW PDP", "2025-10-10T13:35",
        "2025-10-10T15:15");
    assertThrows(EventExistException.class,
        () -> this.calendar.editEventStartFrom("PDP", "subject",
            "2025-10-07T13:35", "NEW PDP"));
    assertThrows(UnsupportedOperationException.class,
        () -> this.calendar.editEventStartFrom("PDP", "end",
            "2025-10-07T13:35", "2025-10-10T13:35"));
  }

  @Test
  public void testEditSeriesStartFrom() {
    this.calendar.createSeriesEventWithEndDate("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15",
        Set.of(Weekday.TUESDAY, Weekday.FRIDAY), "2025-10-17");
    this.calendar.editSeriesStartFrom("PDP", "subject",
        "2025-10-17T13:35", "NEW PDP");
    String expected =
        "subject NEW PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 15:15\n"
            + "subject NEW PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 15:15\n"
            + "subject NEW PDP starting on 2025-10-14 at 13:35, ending on 2025-10-14 at 15:15\n"
            + "subject NEW PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 15:15";
    String result = this.calendar.getEventsInRange("2025-10-07T13:35", "2025-10-17T15:15");
    assertEquals(expected, result.trim());
  }

  @Test
  public void testEditSingleEventStartFrom() {
    this.calendar.createSingleEventWithTime("M1", "2025-11-01T10:00",
        "2025-11-01T11:00");
    this.calendar.createSingleEventWithTime("M2", "2025-11-01T10:00",
        "2025-11-01T11:00");
    this.calendar.editEventStartFrom("M1", "subject", "2025-11-01T10:00",
        "m1");
    this.calendar.editSeriesStartFrom("M2", "subject",
        "2025-11-01T10:00", "m2");
    String expected =
        "subject m1 starting on 2025-11-01 at 10:00, ending on 2025-11-01 at 11:00\n"
            + "subject m2 starting on 2025-11-01 at 10:00, ending on 2025-11-01 at 11:00";
    assertEquals(expected,
        this.calendar.getEventsInRange("2025-11-01T10:00", "2025-11-01T11:00").trim());
  }

  @Test
  public void testEditSeriesStartFromException() {
    this.calendar.createSeriesEventWithEndDate("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        "2025-10-17");
    assertThrows(EventNotFoundException.class,
        () -> this.calendar.editSeriesStartFrom("PDP", "subject",
            "2025-10-08T13:35", "First Half PDP"));
    this.calendar.createSingleEventWithTime("NEW PDP", "2025-10-10T13:35",
        "2025-10-10T15:15");
    assertThrows(EventExistException.class,
        () -> this.calendar.editSeriesStartFrom("PDP", "subject",
            "2025-10-07T13:35", "NEW PDP"));
    assertThrows(UnsupportedOperationException.class,
        () -> this.calendar.editSeriesStartFrom("PDP", "end",
            "2025-10-07T13:35", "2025-10-10T13:35"));
  }

  @Test
  public void testEditSingleEventInSeries() {
    this.calendar.createSeriesEventWithEndDate("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        "2025-10-17");
    this.calendar.editSingleEvent(List.of("PDP", "end", "2025-10-07T13:35",
        "2025-10-07T15:15", "2025-10-07T16:00"));
    String expected1 =
        "subject PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 16:00\n"
            + "subject PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 15:15\n"
            + "subject PDP starting on 2025-10-14 at 13:35, ending on 2025-10-14 at 15:15\n"
            + "subject PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 15:15";
    assertEquals(expected1,
        this.calendar.getEventsInRange("2025-10-07T13:35", "2025-10-17T15:15").trim());
    assertThrows(UnsupportedOperationException.class,
        () -> this.calendar.editSingleEvent(List.of("PDP", "end",
            "2025-10-07T13:35", "2025-10-07T16:00",
            "2025-10-08T16:00")));
    this.calendar.editSingleEvent(List.of("PDP", "subject", "2025-10-14T13:35",
        "2025-10-14T15:15", "PDP (Canceled)"));
    String expected2 =
        "subject PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 16:00\n"
            + "subject PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 15:15\n"
            + "subject PDP (Canceled) starting on 2025-10-14 at 13:35, "
            + "ending on 2025-10-14 at 15:15\n"
            + "subject PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 15:15";
    assertEquals(expected2,
        this.calendar.getEventsInRange("2025-10-07T13:35", "2025-10-17T15:15").trim());
    this.calendar.editSingleEvent(List.of("PDP", "start", "2025-10-07T13:35",
        "2025-10-07T16:00", "2025-10-08T13:35"));
    String expected3 =
        "subject PDP starting on 2025-10-08 at 13:35, ending on 2025-10-08 at 16:00\n"
            + "subject PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 15:15\n"
            + "subject PDP (Canceled) starting on 2025-10-14 at 13:35, "
            + "ending on 2025-10-14 at 15:15\n"
            + "subject PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 15:15";
    assertEquals(expected3,
        this.calendar.getEventsInRange("2025-10-07T00:00", "2025-10-17T23:59").trim());
    this.calendar.editSingleEvent(List.of("PDP", "start", "2025-10-10T13:35",
        "2025-10-10T15:15", "2025-10-10T14:00"));
    String expected4 =
        "subject PDP starting on 2025-10-08 at 13:35, ending on 2025-10-08 at 16:00\n"
            + "subject PDP starting on 2025-10-10 at 14:00, ending on 2025-10-10 at 15:40\n"
            + "subject PDP (Canceled) starting on 2025-10-14 at 13:35, "
            + "ending on 2025-10-14 at 15:15\n"
            + "subject PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 15:15";
    assertEquals(expected4,
        this.calendar.getEventsInRange("2025-10-07T00:00", "2025-10-17T23:59").trim());
    this.calendar.editSeriesStartFrom("PDP", "subject",
        "2025-10-08T13:35", "Programming Design Paradigm");
    String expected5 =
        "subject Programming Design Paradigm starting on 2025-10-08 at 13:35, "
            + "ending on 2025-10-08 at 16:00\n"
            + "subject PDP starting on 2025-10-10 at 14:00, ending on 2025-10-10 at 15:40\n"
            + "subject Programming Design Paradigm starting on 2025-10-14 at 13:35, "
            + "ending on 2025-10-14 at 15:15\n"
            + "subject Programming Design Paradigm starting on 2025-10-17 at 13:35, "
            + "ending on 2025-10-17 at 15:15";
    assertEquals(expected5,
        this.calendar.getEventsInRange("2025-10-07T00:00", "2025-10-17T23:59").trim());
    this.calendar.editSeriesStartFrom("PDP", "subject",
        "2025-10-10T14:00", "PDP (Detached)");
    String expected6 =
        "subject Programming Design Paradigm starting on 2025-10-08 at 13:35, "
            + "ending on 2025-10-08 at 16:00\n"
            + "subject PDP (Detached) starting on 2025-10-10 at 14:00, "
            + "ending on 2025-10-10 at 15:40\n"
            + "subject Programming Design Paradigm starting on 2025-10-14 at 13:35, "
            + "ending on 2025-10-14 at 15:15\n"
            + "subject Programming Design Paradigm starting on 2025-10-17 at 13:35, "
            + "ending on 2025-10-17 at 15:15";
    assertEquals(expected6,
        this.calendar.getEventsInRange("2025-10-07T00:00", "2025-10-17T23:59").trim());
  }

  @Test
  public void testDetachSeriesByUpdatingStartDate() {
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        6);
    String expected1 =
        "subject PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 15:15\n"
            + "subject PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 15:15\n"
            + "subject PDP starting on 2025-10-14 at 13:35, ending on 2025-10-14 at 15:15\n"
            + "subject PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 15:15\n"
            + "subject PDP starting on 2025-10-21 at 13:35, ending on 2025-10-21 at 15:15\n"
            + "subject PDP starting on 2025-10-24 at 13:35, ending on 2025-10-24 at 15:15";
    assertEquals(expected1,
        this.calendar.getEventsInRange("2025-10-01T13:35", "2025-10-31T15:15").trim());
    this.calendar.editSingleEvent(List.of("PDP", "location", "2025-10-17T13:35",
        "2025-10-17T15:15", "SN"));
    this.calendar.editEventStartFrom("PDP", "start", "2025-10-17T13:35",
        "2025-10-20T13:35");
    String expected2 =
        "subject PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 15:15\n"
            + "subject PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 15:15\n"
            + "subject PDP starting on 2025-10-14 at 13:35, ending on 2025-10-14 at 15:15\n"
            + "subject PDP starting on 2025-10-21 at 13:35, ending on 2025-10-21 at 15:15 at SN\n"
            + "subject PDP starting on 2025-10-24 at 13:35, ending on 2025-10-24 at 15:15 at SN\n"
            + "subject PDP starting on 2025-10-28 at 13:35, ending on 2025-10-28 at 15:15 at SN";
    assertEquals(expected2,
        this.calendar.getEventsInRange("2025-10-01T13:35", "2025-10-31T15:15").trim());
  }

  @Test
  public void testDetachSeriesByUpdatingStartTime() {
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        6);
    String expected1 =
        "subject PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 15:15\n"
            + "subject PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 15:15\n"
            + "subject PDP starting on 2025-10-14 at 13:35, ending on 2025-10-14 at 15:15\n"
            + "subject PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 15:15\n"
            + "subject PDP starting on 2025-10-21 at 13:35, ending on 2025-10-21 at 15:15\n"
            + "subject PDP starting on 2025-10-24 at 13:35, ending on 2025-10-24 at 15:15";
    assertEquals(expected1,
        this.calendar.getEventsInRange("2025-10-01T13:35", "2025-10-31T15:15").trim());
    this.calendar.editSingleEvent(List.of("PDP", "location", "2025-10-17T13:35",
        "2025-10-17T15:15", "SN"));
    this.calendar.editEventStartFrom("PDP", "start", "2025-10-17T13:35",
        "2025-10-17T14:00");
    String expected2 =
        "subject PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 15:15\n"
            + "subject PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 15:15\n"
            + "subject PDP starting on 2025-10-14 at 13:35, ending on 2025-10-14 at 15:15\n"
            + "subject PDP starting on 2025-10-17 at 14:00, ending on 2025-10-17 at 15:40 at SN\n"
            + "subject PDP starting on 2025-10-21 at 14:00, ending on 2025-10-21 at 15:40 at SN\n"
            + "subject PDP starting on 2025-10-24 at 14:00, ending on 2025-10-24 at 15:40 at SN";
    assertEquals(expected2,
        this.calendar.getEventsInRange("2025-10-01T13:35", "2025-10-31T15:15").trim());
  }

  @Test
  public void testCalendarStatus() {
    this.calendar.createSeriesEventWithEndDate("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        "2025-10-17");
    assertEquals(CalendarStatus.BUSY, this.calendar.isBusy("2025-10-07T13:35"));
    assertEquals(CalendarStatus.AVAILABLE, this.calendar.isBusy("2025-10-07T15:35"));
  }

  @Test
  public void testEditSingleEventStartPreservesDuration() {
    this.calendar.createSingleEventWithTime("Lecture", "2025-11-05T09:00",
        "2025-11-05T11:00");
    this.calendar.editSingleEvent(List.of("Lecture", "start", "2025-11-05T09:00",
        "2025-11-05T11:00", "2025-11-05T10:00"));
    String expected =
        "subject Lecture starting on 2025-11-05 at 10:00, ending on 2025-11-05 at 12:00";
    assertEquals(expected, this.calendar.getEventsOnDate("2025-11-05").trim());
  }

  @Test
  public void testEditSeriesAllStartPreservesDuration() {
    this.calendar.createSeriesEventWithEndDate("Workshop", "2025-11-07T14:00",
        "2025-11-07T16:00", Set.of(Weekday.FRIDAY), "2025-11-21");
    this.calendar.editSeriesStartFrom("Workshop", "start",
        "2025-11-07T14:00", "2025-11-07T14:30");
    String expected =
        "subject Workshop starting on 2025-11-07 at 14:30, ending on 2025-11-07 at 16:30\n"
            + "subject Workshop starting on 2025-11-14 at 14:30, ending on 2025-11-14 at 16:30\n"
            + "subject Workshop starting on 2025-11-21 at 14:30, ending on 2025-11-21 at 16:30";
    String result = this.calendar.getEventsInRange("2025-11-07T00:00", "2025-11-30T23:59");
    assertEquals(expected, result.trim());
  }

  @Test
  public void testEditStartDateInSeriesFirstEventSubjectChange() {
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        6);
    assertThrows(UnsupportedOperationException.class,
        () -> this.calendar.editSeriesStartFrom("PDP", "start",
            "2025-10-07T13:35", "2025-10-08T13:35"));
    this.calendar.editSingleEvent(List.of("PDP", "subject", "2025-10-07T13:35",
        "2025-10-07T15:15", "NEW PDP"));
    this.calendar.editEventStartFrom("NEW PDP", "start",
        "2025-10-07T13:35", "2025-10-08T13:35");
    String expected =
        "subject NEW PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 15:15\n"
            + "subject NEW PDP starting on 2025-10-14 at 13:35, ending on 2025-10-14 at 15:15\n"
            + "subject NEW PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 15:15\n"
            + "subject NEW PDP starting on 2025-10-21 at 13:35, ending on 2025-10-21 at 15:15\n"
            + "subject NEW PDP starting on 2025-10-24 at 13:35, ending on 2025-10-24 at 15:15\n"
            + "subject NEW PDP starting on 2025-10-28 at 13:35, ending on 2025-10-28 at 15:15";
    assertEquals(expected,
        this.calendar.getEventsInRange("2025-10-07T13:35", "2025-10-31T13:35").trim());
  }

  @Test
  public void testEditStartTimeSeriesStartFrom() {
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        6);
    this.calendar.editSeriesStartFrom("PDP", "start", "2025-10-10T13:35",
        "2025-10-10T16:00");
    String expected =
        "subject PDP starting on 2025-10-07 at 16:00, ending on 2025-10-07 at 17:40\n"
            + "subject PDP starting on 2025-10-10 at 16:00, ending on 2025-10-10 at 17:40\n"
            + "subject PDP starting on 2025-10-14 at 16:00, ending on 2025-10-14 at 17:40\n"
            + "subject PDP starting on 2025-10-17 at 16:00, ending on 2025-10-17 at 17:40\n"
            + "subject PDP starting on 2025-10-21 at 16:00, ending on 2025-10-21 at 17:40\n"
            + "subject PDP starting on 2025-10-24 at 16:00, ending on 2025-10-24 at 17:40";
    assertEquals(expected,
        this.calendar.getEventsInRange("2025-10-07T13:35", "2025-10-31T13:35").trim());
  }

  @Test
  public void testEditEndDateInSeries() {
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        6);
    assertThrows(UnsupportedOperationException.class,
        () -> this.calendar.editSeriesStartFrom("PDP", "end",
            "2025-10-07T13:35", "2025-10-08T13:35"));
  }

  @Test
  public void testEditEndTimeInSeries() {
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        6);
    this.calendar.editSeriesStartFrom("PDP", "end", "2025-10-10T13:35",
        "2025-10-10T16:00");
    String expected =
        "subject PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 16:00\n"
            + "subject PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 16:00\n"
            + "subject PDP starting on 2025-10-14 at 13:35, ending on 2025-10-14 at 16:00\n"
            + "subject PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 16:00\n"
            + "subject PDP starting on 2025-10-21 at 13:35, ending on 2025-10-21 at 16:00\n"
            + "subject PDP starting on 2025-10-24 at 13:35, ending on 2025-10-24 at 16:00";
    assertEquals(expected,
        this.calendar.getEventsInRange("2025-10-07T13:35", "2025-10-31T13:35").trim());
  }

  @Test
  public void testEditStartDateInSeriesFromFirst() {
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        6);
    this.calendar.editEventStartFrom("PDP", "start", "2025-10-07T13:35",
        "2025-10-08T13:35");
    String expected =
        "subject PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 15:15\n"
            + "subject PDP starting on 2025-10-14 at 13:35, ending on 2025-10-14 at 15:15\n"
            + "subject PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 15:15\n"
            + "subject PDP starting on 2025-10-21 at 13:35, ending on 2025-10-21 at 15:15\n"
            + "subject PDP starting on 2025-10-24 at 13:35, ending on 2025-10-24 at 15:15\n"
            + "subject PDP starting on 2025-10-28 at 13:35, ending on 2025-10-28 at 15:15";
    assertEquals(expected,
        this.calendar.getEventsInRange("2025-10-07T13:35", "2025-11-07T13:35").trim());
  }

  @Test
  public void testEditStartDateInSeriesFromMiddle() {
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        6);
    this.calendar.editEventStartFrom("PDP", "start", "2025-10-10T13:35",
        "2025-10-11T13:35");
    String expected =
        "subject PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 15:15\n"
            + "subject PDP starting on 2025-10-14 at 13:35, ending on 2025-10-14 at 15:15\n"
            + "subject PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 15:15\n"
            + "subject PDP starting on 2025-10-21 at 13:35, ending on 2025-10-21 at 15:15\n"
            + "subject PDP starting on 2025-10-24 at 13:35, ending on 2025-10-24 at 15:15\n"
            + "subject PDP starting on 2025-10-28 at 13:35, ending on 2025-10-28 at 15:15";
    assertEquals(expected,
        this.calendar.getEventsInRange("2025-10-07T13:35", "2025-11-07T13:35").trim());
  }

  @Test
  public void testEditStartDateInSeriesFromLast() {
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        6);
    this.calendar.editEventStartFrom("PDP", "start", "2025-10-24T13:35",
        "2025-10-25T13:35");
    String expected =
        "subject PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 15:15\n"
            + "subject PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 15:15\n"
            + "subject PDP starting on 2025-10-14 at 13:35, ending on 2025-10-14 at 15:15\n"
            + "subject PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 15:15\n"
            + "subject PDP starting on 2025-10-21 at 13:35, ending on 2025-10-21 at 15:15\n"
            + "subject PDP starting on 2025-10-28 at 13:35, ending on 2025-10-28 at 15:15";
    assertEquals(expected,
        this.calendar.getEventsInRange("2025-10-07T13:35", "2025-11-07T13:35").trim());
  }

  @Test
  public void testEditStartTimeInSeriesFromFirst() {
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        6);
    this.calendar.editEventStartFrom("PDP", "start", "2025-10-07T13:35",
        "2025-10-07T16:00");
    String expected =
        "subject PDP starting on 2025-10-07 at 16:00, ending on 2025-10-07 at 17:40\n"
            + "subject PDP starting on 2025-10-10 at 16:00, ending on 2025-10-10 at 17:40\n"
            + "subject PDP starting on 2025-10-14 at 16:00, ending on 2025-10-14 at 17:40\n"
            + "subject PDP starting on 2025-10-17 at 16:00, ending on 2025-10-17 at 17:40\n"
            + "subject PDP starting on 2025-10-21 at 16:00, ending on 2025-10-21 at 17:40\n"
            + "subject PDP starting on 2025-10-24 at 16:00, ending on 2025-10-24 at 17:40";
    assertEquals(expected,
        this.calendar.getEventsInRange("2025-10-07T13:35", "2025-11-07T13:35").trim());
  }

  @Test
  public void testEditStartTimeInSeriesFromMiddle() {
    this.calendar.createSeriesEventWithEndDate("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        "2025-10-24");
    this.calendar.editEventStartFrom("PDP", "status", "2025-10-07T13:35",
        "private");
    this.calendar.editEventStartFrom("PDP", "start", "2025-10-10T13:35",
        "2025-10-10T16:00");
    String expected =
        "subject PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 15:15\n"
            + "subject PDP starting on 2025-10-10 at 16:00, ending on 2025-10-10 at 17:40\n"
            + "subject PDP starting on 2025-10-14 at 16:00, ending on 2025-10-14 at 17:40\n"
            + "subject PDP starting on 2025-10-17 at 16:00, ending on 2025-10-17 at 17:40\n"
            + "subject PDP starting on 2025-10-21 at 16:00, ending on 2025-10-21 at 17:40\n"
            + "subject PDP starting on 2025-10-24 at 16:00, ending on 2025-10-24 at 17:40";
    assertEquals(expected,
        this.calendar.getEventsInRange("2025-10-07T13:35", "2025-11-07T13:35").trim());
  }

  @Test
  public void testEditStartTimeInSeriesFromLast() {
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        6);
    this.calendar.editEventStartFrom("PDP", "start", "2025-10-24T13:35",
        "2025-10-24T16:00");
    String expected =
        "subject PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 15:15\n"
            + "subject PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 15:15\n"
            + "subject PDP starting on 2025-10-14 at 13:35, ending on 2025-10-14 at 15:15\n"
            + "subject PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 15:15\n"
            + "subject PDP starting on 2025-10-21 at 13:35, ending on 2025-10-21 at 15:15\n"
            + "subject PDP starting on 2025-10-24 at 16:00, ending on 2025-10-24 at 17:40";
    assertEquals(expected,
        this.calendar.getEventsInRange("2025-10-07T13:35", "2025-11-07T13:35").trim());
  }

  @Test
  public void testEditEndTimeInSeriesFromFirst() {
    this.calendar.createSeriesEventWithEndDate("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        "2025-10-24");
    this.calendar.editEventStartFrom("PDP", "end", "2025-10-07T13:35",
        "2025-10-07T23:00");
    String expected =
        "subject PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 23:00\n"
            + "subject PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 23:00\n"
            + "subject PDP starting on 2025-10-14 at 13:35, ending on 2025-10-14 at 23:00\n"
            + "subject PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 23:00\n"
            + "subject PDP starting on 2025-10-21 at 13:35, ending on 2025-10-21 at 23:00\n"
            + "subject PDP starting on 2025-10-24 at 13:35, ending on 2025-10-24 at 23:00";
    assertEquals(expected,
        this.calendar.getEventsInRange("2025-10-07T13:35", "2025-11-07T13:35").trim());
  }

  @Test
  public void testEditEndTimeInSeriesFromMiddle() {
    this.calendar.createSeriesEventWithEndDate("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        "2025-10-24");
    this.calendar.editEventStartFrom("PDP", "end", "2025-10-10T13:35",
        "2025-10-10T23:00");
    String expected =
        "subject PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 15:15\n"
            + "subject PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 23:00\n"
            + "subject PDP starting on 2025-10-14 at 13:35, ending on 2025-10-14 at 23:00\n"
            + "subject PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 23:00\n"
            + "subject PDP starting on 2025-10-21 at 13:35, ending on 2025-10-21 at 23:00\n"
            + "subject PDP starting on 2025-10-24 at 13:35, ending on 2025-10-24 at 23:00";
    assertEquals(expected,
        this.calendar.getEventsInRange("2025-10-07T13:35", "2025-11-07T13:35").trim());
  }

  @Test
  public void testEditEndTimeInSeriesFromLast() {
    this.calendar.createSeriesEventWithEndDate("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        "2025-10-24");
    this.calendar.editEventStartFrom("PDP", "end", "2025-10-24T13:35",
        "2025-10-24T23:00");
    String expected =
        "subject PDP starting on 2025-10-07 at 13:35, ending on 2025-10-07 at 15:15\n"
            + "subject PDP starting on 2025-10-10 at 13:35, ending on 2025-10-10 at 15:15\n"
            + "subject PDP starting on 2025-10-14 at 13:35, ending on 2025-10-14 at 15:15\n"
            + "subject PDP starting on 2025-10-17 at 13:35, ending on 2025-10-17 at 15:15\n"
            + "subject PDP starting on 2025-10-21 at 13:35, ending on 2025-10-21 at 15:15\n"
            + "subject PDP starting on 2025-10-24 at 13:35, ending on 2025-10-24 at 23:00";
    assertEquals(expected,
        this.calendar.getEventsInRange("2025-10-07T13:35", "2025-11-07T13:35").trim());
  }

  @Test
  public void testExport() {
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        6);
    List<EventReadOnlyInterface> events = this.calendar.getAllEventsReadOnly();
    assertEquals(6, events.size());
  }

  @Test
  public void testEditBranchCoverage() {
    // eventsStartAt.isEmpty() && seriesList.isEmpty()
    // eventsStartAt.isEmpty()
    // seriesList.isEmpty()
    // all non-empty

    this.calendar.createAllDaySingleEvent("DBMS", "2025-11-06");
    this.calendar.createSingleEventWithTime("PDP", "2025-11-07T13:35",
        "2025-11-07T15:00");
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        60);

    // all empty
    assertThrows(EventNotFoundException.class,
        () -> this.calendar.editEventStartFrom("X", "subject",
            "2026-11-01T11:00", "Twitter"));

    // eventsStartAt.isEmpty()
    this.calendar.editEventStartFrom("PDP", "subject",
        "2025-11-14T13:35", "Programming Design Paradigm");

    // seriesList.isEmpty()
    this.calendar.editEventStartFrom("DBMS", "subject",
        "2025-11-06T08:00", "Database Management Systems");

    // all non-empty
    this.calendar.editEventStartFrom("PDP", "subject",
        "2025-11-07T13:35", "XDD");

    String expected =
        "subject XDD starting on 2025-11-07 at 13:35, ending on 2025-11-07 at 15:00\n"
            + "subject XDD starting on 2025-11-07 at 13:35, ending on 2025-11-07 at 15:15";
    assertEquals(expected, this.calendar.getEventsOnDate("2025-11-07").trim());
  }

  @Test
  public void testIsSeriesEventFalse() {
    this.calendar.createAllDaySingleEvent("DBMS", "2025-11-06");
    for (EventReadOnlyInterface event : this.calendar.getAllEventsReadOnly()) {
      assertFalse(this.calendar.isSeriesEvent(event));
    }
  }

  @Test
  public void testIsSeriesEventTrue() {
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        60);
    for (EventReadOnlyInterface event : this.calendar.getAllEventsReadOnly()) {
      assertTrue(this.calendar.isSeriesEvent(event));
    }
  }

  @Test
  public void testGetSeriesWeekdays() {
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        60);
    for (EventReadOnlyInterface event : this.calendar.getAllEventsReadOnly()) {
      assertFalse(this.calendar.getSeriesWeekdays(event).contains(Weekday.MONDAY));
      assertTrue(this.calendar.getSeriesWeekdays(event).contains(Weekday.TUESDAY));
      assertFalse(this.calendar.getSeriesWeekdays(event).contains(Weekday.WEDNESDAY));
      assertFalse(this.calendar.getSeriesWeekdays(event).contains(Weekday.THURSDAY));
      assertTrue(this.calendar.getSeriesWeekdays(event).contains(Weekday.FRIDAY));
      assertFalse(this.calendar.getSeriesWeekdays(event).contains(Weekday.SATURDAY));
      assertFalse(this.calendar.getSeriesWeekdays(event).contains(Weekday.SUNDAY));
    }
  }

  @Test
  public void testGetSeriesUntilEnd() {
    this.calendar.createSeriesEventWithEndDate("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        "2025-10-24");
    for (EventReadOnlyInterface event : this.calendar.getAllEventsReadOnly()) {
      assertEquals("2025-10-24", this.calendar.getSeriesUntilEnd(event).toString());
    }
  }

  @Test
  public void testGetSeriesUntilEndNull() {
    assertThrows(NullPointerException.class, () -> this.calendar.getSeriesUntilEnd(null));
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        60);
    for (EventReadOnlyInterface event : this.calendar.getAllEventsReadOnly()) {
      assertNull(this.calendar.getSeriesUntilEnd(event));
    }
  }

  @Test
  public void testGetSeriesOccurrence() {
    this.calendar.createSeriesEventWithOccurrence("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        6);
    for (EventReadOnlyInterface event : this.calendar.getAllEventsReadOnly()) {
      assertEquals("6", String.valueOf(this.calendar.getSeriesOccurrence(event)));
    }

    this.calendar.editSingleEvent(
        List.of("PDP", "start", "2025-10-14T13:35", "2025-10-14T15:15", "2025-10-14T14:35"));
    for (EventReadOnlyInterface event : this.calendar.getAllEventsReadOnly()) {
      if (this.calendar.isSeriesEvent(event)) {
        assertEquals("5", String.valueOf(this.calendar.getSeriesOccurrence(event)));
      }
    }
  }

  @Test
  public void testGetSeriesOccurrenceNull() {
    assertThrows(NullPointerException.class, () -> this.calendar.getSeriesOccurrence(null));
    this.calendar.createSeriesEventWithEndDate("PDP", "2025-10-07T13:35",
        "2025-10-07T15:15", Set.of(Weekday.TUESDAY, Weekday.FRIDAY),
        "2025-10-24");
    for (EventReadOnlyInterface event : this.calendar.getAllEventsReadOnly()) {
      assertNull(this.calendar.getSeriesOccurrence(event));
    }
  }
}
