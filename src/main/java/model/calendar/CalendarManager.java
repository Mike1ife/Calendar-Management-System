package model.calendar;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import model.event.EventExistException;
import model.event.EventInterface;
import model.event.EventNotFoundException;
import model.event.SeriesInterface;

/**
 * This class implements CalendarManagerInterface to manage multiple calendars.
 */
public class CalendarManager implements CalendarManagerInterface {
  private final Map<String, TimezoneCalendarModelInterface> calendars;
  private final Map<TimezoneCalendarModelInterface, ZoneId> calendarToTimeZoneMap;
  private TimezoneCalendarModelInterface activeCalendar;

  /**
   * Create a CalendarManager with top maps: calendar name to model, and calendar to its timezone.
   */
  public CalendarManager() {
    calendars = new LinkedHashMap<>();
    calendarToTimeZoneMap = new HashMap<>();
  }

  @Override
  public void addCalendar(String name, String timeZone) throws CalendarExistException,
      DateTimeException {
    if (calendars.containsKey(name)) {
      throw new CalendarExistException("Calendar already exists");
    }
    TimezoneCalendarModelInterface calendar = new TimezoneCalendar();
    this.calendars.put(name, calendar);
    this.calendarToTimeZoneMap.put(calendar, ZoneId.of(timeZone));
  }

  @Override
  public void editCalendar(String name, String property, String newValue)
      throws CalendarNotFoundException, IllegalArgumentException, CalendarExistException,
      DateTimeException {
    if (!calendars.containsKey(name)) {
      throw new CalendarNotFoundException("Calendar does not exist");
    }

    TimezoneCalendarProperty timezoneCalendarProperty =
        TimezoneCalendarProperty.valueOf(property.toUpperCase());

    if (timezoneCalendarProperty == TimezoneCalendarProperty.NAME) {
      editCalendarNameHelper(name, newValue);
    } else {
      editCalendarTimeZoneHelper(this.calendars.get(name), newValue);
    }
  }

  /**
   * Help edit calendar name.
   *
   * @param oldName original name
   * @param newName new name
   * @throws CalendarExistException if calendar {@param newName} has already existed
   */
  private void editCalendarNameHelper(String oldName, String newName)
      throws CalendarExistException {

    if (this.calendars.containsKey(newName)) {
      throw new CalendarExistException("Calendar already exists");
    }

    Map<String, TimezoneCalendarModelInterface> updated = new LinkedHashMap<>();

    for (Map.Entry<String, TimezoneCalendarModelInterface> entry : this.calendars.entrySet()) {
      if (entry.getKey().equals(oldName)) {
        updated.put(newName, entry.getValue());
      } else {
        updated.put(entry.getKey(), entry.getValue());
      }
    }

    this.calendars.clear();
    this.calendars.putAll(updated);
  }

  /**
   * Help shift calendar timezone.
   *
   * @param calendar    calendar to be edited
   * @param newTimeZone new time zone
   * @throws DateTimeException if the zone ID has an invalid format or cannot be found
   */
  private void editCalendarTimeZoneHelper(TimezoneCalendarModelInterface calendar,
                                          String newTimeZone)
      throws DateTimeException {
    ZoneId newTimeZoneId = ZoneId.of(newTimeZone);
    ZoneId oldTimeZoneId = this.calendarToTimeZoneMap.get(calendar);

    calendar.shiftTimeZone(oldTimeZoneId, newTimeZoneId);
    this.calendarToTimeZoneMap.put(calendar, newTimeZoneId);
  }

  @Override
  public void activateCalendar(String name) {
    if (!calendars.containsKey(name)) {
      throw new CalendarNotFoundException("Calendar not found");
    }
    this.activeCalendar = calendars.get(name);
  }

  @Override
  public TimezoneCalendarModelInterface getActiveCalendar() throws CalendarNotFoundException {
    Objects.requireNonNull(this.activeCalendar, "Must activate calendar first");
    return this.activeCalendar;
  }

