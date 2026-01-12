package controller.command.evet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.calendar.CalendarModelInterface;
import model.calendar.CalendarStatus;
import view.CalendarViewInterface;

/**
 * Handles show status commands with a flexible input format.
 */
public class ShowStatusCommand implements EventCommandInterface {

  @Override
  public boolean canHandle(String command) {
    return command.toLowerCase().contains("show") && command.toLowerCase().contains("status");
  }

  @Override
  public void execute(String command, CalendarModelInterface model, CalendarViewInterface view)
      throws IllegalArgumentException {

    Pattern pattern = Pattern.compile(
        "on\\s+.*?(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(command);

    if (!matcher.find()) {
      throw new IllegalArgumentException("DateTime not found in show status command");
    }

    String startDateTime = matcher.group(1);

    CalendarStatus isBusy = model.isBusy(startDateTime);
    view.displayStatus(isBusy);
  }
}