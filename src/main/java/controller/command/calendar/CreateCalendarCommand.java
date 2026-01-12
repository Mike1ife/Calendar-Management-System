package controller.command.calendar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.calendar.CalendarManagerInterface;
import view.CalendarViewInterface;

/**
 * A command implementation that handles the creation of new calendars. This class
 * parses and validates the provided command to extract calendar-related information,
 * including the calendar name and timezone, and delegates the creation process to
 * the provided model. Successful execution is communicated through the view.
 */
public class CreateCalendarCommand implements CalendarCommandInterface {
  @Override
  public boolean canHandle(String command) {
    return command.toLowerCase().contains("create")
        && command.toLowerCase().contains("calendar");
  }

  @Override
  public void execute(String command, CalendarManagerInterface model, CalendarViewInterface view)
      throws IllegalArgumentException {
    Pattern namePattern = Pattern.compile(
        "--name\\s+(\\S+)",
        Pattern.CASE_INSENSITIVE
    );

    Matcher nameMatcher = namePattern.matcher(command);

    if (!nameMatcher.find()) {
      throw new IllegalArgumentException("Calendar name not found (--name required)");
    }

    String calendarName = nameMatcher.group(1);

    Pattern timezonePattern = Pattern.compile(
        "--timezone\\s+(\\S+/\\S+)",
        Pattern.CASE_INSENSITIVE
    );

    Matcher timezoneMatcher = timezonePattern.matcher(command);

    if (!timezoneMatcher.find()) {
      throw new IllegalArgumentException("Timezone not found (--timezone required)");
    }

    String timezone = timezoneMatcher.group(1);

    model.addCalendar(calendarName, timezone);
    view.displaySuccess("Calendar " + calendarName + " created");

  }
}
