package controller.command.evet;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.calendar.CalendarModelInterface;
import view.CalendarViewInterface;

/**
 * Handles commands for editing events with a flexible input format.
 * Allows extra words in various positions throughout the command.
 */
public class EditEventCommand implements EventCommandInterface {

  @Override
  public boolean canHandle(String command) {
    return command.toLowerCase().contains("edit")
        && (command.toLowerCase().contains("event")
        || command.toLowerCase().contains("events")
        || command.toLowerCase().contains("series"));
  }

  @Override
  public void execute(String command, CalendarModelInterface model, CalendarViewInterface view)
      throws IllegalArgumentException {

    String lowerCommand = command.toLowerCase();

    if (lowerCommand.contains("edit") && lowerCommand.contains("series")) {
      handleEditSeries(command, model, view);
    } else if (lowerCommand.contains("edit") && lowerCommand.contains("events")) {
      handleEditEventsFromDate(command, model, view);
    } else if (lowerCommand.contains("edit") && lowerCommand.contains("event")) {
      handleEditSingleEvent(command, model, view);
    }
  }

  private String extractSubjectAfterProperty(String command, String property) {
    Pattern quotedPattern = Pattern.compile(
        property + "\\s+.*?\"([^\"]+)\"",
        Pattern.CASE_INSENSITIVE);
    Matcher quotedMatcher = quotedPattern.matcher(command);

    if (quotedMatcher.find()) {
      return quotedMatcher.group(1);
    }

    Pattern singleWordPattern = Pattern.compile(
        property + "\\s+(?!from|with)(\\S+)",
        Pattern.CASE_INSENSITIVE);
    Matcher singleWordMatcher = singleWordPattern.matcher(command);

    if (singleWordMatcher.find()) {
      return singleWordMatcher.group(1);
    }

    throw new IllegalArgumentException("Event subject not found");
  }

  private void handleEditSingleEvent(String command, CalendarModelInterface model,
                                     CalendarViewInterface view) {

    Pattern propertyPattern = Pattern.compile(
        "(Subject|StartDate|StartTime|EndDate|EndTime|Location|Description|IsPublic)",
        Pattern.CASE_INSENSITIVE);
    Matcher propertyMatcher = propertyPattern.matcher(command);

    if (!propertyMatcher.find()) {
      throw new IllegalArgumentException("Property not found in edit command");
    }
    final String propertyStr = propertyMatcher.group(1);

    String subject = extractSubjectAfterProperty(command, propertyStr);

    Pattern timePattern = Pattern.compile(
        "from\\s+.*?(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})\\s+.*?to\\s+.*?"
            + "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher timeMatcher = timePattern.matcher(command);

    if (!timeMatcher.find()) {
      throw new IllegalArgumentException("From/to times not found");
    }
    String startDateTime = timeMatcher.group(1);
    String endDateTime = timeMatcher.group(2);

    Pattern withPattern = Pattern.compile(
        "with\\s+(.+?)\\s*$",
        Pattern.CASE_INSENSITIVE);
    Matcher withMatcher = withPattern.matcher(command);

    if (!withMatcher.find()) {
      throw new IllegalArgumentException("New value (with ...) not found");
    }
    String newValue = withMatcher.group(1).trim();

    model.editSingleEvent(List.of(subject, propertyStr, startDateTime, endDateTime, newValue));
    view.displaySuccess("Event '" + subject + "' updated successfully");
  }

  private void handleEditEventsFromDate(String command, CalendarModelInterface model,
                                        CalendarViewInterface view) {

    Pattern propertyPattern = Pattern.compile(
        "(Subject|StartDate|StartTime|EndDate|EndTime|Location|Description|IsPublic)",
        Pattern.CASE_INSENSITIVE);
    Matcher propertyMatcher = propertyPattern.matcher(command);

    if (!propertyMatcher.find()) {
      throw new IllegalArgumentException("Property not found in edit command");
    }
    final String propertyStr = propertyMatcher.group(1);

    String subject = extractSubjectAfterProperty(command, propertyStr);

    Pattern timePattern = Pattern.compile(
        "from\\s+.*?(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher timeMatcher = timePattern.matcher(command);

    if (!timeMatcher.find()) {
      throw new IllegalArgumentException("From time not found");
    }
    String startDateTime = timeMatcher.group(1);

    Pattern withPattern = Pattern.compile(
        "with\\s+(.+?)\\s*$",
        Pattern.CASE_INSENSITIVE);
    Matcher withMatcher = withPattern.matcher(command);

    if (!withMatcher.find()) {
      throw new IllegalArgumentException("New value (with ...) not found");
    }
    String newValue = withMatcher.group(1).trim();

    model.editEventStartFrom(subject, propertyStr, startDateTime, newValue);
    view.displaySuccess("Events updated successfully");
  }

  private void handleEditSeries(String command, CalendarModelInterface model,
                                CalendarViewInterface view) {

    Pattern propertyPattern = Pattern.compile(
        "(Subject|StartDate|StartTime|EndDate|EndTime|Location|Description|IsPublic)",
        Pattern.CASE_INSENSITIVE);
    Matcher propertyMatcher = propertyPattern.matcher(command);

    if (!propertyMatcher.find()) {
      throw new IllegalArgumentException("Property not found in edit command");
    }
    final String propertyStr = propertyMatcher.group(1);

    String subject = extractSubjectAfterProperty(command, propertyStr);

    Pattern timePattern = Pattern.compile(
        "from\\s+.*?(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher timeMatcher = timePattern.matcher(command);

    if (!timeMatcher.find()) {
      throw new IllegalArgumentException("From time not found");
    }
    String startDateTime = timeMatcher.group(1);

    Pattern withPattern = Pattern.compile(
        "with\\s+(.+?)\\s*$",
        Pattern.CASE_INSENSITIVE);
    Matcher withMatcher = withPattern.matcher(command);

    if (!withMatcher.find()) {
      throw new IllegalArgumentException("New value (with ...) not found");
    }
    String newValue = withMatcher.group(1).trim();

    model.editSeriesStartFrom(subject, propertyStr, startDateTime, newValue);
    view.displaySuccess("Event series updated successfully");
  }
}