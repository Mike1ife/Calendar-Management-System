package model.event.command;

import java.time.LocalTime;
import model.event.EventInterface;

/**
 * This class implements IEventCommand interface to update event end date and time.
 */
public class EventEndTimeCommand implements EventCommandInterface {
  @Override
  public EventInterface execute(EventInterface event, String newValue) {
    return event.toBuilder().setEndTime(LocalTime.parse(newValue)).build();
  }
}
