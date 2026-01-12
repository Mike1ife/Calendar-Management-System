package model.event;

import java.util.HashMap;
import java.util.Map;
import model.event.command.EventCommandInterface;
import model.event.command.EventDescriptionCommand;
import model.event.command.EventEndCommand;
import model.event.command.EventEndTimeCommand;
import model.event.command.EventLocationCommand;
import model.event.command.EventStartCommand;
import model.event.command.EventStatusCommand;
import model.event.command.EventSubjectCommand;

/**
 * This class represent the general purposes of editing Event and Series.
 */
public class EventEditor {
  private final Map<EventProperty, EventCommandInterface> eventCommands;

  /**
   * Create an EventEditor with default commands.
   */
  public EventEditor() {
    this.eventCommands = new HashMap<>();
    this.eventCommands.put(EventProperty.SUBJECT, new EventSubjectCommand());
    this.eventCommands.put(EventProperty.START, new EventStartCommand());
    this.eventCommands.put(EventProperty.END, new EventEndCommand());
    this.eventCommands.put(EventProperty.END_TIME, new EventEndTimeCommand());
    this.eventCommands.put(EventProperty.DESCRIPTION, new EventDescriptionCommand());
    this.eventCommands.put(EventProperty.LOCATION, new EventLocationCommand());
    this.eventCommands.put(EventProperty.STATUS, new EventStatusCommand());
  }

  /**
   * Edit a property of a SingleEvent.
   *
   * @param event    event to be edited.
   * @param property property to be edited in event.
   * @param newValue new value for the property
   * @return edited event
   * @throws IllegalArgumentException if invalid status value
   */
  public EventInterface editEvent(EventInterface event, EventProperty property, String newValue)
      throws IllegalArgumentException {
    return this.eventCommands.get(property).execute(event, newValue);
  }
}
