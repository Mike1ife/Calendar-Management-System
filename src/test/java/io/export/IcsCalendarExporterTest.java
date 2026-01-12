package io.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import model.calendar.CalendarManager;
import model.calendar.CalendarManagerInterface;
import model.calendar.CalendarModelInterface;
import model.calendar.Weekday;
import model.event.EventReadOnlyInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Comprehensive test for IcsCalendarExporter with improved coverage.
 */
public class IcsCalendarExporterTest {
  private String testFilename;
  private CalendarModelInterface calendar;

  /**
   * Sets up the test environment by initializing the necessary parts and creating test data.
   */
  @Before
  public void setUp() throws IOException {
    testFilename = "test_export.ics";

    CalendarManagerInterface calendarManager = new CalendarManager();
    calendarManager.addCalendar("TestCalendar", "America/New_York");
    calendarManager.activateCalendar("TestCalendar");

    calendar = calendarManager.getActiveCalendar();
  }

  /**
   * Cleans up the test environment by deleting the test file.
   */
  @After
  public void tearDown() throws IOException {
    if (Files.exists(Paths.get(testFilename))) {
      Files.delete(Paths.get(testFilename));
    }
    if (Files.exists(Paths.get("exports", testFilename))) {
      Files.delete(Paths.get("exports", testFilename));
    }
  }

  @Test
  public void testBasicEventExport() throws IOException {
    calendar.createSingleEventWithTime("Team Meeting",
        "2025-10-24T10:00", "2025-10-24T11:00");

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    IcsCalendarExporter exporter = new IcsCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    assertTrue(Files.exists(Paths.get("exports", testFilename)));
  }

  @Test
  public void testEventWithDescription() throws IOException {
    calendar.createSingleEventWithTime("Meeting with Description",
        "2025-10-24T10:00", "2025-10-24T11:00");
    calendar.editSingleEvent(List.of("Meeting with Description", "description",
        "2025-10-24T10:00", "2025-10-24T11:00",
        "This is a detailed description"));

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    IcsCalendarExporter exporter = new IcsCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String content = readFileContent();
    assertTrue(content.contains("DESCRIPTION:"));
    assertTrue(content.contains("This is a detailed description"));
  }

  @Test
  public void testEventWithEmptyDescription() throws IOException {
    calendar.createSingleEventWithTime("Meeting without Description",
        "2025-10-24T10:00", "2025-10-24T11:00");
    calendar.editSingleEvent(List.of("Meeting without Description", "description",
        "2025-10-24T10:00", "2025-10-24T11:00", ""));

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    IcsCalendarExporter exporter = new IcsCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String content = readFileContent();
    int descCount = countOccurrences(content, "DESCRIPTION:");
    assertTrue(descCount == 0 || !content.contains("DESCRIPTION:\n"));
  }

  @Test
  public void testEventWithLocation() throws IOException {
    calendar.createSingleEventWithTime("Meeting with Location",
        "2025-10-24T10:00", "2025-10-24T11:00");
    calendar.editSingleEvent(List.of("Meeting with Location", "location",
        "2025-10-24T10:00", "2025-10-24T11:00",
        "Conference Room A"));

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    IcsCalendarExporter exporter = new IcsCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String content = readFileContent();
    assertTrue(content.contains("LOCATION:"));
    assertTrue(content.contains("Conference Room A"));
  }

  @Test
  public void testEventWithEmptyLocation() throws IOException {
    calendar.createSingleEventWithTime("Meeting without Location",
        "2025-10-24T10:00", "2025-10-24T11:00");
    calendar.editSingleEvent(List.of("Meeting without Location", "location",
        "2025-10-24T10:00", "2025-10-24T11:00", ""));

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    IcsCalendarExporter exporter = new IcsCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String content = readFileContent();
    int locCount = countOccurrences(content, "LOCATION:");
    assertTrue(locCount == 0 || !content.contains("LOCATION:\n"));
  }

  @Test
  public void testEventWithPublicStatus() throws IOException {
    calendar.createSingleEventWithTime("Public Meeting",
        "2025-10-24T10:00", "2025-10-24T11:00");
    calendar.editSingleEvent(List.of("Public Meeting", "status",
        "2025-10-24T10:00", "2025-10-24T11:00", "PUBLIC"));

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    IcsCalendarExporter exporter = new IcsCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String content = readFileContent();
    assertTrue(content.contains("CLASS:PUBLIC"));
  }

