package controller;

import controller.command.EventCommandManager;
import java.util.Scanner;
import model.calendar.CalendarModelInterface;
import view.CalendarViewInterface;

/**
 * Controller for the calendar application.
 * Uses CommandManager to delegate command execution.
 */
public class CalendarController implements CalendarControllerInterface {
  protected final CalendarModelInterface model;
  protected final CalendarViewInterface view;
  protected final Readable input;
  protected final EventCommandManager eventCommandManager;

  /**
   * Constructs a CalendarController object that connects the model and the view
   * while managing input commands for the calendar application.
   *
   * @param model the calendar model interface used for handling calendar data
   * @param view  the calendar view interface used for display and output interactions
   * @param input the readable input stream used for reading commands
   * @throws IllegalArgumentException if any of the parameters (model, view, or input) are null
   */
  public CalendarController(CalendarModelInterface model, CalendarViewInterface view,
                            Readable input)
      throws IllegalArgumentException {
    if (view == null || input == null) {
      throw new IllegalArgumentException("Model, view and input cannot be null");
    }
    this.model = model;
    this.view = view;
    this.input = input;
    this.eventCommandManager = new EventCommandManager();
  }

  @Override
  public void go() {
    Scanner scanner = new Scanner(input);

    while (true) {
      view.displayPrompt("Enter command: ");

      if (!scanner.hasNextLine()) {
        break;
      }

      String command = scanner.nextLine().trim();

      if (command.isEmpty()) {
        continue;
      }

      if (command.equalsIgnoreCase("exit")) {
        view.displayExit();
        break;
      }

      try {
        processCommand(command);
      } catch (IllegalArgumentException e) {
        view.displayError(e.getMessage());
      } catch (Exception e) {
        view.displayError("Unexpected error: " + e.getMessage());
      }
    }
  }

  /**
   * Processes the specified command by delegating it to the {@code EventCommandManager}
   * for execution. The method uses the provided calendar model and view to handle
   * the command's behavior and output.
   *
   * @param command the command to process; must be a valid instruction recognized by
   *                the {@code EventCommandManager}
   * @throws IllegalArgumentException if the command is invalid or no handler
   *                                  is available for the command
   */
  protected void processCommand(String command) throws IllegalArgumentException {
    eventCommandManager.executeCommand(command, model, view);
  }
}