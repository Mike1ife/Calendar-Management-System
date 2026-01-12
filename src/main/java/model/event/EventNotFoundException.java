package model.event;

/**
 * This class represents the exception when the event we want to find doesn't exist.
 */
public class EventNotFoundException extends RuntimeException {
  /**
   * Create an EventNotFoundException with exception message.
   *
   * @param message exception message
   */
  public EventNotFoundException(String message) {
    super(message);
  }
}
