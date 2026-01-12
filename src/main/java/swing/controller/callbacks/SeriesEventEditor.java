package swing.controller.callbacks;

import java.time.Duration;
import java.time.LocalDateTime;
import model.calendar.CalendarModelInterface;
import model.event.EventReadOnlyInterface;
import swing.view.dialogs.event.data.BaseData;

/**
 * Utility class containing static methods for editing recurring events. This class provides logic
 * for updating either a single instance and all following events in the series, or the entire event
 * series.
 */
public class SeriesEventEditor {
  /**
   * Handle editing an event and all following occurrences in the series based on the modifications
   * contained in {@code data}.
   *
   * @param calendar      active calendar model
   * @param originalEvent original event being edited
   * @param data          updated field values and edit mode
   */
  public static void handleEditEventStartFrom(CalendarModelInterface calendar,
                                              EventReadOnlyInterface originalEvent,
                                              BaseData data) {
    editEventStartFromIdentifierHelper(calendar, originalEvent, data);
  }

  /**
   * Handle editing all occurrences in the series based on the modifications contained in
   * {@code data}.
   *
   * @param calendar      active calendar model
   * @param originalEvent original event being edited
   * @param data          updated field values and edit mode
   */
  public static void handleEditSeries(CalendarModelInterface calendar,
                                      EventReadOnlyInterface originalEvent,
                                      BaseData data) {
    editSeriesIdentifierHelper(calendar, originalEvent, data);
  }

  /**
   * Update identifying properties (subject, start, end) of the event and all events following it in
   * the series.
   *
   * @param calendar      active calendar model
   * @param originalEvent original event before update
   * @param data          updated field values
   */
  private static void editEventStartFromIdentifierHelper(CalendarModelInterface calendar,
                                                         EventReadOnlyInterface originalEvent,
                                                         BaseData data) {
    Duration originalDuration =
        Duration.between(originalEvent.getStartDateTime(), originalEvent.getEndDateTime());

    String eventSubject = originalEvent.getSubject();
    LocalDateTime eventStart = originalEvent.getStartDateTime();
    LocalDateTime eventEnd = originalEvent.getEndDateTime();

    if (!data.getSubject().equals(originalEvent.getSubject())) {
      calendar.editEventStartFrom(eventSubject, "subject", eventStart.toString(),
          data.getSubject());
      eventSubject = data.getSubject();
    }

    if (!data.getStartDateTime().equals(eventStart.toString())) {
      calendar.editEventStartFrom(eventSubject, "start", eventStart.toString(),
          data.getStartDateTime());
      eventStart = LocalDateTime.parse(data.getStartDateTime());
      eventEnd = eventStart.plus(originalDuration);
    }

    if (!data.getEndDateTime().equals(eventEnd.toString())) {
      calendar.editEventStartFrom(eventSubject, "end", eventStart.toString(),
          data.getEndDateTime());
    }

    editEventStartFromOptionalHelper(calendar, originalEvent, data,
        eventSubject, eventStart.toString());
  }

  /**
   * Update optional non-identifying properties (description, location, status) for this and
   * following occurrences.
   *
   * @param calendar      active calendar model
   * @param originalEvent original event
   * @param data          updated field values
   * @param subject       subject after changes
   * @param start         updated start datetime string
   */
  private static void editEventStartFromOptionalHelper(CalendarModelInterface calendar,
                                                       EventReadOnlyInterface originalEvent,
                                                       BaseData data, String subject,
                                                       String start) {
    if (!data.getDescription().isEmpty()) {
      calendar.editEventStartFrom(subject, "description", start, data.getDescription());
    }

    if (!data.getLocation().isEmpty()) {
      calendar.editEventStartFrom(subject, "location", start, data.getLocation());
    }

    if (!data.getStatus().isEmpty()) {
      calendar.editEventStartFrom(subject, "status", start, data.getStatus());
    }
  }

  /**
   * Update identifying properties (subject, start, end) for all events in the series.
   *
   * @param calendar      active calendar model
   * @param originalEvent original event before update
   * @param data          updated field values
   */
  private static void editSeriesIdentifierHelper(CalendarModelInterface calendar,
                                                 EventReadOnlyInterface originalEvent,
                                                 BaseData data) {
    Duration originalDuration =
        Duration.between(originalEvent.getStartDateTime(), originalEvent.getEndDateTime());

    String eventSubject = originalEvent.getSubject();
    LocalDateTime eventStart = originalEvent.getStartDateTime();
    LocalDateTime eventEnd = originalEvent.getEndDateTime();

    if (!data.getSubject().equals(originalEvent.getSubject())) {
      calendar.editSeriesStartFrom(eventSubject, "subject", eventStart.toString(),
          data.getSubject());
      eventSubject = data.getSubject();
    }

    if (!data.getStartDateTime().equals(eventStart.toString())) {
      calendar.editSeriesStartFrom(eventSubject, "start", eventStart.toString(),
          data.getStartDateTime());
      eventStart = LocalDateTime.parse(data.getStartDateTime());
      eventEnd = eventStart.plus(originalDuration);
    }

    if (!data.getEndDateTime().equals(eventEnd.toString())) {
      calendar.editSeriesStartFrom(eventSubject, "end", eventStart.toString(),
          data.getEndDateTime());
    }

    editSeriesOptionalHelper(calendar, originalEvent, data,
        eventSubject, eventStart.toString());
  }

  /**
   * Update optional non-identifying properties (description, location, status) for all events in a
   * series.
   *
   * @param calendar      active calendar model
   * @param originalEvent original event before update
   * @param data          updated values
   * @param subject       updated subject
   * @param start         updated start datetime string
   */
  private static void editSeriesOptionalHelper(CalendarModelInterface calendar,
                                               EventReadOnlyInterface originalEvent,
                                               BaseData data, String subject,
                                               String start) {
    if (!data.getDescription().isEmpty()) {
      calendar.editSeriesStartFrom(subject, "description", start, data.getDescription());
    }

    if (!data.getLocation().isEmpty()) {
      calendar.editSeriesStartFrom(subject, "location", start, data.getLocation());
    }

    if (!data.getStatus().isEmpty()) {
      calendar.editSeriesStartFrom(subject, "status", start, data.getStatus());
    }
  }
}
