package controller;

import controller.command.CalendarCommandManager;
import model.calendar.CalendarManagerInterface;
import model.calendar.CalendarModelInterface;
import view.CalendarViewInterface;

/**
 * Extended controller for multiple calendar support (HW5).
 * Extends CalendarController to add multi-calendar functionality
 * while maintaining backwards compatibility with single-calendar event commands.
 */
public class MultiCalendarController extends CalendarController {
  private final CalendarManagerInterface calendarManager;
  private final CalendarCommandManager calendarCommandManager;

  /**
   * Create a CalendarController for multi-calendar application.
   *
   * @param calendarManager the calendar manager (manages multiple calendars)
   * @param view            the calendar view
   * @param input           the input source
   * @throws IllegalArgumentException if any parameter is null
   */
  public MultiCalendarController(CalendarManagerInterface calendarManager,
                                 CalendarViewInterface view,
                                 Readable input) throws IllegalArgumentException {

    super(null, view, input);

    if (calendarManager == null) {
      throw new IllegalArgumentException("Calendar manager cannot be null");
    }

    this.calendarManager = calendarManager;
    this.calendarCommandManager = new CalendarCommandManager();
  }

  @Override
  protected void processCommand(String command) throws IllegalArgumentException {
    if (calendarCommandManager.canHandle(command)) {
      calendarCommandManager.executeCommand(command, calendarManager, view);
      return;
    }

    CalendarModelInterface activeModel = calendarManager.getActiveCalendar();

    if (activeModel == null) {
      throw new IllegalArgumentException(
          "No calendar in use. Use 'use calendar --name <name>' first");
    }

    eventCommandManager.executeCommand(command, activeModel, view);
  }
}