  @Override
  public void copyEvent(String eventName, String startDateTime, String targetCalendarName,
                        String targetStartDateTime)
      throws NullPointerException, CalendarNotFoundException, UnsupportedOperationException,
      DateTimeParseException, EventNotFoundException, EventExistException {
    copyExceptionHelper(targetCalendarName);

    LocalDateTime start = LocalDateTime.parse(startDateTime);
    LocalDateTime target = LocalDateTime.parse(targetStartDateTime);

    List<EventInterface> events = this.activeCalendar.getShiftedEvent(eventName, start, target);
    this.calendars.get(targetCalendarName).addSingleEventFromList(events);
  }

  @Override
  public void copyEventsOnDate(String date, String targetCalendarName, String targetDate)
      throws NullPointerException, CalendarNotFoundException, UnsupportedOperationException,
      DateTimeParseException, EventNotFoundException, EventExistException {
    copyExceptionHelper(targetCalendarName);

    LocalDate original = LocalDate.parse(date);
    LocalDate target = LocalDate.parse(targetDate);

    List<EventInterface> events = this.activeCalendar.getShiftedEventsOnDate(original, target,
        this.calendarToTimeZoneMap.get(this.activeCalendar),
        this.calendarToTimeZoneMap.get(this.calendars.get(targetCalendarName)));
    this.calendars.get(targetCalendarName).addSingleEventFromList(events);
  }

  @Override
  public void copyEventsBetween(String intervalStart, String intervalEnd, String targetCalendarName,
                                String targetIntervalStart)
      throws NullPointerException, CalendarNotFoundException, UnsupportedOperationException,
      DateTimeParseException, EventNotFoundException, EventExistException {
    copyExceptionHelper(targetCalendarName);

    LocalDate intervalStartDate = LocalDate.parse(intervalStart);
    LocalDate intervalEndDate = LocalDate.parse(intervalEnd);
    LocalDate targetIntervalStartDate = LocalDate.parse(targetIntervalStart);

    List<EventInterface> singleEventsBetween =
        this.activeCalendar.getShiftedSingleEventsBetween(intervalStartDate, intervalEndDate,
            targetIntervalStartDate, this.calendarToTimeZoneMap.get(this.activeCalendar),
            this.calendarToTimeZoneMap.get(this.calendars.get(targetCalendarName)));

    Map<SeriesInterface, List<EventInterface>> seriesEventsBetween =
        this.activeCalendar.getShiftedSeriesEventsBetween(intervalStartDate, intervalEndDate,
            targetIntervalStartDate, this.calendarToTimeZoneMap.get(this.activeCalendar),
            this.calendarToTimeZoneMap.get(this.calendars.get(targetCalendarName)));

    if (singleEventsBetween.isEmpty() && seriesEventsBetween.isEmpty()) {
      throw new EventNotFoundException("Events and series not found");
    }

    this.calendars.get(targetCalendarName).addSingleEventFromList(singleEventsBetween);
    this.calendars.get(targetCalendarName).addSeriesEventsFromMap(seriesEventsBetween);
  }

  @Override
  public Set<String> getAllCalendarNames() throws CalendarNotFoundException {
    if (this.calendars.isEmpty()) {
      throw new CalendarNotFoundException("No Calendars");
    }
    return this.calendars.keySet();
  }

  @Override
  public ZoneId getCalendarTimezone(String calendarName) {
    if (!this.calendars.containsKey(calendarName)) {
      throw new CalendarNotFoundException("Calendar not found");
    }
    return this.calendarToTimeZoneMap.get(this.calendars.get(calendarName));
  }

  /**
   * Checks active calendar and target calendar conditions before copying events.
   *
   * @param targetCalendarName target calendar name
   * @throws NullPointerException          if no calendar is active
   * @throws CalendarNotFoundException     if calendar {@param targetCalendarName} doesn't exist
   * @throws UnsupportedOperationException if attempt to copy to the same active calendar
   */
  private void copyExceptionHelper(String targetCalendarName)
      throws NullPointerException, CalendarNotFoundException, UnsupportedOperationException {
    Objects.requireNonNull(this.activeCalendar, "Must activate calendar first");
    if (!this.calendars.containsKey(targetCalendarName)) {
      throw new CalendarNotFoundException("Calendar not found");
    }

    if (this.activeCalendar == this.calendars.get(targetCalendarName)) {
      throw new UnsupportedOperationException("Cannot copy events to the same calendar");
    }
  }
}
