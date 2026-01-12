package model.calendar;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import model.event.EventExistException;
import model.event.EventNotFoundException;
import model.event.EventReadOnlyInterface;

/**
 * This class represents general purposes of a Calendar Model.
 */
public interface CalendarModelInterface {
  /**
   * Creates a single event in the calendar.
   *
   * @param subject       event subject
   * @param startDateTime event start date and time
   * @param endDateTime   event end date and time
   * @throws DateTimeParseException        if date/time string format is wrong
   * @throws EventExistException           if event has already existed
   * @throws UnsupportedOperationException if event ends before it starts
   */
  void createSingleEventWithTime(String subject, String startDateTime, String endDateTime)
      throws DateTimeParseException, EventExistException, UnsupportedOperationException;

  /**
   * Creates a single all day event.
   *
   * @param subject   event subject
   * @param startDate event start date
   * @throws DateTimeParseException if date/time string format is wrong
   * @throws EventExistException    if event has already existed
   */
  void createAllDaySingleEvent(String subject, String startDate)
      throws DateTimeParseException, EventExistException;

  /**
   * Creates a series of events that repeats N times on specific weekdays.
   *
   * @param subject             event subject
   * @param startDateTime       event start date and time
   * @param endDateTime         event end date and time
   * @param weekdays            weekdays event repeat on
   * @param numberOfOccurrences event occurrence time
   * @throws DateTimeParseException        if date/time string format is wrong
   * @throws EventExistException           if event(s) has already existed
   * @throws UnsupportedOperationException if events span more than one day
   */
  void createSeriesEventWithOccurrence(String subject, String startDateTime, String endDateTime,
                                       Set<Weekday> weekdays, int numberOfOccurrences)
      throws DateTimeParseException, EventExistException, UnsupportedOperationException;

  /**
   * Creates a series of all day events that repeats N times on specific weekdays.
   *
   * @param subject             event subject
   * @param startDate           event start date
   * @param weekdays            weekdays event repeat on
   * @param numberOfOccurrences event occurrence time
   * @throws DateTimeParseException if date/time string format is wrong
   * @throws EventExistException    if event(s) has already existed
   */
  void createAllDaySeriesEventWithOccurrence(String subject, String startDate,
                                             Set<Weekday> weekdays,
                                             int numberOfOccurrences)
      throws DateTimeParseException, EventExistException;

  /**
   * Creates an event series until a specific date (inclusive).
   *
   * @param subject       event subject
   * @param startDateTime event start date and time
   * @param endDateTime   event end date and time
   * @param weekdays      weekdays event repeat on
   * @param seriesEndDate series end date
   * @throws DateTimeParseException        if date/time string format is wrong
   * @throws EventExistException           if event(s) has already existed
   * @throws UnsupportedOperationException if events span more than one day
   */
  void createSeriesEventWithEndDate(String subject, String startDateTime, String endDateTime,
                                    Set<Weekday> weekdays, String seriesEndDate)
      throws DateTimeParseException, EventExistException, UnsupportedOperationException;

  /**
   * Creates a series of all day events until a specific date (inclusive).
   *
   * @param subject       event subject
   * @param startDate     event start date
   * @param weekdays      weekdays event repeat on
   * @param seriesEndDate series end date
   * @throws DateTimeParseException if date/time string format is wrong
   * @throws EventExistException    if event(s) has already existed
   */
  void createAllDaySeriesEventWithEndDate(String subject, String startDate, Set<Weekday> weekdays,
                                          String seriesEndDate)
      throws DateTimeParseException, EventExistException;

  /**
   * Identify the event that has the given subject and starts at the given date and time, and edit
   * its property. This results in change in property for a single instance (irrespective of whether
   * the identified event is single or part of a series).
   *
   * @param args list of subject, property, start date time, end date time, new property value.
   * @throws IllegalArgumentException      if property doesn't exist or invalid status value
   * @throws DateTimeParseException        if date/time string format is wrong
   * @throws EventNotFoundException        if no event found
   * @throws UnsupportedOperationException if modify start after end or modify end before start
   * @throws EventExistException           if event(s) has already existed
   */
  void editSingleEvent(List<String> args)
      throws IllegalArgumentException, DateTimeParseException, EventNotFoundException,
      UnsupportedOperationException, EventExistException;

