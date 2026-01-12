package swing.view.listeners;

import java.awt.Color;
import java.time.LocalDate;
import model.event.EventReadOnlyInterface;

/**
 * Listener interface for handling event-related actions in the calendar view. Implementations
 * respond to interactions such as editing events or selecting a specific day.
 */
public interface EventActionListener {
  /**
   * Called when the user requests to edit an event.
   *
   * @param event the event to edit
   * @param color the display color associated with the event's calendar
   */
  void onEditEvent(EventReadOnlyInterface event, Color color);

  /**
   * Called when a specific day in the calendar is clicked.
   *
   * @param date the day selected by the user
   */
  void onDayClicked(LocalDate date);
}
