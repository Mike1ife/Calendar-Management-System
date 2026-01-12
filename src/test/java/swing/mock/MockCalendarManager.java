package swing.mock;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Set;
import model.calendar.CalendarExistException;
import model.calendar.CalendarManagerInterface;
import model.calendar.CalendarModelInterface;
import model.calendar.CalendarNotFoundException;
import model.event.EventExistException;
import model.event.EventNotFoundException;

/**
 * A mock implementation of {@link CalendarManagerInterface} used for controller testing.
 * Instead of maintaining real calendars or events, this class records each invoked method
 * and its arguments into the provided log. Tests can then verify that the controller issues
 * the correct sequence of calls to the calendar manager.
 */
public class MockCalendarManager implements CalendarManagerInterface {
  private final StringBuilder log;

  /**
   * Constructs a MockCalendarManager that appends all method calls to the given log.
   *
   * @param log the StringBuilder used to record interactions with this mock manager
   */
  public MockCalendarManager(StringBuilder log) {
    this.log = log;
  }

  @Override
  public void addCalendar(String name, String timeZone)
      throws CalendarExistException, DateTimeException {
    log.append("addCalendar: ").append(name).append("\n");
  }

  @Override
  public void editCalendar(String name, String property, String newValue)
      throws CalendarNotFoundException, IllegalArgumentException, CalendarExistException,
      DateTimeException {
    log.append("editCalendar: ").append(name).append(" ").append(property).append(" ")
        .append(newValue).append("\n");
  }

  @Override
  public void activateCalendar(String name) throws CalendarNotFoundException {
    log.append("activateCalendar: ").append(name).append("\n");
  }

  @Override
  public CalendarModelInterface getActiveCalendar() throws CalendarNotFoundException {
    log.append("getActiveCalendar\n");
    return new MockCalendarModel(log);
  }

  @Override
  public void copyEvent(String eventName, String startDateTime, String targetCalendarName,
                        String targetStartDateTime)
      throws NullPointerException, CalendarNotFoundException, UnsupportedOperationException,
      DateTimeParseException, EventNotFoundException, EventExistException {
    log.append("copyEvent: ").append(eventName).append(" ").append(startDateTime).append(" ")
        .append(targetCalendarName).append(" ").append(targetStartDateTime).append("\n");
  }

  @Override
  public void copyEventsOnDate(String date, String targetCalendarName, String targetDate)
      throws NullPointerException, CalendarNotFoundException, UnsupportedOperationException,
      DateTimeParseException, EventNotFoundException, EventExistException {
    log.append("copyEventsOnDate: ").append(date).append(" ").append(targetCalendarName).append(" ")
        .append(targetDate).append("\n");
  }

  @Override
  public void copyEventsBetween(String intervalStart, String intervalEnd, String targetCalendarName,
                                String targetIntervalStart)
      throws NullPointerException, CalendarNotFoundException, UnsupportedOperationException,
      DateTimeParseException, EventNotFoundException, EventExistException {
    log.append("copyEventsBetween: ").append(intervalStart).append(" ").append(intervalEnd)
        .append(" ")
        .append(targetCalendarName).append(" ").append(targetIntervalStart).append("\n");
  }

  @Override
  public Set<String> getAllCalendarNames() throws CalendarNotFoundException {
    log.append("getAllCalendarNames\n");
    return Set.of();
  }

  @Override
  public ZoneId getCalendarTimezone(String calendarName) throws CalendarNotFoundException {
    log.append("getCalendarTimezone: ").append(calendarName).append("\n");
    return null;
  }
}