  /**
   * Identify the event(s) that has the given subject and starts at the given date and time and edit
   * its property. If this event is part of a series then the properties of all events in that
   * series that start at or after the given date and time should be changed. If this event is not
   * part of a series then this has the same effect as the command above.
   *
   * @param subject       event subject
   * @param property      property to be edited
   * @param startDateTime event start date and time
   * @param newValue      new property value
   * @throws IllegalArgumentException      if property doesn't exist or invalid status value
   * @throws DateTimeParseException        if date/time string format is wrong
   * @throws EventNotFoundException        if no events and no series found
   * @throws EventExistException           if event(s) has already existed
   * @throws UnsupportedOperationException if try to update end date
   */
  void editEventStartFrom(String subject, String property, String startDateTime,
                          String newValue)
      throws IllegalArgumentException, DateTimeParseException, EventNotFoundException,
      EventExistException, UnsupportedOperationException;

  /**
   * Identify the event that has the given subject and starts at the given date and time and edit
   * its property. If this event is part of a series then the properties of all events in that
   * series should be changed. If this event is not part of a series then this has the same effect
   * as the edit command.
   *
   * @param subject       event subject
   * @param property      property to be edited
   * @param startDateTime event start date and time
   * @param newValue      new property value
   * @throws IllegalArgumentException      if property doesn't exist or invalid status value
   * @throws DateTimeParseException        if date/time string format is wrong
   * @throws EventNotFoundException        if no events and no series found
   * @throws EventExistException           if event(s) has already existed
   * @throws UnsupportedOperationException if try to update start date or end date
   */
  void editSeriesStartFrom(String subject, String property, String startDateTime,
                           String newValue)
      throws IllegalArgumentException, DateTimeParseException, EventNotFoundException,
      EventExistException, UnsupportedOperationException;

  /**
   * Get a bulleted list of all events on that day along with their start and end time and
   * location (if any).
   *
   * @param date event date
   * @return bulleted list of events
   * @throws IllegalArgumentException if property doesn't exist or invalid status value
   * @throws DateTimeParseException   if date/time string format is wrong
   */
  String getEventsOnDate(String date) throws DateTimeParseException;

  /**
   * Print a bulleted list of all events that partly or completely lie in the given interval. Each
   * event should be listed in a single line and must be in the following format: "subject" starting
   * on "start date" at "start time", ending on "end date" at "end time" including their start and
   * end times and location (if any).
   *
   * @param startDateTime range start date and time
   * @param endDateTime   range end date and time
   * @return bulleted list of events
   * @throws IllegalArgumentException if property doesn't exist or invalid status value
   * @throws DateTimeParseException   if date/time string format is wrong
   */
  String getEventsInRange(String startDateTime, String endDateTime) throws DateTimeParseException;

  /**
   * Get busy status if the user has events scheduled on a given day and time, otherwise, available.
   *
   * @param dateTime given date and time
   * @return Status of Calendar
   * @throws IllegalArgumentException if property doesn't exist or invalid status value
   * @throws DateTimeParseException   if date/time string format is wrong
   */
  CalendarStatus isBusy(String dateTime) throws DateTimeParseException;

  /**
   * Get all events in Calendar.
   *
   * @return list of events
   */
  List<EventReadOnlyInterface> getAllEventsReadOnly();

  /**
   * Determines whether the given event is part of a series.
   *
   * @param event event to be checked
   * @return true if the event is part of a series, false otherwise
   * @throws NullPointerException if {@param event} is null
   */
  boolean isSeriesEvent(EventReadOnlyInterface event);

  /**
   * Gets the set of weekdays on which the specified series event repeats.
   *
   * @param event series event
   * @return set of weekdays the event repeats on
   * @throws IllegalArgumentException if {@param event} is not a series event
   * @throws NullPointerException     if {@param event} is null
   */
  Set<Weekday> getSeriesWeekdays(EventReadOnlyInterface event);

  /**
   * Gets the series end date of the given event, if the event is part of a series defined by an
   * until-end date rule.
   *
   * @param event series event
   * @return series end date, or null if event does not use an end-date rule
   * @throws IllegalArgumentException if {@param event} is not a series event
   * @throws NullPointerException     if {@param event} is null
   */
  LocalDate getSeriesUntilEnd(EventReadOnlyInterface event);

  /**
   * Gets the number of occurrences for the specified series event, if the event is created with a
   * fixed number of occurrences.
   *
   * @param event series event
   * @return number of occurrences, or null if event does not use an occurrence-based rule
   * @throws IllegalArgumentException if {@param event} is not a series event
   * @throws NullPointerException     if {@param event} is null
   */
  Integer getSeriesOccurrence(EventReadOnlyInterface event);

}
