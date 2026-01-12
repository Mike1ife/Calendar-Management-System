package controller.utility;

import java.util.HashSet;
import java.util.Set;
import model.calendar.Weekday;

/**
 * Utility class providing helper methods related to command parsing and processing.
 */
public class CommandParserUtils {

  /**
   * Parse a weekday string (e.g., "MWF") into a set of Weekday enums.
   *
   * @param weekdaysStr string containing weekday abbreviations
   * @return set of Weekday enums
   * @throws IllegalArgumentException if invalid weekday character
   */
  public static Set<Weekday> parseWeekdays(String weekdaysStr)
      throws IllegalArgumentException {
    Set<Weekday> weekdays = new HashSet<>();

    for (char c : weekdaysStr.toUpperCase().toCharArray()) {
      switch (c) {
        case 'M':
          weekdays.add(Weekday.MONDAY);
          break;
        case 'T':
          weekdays.add(Weekday.TUESDAY);
          break;
        case 'W':
          weekdays.add(Weekday.WEDNESDAY);
          break;
        case 'R':
          weekdays.add(Weekday.THURSDAY);
          break;
        case 'F':
          weekdays.add(Weekday.FRIDAY);
          break;
        case 'S':
          weekdays.add(Weekday.SATURDAY);
          break;
        case 'U':
          weekdays.add(Weekday.SUNDAY);
          break;
        default:
          throw new IllegalArgumentException("Invalid weekday character: " + c);
      }
    }

    if (weekdays.isEmpty()) {
      throw new IllegalArgumentException("At least one weekday must be specified");
    }

    return weekdays;
  }
}