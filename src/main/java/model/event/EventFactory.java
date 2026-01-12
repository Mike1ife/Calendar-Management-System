package model.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import model.calendar.Weekday;

/**
 * This class serves as a factory to manufacture Event and Series.
 */
public class EventFactory {
  /**
   * Create a SingleEvent.
   *
   * @param subject       event subject
   * @param startDateTime event start date and time
   * @param endDateTime   event end date and time
   * @return new event
   */
  public static EventInterface createSingleEvent(String subject, LocalDateTime startDateTime,
                                                 LocalDateTime endDateTime) {
    return new SingleEvent.SingleEventBuilder()
        .setSubject(subject)
        .setStartDateTime(startDateTime)
        .setEndDateTime(endDateTime)
        .build();
  }

  /**
   * Create a Series repeating N times on specified weekdays.
   *
   * @param weekdays            weekdays event repeat on
   * @param numberOfOccurrences event occurrence time
   * @return new series
   */
  public static SeriesOccurrence createSeriesWithOccurrence(Set<Weekday> weekdays,
                                                            int numberOfOccurrences) {
    return new SeriesOccurrence.SeriesOccurrenceBuilder()
        .setWeekdays(weekdays)
        .setNumberOfOccurrences(numberOfOccurrences)
        .build();
  }

  /**
   * Create a Series until a specified date on specified weekdays.
   *
   * @param weekdays weekdays event repeat on
   * @param endDate  series end date
   * @return new series
   */
  public static SeriesUntilEnd createSeriesWithEndDate(Set<Weekday> weekdays, LocalDate endDate) {
    return new SeriesUntilEnd.SeriesUntilEndBuilder()
        .setWeekdays(weekdays)
        .setEndDate(endDate)
        .build();
  }
}
