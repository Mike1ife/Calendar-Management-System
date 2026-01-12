package swing.controller.callbacks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import model.calendar.CalendarManagerInterface;
import model.calendar.Weekday;
import swing.view.dialogs.event.data.EventData;

/**
 * Callback for creating a new event.
 * Handles saving event data from the dialog and updating the active calendar.
 */
public class CreateEventCallback extends DialogCallbackImpl<EventData>
    implements DialogCallbackInterface<EventData> {
  private final CalendarManagerInterface model;

  /**
   * Create a callback handler for event creation.
   *
   * @param model calendar manager model
   */
  public CreateEventCallback(CalendarManagerInterface model) {
    this.model = model;
  }

  @Override
  public void onSave(EventData data) {
    try {
      model.activateCalendar(data.getCalendarName());
      if (!data.isRepeat()) {
        handleSingleEvent(data);
      } else if (data.hasOccurrences()) {
        handleSeriesEventWithOccurrence(data);
      } else {
        handleSeriesEventWithEndDate(data);
      }
    } catch (Exception e) {
      showError("Error creating event: " + e.getMessage());
    }
  }

  @Override
  public void onCancel() {
    System.out.println("User cancelled event creation");
  }

  /**
   * Create a single event (all-day or timed) and apply optional fields.
   *
   * @param data event data
   */
  private void handleSingleEvent(EventData data) {
    if (data.isAllDay()) {
      this.model.getActiveCalendar().createAllDaySingleEvent(data.getSubject(),
          LocalDateTime.parse(data.getStartDateTime()).toLocalDate().toString());
    } else {
      this.model.getActiveCalendar()
          .createSingleEventWithTime(data.getSubject(), data.getStartDateTime(),
              data.getEndDateTime());
    }

    if (!data.getLocation().isEmpty()) {
      this.model.getActiveCalendar().editSingleEvent(
          List.of(data.getSubject(), "location", data.getStartDateTime(), data.getEndDateTime(),
              data.getLocation()));
    }

    if (!data.getDescription().isEmpty()) {
      this.model.getActiveCalendar().editSingleEvent(
          List.of(data.getSubject(), "description", data.getStartDateTime(), data.getEndDateTime(),
              data.getDescription()));
    }

    if (!data.getStatus().isEmpty()) {
      this.model.getActiveCalendar().editSingleEvent(
          List.of(data.getSubject(), "status", data.getStartDateTime(), data.getEndDateTime(),
              data.getStatus()));
    }
  }

  /**
   * Create a recurring event using a fixed number of occurrences and apply optional fields.
   *
   * @param data event data
   */
  private void handleSeriesEventWithOccurrence(EventData data) {
    if (data.isAllDay()) {
      this.model.getActiveCalendar().createAllDaySeriesEventWithOccurrence(data.getSubject(),
          LocalDateTime.parse(data.getStartDateTime()).toLocalDate().toString(), data.getWeekdays(),
          data.getOccurrences());
    } else {
      this.model.getActiveCalendar()
          .createSeriesEventWithOccurrence(data.getSubject(), data.getStartDateTime(),
              data.getEndDateTime(), data.getWeekdays(), data.getOccurrences());
    }

    handleSeriesEventHelper(data);
  }

  /**
   * Create a recurring event using an end date and apply optional fields.
   *
   * @param data event data
   */
  private void handleSeriesEventWithEndDate(EventData data) {
    if (data.isAllDay()) {
      this.model.getActiveCalendar().createAllDaySeriesEventWithEndDate(data.getSubject(),
          LocalDateTime.parse(data.getStartDateTime()).toLocalDate().toString(), data.getWeekdays(),
          data.getUntilDate());
    } else {
      this.model.getActiveCalendar()
          .createSeriesEventWithEndDate(data.getSubject(), data.getStartDateTime(),
              data.getEndDateTime(), data.getWeekdays(), data.getUntilDate());
    }

    handleSeriesEventHelper(data);
  }

  /**
   * Apply optional fields (location, description, status) to a newly created series.
   *
   * @param data event data
   */
  private void handleSeriesEventHelper(EventData data) {
    LocalDateTime actualStart = LocalDateTime.parse(data.getStartDateTime());
    Weekday currentWeekday = Weekday.valueOf(actualStart.getDayOfWeek().toString());
    while (!data.getWeekdays().contains(currentWeekday)) {
      actualStart = actualStart.plusDays(1);
      currentWeekday = Weekday.valueOf(actualStart.getDayOfWeek().toString());
    }

    if (data.getUntilDate() != null) {
      if (actualStart.toLocalDate().isAfter(LocalDate.parse(data.getUntilDate()))) {
        return;
      }
    }

    if (!data.getLocation().isEmpty()) {
      this.model.getActiveCalendar()
          .editSeriesStartFrom(data.getSubject(), "location", actualStart.toString(),
              data.getLocation());
    }

    if (!data.getDescription().isEmpty()) {
      this.model.getActiveCalendar()
          .editSeriesStartFrom(data.getSubject(), "description", actualStart.toString(),
              data.getDescription());
    }

    if (!data.getStatus().isEmpty()) {
      this.model.getActiveCalendar()
          .editSeriesStartFrom(data.getSubject(), "status", actualStart.toString(),
              data.getStatus());
    }
  }
}