package swing.view.listeners;

/**
 * Listener interface for handling calendar selection actions. Implementations respond to user
 * interactions such as toggling a calendar's visibility or invoking edit actions.
 */
public interface CalendarSelectListener {
  /**
   * Invoked when a calendar's visibility checkbox is toggled.
   */
  void onToggle();

  /**
   * Invoked when the user chooses to edit a specific calendar.
   *
   * @param calendarName name of the calendar to edit
   */
  void onEdit(String calendarName);
}
