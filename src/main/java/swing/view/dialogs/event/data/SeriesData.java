package swing.view.dialogs.event.data;

/**
 * SeriesData extends {@link BaseData} to represent edit instructions for a recurring event. In
 * addition to the base event fields, this class indicates whether the user intends to edit only
 * future occurrences or the entire series.
 */
public class SeriesData extends BaseData {
  private final boolean isEditStartFrom;
  private final boolean isEditSeries;

  /**
   * Constructs a {@code SeriesData} object containing event details and user selection for how a
   * recurring event should be edited.
   *
   * @param subject         event subject
   * @param startDateTime   event start date and time
   * @param endDateTime     event end date and time
   * @param location        event location
   * @param description     event description
   * @param status          event status string
   * @param isAllDay        whether the event is an all-day event
   * @param calendarName    name of the calendar this event belongs to
   * @param isEditStartFrom {@code true} if editing this and all following occurrences
   * @param isEditSeries    {@code true} if editing all events in the series
   */
  public SeriesData(String subject, String startDateTime, String endDateTime,
                    String location, String description, String status,
                    boolean isAllDay, String calendarName,
                    boolean isEditStartFrom, boolean isEditSeries) {

    super(subject, startDateTime, endDateTime, location, description,
        status, isAllDay, calendarName);

    this.isEditStartFrom = isEditStartFrom;
    this.isEditSeries = isEditSeries;
  }

  /**
   * Indicates whether the edit applies to this event and all following occurrences in the series.
   *
   * @return {@code true} if editing from this event onward
   */
  public boolean isEditStartFrom() {
    return isEditStartFrom;
  }

  /**
   * Indicates whether the edit applies to the entire series.
   *
   * @return {@code true} if editing all occurrences of the series
   */
  public boolean isEditSeries() {
    return isEditSeries;
  }
}