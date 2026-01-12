package model.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import model.calendar.Weekday;

/**
 * This class represent general purposes of a Series in Calendar. Each Series is uniquely marked by
 * a unique series id. A Series can generate a series of SingleEvents of same start and end time.
 */
public interface SeriesInterface {
  /**
   * Generate a series of SingleEvents of the same subject, start date and time, end date and time.
   *
   * @param subject            event subject
   * @param eventStartDateTime event start date time
   * @param eventEndTime       event end time
   * @return a series SingleEvents
   */
  List<EventInterface> generateEvents(String subject, LocalDateTime eventStartDateTime,
                                      LocalTime eventEndTime);

  /**
   * Copy {@code this}.
   *
   * @return the copy of {@code this}
   */
  SeriesInterface copy();

  /**
   * Get series id.
   *
   * @return series id
   */
  String getSeriesId();

  /**
   * Get weekdays events in series repeat on.
   *
   * @return set of weekdays
   */
  Set<Weekday> getWeekdays();

  /**
   * Get Series end date (only SeriesUntilDate has series end date).
   *
   * @return series end date
   */
  LocalDate getEndDate();

  /**
   * Get Series occurrence time (only SeriesOccurrence has number of occurrence).
   *
   * @return number of occurrence
   */
  Integer getNumberOfOccurrences();

  /**
   * Decrease number of occurrences.
   */
  void decrementNumberOfOccurrences();
}
