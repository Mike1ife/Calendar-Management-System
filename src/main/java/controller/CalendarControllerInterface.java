package controller;

import java.io.IOException;

/**
 * The ICalendarController interface defines the contract for a controller
 * in a model-view-controller (MVC) architecture, specifically for managing
 * a calendar application. It mediates the interaction between the model
 * (representing the calendar data) and the view (responsible for user interface).
 *
 * <p>Implementations of this interface are responsible for processing user input,
 * delegating commands to the model, and updating the view as necessary.
 */
public interface CalendarControllerInterface {
  /**
   * Start the controller and handle user interaction.
   */
  void go() throws IOException;
}