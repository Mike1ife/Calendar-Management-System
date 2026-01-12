package model.calendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import model.event.EventExistException;
import model.event.EventInterface;
import model.event.EventNotFoundException;
import model.event.SeriesInterface;

/**
 * This class represents general purposes of a Calendar Model.
 */
public interface TimezoneCalendarModelInterface extends CalendarModelInterface {
  /**
   * Get updated event(s) named {@param subject} shifted starting from {@param start} to
   * {@param target}.
   *
   * @param subject event subject
   * @param start   event start date time
   * @param target  target start date time
   * @return list of events
   * @throws EventNotFoundException if event(s) not found
   */
  List<EventInterface> getShiftedEvent(String subject, LocalDateTime start, LocalDateTime target)
      throws EventNotFoundException;

  /**
   * Add list of events to Calendar.
   *
   * @param events list of events
   * @throws EventExistException if event(s) has already existed
   */
  void addSingleEventFromList(List<EventInterface> events) throws EventExistException;

  /**
   * Add Series and its events to Calendar and attaching events to series.
   *
   * @param seriesEventsBetween map of series to list of series events
   * @throws EventExistException if event(s) has already existed
   */
  void addSeriesEventsFromMap(Map<SeriesInterface, List<EventInterface>> seriesEventsBetween)
      throws EventExistException;

  /**
   * Shift timezone of {@code this} from {@param oldTimeZoneId} to {@param newTimeZoneId}.
   *
   * @param oldTimeZoneId old time zone
   * @param newTimeZoneId new time zone
   */
  void shiftTimeZone(ZoneId oldTimeZoneId, ZoneId newTimeZoneId);

  /**
   * Get updated events on {@param original} shifted to {@param target}. Meanwhile, transfer event
   * timezone from {@param oldTimeZoneId} to {@param newTimeZoneId}.
   *
   * @param original      original date
   * @param target        target date
   * @param oldTimeZoneId original timezone
   * @param newTimeZoneId target timezone
   * @return list of events
   * @throws EventNotFoundException if event(s) not found
   */
  List<EventInterface> getShiftedEventsOnDate(LocalDate original, LocalDate target,
                                              ZoneId oldTimeZoneId, ZoneId newTimeZoneId)
      throws EventNotFoundException;

  /**
   * Get updated single events (not in series) between {@param intervalStart} and
   * {@param intervalEnd} shifted to {@param targetIntervalStart}. Meanwhile, transfer event
   * timezone from {@param oldTimeZoneId} to {@param newTimeZoneId}.
   *
   * @param intervalStart       interval start date
   * @param intervalEnd         interval end date
   * @param targetIntervalStart interval start date in target calendar
   * @param oldTimeZoneId       original timezone
   * @param newTimeZoneId       target timezone
   * @return list of single events
   */
  List<EventInterface> getShiftedSingleEventsBetween(LocalDate intervalStart, LocalDate intervalEnd,
                                                     LocalDate targetIntervalStart,
                                                     ZoneId oldTimeZoneId, ZoneId newTimeZoneId);

  /**
   * Get updated Series Events and corresponding Series between {@param intervalStart} and
   * {@param intervalEnd} shifted to start from {@param targetIntervalStart} while maintain
   * recurring weekdays. Meanwhile, transfer event timezone from {@param oldTimeZoneId} to
   * {@param newTimeZoneId}.
   *
   * @param intervalStart       interval start date
   * @param intervalEnd         interval end date
   * @param targetIntervalStart interval start date in target calendar
   * @param oldTimeZoneId       original timezone
   * @param newTimeZoneId       target timezone
   * @return map of series to list of its events in range
   * @throws UnsupportedOperationException if shifted series events span more than one day
   */
  Map<SeriesInterface, List<EventInterface>> getShiftedSeriesEventsBetween(
      LocalDate intervalStart,
      LocalDate intervalEnd,
      LocalDate targetIntervalStart,
      ZoneId oldTimeZoneId,
      ZoneId newTimeZoneId);
}
