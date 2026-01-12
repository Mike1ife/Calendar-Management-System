package model.event;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import model.calendar.Weekday;

/**
 * This class demonstrate general attributes and behaviors of a Series in Calendar. It contains
 * double dispatch to not only retrieve end date for Series ending at a specified date, but also
 * distinguish between its subclasses.
 */
public abstract class SeriesImpl implements SeriesInterface {
  protected final UUID seriesId;
  protected final Set<Weekday> weekdays;

  /**
   * Create a Series of events from all specified attributes.
   *
   * @param seriesId series uid
   * @param weekdays weekdays event repeat on
   */
  protected SeriesImpl(UUID seriesId, Set<Weekday> weekdays) {
    this.seriesId = seriesId;
    this.weekdays = weekdays;
  }

  @Override
  public String getSeriesId() {
    return this.seriesId.toString();
  }

  @Override
  public Set<Weekday> getWeekdays() {
    return Set.copyOf(this.weekdays);
  }

  @Override
  public LocalDate getEndDate() {
    return null;
  }

  @Override
  public Integer getNumberOfOccurrences() {
    return null;
  }

  @Override
  public void decrementNumberOfOccurrences() {
  }
}
