package model.calendar;

/**
 * This class represents the exception when the calendar we want to add has already existed.
 */
public class CalendarExistException extends RuntimeException {
  /**
   * Create an CalendarExistException with exception message.
   *
   * @param message exception message
   */
  public CalendarExistException(String message) {
    super(message);
  }
}
