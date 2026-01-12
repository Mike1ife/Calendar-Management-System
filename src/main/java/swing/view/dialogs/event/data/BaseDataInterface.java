package swing.view.dialogs.event.data;

/**
 * Common interface for dialog event data.
 * Provides accessors for subject, timing, location,
 * description, status, all-day flag, and calendar name.
 */
public interface BaseDataInterface {
  /**
   * Get event subject.
   *
   * @return subject
   */
  String getSubject();

  /**
   * Get event start date and time.
   *
   * @return start date-time string
   */
  String getStartDateTime();

  /**
   * Get event end date and time.
   *
   * @return end date-time string
   */
  String getEndDateTime();

  /**
   * Get event location.
   *
   * @return location
   */
  String getLocation();

  /**
   * Get event description.
   *
   * @return description
   */
  String getDescription();

  /**
   * Get event status value.
   *
   * @return status string
   */
  String getStatus();

  /**
   * Determine whether the event is all day.
   *
   * @return true if all day, false otherwise
   */
  boolean isAllDay();

  /**
   * Get the associated calendar name.
   *
   * @return calendar name
   */
  String getCalendarName();
}
