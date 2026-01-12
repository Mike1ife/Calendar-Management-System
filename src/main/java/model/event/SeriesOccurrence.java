package model.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import model.calendar.Weekday;

/**
 * This class represents Series repeating N times on specified weekdays. It can generate a series
 * of {@code numberOfOccurrences} SingleEvents.
 */
public class SeriesOccurrence extends SeriesImpl {
  private Integer numberOfOccurrences;

  /**
   * Create a SeriesOccurrence with generated unique series id, start date, repeating weekdays, and
   * the number of occurrences.
   *
   * @param seriesId            unique series id
   * @param weekdays            weekdays events repeat on
   * @param numberOfOccurrences number of events associated with {@code this}
   */
  private SeriesOccurrence(UUID seriesId, Set<Weekday> weekdays,
                           int numberOfOccurrences) {
    super(seriesId, weekdays);
    this.numberOfOccurrences = numberOfOccurrences;
  }

  @Override
  public List<EventInterface> generateEvents(String subject, LocalDateTime eventStartDateTime,
                                             LocalTime eventEndTime) {
    List<EventInterface> events = new ArrayList<>();
    LocalDate currentDate = eventStartDateTime.toLocalDate();
    LocalTime eventStartTime = eventStartDateTime.toLocalTime();

    while (events.size() < this.numberOfOccurrences) {
      Weekday currentWeekday = Weekday.valueOf(currentDate.getDayOfWeek().toString());
      if (this.weekdays.contains(currentWeekday)) {
        EventInterface event = new SingleEvent.SingleEventBuilder()
            .setSubject(subject)
            .setStartDateTime(LocalDateTime.of(currentDate, eventStartTime))
            .setEndDateTime(LocalDateTime.of(currentDate, eventEndTime))
            .build();
        events.add(event);
      }
      currentDate = currentDate.plusDays(1);
    }
    return events;
  }

  @Override
  public Integer getNumberOfOccurrences() {
    return this.numberOfOccurrences;
  }

  @Override
  public SeriesOccurrence copy() {
    return new SeriesOccurrenceBuilder()
        .setWeekdays(this.weekdays)
        .setNumberOfOccurrences(this.numberOfOccurrences)
        .build();
  }

  @Override
  public void decrementNumberOfOccurrences() {
    this.numberOfOccurrences -= 1;
  }

  /**
   * This class represents builder for SeriesOccurrence.
   */
  public static class SeriesOccurrenceBuilder extends SeriesBuilder<SeriesOccurrenceBuilder> {
    private Integer numberOfOccurrences;

    /**
     * Set repeating times for builder.
     *
     * @param numberOfOccurrences event occurrence time
     * @return updated builder
     */
    public SeriesOccurrenceBuilder setNumberOfOccurrences(int numberOfOccurrences) {
      this.numberOfOccurrences = numberOfOccurrences;
      return returnBuilder();
    }

    @Override
    protected SeriesOccurrenceBuilder returnBuilder() {
      return this;
    }

    @Override
    public SeriesOccurrence build() {
      return new SeriesOccurrence(UUID.randomUUID(), this.weekdays, this.numberOfOccurrences);
    }
  }
}
