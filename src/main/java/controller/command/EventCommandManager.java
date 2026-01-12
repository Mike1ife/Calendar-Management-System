package controller.command;

import controller.command.evet.CreateEventCommand;
import controller.command.evet.EditEventCommand;
import controller.command.evet.EventCommandInterface;
import controller.command.evet.ExportCommand;
import controller.command.evet.PrintEventsCommand;
import controller.command.evet.ShowStatusCommand;
import java.util.HashMap;
import java.util.Map;
import model.calendar.CalendarModelInterface;
import view.CalendarViewInterface;

/**
 * Manager for handling command execution.
 * Similar to EventEditor pattern - uses a Map to store and retrieve handlers.
 */
public class EventCommandManager {
  private final Map<String, EventCommandInterface> eventCommands;

  /**
   * Create a CommandManager with default handlers.
   */
  public EventCommandManager() {
    this.eventCommands = new HashMap<>();
    registerDefaultCommands();
  }

  /**
   * Registers the default set of event command handlers.
   * This method initializes a predefined set of handlers, each associated
   * with a specific operation key, into the {@code eventCommands} map.
   * The default handlers support basic event-related operations:
   * - "create": Handles event creation.
   * - "edit": Handles editing of existing events.
   * - "print": Handles printing all events.
   * - "show": Displays the current status or details of an event.
   * - "export": Handles exporting events.
   * These handlers are instances of classes implementing the {@code EventCommandInterface}.
   * The method uses the {@code registerEventCommand} helper to associate each key
   * with its corresponding handler.
   */
  private void registerDefaultCommands() {
    registerEventCommand("create", new CreateEventCommand());
    registerEventCommand("edit", new EditEventCommand());
    registerEventCommand("print", new PrintEventsCommand());
    registerEventCommand("show", new ShowStatusCommand());
    registerEventCommand("export", new ExportCommand());
  }

  /**
   * Registers a new event command handler with the specified key.
   * The key uniquely identifies the handler, which is responsible for
   * executing a specific event-related command.
   *
   * @param key     the identifier for the event command handler
   * @param handler the command handler implementing {@code EventCommandInterface}
   */
  public void registerEventCommand(String key, EventCommandInterface handler) {
    this.eventCommands.put(key, handler);
  }

  /**
   * Executes the specified command by delegating it to the appropriate handler.
   * The method iterates through the registered handlers and invokes the
   * corresponding handler if it can process the provided command. If no handler
   * is found, an exception is thrown.
   *
   * @param command the command to be executed
   * @param model   the model representing the calendar data
   * @param view    the view interface for interacting with the user
   * @throws IllegalArgumentException if no handler is found for the specified command
   */
  public void executeCommand(String command, CalendarModelInterface model,
                             CalendarViewInterface view)
      throws IllegalArgumentException {

    for (EventCommandInterface handler : eventCommands.values()) {
      if (handler.canHandle(command)) {
        handler.execute(command, model, view);
        return;
      }
    }

    throw new IllegalArgumentException("Unknown command: " + command);
  }
}