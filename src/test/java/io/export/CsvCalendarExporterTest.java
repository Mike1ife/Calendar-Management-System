package io.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
 * Test class for CsvCalendarExporter.
 */
public class CsvCalendarExporterTest {
  private String testFilename;
  private CalendarModelInterface calendar;

  /**
   * Sets up the test environment by initializing the necessary parts and creating test data.
   */
  @Before
  public void setUp() throws IOException {
    testFilename = "test_export.csv";

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
  public void testExportBasicEvents() throws IOException {
    calendar.createSingleEventWithTime("Team Meeting",
        "2025-10-24T10:00", "2025-10-24T11:00");

    calendar.createAllDaySingleEvent("Birthday", "2025-10-25");

    calendar.createSeriesEventWithOccurrence("Weekly Standup",
        "2025-10-27T09:00", "2025-10-27T09:30",
        Set.of(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.FRIDAY), 3);

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    CsvCalendarExporter exporter = new CsvCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    assertTrue(Files.exists(Paths.get("exports", testFilename)));
  }

  @Test
  public void testFileHasHeader() throws IOException {
    calendar.createSingleEventWithTime("Meeting",
        "2025-10-24T10:00", "2025-10-24T11:00");

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    CsvCalendarExporter exporter = new CsvCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String filePath = Files.exists(Paths.get(testFilename))
        ? testFilename : "exports/" + testFilename;

    List<String> lines = Files.readAllLines(Paths.get(filePath));

    assertEquals("Subject,Start Date,Start Time,End Date,End Time,Description,Location,Private",
        lines.get(0));
  }

  @Test
  public void testEventWithNullDescription() throws IOException {
    calendar.createSingleEventWithTime("Meeting Without Description",
        "2025-10-24T10:00", "2025-10-24T11:00");

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    CsvCalendarExporter exporter = new CsvCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String filePath = Files.exists(Paths.get(testFilename))
        ? testFilename : "exports/" + testFilename;

    List<String> lines = Files.readAllLines(Paths.get(filePath));

    String eventLine = lines.stream()
        .filter(line -> line.contains("Meeting Without Description"))
        .findFirst()
        .orElseThrow(() -> new AssertionError("Event not found in CSV"));

    assertTrue("Description should be empty", eventLine.contains(",,"));
  }

  @Test
  public void testEventWithNullLocation() throws IOException {
    calendar.createSingleEventWithTime("Meeting Without Location",
        "2025-10-24T10:00", "2025-10-24T11:00");

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    CsvCalendarExporter exporter = new CsvCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String filePath = Files.exists(Paths.get(testFilename))
        ? testFilename : "exports/" + testFilename;

    List<String> lines = Files.readAllLines(Paths.get(filePath));

    String eventLine = lines.stream()
        .filter(line -> line.contains("Meeting Without Location"))
        .findFirst()
        .orElseThrow(() -> new AssertionError("Event not found in CSV"));

    assertTrue("Location should be empty", eventLine.contains(",,"));
  }

  @Test
  public void testEventWithPrivateStatus() throws IOException {
    calendar.createSingleEventWithTime("Private Meeting",
        "2025-10-24T10:00", "2025-10-24T11:00");

    calendar.editSingleEvent(List.of("Private Meeting", "status",
        "2025-10-24T10:00", "2025-10-24T11:00", "PRIVATE"));

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    CsvCalendarExporter exporter = new CsvCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String filePath = Files.exists(Paths.get(testFilename))
        ? testFilename : "exports/" + testFilename;

    List<String> lines = Files.readAllLines(Paths.get(filePath));

    String eventLine = lines.stream()
        .filter(line -> line.contains("Private Meeting"))
        .findFirst()
        .orElseThrow(() -> new AssertionError("Event not found in CSV"));

    assertTrue("Event should be marked as private", eventLine.endsWith("TRUE"));
  }

  @Test
  public void testEventWithPublicStatus() throws IOException {
    calendar.createSingleEventWithTime("Public Meeting",
        "2025-10-24T10:00", "2025-10-24T11:00");

    calendar.editSingleEvent(List.of("Public Meeting", "status",
        "2025-10-24T10:00", "2025-10-24T11:00", "PUBLIC"));

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    CsvCalendarExporter exporter = new CsvCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String filePath = Files.exists(Paths.get(testFilename))
        ? testFilename : "exports/" + testFilename;

    List<String> lines = Files.readAllLines(Paths.get(filePath));

    String eventLine = lines.stream()
        .filter(line -> line.contains("Public Meeting"))
        .findFirst()
        .orElseThrow(() -> new AssertionError("Event not found in CSV"));

    assertTrue("Event should be marked as public", eventLine.endsWith("FALSE"));
  }

  @Test
  public void testEventWithEmptyDescription() throws IOException {
    calendar.createSingleEventWithTime("Meeting",
        "2025-10-24T10:00", "2025-10-24T11:00");

    calendar.editSingleEvent(List.of("Meeting", "description",
        "2025-10-24T10:00", "2025-10-24T11:00", ""));

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    CsvCalendarExporter exporter = new CsvCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String filePath = Files.exists(Paths.get(testFilename))
        ? testFilename : "exports/" + testFilename;

    List<String> lines = Files.readAllLines(Paths.get(filePath));

    String eventLine = lines.stream()
        .filter(line -> line.contains("Meeting"))
        .findFirst()
        .orElseThrow(() -> new AssertionError("Event not found in CSV"));

    assertTrue("Description should be empty", eventLine.contains(",,"));
  }

  @Test
  public void testEventWithEmptyLocation() throws IOException {
    calendar.createSingleEventWithTime("Meeting",
        "2025-10-24T10:00", "2025-10-24T11:00");

    calendar.editSingleEvent(List.of("Meeting", "location",
        "2025-10-24T10:00", "2025-10-24T11:00", ""));

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    CsvCalendarExporter exporter = new CsvCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String filePath = Files.exists(Paths.get(testFilename))
        ? testFilename : "exports/" + testFilename;

    List<String> lines = Files.readAllLines(Paths.get(filePath));

    String eventLine = lines.stream()
        .filter(line -> line.contains("Meeting"))
        .findFirst()
        .orElseThrow(() -> new AssertionError("Event not found in CSV"));

    assertTrue("Location should be empty", eventLine.contains(",,"));
  }

  @Test
  public void testEventWithAllFields() throws IOException {
    calendar.createSingleEventWithTime("Complete Meeting",
        "2025-10-24T10:00", "2025-10-24T11:00");

    calendar.editSingleEvent(List.of("Complete Meeting", "description",
        "2025-10-24T10:00", "2025-10-24T11:00", "Important discussion"));

    calendar.editSingleEvent(List.of("Complete Meeting", "location",
        "2025-10-24T10:00", "2025-10-24T11:00", "Conference Room A"));

    calendar.editSingleEvent(List.of("Complete Meeting", "status",
        "2025-10-24T10:00", "2025-10-24T11:00", "PUBLIC"));

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    CsvCalendarExporter exporter = new CsvCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String filePath = Files.exists(Paths.get(testFilename))
        ? testFilename : "exports/" + testFilename;

    String content = new String(Files.readAllBytes(Paths.get(filePath)));

    assertTrue(content.contains("Complete Meeting"));
    assertTrue(content.contains("Important discussion"));
    assertTrue(content.contains("Conference Room A"));
    assertTrue(content.contains("FALSE"));
  }

  @Test
  public void testSeriesCreatesMultipleEvents() throws IOException {
    calendar.createSeriesEventWithOccurrence("Weekly Standup",
        "2025-10-27T09:00", "2025-10-27T09:30",
        Set.of(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.FRIDAY), 3);

    List<EventReadOnlyInterface> events = calendar.getAllEventsReadOnly();
    CsvCalendarExporter exporter = new CsvCalendarExporter();
    exporter.exportCalendar(events, testFilename);

    String filePath = Files.exists(Paths.get(testFilename))
        ? testFilename : "exports/" + testFilename;

    List<String> lines = Files.readAllLines(Paths.get(filePath));

    int standupCount = 0;
    for (String line : lines) {
      if (line.contains("Weekly Standup")) {
        standupCount++;
      }
    }

    assertEquals(3, standupCount);
  }
}