package model.event.command;

import model.event.EventInterface;

/**
 * This interface represent command to update an event.
 */
public interface EventCommandInterface {
  /**
   * Create a new Event with new value.
   *
   * @param event    event to be updated
   * @param newValue new value for event attribute
   * @return updated event
   */
  EventInterface execute(EventInterface event, String newValue);
}
