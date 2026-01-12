package controller.command.evet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.calendar.CalendarModelInterface;
import view.CalendarViewInterface;

/**
 * Handles print events commands with a flexible input format.
 */
public class PrintEventsCommand implements EventCommandInterface {

  @Override
  public boolean canHandle(String command) {
    return command.toLowerCase().contains("print") && command.toLowerCase().contains("events");
  }

  @Override
  public void execute(String command, CalendarModelInterface model, CalendarViewInterface view)
      throws IllegalArgumentException {

    String lowerCommand = command.toLowerCase();

    if (lowerCommand.contains("from") && lowerCommand.contains("to")) {
      handlePrintEventsFrom(command, model, view);
    } else if (lowerCommand.contains("on")) {
      handlePrintEventsOn(command, model, view);
    } else {
      throw new IllegalArgumentException("Invalid print events command format");
    }
  }

  private void handlePrintEventsOn(String command, CalendarModelInterface model,
                                   CalendarViewInterface view) {

    Pattern pattern = Pattern.compile(
        "on\\s+.*?(\\d{4}-\\d{2}-\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(command);

    if (!matcher.find()) {
      throw new IllegalArgumentException("Date not found in print events on command");
    }

    String date = matcher.group(1);
    String events = model.getEventsOnDate(date);
    view.displayPrompt(events);
  }

  private void handlePrintEventsFrom(String command, CalendarModelInterface model,
                                     CalendarViewInterface view) {

    Pattern pattern = Pattern.compile(
        "from\\s+.*?(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})\\s+.*?to\\s+.*?"
            + "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(command);

    if (!matcher.find()) {
      throw new IllegalArgumentException("From/to dates not found in print events command");
    }

    String startDateTime = matcher.group(1);
    String endDateTime = matcher.group(2);

    String events = model.getEventsInRange(startDateTime, endDateTime);
    view.displayPrompt(events);
  }
}