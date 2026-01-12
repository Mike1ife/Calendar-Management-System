package swing.controller.callbacks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import model.calendar.CalendarModelInterface;
import model.event.EventReadOnlyInterface;
import swing.view.dialogs.event.data.BaseData;

/**
 * Utility class containing static methods for updating a single event. This class applies
 * modifications to an individual event instance, updating identifying fields (subject, start, end)
 * and optional properties (location, description, status) as needed.
 */
public class SingleEventEditor {
  /**
   * Handle editing a single event using the updated values provided in {@code data}. This method
   * delegates work to an internal helper to update identifying fields first.
   *
   * @param calendar      calendar the event belongs to
   * @param originalEvent original event before modification
   * @param data          updated values for the event
   */
  public static void handleEditSingleEvent(CalendarModelInterface calendar,
                                           EventReadOnlyInterface originalEvent,
                                           BaseData data) {
    editSingleEventIdentifierHelper(calendar, originalEvent, data);
  }

  /**
   * Update identifying fields of a single event (subject, start, end). If an identifying field
   * changes, the calendar removes the old event and inserts an updated one. After identifiers are
   * processed, optional properties are handled in a separate helper.
   *
   * @param calendar      calendar model
   * @param originalEvent original event values
   * @param data          updated event values
   */
  private static void editSingleEventIdentifierHelper(CalendarModelInterface calendar,
                                                      EventReadOnlyInterface originalEvent,
                                                      BaseData data) {

    Duration originalDuration =
        Duration.between(originalEvent.getStartDateTime(), originalEvent.getEndDateTime());

    String eventSubject = originalEvent.getSubject();
    LocalDateTime eventStart = originalEvent.getStartDateTime();
    LocalDateTime eventEnd = originalEvent.getEndDateTime();

    if (!data.getSubject().equals(originalEvent.getSubject())) {
      calendar.editSingleEvent(
          List.of(eventSubject, "subject", eventStart.toString(),
              eventEnd.toString(), data.getSubject()));
      eventSubject = data.getSubject();
    }

    if (!data.getStartDateTime().equals(eventStart.toString())) {
      calendar.editSingleEvent(
          List.of(eventSubject, "start", eventStart.toString(),
              eventEnd.toString(), data.getStartDateTime()));
      eventStart = LocalDateTime.parse(data.getStartDateTime());
      eventEnd = eventStart.plus(originalDuration);
    }

    if (!data.getEndDateTime().equals(eventEnd.toString())) {
      calendar.editSingleEvent(
          List.of(eventSubject, "end", eventStart.toString(),
              eventEnd.toString(), data.getEndDateTime()));
      eventEnd = LocalDateTime.parse(data.getEndDateTime());
    }

    editSingleEventOptionalHelper(calendar, originalEvent, data,
        eventSubject, eventStart.toString(), eventEnd.toString());
  }

  /**
   * Update optional non-identifying properties of a single event, including description, location,
   * and status.
   *
   * @param calendar      calendar model
   * @param originalEvent original event before modification
   * @param data          updated values
   * @param subject       updated subject value
   * @param start         updated start datetime string
   * @param end           updated end datetime string
   */
  private static void editSingleEventOptionalHelper(CalendarModelInterface calendar,
                                                    EventReadOnlyInterface originalEvent,
                                                    BaseData data, String subject,
                                                    String start, String end) {
    if (!data.getDescription().isEmpty()) {
      calendar.editSingleEvent(
          List.of(subject, "description", start, end, data.getDescription()));
    }

    if (!data.getLocation().isEmpty()) {
      calendar.editSingleEvent(
          List.of(subject, "location", start, end, data.getLocation()));
    }

    if (!data.getStatus().isEmpty()) {
      calendar.editSingleEvent(
          List.of(subject, "status", start, end, data.getStatus()));
    }
  }
}
