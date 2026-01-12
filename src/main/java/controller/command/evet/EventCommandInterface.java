package controller.command.evet;

import model.calendar.CalendarModelInterface;
import view.CalendarViewInterface;

/**
 * This interface defines the contract for handling calendar-related commands.
 * Implementations of this interface are responsible for determining if a specific
 * command can be processed and for executing the corresponding logic when applicable.
 *
 * <p>Implementers should define custom behavior for specific commands by overriding the
 * methods provided in this interface.
 */
public interface EventCommandInterface {
  /**
   * Check if this handler can process the given command.
   */
  boolean canHandle(String command);

  /**
   * Execute the command.
   */
  void execute(String command, CalendarModelInterface model, CalendarViewInterface view)
      throws IllegalArgumentException;
}