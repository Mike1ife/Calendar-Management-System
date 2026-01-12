package io.export;

import java.io.IOException;
import java.util.List;
import model.event.EventReadOnlyInterface;

/**
 * An interface for exporting calendar events to a file in a specific format.
 * Implementations should handle exporting events provided as a list, and
 * output the data to a file with a specified name.
 */
public interface CalendarExporterInterface {

  /**
   * Exports a list of calendar events to a file in the specified format.
   *
   * @param events   the list of events to be exported must implement the EventInterface
   * @param filename the name of the file to which the events will be exported,
   *                 including its extension
   * @return the absolute path of the exported file
   * @throws IOException if an I/O error occurs during the export process
   */
  String exportCalendar(List<EventReadOnlyInterface> events, String filename) throws IOException;
}
