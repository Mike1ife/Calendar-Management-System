package model.calendar;

/**
 * This class represents the exception when the calendar we want to find doesn't exist.
 */
public class CalendarNotFoundException extends RuntimeException {
  /**
   * Create an CalendarNotFoundException with exception message.
   *
   * @param message exception message
   */
  public CalendarNotFoundException(String message) {
    super(message);
  }
}
