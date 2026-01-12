package model.event.command;

import model.event.EventInterface;

/**
 * This class implements IEventCommand interface to update event subject.
 */
public class EventSubjectCommand implements EventCommandInterface {
  @Override
  public EventInterface execute(EventInterface event, String newValue) {
    return event.toBuilder().setSubject(newValue).build();
  }
}
