package controller.command.evet;

import static controller.utility.CommandParserUtils.parseWeekdays;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.calendar.CalendarModelInterface;
import model.calendar.Weekday;
import view.CalendarViewInterface;

/**
 * The CreateEventCommandHandler class is responsible for handling commands
 * to create events in the calendar application.
 *
 * <p>This handler now supports flexible input formats, allowing extra words
 * in various positions throughout the command.
 */
public class CreateEventCommand implements EventCommandInterface {
  @Override
  public boolean canHandle(String command) {
    return command.toLowerCase().contains("create")
        && command.toLowerCase().contains("event");
  }

  @Override
  public void execute(String command, CalendarModelInterface model, CalendarViewInterface view)
      throws IllegalArgumentException {

    String subject = extractSubject(command);

    boolean isSeries = command.toLowerCase().contains("repeats");

    if (isSeries) {
      handleSeriesEvent(command, subject, model, view);
    } else {
      handleSingleEvent(command, subject, model, view);
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
        "event\\s+(?!on|from)(\\S+)",
        Pattern.CASE_INSENSITIVE);
    Matcher singleWordMatcher = singleWordPattern.matcher(command);

    if (singleWordMatcher.find()) {
      return singleWordMatcher.group(1);
    }

    throw new IllegalArgumentException("Event subject not found");
  }

  private void handleSingleEvent(String command, String subject,
                                 CalendarModelInterface model, CalendarViewInterface view) {
    Pattern timePattern = Pattern.compile(
        "from\\s+.*?(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})\\s+.*?to\\s+.*?"
            + "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher timeMatcher = timePattern.matcher(command);

    if (timeMatcher.find()) {
      String startDateTime = timeMatcher.group(1);
      String endDateTime = timeMatcher.group(2);

      model.createSingleEventWithTime(subject, startDateTime, endDateTime);
      view.displaySuccess("Event '" + subject + "' created successfully");
      return;
    }

    Pattern datePattern = Pattern.compile(
        "on\\s+.*?(\\d{4}-\\d{2}-\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher dateMatcher = datePattern.matcher(command);

    if (dateMatcher.find()) {
      String date = dateMatcher.group(1);

      model.createAllDaySingleEvent(subject, date);
      view.displaySuccess("Event '" + subject + "' created successfully");
      return;
    }

    throw new IllegalArgumentException("Invalid create event command format");
  }

  private void handleSeriesEvent(String command, String subject,
                                 CalendarModelInterface model, CalendarViewInterface view) {

    Pattern weekdayPattern = Pattern.compile(
        "repeats\\s+(?:.*?\\s+)?([MTWRFSU]+)\\s+(?:for|until)",
        Pattern.CASE_INSENSITIVE);
    Matcher weekdayMatcher = weekdayPattern.matcher(command);

    if (!weekdayMatcher.find()) {
      throw new IllegalArgumentException("Weekdays not found in repeats command");
    }
    String weekdaysStr = weekdayMatcher.group(1);
    Set<Weekday> weekdays = parseWeekdays(weekdaysStr);

    Pattern occurrencePattern = Pattern.compile(
        "for\\s+.*?(\\d+)\\s+times",
        Pattern.CASE_INSENSITIVE);
    Matcher occurrenceMatcher = occurrencePattern.matcher(command);
    boolean hasOccurrences = occurrenceMatcher.find();

    Pattern untilPattern = Pattern.compile(
        "until\\s+.*?(\\d{4}-\\d{2}-\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher untilMatcher = untilPattern.matcher(command);
    boolean hasUntil = untilMatcher.find();

    Pattern timePattern = Pattern.compile(
        "from\\s+.*?(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})\\s+.*?to\\s+.*?(\\d{4}-\\d{2}-\\d{2}"
            + "T\\d{2}:\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher timeMatcher = timePattern.matcher(command);
    boolean hasTime = timeMatcher.find();

    Pattern datePattern = Pattern.compile(
        "on\\s+.*?(\\d{4}-\\d{2}-\\d{2})",
        Pattern.CASE_INSENSITIVE);
    Matcher dateMatcher = datePattern.matcher(command);
    boolean hasDate = dateMatcher.find();

    if (hasTime) {
      if (hasOccurrences) {
        String startDateTime = timeMatcher.group(1);
        String endDateTime = timeMatcher.group(2);
        int occurrences = Integer.parseInt(occurrenceMatcher.group(1));

        model.createSeriesEventWithOccurrence(subject, startDateTime, endDateTime,
            weekdays, occurrences);
        view.displaySuccess("Event series '" + subject + "' created with "
            + occurrences + " occurrences");
        return;
      } else if (hasUntil) {
        String startDateTime = timeMatcher.group(1);
        String endDateTime = timeMatcher.group(2);
        String untilDate = untilMatcher.group(1);

        model.createSeriesEventWithEndDate(subject, startDateTime, endDateTime,
            weekdays, untilDate);
        view.displaySuccess("Event series '" + subject + "' created until " + untilDate);
        return;
      }
    }

    if (hasDate) {
      if (hasOccurrences) {
        String date = dateMatcher.group(1);
        int occurrences = Integer.parseInt(occurrenceMatcher.group(1));

        model.createAllDaySeriesEventWithOccurrence(subject, date, weekdays, occurrences);
        view.displaySuccess("Event series '" + subject + "' created with "
            + occurrences + " occurrences");
        return;
      } else if (hasUntil) {
        String date = dateMatcher.group(1);
        String untilDate = untilMatcher.group(1);

        model.createAllDaySeriesEventWithEndDate(subject, date, weekdays, untilDate);
        view.displaySuccess("Event series '" + subject + "' created until " + untilDate);
        return;
      }
    }

    throw new IllegalArgumentException("Invalid create series event command format");
  }
}