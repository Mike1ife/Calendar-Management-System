package swing.view.dialogs.event.data;

/**
 * Represents the finalized edit options for an event after the user has interacted with the
 * edit-option dialog. This class extends {@link BaseData} by including additional flags that
 * specify whether the user intends to update only the selected event, update the event and all
 * following occurrences in the series, or update the entire recurring series.
 */
public class EventEditData extends BaseData {
  private final boolean isStartFrom;
  private final boolean isAllSeries;

  /**
   * Constructs an EventEditData object containing both the updated event fields and
   * the edit-scope options selected by the user.
   *
   * @param subject       the updated subject or title of the event
   * @param startDateTime the updated event start time in ISO-8601 format
   * @param endDateTime   the updated event end time in ISO-8601 format
   * @param location      the updated event location
   * @param description   the updated event description
   * @param status        the updated event status (e.g., public/private)
   * @param isAllDay      whether the updated event spans the entire day
   * @param calendarName  the name of the calendar this event belongs to
   * @param isStartFrom   true if the user selected “edit this and following events” in a series
   * @param isAllSeries   true if the user selected “edit entire series”
   */
  public EventEditData(String subject, String startDateTime, String endDateTime,
                       String location, String description, String status,
                       boolean isAllDay, String calendarName, boolean isStartFrom,
                       boolean isAllSeries) {

    super(subject, startDateTime, endDateTime, location, description,
        status, isAllDay, calendarName);
    this.isStartFrom = isStartFrom;
    this.isAllSeries = isAllSeries;
  }

  /**
   * Get the edit option of this event edition.
   *
   * @return true if edit events of the same subject and start date/time and the following series
   */
  public boolean isStartFrom() {
    return isStartFrom;
  }

  /**
   * Get the edit option of this event edition.
   *
   * @return true if edit events of the same subject and start date/time and the whole series
   */
  public boolean isAllSeries() {
    return isAllSeries;
  }
}
