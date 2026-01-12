package model.calendar;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Set;
import model.event.EventExistException;
import model.event.EventNotFoundException;

/**
 * This class represents the general purposes of a wrapper class to manage multiple calendars. It
 * can add and edit a specified calendar, activate a calendar to be used, and copy events between
 * two calendars.
 */
public interface CalendarManagerInterface {
  /**
   * Create a new calendar with a unique name and timezone as specified.
   *
   * @param name     calendar name
   * @param timeZone calendar timezone
   * @throws CalendarExistException if calendar {@param name} has already existed
   * @throws DateTimeException      if the zone ID has an invalid format or cannot be found
   */
  void addCalendar(String name, String timeZone) throws CalendarExistException, DateTimeException;

  /**
   * Edit an existing property (name or timezone) of the calendar.
   *
   * @param name     calendar name
   * @param property calendar property to be edited
   * @throws CalendarNotFoundException if calendar {@param name} doesn't exist
   * @throws IllegalArgumentException  if property not found
   * @throws CalendarExistException    if calendar {@param name} has already existed
   * @throws DateTimeException         if the zone ID has an invalid format or cannot be found
   */
  void editCalendar(String name, String property, String newValue)
      throws CalendarNotFoundException, IllegalArgumentException, CalendarExistException,
      DateTimeException;

  /**
   * Set calendar context to create/edit/print/export events in the context of that calendar.
   *
   * @param name calendar name.
   * @throws CalendarNotFoundException if calendar {@param name} doesn't exist
   */
  void activateCalendar(String name) throws CalendarNotFoundException;

  /**
   * Get currently active calendar.
   *
   * @return active calendar
   * @throws CalendarNotFoundException if no calendar has been activated
   */
  CalendarModelInterface getActiveCalendar() throws CalendarNotFoundException;

  /**
   * Copy a specific event with the given name and start date/time from the current calendar to the
   * target calendar to start at the specified date/time.
   *
   * @param eventName           event subject
   * @param startDateTime       event start date time
   * @param targetCalendarName  target calendar name
   * @param targetStartDateTime target event date time
   * @throws NullPointerException          if no calendar is active
   * @throws CalendarNotFoundException     if calendar {@param targetCalendarName} doesn't exist
   * @throws UnsupportedOperationException if copy to active calendar itself
   * @throws DateTimeParseException        if date/time string format is wrong
   * @throws EventNotFoundException        if no events  found
   * @throws EventExistException           if event(s) has already existed
   */
  void copyEvent(String eventName, String startDateTime, String targetCalendarName,
                 String targetStartDateTime)
      throws NullPointerException, CalendarNotFoundException, UnsupportedOperationException,
      DateTimeParseException, EventNotFoundException, EventExistException;

  /**
   * Copy events scheduled on {@param date} in active calendar to {@param targetCalendarName} on
   * {@param targetDate}. Series events can only span one day. All events copied, despite originally
   * in series, will become SingleEvent because no other events in the series will be copied
   * together. Additionally, events scheduled on {@param date} includes events start before that
   * date. In other words, as long as an event covers {@param date}, this method will copy it.
   *
   * @param date               date to be copied
   * @param targetCalendarName target calendar name
   * @param targetDate         target date
   * @throws NullPointerException          if no calendar is active
   * @throws CalendarNotFoundException     if calendar {@param targetCalendarName} doesn't exist
   * @throws UnsupportedOperationException if copy to active calendar itself
   * @throws DateTimeParseException        if date/time string format is wrong
   * @throws EventNotFoundException        if no events found
   * @throws EventExistException           if event(s) has already existed
   */
  void copyEventsOnDate(String date, String targetCalendarName, String targetDate)
      throws NullPointerException, CalendarNotFoundException, UnsupportedOperationException,
      DateTimeParseException, EventNotFoundException, EventExistException;

  /**
   * Copy events scheduled between {@param intervalStart} and {@param intervalEnd} in active
   * calendar to {@param targetCalendarName} with {@param targetIntervalStart} as the start of the
   * interval in target calendar. If an event series partly overlaps with the specified range, only
   * those events in the series that overlap with the specified range should be copied, and their
   * status as part of a series should be retained in the destination calendar.
   *
   * @param intervalStart       start of the interval
   * @param intervalEnd         end of the interval (inclusive)
   * @param targetCalendarName  target calendar name
   * @param targetIntervalStart start of the interval in target calendar
   * @throws NullPointerException          if no calendar is active
   * @throws CalendarNotFoundException     if calendar {@param targetCalendarName} doesn't exist
   * @throws UnsupportedOperationException if copy to active calendar itself or series events span
   *                                       more than one day by changing timezone
   * @throws DateTimeParseException        if date/time string format is wrong
   * @throws EventNotFoundException        if no events found
   * @throws EventExistException           if event(s) has already existed
   */
  void copyEventsBetween(String intervalStart, String intervalEnd, String targetCalendarName,
                         String targetIntervalStart)
      throws NullPointerException, CalendarNotFoundException, UnsupportedOperationException,
      DateTimeParseException, EventNotFoundException, EventExistException;

  /**
   * Gets a set of names of all calendars in this manager.
   *
   * @return a set of names
   * @throws CalendarNotFoundException if no calendar in this manager
   */
  Set<String> getAllCalendarNames() throws CalendarNotFoundException;

  /**
   * Get the timezone of the specified calendar.
   *
   * @param calendarName calendar name
   * @return timezone of the specified calendar
   * @throws CalendarNotFoundException if calendar {@param calendarName} doesn't exist
   */
  ZoneId getCalendarTimezone(String calendarName) throws CalendarNotFoundException;

}
