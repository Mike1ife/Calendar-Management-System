package controller.command.calendar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.calendar.CalendarManagerInterface;
import view.CalendarViewInterface;

/**
 * Handles commands for copying events between calendars.
 * Supports copying single events, events on a specific day, and events in a date range.
 * Allows extra words in various positions throughout the command.
 */
public class CopyEventCommand implements CalendarCommandInterface {

  @Override
  public boolean canHandle(String command) {
    String lowerCommand = command.toLowerCase();
    return lowerCommand.contains("copy")
        && (lowerCommand.contains("event") || lowerCommand.contains("events"));
  }

  @Override
  public void execute(String command, CalendarManagerInterface model, CalendarViewInterface view)
      throws IllegalArgumentException {

    String lowerCommand = command.toLowerCase();

    if (lowerCommand.contains("copy") && lowerCommand.contains("event")
        && !lowerCommand.contains("events")) {
      handleCopySingleEvent(command, model, view);
    } else if (lowerCommand.contains("between") && lowerCommand.contains("and")) {
      handleCopyEventsBetween(command, model, view);
    } else if (lowerCommand.contains("on")) {
      handleCopyEventsOn(command, model, view);
    } else {
      throw new IllegalArgumentException("Invalid copy command format");
    }
  }

  private String extractSubject(String command) {
    Pattern quotedPattern = Pattern.compile(
        "event\\s+.*?\"([^\"]+)\"",
        Pattern.CASE_INSENSITIVE);
    Matcher quotedMatcher = quotedPattern.matcher(command);

    if (quotedMatcher.find()) {
      return quotedMatcher.group(1);
    }

    Pattern singleWordPattern = Pattern.compile(
        "event\\s+(?!on)(\\S+)",
        Pattern.CASE_INSENSITIVE);
    Matcher singleWordMatcher = singleWordPattern.matcher(command);

    if (singleWordMatcher.find()) {
      return singleWordMatcher.group(1);
    }

    throw new IllegalArgumentException("Event subject not found");
  }

  private void handleCopySingleEvent(String command, CalendarManagerInterface model,
                                     CalendarViewInterface view) {

    final String eventSubject = extractSubject(command);

    Pattern sourceDatePattern = Pattern.compile(
        "on\\s+(?!--)(\\S+\\s+)?(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher sourceDateMatcher = sourceDatePattern.matcher(command);
    if (!sourceDateMatcher.find()) {
      throw new IllegalArgumentException("Source date/time not found after 'on'");
    }

    Pattern targetPattern = Pattern.compile(
        "--target\\s+(\\S+)",
        Pattern.CASE_INSENSITIVE);
    Matcher targetMatcher = targetPattern.matcher(command);
    if (!targetMatcher.find()) {
      throw new IllegalArgumentException("Target calendar not found (--target required)");
    }

    Pattern targetDatePattern = Pattern.compile(
        "to\\s+(?!--)(\\S+\\s+)?(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher targetDateMatcher = targetDatePattern.matcher(command);
    if (!targetDateMatcher.find()) {
      throw new IllegalArgumentException("Target date/time not found after 'to'");
    }

    String sourceDateTime = sourceDateMatcher.group(2);
    String targetCalendar = targetMatcher.group(1);
    String targetDateTime = targetDateMatcher.group(2);

    model.copyEvent(eventSubject, sourceDateTime, targetCalendar, targetDateTime);
    view.displaySuccess("Event '" + eventSubject + "' copied to " + targetCalendar);
  }

  private void handleCopyEventsOn(String command, CalendarManagerInterface model,
                                  CalendarViewInterface view) {

    Pattern sourceDatePattern = Pattern.compile(
        "on\\s+(?!--)(\\S+\\s+)?(\\d{4}-\\d{2}-\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher sourceDateMatcher = sourceDatePattern.matcher(command);
    if (!sourceDateMatcher.find()) {
      throw new IllegalArgumentException("Source date not found after 'on'");
    }

    Pattern targetPattern = Pattern.compile(
        "--target\\s+(\\S+)",
        Pattern.CASE_INSENSITIVE);
    Matcher targetMatcher = targetPattern.matcher(command);
    if (!targetMatcher.find()) {
      throw new IllegalArgumentException("Target calendar not found (--target required)");
    }

    Pattern targetDatePattern = Pattern.compile(
        "to\\s+(?!--)(\\S+\\s+)?(\\d{4}-\\d{2}-\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher targetDateMatcher = targetDatePattern.matcher(command);
    if (!targetDateMatcher.find()) {
      throw new IllegalArgumentException("Target date not found after 'to'");
    }

    String sourceDate = sourceDateMatcher.group(2);
    String targetCalendar = targetMatcher.group(1);
    String targetDate = targetDateMatcher.group(2);

    model.copyEventsOnDate(sourceDate, targetCalendar, targetDate);
    view.displaySuccess("Events on " + sourceDate + " copied to " + targetCalendar);
  }

  private void handleCopyEventsBetween(String command, CalendarManagerInterface model,
                                       CalendarViewInterface view) {

    Pattern startDatePattern = Pattern.compile(
        "between\\s+(?!and)(\\S+\\s+)?(\\d{4}-\\d{2}-\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher startDateMatcher = startDatePattern.matcher(command);
    if (!startDateMatcher.find()) {
      throw new IllegalArgumentException("Start date not found after 'between'");
    }

    Pattern endDatePattern = Pattern.compile(
        "and\\s+(?!--)(\\S+\\s+)?(\\d{4}-\\d{2}-\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher endDateMatcher = endDatePattern.matcher(command);
    if (!endDateMatcher.find()) {
      throw new IllegalArgumentException("End date not found after 'and'");
    }

    Pattern targetPattern = Pattern.compile(
        "--target\\s+(\\S+)",
        Pattern.CASE_INSENSITIVE);
    Matcher targetMatcher = targetPattern.matcher(command);
    if (!targetMatcher.find()) {
      throw new IllegalArgumentException("Target calendar not found (--target required)");
    }

    Pattern targetDatePattern = Pattern.compile(
        "to\\s+(?!--)(\\S+\\s+)?(\\d{4}-\\d{2}-\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher targetDateMatcher = targetDatePattern.matcher(command);
    if (!targetDateMatcher.find()) {
      throw new IllegalArgumentException("Target date not found after 'to'");
    }

    String startDate = startDateMatcher.group(2);
    String endDate = endDateMatcher.group(2);
    String targetCalendar = targetMatcher.group(1);
    String targetDate = targetDateMatcher.group(2);

    model.copyEventsBetween(startDate, endDate, targetCalendar, targetDate);
    view.displaySuccess("Events from " + startDate + " to " + endDate
        + " copied to " + targetCalendar);
  }
}