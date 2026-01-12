package swing.view.listeners;

import java.time.LocalDate;
import model.event.EventReadOnlyInterface;

/**
 * Listener interface for handling high-level user actions in the calendar view. Implementations act
 * as controllers that respond to navigation, calendar management, and event-related interactions.
 */
public interface ViewListener {
  /**
   * Invoked when the user selects the "Today" action.
   */
  void handleToday();

  /**
   * Invoked when the user navigates to the previous month.
   */
  void handlePreviousMonth();

  /**
   * Invoked when the user navigates to the next month.
   */
  void handleNextMonth();

  /**
   * Called when the user requests to create a new calendar.
   */
  void handleCreateCalendar();

  /**
   * Called when the user requests to create a new event.
   */
  void handleCreateEvent();

  /**
   * Called when the user chooses to edit an existing calendar.
   *
   * @param calendarName name of the calendar to edit
   */
  void handleEditCalendar(String calendarName);

  /**
   * Called when the user chooses to edit an event.
   *
   * @param calendarName name of the calendar that contains the event
   * @param event        the event to be edited
   */
  void handleEditEvent(String calendarName, EventReadOnlyInterface event);

  /**
   * Called when a specific day in the calendar is clicked.
   *
   * @param date the day selected by the user
   */
  void handleDayClick(LocalDate date);
}
