package swing.view.dialogs.event.data;

/**
 * Basic data container for event dialogs. Stores common event fields such as subject, timing,
 * location, description, status, all-day flag, and calendar name.
 */
public class BaseData implements BaseDataInterface {
  protected final String subject;
  protected final String startDateTime;
  protected final String endDateTime;
  protected final String location;
  protected final String description;
  protected final String status;
  protected final boolean isAllDay;
  protected final String calendarName;

  /**
   * Create a base event data object.
   *
   * @param subject       event subject
   * @param startDateTime event start date-time string
   * @param endDateTime   event end date-time string
   * @param location      event location
   * @param description   event description
   * @param status        event status value
   * @param isAllDay      whether event is all day
   * @param calendarName  associated calendar name
   */
  public BaseData(String subject, String startDateTime, String endDateTime,
                  String location, String description, String status,
                  boolean isAllDay, String calendarName) {
    this.subject = subject;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.location = location;
    this.description = description;
    this.status = status;
    this.isAllDay = isAllDay;
    this.calendarName = calendarName;
  }

  @Override
  public String getSubject() {
    return subject;
  }

  @Override
  public String getStartDateTime() {
    return startDateTime;
  }

  @Override
  public String getEndDateTime() {
    return endDateTime;
  }

  @Override
  public String getLocation() {
    return location;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getStatus() {
    return status;
  }

  @Override
  public boolean isAllDay() {
    return isAllDay;
  }

  @Override
  public String getCalendarName() {
    return calendarName;
  }
}
