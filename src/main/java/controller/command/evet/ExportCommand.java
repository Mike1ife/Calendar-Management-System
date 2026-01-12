package controller.command.evet;

import io.export.CalendarExporterInterface;
import io.export.CsvCalendarExporter;
import io.export.IcsCalendarExporter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.calendar.CalendarModelInterface;
import model.event.EventReadOnlyInterface;
import view.CalendarViewInterface;

/**
 * The ExportCommand class is responsible for handling the export command for calendar events.
 * This command allows exporting events to a specified file format, such as CSV or ICS.
 * It determines the appropriate exporter based on the file extension in the command
 * and delegates the export operation to the corresponding exporter implementation.
 * The class supports registering custom export formats and their corresponding exporters.
 * By default, CSV and ICS export formats are supported.
 */
public class ExportCommand implements EventCommandInterface {

  private final Map<String, CalendarExporterInterface> exportOptions;

  /**
   * Constructs a new ExportCommand instance.
   * Initializes the internal export options map and registers the default export
   * formats (CSV and ICS) along with their associated exporter implementations.
   */
  public ExportCommand() {
    this.exportOptions = new HashMap<>();
    registerDefaultExportOptions();
  }

  private void registerDefaultExportOptions() {
    registerExportOption("csv", new CsvCalendarExporter());
    registerExportOption("ics", new IcsCalendarExporter());
    registerExportOption("ical", new IcsCalendarExporter());
  }

  /**
   * Registers a custom export option for calendar events.
   * Associates a specific file format with an implementation of the
   * CalendarExporterInterface, enabling export functionality for that format.
   *
   * @param format   the file format (e.g., "csv", "ics") to be supported
   *                 by the export option
   * @param exporter an implementation of the CalendarExporterInterface
   *                 responsible for handling the export process
   */
  public void registerExportOption(String format, CalendarExporterInterface exporter) {
    this.exportOptions.put(format, exporter);
  }

  @Override
  public boolean canHandle(String command) {
    return command.toLowerCase().contains("export") && command.toLowerCase().contains("cal");
  }

  @Override
  public void execute(String command, CalendarModelInterface model, CalendarViewInterface view)
      throws IllegalArgumentException {

    Pattern pattern = Pattern.compile(
        "([^\\s]+\\.(\\w+))",
        Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(command);

    if (!matcher.find()) {
      throw new IllegalArgumentException("Filename with extension not found in export command");
    }

    String filename = matcher.group(1);
    String extension = matcher.group(2);

    CalendarExporterInterface exporter = exportOptions.get(extension);

    if (exporter == null) {
      throw new IllegalArgumentException("Unsupported export format: " + extension);
    }

    try {
      List<EventReadOnlyInterface> events = model.getAllEventsReadOnly();
      String absolutePath = exporter.exportCalendar(events, filename);
      view.displayExportResult(absolutePath);
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to export: " + e.getMessage());
    }
  }
}