  @Test
  public void testEventWithPrivateStatus() throws IOException {
    calendar.createSingleEventWithTime("Private Meeting",
        "2025-10-24T10:00", "2025-10-24T11:00");
    calendar.editSingleEvent(List.of("Private Meeting", "status",
        "2025-10-24T10:00", "2025-10-24T11:00", "PRIVATE"));

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    IcsCalendarExporter exporter = new IcsCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String content = readFileContent();
    assertTrue(content.contains("CLASS:PRIVATE"));
  }

  @Test
  public void testEventWithoutStatus() throws IOException {
    calendar.createSingleEventWithTime("No Status Meeting",
        "2025-10-24T10:00", "2025-10-24T11:00");

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    IcsCalendarExporter exporter = new IcsCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String content = readFileContent();
    assertTrue(content.contains("BEGIN:VEVENT"));
    assertTrue(content.contains("END:VEVENT"));
  }

  @Test
  public void testEventWithSpecialCharactersInDescription() throws IOException {
    calendar.createSingleEventWithTime("Meeting",
        "2025-10-24T10:00", "2025-10-24T11:00");
    calendar.editSingleEvent(List.of("Meeting", "description",
        "2025-10-24T10:00", "2025-10-24T11:00",
        "Test with special chars: comma, semicolon; backslash\\ newline\n"));

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    IcsCalendarExporter exporter = new IcsCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String content = readFileContent();
    assertTrue(content.contains("DESCRIPTION:"));
    assertTrue(content.contains("\\,") || content.contains("\\;")
        || content.contains("\\\\"));
  }

  @Test
  public void testMultipleEventsWithMixedProperties() throws IOException {
    calendar.createSingleEventWithTime("Complete Event",
        "2025-10-24T10:00", "2025-10-24T11:00");
    calendar.editSingleEvent(List.of("Complete Event", "description",
        "2025-10-24T10:00", "2025-10-24T11:00",
        "Full description"));
    calendar.editSingleEvent(List.of("Complete Event", "location",
        "2025-10-24T10:00", "2025-10-24T11:00", "Room 101"));
    calendar.editSingleEvent(List.of("Complete Event", "status",
        "2025-10-24T10:00", "2025-10-24T11:00", "PUBLIC"));
    calendar.createSingleEventWithTime("Minimal Event",
        "2025-10-25T14:00", "2025-10-25T15:00");
    calendar.createSingleEventWithTime("Description Only",
        "2025-10-26T09:00", "2025-10-26T10:00");
    calendar.editSingleEvent(List.of("Description Only", "description",
        "2025-10-26T09:00", "2025-10-26T10:00",
        "Only has description"));

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    IcsCalendarExporter exporter = new IcsCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String content = readFileContent();
    assertTrue(content.contains("SUMMARY:Complete Event"));
    assertTrue(content.contains("SUMMARY:Minimal Event"));
    assertTrue(content.contains("SUMMARY:Description Only"));

    assertEquals(3, countOccurrences(content, "BEGIN:VEVENT"));
    assertEquals(3, countOccurrences(content, "END:VEVENT"));
  }

  @Test
  public void testSeriesEvents() throws IOException {
    calendar.createSeriesEventWithOccurrence("Weekly Meeting",
        "2025-10-27T14:00", "2025-10-27T15:30",
        Set.of(Weekday.MONDAY, Weekday.WEDNESDAY), 3);

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    IcsCalendarExporter exporter = new IcsCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String content = readFileContent();

    assertEquals(3, countOccurrences(content, "SUMMARY:Weekly Meeting"));
  }

  @Test
  public void testEmptyEventList() throws IOException {
    List<EventReadOnlyInterface> events = new ArrayList<>();
    IcsCalendarExporter exporter = new IcsCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String content = readFileContent();

    assertTrue(content.contains("BEGIN:VCALENDAR"));
    assertTrue(content.contains("END:VCALENDAR"));

    assertFalse(content.contains("BEGIN:VEVENT"));
  }

  @Test
  public void testFileCreationInExportsDirectory() throws IOException {
    calendar.createSingleEventWithTime("Test Event",
        "2025-10-24T10:00", "2025-10-24T11:00");

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    IcsCalendarExporter exporter = new IcsCalendarExporter();
    String absolutePath = exporter.exportCalendar(events, testFilename);

    assertTrue(absolutePath.contains(testFilename));
    assertTrue(Files.exists(Paths.get(absolutePath)));
  }


  private String readFileContent() throws IOException {
    String filePath = Files.exists(Paths.get(testFilename))
        ? testFilename : "exports/" + testFilename;
    return new String(Files.readAllBytes(Paths.get(filePath)));
  }

  private int countOccurrences(String text, String substring) {
    int count = 0;
    int index = 0;
    while ((index = text.indexOf(substring, index)) != -1) {
      count++;
      index += substring.length();
    }
    return count;
  }
}