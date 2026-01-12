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
 * This class represents Series recurring on specified weekdays until a specified end date. It can
 * generate a series SingleEvents from start to end date.
 */
public class SeriesUntilEnd extends SeriesImpl {
  private final LocalDate endDate;

  /**
   * Create a SeriesUntilEnd with generated unique series id, start date, repeating weekdays, and
   * end date.
   *
   * @param seriesId unique series id
   * @param weekdays weekdays events repeat on
   * @param endDate  series end date
   */
  private SeriesUntilEnd(UUID seriesId, Set<Weekday> weekdays,
                         LocalDate endDate) {
    super(seriesId, weekdays);
    this.endDate = endDate;
  }

  @Override
  public List<EventInterface> generateEvents(String subject, LocalDateTime eventStartDateTime,
                                             LocalTime eventEndTime) {
    List<EventInterface> events = new ArrayList<>();
    LocalDate currentDate = eventStartDateTime.toLocalDate();
    LocalTime eventStartTime = eventStartDateTime.toLocalTime();

    while (!currentDate.isAfter(this.endDate)) {
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
  public LocalDate getEndDate() {
    return this.endDate;
  }

  @Override
  public SeriesUntilEnd copy() {
    return new SeriesUntilEndBuilder()
        .setWeekdays(this.weekdays)
        .setEndDate(this.endDate)
        .build();
  }

  /**
   * This class extends SeriesBuilder to build SeriesUntilEnd with series end date setting and
   * building.
   */
  public static class SeriesUntilEndBuilder extends SeriesBuilder<SeriesUntilEndBuilder> {
    private LocalDate endDate;

    /**
     * Set series end date for builder.
     *
     * @param endDate series end date
     * @return updated builder
     */
    public SeriesUntilEndBuilder setEndDate(LocalDate endDate) {
      this.endDate = endDate;
      return returnBuilder();
    }

    @Override
    protected SeriesUntilEndBuilder returnBuilder() {
      return this;
    }

    @Override
    public SeriesUntilEnd build() {
      return new SeriesUntilEnd(UUID.randomUUID(), this.weekdays, this.endDate);
    }
  }
}
