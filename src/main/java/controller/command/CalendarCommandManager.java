package controller.command;

import controller.command.calendar.CalendarCommandInterface;
import controller.command.calendar.CopyEventCommand;
import controller.command.calendar.CreateCalendarCommand;
import controller.command.calendar.EditCalendarCommand;
import controller.command.calendar.UseCalendarCommand;
import java.util.HashMap;
import java.util.Map;
import model.calendar.CalendarManagerInterface;
import view.CalendarViewInterface;

/**
 * Command manager specifically for calendar management commands (HW5).
 * Separate from the original CommandManager to maintain clear separation.
 */
public class CalendarCommandManager {
  private final Map<String, CalendarCommandInterface> calendarCommands;

  /**
   * Constructs a new instance of the CalendarCommandManager.
   * Initializes the internal handler map and registers the default set of
   * calendar command handlers. The handlers provide support for various
   * operations such as creating a calendar, editing a calendar, using
   * a calendar, and copying an event.
   */
  public CalendarCommandManager() {
    this.calendarCommands = new HashMap<>();
    registerDefaultCommands();
  }

  /**
   * Registers the default set of handlers for calendar-related commands.
   * This method predefines handlers for the following operations:
   * - Creating a new calendar.
   * - Editing an existing calendar.
   * - Selecting or using a specific calendar.
   * - Copying an event within a calendar.
   * The method internally calls the `registerHandler` method for each specific
   * command, associating the command names ("create", "edit", "use", "copy") with
   * their corresponding implementations of the `CalendarCommandInterface`.
   * These handlers enable the command manager to process and execute appropriate
   * actions based on user input.
   */
  private void registerDefaultCommands() {
    registerCalendarCommand("create", new CreateCalendarCommand());
    registerCalendarCommand("edit", new EditCalendarCommand());
    registerCalendarCommand("use", new UseCalendarCommand());
    registerCalendarCommand("copy", new CopyEventCommand());
  }

  /**
   * Registers a new command handler with the specified key.
   * Associates the provided handler with the key, enabling processing of
   * calendar-related commands associated with the given key.
   *
   * @param key     the identifier for the calendar command handler
   * @param handler the command handler implementing {@code CalendarCommandInterface}
   */
  public void registerCalendarCommand(String key, CalendarCommandInterface handler) {
    this.calendarCommands.put(key, handler);
  }

  /**
   * Checks if any registered command can handle the given command string.
   *
   * @param command the command string to check
   * @return true if at least one command handler can process this command
   */
  public boolean canHandle(String command) {
    for (CalendarCommandInterface handler : calendarCommands.values()) {
      if (handler.canHandle(command)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Executes a calendar-related command by delegating it to the appropriate handler.
   * The method iterates through registered command handlers and executes the first one
   * that can handle the provided command. If no suitable handler is found, an exception
   * is thrown to indicate an unrecognized command.
   *
   * @param command         the command to execute, represented as a string
   * @param calendarManager the calendar manager instance for managing calendar operations
   * @param view            the view interface for interacting with the calendar view
   * @throws IllegalArgumentException if the provided command is not recognized
   */
  public void executeCommand(String command, CalendarManagerInterface calendarManager,
                             CalendarViewInterface view) throws IllegalArgumentException {

    for (CalendarCommandInterface handler : calendarCommands.values()) {
      if (handler.canHandle(command)) {
        handler.execute(command, calendarManager, view);
        return;
      }
    }
    throw new IllegalArgumentException("Unknown command: " + command);
  }
}