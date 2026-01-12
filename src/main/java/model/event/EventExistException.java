package model.event;

/**
 * This class represents the exception when the event we want to add has already existed.
 */
public class EventExistException extends RuntimeException {
  /**
   * Create an EventExistException with exception message.
   *
   * @param message exception message
   */
  public EventExistException(String message) {
    super(message);
  }
}
