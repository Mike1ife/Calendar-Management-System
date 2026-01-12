package swing.view.dialogs.calendar;

/**
 * Data holder for calendar name and timezone as entered in calendar dialogs.
 */
public class CalendarData {

  private final String name;
  private final String timezone;

  /**
   * Create a calendar data object.
   *
   * @param name     calendar name
   * @param timezone calendar timezone
   */
  public CalendarData(String name, String timezone) {
    this.name = name;
    this.timezone = timezone;
  }

  /**
   * Get the calendar name.
   *
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Get the calendar timezone.
   *
   * @return timezone
   */
  public String getTimezone() {
    return timezone;
  }
}
