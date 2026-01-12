package controller.command.calendar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.calendar.CalendarManagerInterface;
import view.CalendarViewInterface;

/**
 * A command to activate a specific calendar by name. This class implements the
 * CalendarCommandInterface and is responsible for handling "use calendar" commands.
 * It interacts with the provided model to activate the specified calendar and expects
 * the command to include a calendar name using the "--name" flag.
 */
public class UseCalendarCommand implements CalendarCommandInterface {
  @Override
  public boolean canHandle(String command) {
    return command.toLowerCase().contains("use")
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

    model.activateCalendar(calendarName);
    view.displaySuccess("Calendar " + calendarName + " activated");

  }
}
