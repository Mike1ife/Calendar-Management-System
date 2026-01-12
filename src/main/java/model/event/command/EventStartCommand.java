package model.event.command;

import java.time.LocalDateTime;
import model.event.EventInterface;

/**
 * This class implements IEventCommand interface to update event start date and time.
 */
public class EventStartCommand implements EventCommandInterface {
  @Override
  public EventInterface execute(EventInterface event, String newValue) {
    return event.toBuilder().setStartDateTime(LocalDateTime.parse(newValue)).build();
  }
}
