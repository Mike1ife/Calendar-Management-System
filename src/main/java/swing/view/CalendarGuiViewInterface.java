package swing.view;

import java.awt.Color;
import java.time.YearMonth;
import java.util.List;
import model.event.EventReadOnlyInterface;
import swing.view.listeners.CalendarSelectListener;
import swing.view.listeners.EventActionListener;
import swing.view.listeners.ViewListener;

/**
 * This interface defines the required operations for a graphical calendar view. Implementations are
 * responsible for rendering calendars, displaying events, and handling user interactions such as
 * navigation, calendar selection, and event actions. The view acts as the visual layer and
 * communicates user actions back to the controller through registered listeners.
 */
public interface CalendarGuiViewInterface {
  /**
   * Displays the GUI and makes it visible to the user.
   */
  void display();

  /**
   * Registers a ViewListener to handle user-triggered actions such as navigation, event creation,
   * or calendar editing.
   *
   * @param viewListener the listener responsible for handling view-level actions
   */
  void addViewListener(ViewListener viewListener);

  /**
   * Registers a listener that responds to calendar selection actions
   * such as toggling a calendar's visibility or requesting its modification.
   *
   * @param listener the CalendarSelectListener to register
   */
  void setCalendarSelectListener(CalendarSelectListener listener);

  /**
   * Registers a listener that responds to event-related actions such as
   * clicking a day or editing a specific event.
   *
   * @param listener the EventActionListener to register
   */
  void setEventActionListener(EventActionListener listener);

  /**
   * Renders a calendar name and its associated color into the view.
   *
   * @param calendarName the name of the calendar to render
   * @param color        the color used to visually represent the calendar
   */
  void renderCalendar(String calendarName, Color color);

  /**
   * Renders a single event in the view using the specified display color.
   *
   * @param event the read-only event to be displayed
   * @param color the color associated with the event's calendar
   */
  void renderEvent(EventReadOnlyInterface event, Color color);

  /**
   * Retrieves the list of currently selected calendar names.
   *
   * @return list of selected calendar names
   */
  List<String> getSelectedCalendars();

  /**
   * Marks the given list of calendar names as selected in the view.
   *
   * @param calendarNames list of calendars to select
   */
  void setSelectedCalendars(List<String> calendarNames);

  /**
   * Navigates the view to the current day.
   */
  void goToToday();

  /**
   * Navigates the view to the previous month.
   */
  void goToPreviousMonth();

  /**
   * Navigates the view to the next month.
   */
  void goToNextMonth();

  /**
   * Adds a new calendar into the view with its associated color.
   *
   * @param calendarName the name of the new calendar
   * @param color        the color used to visually represent it
   */
  void addCalendar(String calendarName, Color color);

  /**
   * Updates the displayed name of a calendar.
   *
   * @param originalName the current name of the calendar
   * @param newName      the updated name to display
   */
  void updateCalendarName(String originalName, String newName);

  /**
   * Clears all displayed calendars from the view.
   */
  void clearAllCalenders();

  /**
   * Clears all rendered events from the view.
   */
  void clearAllEvents();

  /**
   * Retrieves the month currently shown in the view.
   *
   * @return the active YearMonth in the calendar display
   */
  YearMonth getCurrentMonth();
}
