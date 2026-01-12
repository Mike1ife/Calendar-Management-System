package model.event;

import java.util.Set;
import model.calendar.Weekday;

/**
 * This class represents general purposes of a builder of Series.
 *
 * @param <T> builder type
 */
public abstract class SeriesBuilder<T extends SeriesBuilder<T>> {
  protected Set<Weekday> weekdays;

  /**
   * Return {@code this}.
   *
   * @return {@code this}
   */
  protected abstract T returnBuilder();

  /**
   * Set weekdays for builder.
   *
   * @param weekdays series repeating weekdays
   * @return updated builder
   */
  public T setWeekdays(Set<Weekday> weekdays) {
    this.weekdays = weekdays;
    return returnBuilder();
  }

  /**
   * Build a Series.
   *
   * @return built Series
   */
  public abstract SeriesImpl build();
}
