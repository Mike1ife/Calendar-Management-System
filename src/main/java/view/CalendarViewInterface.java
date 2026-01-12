package view;

import model.calendar.CalendarStatus;

/**
 * The ICalendarView interface defines the contract for a view component in a
 * model-view-controller (MVC) architecture, specifically for a calendar-related application.
 * It is responsible for rendering various user-facing messages and statuses to an output medium.
 *
 * <p>Implementations of this interface are expected to provide specific ways of displaying
 * prompts, error messages, success messages, calendar status updates, and other information
 * relevant to the application's operations.
 */
public interface CalendarViewInterface {
  /**
   * Display prompt.
   *
   * @param message prompt message
   */
  void displayPrompt(String message);

  /**
   * Display error.
   *
   * @param message error message
   */
  void displayError(String message);

  /**
   * Display success message.
   *
   * @param message success message
   */
  void displaySuccess(String message);

  /**
   * Display exist.
   */
  void displayExit();

  /**
   * Display Calendar status.
   *
   * @param isBusy is busy in Calendar
   */
  void displayStatus(CalendarStatus isBusy);

  /**
   * Display export result.
   *
   * @param absolutePath file absolute path
   */
  void displayExportResult(String absolutePath);
}
