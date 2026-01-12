package model.event.command;

import model.event.EventInterface;
import model.event.EventStatus;

/**
 * This class implements IEventCommand interface to update event status.
 */
// TODO: test it with Event Read Only
public class EventStatusCommand implements EventCommandInterface {
  @Override
  public EventInterface execute(EventInterface event, String newValue)
      throws IllegalArgumentException {
    return event.toBuilder().setStatus(EventStatus.valueOf(newValue.toUpperCase())).build();
  }
}
