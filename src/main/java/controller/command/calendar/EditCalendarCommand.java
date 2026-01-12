package controller.command.calendar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.calendar.CalendarManagerInterface;
import view.CalendarViewInterface;

/**
 * Represents a command for editing calendar attributes such as name or timezone.
 * This command is validated and executed by interacting with the provided calendar model
 * and view interfaces. It checks if the command specifies the calendar name, the
 * property to be updated, and the new value before performing the operation.
 */
public class EditCalendarCommand implements CalendarCommandInterface {
  @Override
  public boolean canHandle(String command) {
    return command.toLowerCase().contains("edit")
        && command.toLowerCase().contains("calendar");
  }

  @Override
  public void execute(String command, CalendarManagerInterface model, CalendarViewInterface view)
      throws IllegalArgumentException {
    Pattern namePattern = Pattern.compile(
        "--name\\s+(\\S+)",
        Pattern.CASE_INSENSITIVE);
    Matcher nameMatcher = namePattern.matcher(command);

    if (!nameMatcher.find()) {
      throw new IllegalArgumentException("Calendar name not found (--name required)");
    }
    String calendarName = nameMatcher.group(1);

    Pattern propertyPattern = Pattern.compile(
        "--property\\s+.*?(name|timezone)",
        Pattern.CASE_INSENSITIVE);
    Matcher propertyMatcher = propertyPattern.matcher(command);

    if (!propertyMatcher.find()) {
      throw new IllegalArgumentException("Property not found (--property required)");
    }
    String property = propertyMatcher.group(1).toLowerCase();

    Pattern valuePattern = Pattern.compile(
        "--property\\s+.*?(?:name|timezone)\\s+.*?(\\S+)(?:\\s|$)",
        Pattern.CASE_INSENSITIVE);
    Matcher valueMatcher = valuePattern.matcher(command);

    if (!valueMatcher.find()) {
      throw new IllegalArgumentException("New value not found");
    }
    String newValue = valueMatcher.group(1);

    model.editCalendar(calendarName, property, newValue);
    view.displaySuccess("Calendar " + calendarName + " updated successfully");
  }
}