package controller.command.calendar;

import model.calendar.CalendarManagerInterface;
import view.CalendarViewInterface;

/**
 * Represents a command interface for calendar-related operations. Implementations of this
 * interface are responsible for handling specific calendar commands and performing the
 * required operations by interacting with a provided model and view.
 */
public interface CalendarCommandInterface {

  /**
   * Determines if the given command is supported by the implementing class.
   *
   * @param command the command string to evaluate
   * @return true if the implementing class can handle the given command, otherwise false
   */
  boolean canHandle(String command);

  /**
   * Executes a specific command related to calendar operations by interacting with the
   * provided model and updating the view accordingly.
   *
   * @param command the command string representing the operation to execute
   * @param model   the calendar manager interface used for managing calendar-related data
   * @param view    the calendar view interface used for presenting updates to the user
   * @throws IllegalArgumentException if the command is invalid or cannot be processed
   */
  void execute(String command, CalendarManagerInterface model, CalendarViewInterface view)
      throws IllegalArgumentException;
}
