package swing.view.dialogs.event.data;

import controller.utility.CommandParserUtils;
import java.util.Set;
import model.calendar.Weekday;

/**
 * Data holder for event creation. Extends {@link BaseData} and adds fields for repeat options such
 * as weekdays, occurrence count, and end date.
 */
public class EventData extends BaseData {
  private final boolean isRepeat;
  private final String weekdayString;
  private final Integer occurrences;
  private final String untilDate;

  /**
   * Create an EventData object containing all event fields, including repeat options.
   *
   * @param subject       event subject
   * @param startDateTime start date and time in ISO format
   * @param endDateTime   end date and time in ISO format
   * @param location      event location
   * @param description   event description
   * @param status        event visibility or status
   * @param isAllDay      whether the event spans a full day
   * @param isRepeat      whether the event repeats
   * @param weekdays      string representing selected weekdays for repetition
   * @param occurrences   number of occurrences if repetition is occurrence-based
   * @param untilDate     end date of repetition if date-based
   * @param calendarName  calendar name
   */
  public EventData(String subject, String startDateTime, String endDateTime,
                   String location, String description, String status,
                   boolean isAllDay, boolean isRepeat, String weekdays,
                   Integer occurrences, String untilDate, String calendarName) {

    super(subject, startDateTime, endDateTime, location, description,
        status, isAllDay, calendarName);

    this.isRepeat = isRepeat;
    this.weekdayString = weekdays;
    this.occurrences = occurrences;
    this.untilDate = untilDate;
  }

  /**
   * Whether the event is set to repeat.
   *
   * @return true if repeat is enabled
   */
  public boolean isRepeat() {
    return isRepeat;
  }

  /**
   * Parse and return the selected repeat weekdays.
   *
   * @return set of repeat weekdays
   */
  public Set<Weekday> getWeekdays() {
    return CommandParserUtils.parseWeekdays(weekdayString);
  }

  /**
   * Get the number of occurrences for the repeating event.
   *
   * @return occurrence count or null
   */
  public Integer getOccurrences() {
    return occurrences;
  }

  /**
   * Whether the user specified a fixed number of occurrences.
   *
   * @return true if occurrences is not null
   */
  public boolean hasOccurrences() {
    return occurrences != null;
  }

  /**
   * Get the end date of the repeat rule.
   *
   * @return until date string or null
   */
  public String getUntilDate() {
    return untilDate;
  }
}
