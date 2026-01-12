package model.event.command;

import java.time.LocalDateTime;
import model.event.EventInterface;

/**
 * This class implements IEventCommand interface to update event end date and time.
 */
public class EventEndCommand implements EventCommandInterface {
  @Override
  public EventInterface execute(EventInterface event, String newValue) {
    return event.toBuilder().setEndDateTime(LocalDateTime.parse(newValue)).build();
  }
}
