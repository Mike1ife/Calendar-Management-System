package io.export;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.event.EventReadOnlyInterface;

/**
 * A class for exporting calendar events to an iCalendar (.ics) file format.
 * Implements the {@link CalendarExporterInterface} to handle converting a list of events
 * into the iCalendar format and writing them to a specified file.
 */
public class IcsCalendarExporter implements CalendarExporterInterface {

  private final DateTimeFormatter icsDatetimeFormat =
      DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

  @Override
  public String exportCalendar(List<EventReadOnlyInterface> events, String filename)
      throws IOException {
    Path outputPath = Paths.get("exports", filename);

    if (outputPath.getParent() != null) {
      Files.createDirectories(outputPath.getParent());
    }

    try (PrintWriter writer = new PrintWriter(
        Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8))) {

      writer.println("BEGIN:VCALENDAR");
      writer.println("VERSION:2.0");
      writer.println("PRODID:-//Calendar Application//EN");
      writer.println("CALSCALE:GREGORIAN");
      writer.println("METHOD:PUBLISH");

      for (EventReadOnlyInterface event : events) {
        writeEventToIcs(writer, event);
      }

      writer.println("END:VCALENDAR");
    }

    return outputPath.toAbsolutePath().toString();
  }

  private void writeEventToIcs(PrintWriter writer, EventReadOnlyInterface event) {
    writer.println("BEGIN:VEVENT");

    writer.println("UID:" + generateUid(event));

    LocalDateTime startDateTime = event.getStartDateTime();
    writer.println("DTSTART:" + formatDateTime(startDateTime));

    LocalDateTime endDateTime = event.getEndDateTime();
    writer.println("DTEND:" + formatDateTime(endDateTime));

    writer.println("SUMMARY:" + escapeText(event.getSubject()));

    if (event.getDescription() != null && !event.getDescription().isEmpty()) {
      writer.println("DESCRIPTION:" + escapeText(event.getDescription()));
    }

    if (event.getLocation() != null && !event.getLocation().isEmpty()) {
      writer.println("LOCATION:" + escapeText(event.getLocation()));
    }

    if (event.getStatus() != null) {
      if (event.getStatus().toString().equals("PUBLIC")) {
        writer.println("CLASS:PUBLIC");
      } else {
        writer.println("CLASS:PRIVATE");
      }
    }

    writer.println("DTSTAMP:" + formatDateTime(LocalDateTime.now()));

    writer.println("END:VEVENT");
  }

  private String formatDateTime(LocalDateTime dateTime) {
    return dateTime.format(icsDatetimeFormat);
  }

  private String generateUid(EventReadOnlyInterface event) {
    return event.getSubject().replaceAll("[^a-zA-Z0-9]", "") + "-"
        + event.getStartDateTime().format(icsDatetimeFormat) + "@calendar-app";
  }

  private String escapeText(String text) {
    if (text == null) {
      return "";
    }
    return text.replace("\\", "\\\\")
        .replace(",", "\\,")
        .replace(";", "\\;")
        .replace("\n", "\\n");
  }
}