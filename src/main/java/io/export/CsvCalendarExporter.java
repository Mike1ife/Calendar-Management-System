package io.export;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import model.event.EventReadOnlyInterface;
import model.event.EventStatus;

/**
 * A CSV-based implementation of the {@link CalendarExporterInterface} for exporting
 * a list of calendar events to a CSV file. Each event is represented as a row
 * in the file with standard fields such as subject, start/end date and time,
 * description, location, and privacy status.
 * The first line of the generated CSV file acts as a header and includes the column names.
 * Each later line represents an individual event in the specified format.
 */
public class CsvCalendarExporter implements CalendarExporterInterface {

  @Override
  public String exportCalendar(List<EventReadOnlyInterface> events, String filename)
      throws IOException {
    Path outputPath = Paths.get("exports", filename);


    if (outputPath.getParent() != null) {
      Files.createDirectories(outputPath.getParent());
    }

    try (PrintWriter writer = new PrintWriter(
        Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8))) {

      writer.println(
          "Subject,Start Date,Start Time,End Date,End Time,Description,Location,Private");

      for (EventReadOnlyInterface event : events) {
        writeEventToCsv(writer, event);
      }
    }

    return outputPath.toAbsolutePath().toString();
  }

  /**
   * Write a single event as a CSV row.
   */
  private void writeEventToCsv(PrintWriter writer, EventReadOnlyInterface event) {
    StringBuilder sb = new StringBuilder();

    LocalDateTime startDateTime = event.getStartDateTime();
    LocalDateTime endDateTime = event.getEndDateTime();

    sb.append(event.getSubject()).append(",");
    sb.append(startDateTime.toLocalDate().toString()).append(",");
    sb.append(startDateTime.toLocalTime().toString()).append(",");
    sb.append(endDateTime.toLocalDate().toString()).append(",");
    sb.append(endDateTime.toLocalTime().toString()).append(",");
    sb.append(event.getDescription() != null ? event.getDescription() : "").append(",");
    sb.append(event.getLocation() != null ? event.getLocation() : "").append(",");

    if (event.getStatus() == EventStatus.PUBLIC) {
      sb.append("FALSE");
    } else {
      sb.append("TRUE");
    }

    writer.println(sb);
  }
}
