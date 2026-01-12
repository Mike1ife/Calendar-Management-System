package model.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

/**
 * This class represent a printer to output formatted events summary.
 */
public class EventPrinter {
  /**
   * Format a list of events to bulleted list.
   *
   * @param events list of events
   * @return bulleted list of events
   */
  public static String print(List<EventInterface> events) {
    events.sort(Comparator.comparing(EventInterface::getStartDateTime));

    StringBuilder stringBuilder = new StringBuilder();
    for (EventInterface event : events) {
      LocalDateTime startDateTime = event.getStartDateTime();
      LocalDate startDate = startDateTime.toLocalDate();
      LocalTime startTime = startDateTime.toLocalTime();
      LocalDateTime end = event.getEndDateTime();
      LocalDate endDate = end.toLocalDate();
      LocalTime endTime = end.toLocalTime();
      String location = event.getLocation();

      stringBuilder.append(String.format("subject %s starting on %s at %s, ending on %s at %s",
          event.getSubject(), startDate.toString(), startTime.toString(), endDate.toString(),
          endTime.toString()));

      if (location != null && !location.isEmpty()) {
        stringBuilder.append(" at ").append(location);
      }

      stringBuilder.append("\n");
    }

    return stringBuilder.toString();
  }
}
