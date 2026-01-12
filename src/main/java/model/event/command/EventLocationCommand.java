package model.event.command;

import model.event.EventInterface;

/**
 * This class implements IEventCommand interface to update event location.
 */
public class EventLocationCommand implements EventCommandInterface {
  @Override
  public EventInterface execute(EventInterface event, String newValue) {
    return event.toBuilder().setLocation(newValue).build();
  }
}
