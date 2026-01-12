package view;

import java.io.IOException;
import model.calendar.CalendarStatus;

/**
 * CalendarTextView is an implementation of the ICalendarView interface. This class provides
 * textual representations for various calendar-related operations such as displaying prompts,
 * errors, success messages, calendar status, and export results. The messages are written to
 * an output target specified during the initialization of the class.
 */
public class CalendarTextView implements CalendarViewInterface {
  private final Appendable output;

  /**
   * Create a view for Calendar with specified appendable output.
   *
   * @param output appendable output
   * @throws IllegalArgumentException if output is null
   */
  public CalendarTextView(Appendable output) throws IllegalArgumentException {
    if (output == null) {
      throw new IllegalArgumentException("Output cannot be null");
    }
    this.output = output;
  }

  @Override
  public void displayPrompt(String prompt) throws IllegalArgumentException {
    appendToOutput(prompt + "\n");
  }

  @Override
  public void displayError(String message) throws IllegalArgumentException {
    appendToOutput("Error: " + message + "\n");
  }

  @Override
  public void displaySuccess(String message) throws IllegalArgumentException {
    appendToOutput("Success: " + message + "\n");
  }

  @Override
  public void displayExit() throws IllegalArgumentException {
    appendToOutput("Exiting...");
  }

  @Override
  public void displayStatus(CalendarStatus isBusy) {
    appendToOutput("Calendar status: " + isBusy.toString() + "\n");
  }

  @Override
  public void displayExportResult(String absolutePath) {
    appendToOutput("Exported to: " + absolutePath + "\n");
  }

  /**
   * Append the given text to the output target.
   *
   * @param text text to append
   * @throws IllegalStateException if writing to the output fails
   */
  private void appendToOutput(String text) {
    try {
      output.append(text);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write to output", e);
    }
  }
}