package model.event.command;

import model.event.EventInterface;

/**
 * This class implements IEventCommand interface to update event description.
 */
public class EventDescriptionCommand implements EventCommandInterface {
  @Override
  public EventInterface execute(EventInterface event, String newValue) {
    return event.toBuilder().setDescription(newValue).build();
  }
}
