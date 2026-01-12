package model.event;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import model.calendar.Weekday;

/**
 * This class is a collaborator to help update Series for Calendar. It handles Series updating and
 * splitting logics.
 */
public class SeriesUpdater {
  private final EventManager eventManager;
  private final EventEditor eventEditor;

  /**
   * Create SeriesUpdater with given manager and editor.
   *
   * @param eventManager event manager
   * @param eventEditor  event editor
   */
  public SeriesUpdater(EventManager eventManager, EventEditor eventEditor) {
    this.eventManager = eventManager;
    this.eventEditor = eventEditor;
  }

  /**
   * Update the property {@param property} of events in {@param series} starting from
   * {@param startFrom} with {@param newValue}.
   *
   * @param series    series to be updated
   * @param property  property to be updated
   * @param startFrom start date and time for events in Series update
   * @param newValue  new property value
   */
  public void updateSeriesStartFrom(SeriesInterface series, EventProperty property,
                                    LocalDateTime startFrom, String newValue) {
    if (property == EventProperty.START) {
      handleSeriesStartFromUpdateEventStartDateTimeHelper(series, startFrom, newValue);
    } else if (property == EventProperty.END) {
      handleSeriesStartFromUpdateEventEndDateTimeHelper(series, startFrom, newValue);
    } else {
      handleSeriesStartFromUpdateEventPropertyHelper(series, property, startFrom, newValue);
    }
  }

  /**
   * Update the property {@param property} of all events in {@param series} with {@param newValue}.
   *
   * @param series   series to be updated
   * @param property property to be updated
   * @param startAt  start date and time of the target event in the Series
   * @param newValue new property value
   */
  public void updateSeriesAll(SeriesInterface series, EventProperty property, LocalDateTime startAt,
                              String newValue) {
    if (property == EventProperty.START) {
      handleSeriesAllUpdateEventStartDateTimeHelper(series, startAt, newValue);
    } else if (property == EventProperty.END) {
      handleSeriesAllUpdateEventEndDateTimeHelper(series, startAt, newValue);
    } else {
      handleSeriesAllUpdateEventPropertyHelper(series, property, newValue);
    }
  }

  /**
   * Handle updating the start date or time of a SingleEvent starting at a given date and time,
   * along with future events in the Series as well. This process involves splitting Series into two
   * new Series of past events and events start from given date and time respectively. The former
   * will become a Series ending at one day before given date. The latter will remain as a new
   * Series repeating N times or Series until original end date.
   *
   * @param series    series to be updated
   * @param startFrom start date and time for events in Series update
   * @param newValue  new start date and time
   */
  private void handleSeriesStartFromUpdateEventStartDateTimeHelper(SeriesInterface series,
                                                                   LocalDateTime startFrom,
                                                                   String newValue) {
    handlePastEventsHelper(series, startFrom);
    handleAfterwardEventsHelper(series, startFrom, newValue);
  }

  /**
   * Help handle past events. Past events in Series will attach to a new Series ending at one day
   * before {@code startFrom}.
   *
   * @param series    series to be updated
   * @param startFrom start date and time for events in Series update
   */
  private void handlePastEventsHelper(SeriesInterface series, LocalDateTime startFrom) {
    List<EventInterface> pastEvents =
        this.eventManager.filterEventsInSeriesBefore(series, startFrom);
    if (!pastEvents.isEmpty()) {
      SeriesUntilEnd updatedPastSeries =
          EventFactory.createSeriesWithEndDate(series.getWeekdays(),
              startFrom.toLocalDate().minusDays(1));
      this.eventManager.attachEventsToNewSeries(updatedPastSeries, pastEvents);
    }
  }

  /**
   * Help handle afterward events. These updated events will attach to a new Series repeating N
   * times or Series until original end date (N = 1 + the number of future events.) Original Series
   * and original events requiring update will be removed.
   *
   * @param series    series to be updated
   * @param startFrom start date and time for events in Series update
   * @param newValue  new start date and time
   */
  private void handleAfterwardEventsHelper(SeriesInterface series, LocalDateTime startFrom,
                                           String newValue) {
    LocalDateTime newStartDateTime = LocalDateTime.parse(newValue);
    List<EventInterface> eventsToBeUpdated =
        this.eventManager.filterEventsInSeriesStartingFrom(series, startFrom);
    SeriesImpl seriesImpl = (SeriesImpl) series;
    SeriesInterface newSeries;
    if (seriesImpl.getEndDate() == null) {
      newSeries = EventFactory.createSeriesWithOccurrence(seriesImpl.getWeekdays(),
          eventsToBeUpdated.size());
    } else {
      newSeries = EventFactory.createSeriesWithEndDate(seriesImpl.getWeekdays(),
          seriesImpl.getEndDate());
    }

    EventInterface targetEvent = this.eventManager.filterEventsInSeriesStartAt(series, startFrom);

    Duration eventDuration = Duration.between(
        targetEvent.getStartDateTime(),
        targetEvent.getEndDateTime()
    );

    LocalTime newStartTime = newStartDateTime.toLocalTime();
    LocalTime newEndTime = newStartTime.plus(eventDuration);

    List<EventInterface> eventsInNewSeries =
        newSeries.generateEvents(targetEvent.getSubject(), newStartDateTime, newEndTime);

    List<EventInterface> updatedEventsInNewSeries = new ArrayList<>();
    for (EventInterface event : eventsInNewSeries) {
      EventInterface updatedEvent = this.eventEditor.editEvent(event, EventProperty.DESCRIPTION,
          targetEvent.getDescription());
      updatedEvent =
          this.eventEditor.editEvent(updatedEvent, EventProperty.LOCATION,
              targetEvent.getLocation());
      if (targetEvent.getStatus() != null) {
        updatedEvent = this.eventEditor.editEvent(updatedEvent, EventProperty.STATUS,
            targetEvent.getStatus().toString());
      }
      updatedEventsInNewSeries.add(updatedEvent);
    }

    this.eventManager.removeEvents(eventsToBeUpdated);
    this.eventManager.removeSeries(series);
    this.eventManager.attachEventsToNewSeries(newSeries, updatedEventsInNewSeries);
  }

  /**
   * Handle updating the end time a SingleEvent starting at a given date and time, along with
   * future events in the Series as well. This process involves replacing old events with updated
   * ones in the original Series. (Our design doesn't allow Series Event to span more than 1 day,
   * so updating end date is not supported.)
   *
   * @param series    series to be updated
   * @param startFrom start date and time for events in Series update
   * @param newValue  new end time
   * @throws UnsupportedOperationException if try to update end date
   */
  private void handleSeriesStartFromUpdateEventEndDateTimeHelper(SeriesInterface series,
                                                                 LocalDateTime startFrom,
                                                                 String newValue)
      throws UnsupportedOperationException {
    LocalDateTime newEndDateTime = LocalDateTime.parse(newValue);
    if (!startFrom.toLocalDate().equals(newEndDateTime.toLocalDate())) {
      throw new UnsupportedOperationException("A series event must not span more than one day");
    }
    List<EventInterface> eventsToBeUpdated =
        this.eventManager.filterEventsInSeriesStartingFrom(series, startFrom);
    updateSeriesEventEndHelper(newEndDateTime, eventsToBeUpdated);
  }

  /**
   * Help update end time for list of Series Events.
   *
   * @param newEndDateTime    updated event end time
   * @param eventsToBeUpdated list of Series Events
   */
  private void updateSeriesEventEndHelper(LocalDateTime newEndDateTime,
                                          List<EventInterface> eventsToBeUpdated) {
    List<EventInterface> updatedEvents = new ArrayList<>();
    for (EventInterface oldEvent : eventsToBeUpdated) {
      EventInterface newEvent = this.eventEditor.editEvent(oldEvent, EventProperty.END_TIME,
          newEndDateTime.toLocalTime().toString());
      updatedEvents.add(newEvent);
    }
    this.eventManager.updateSingleEventsWithList(eventsToBeUpdated, updatedEvents,
        EventProperty.END);
  }

  /**
   * Handle updating the property other than START and END of a SingleEvent starting at a given
   * date and time, along with future events in the Series as well. This process simply involves
   * replacing original events with updated ones in the same Series.
   *
   * @param series    series to be updated
   * @param property  property to be updated
   * @param startFrom start date and time for events in Series update
   * @param newValue  new property value
   */
  private void handleSeriesStartFromUpdateEventPropertyHelper(SeriesInterface series,
                                                              EventProperty property,
                                                              LocalDateTime startFrom,
                                                              String newValue) {
    List<EventInterface> eventsToBeUpdated =
        this.eventManager.filterEventsInSeriesStartingFrom(series, startFrom);
    this.eventManager.updateEventsProperty(eventsToBeUpdated, property, newValue, this.eventEditor);
  }

  /**
   * Handle updating the start date or time of a SingleEvent starting at a given date and time,
   * along all events in the Series as well. This process involves replacing all events in the
   * Series with updated ones. (Google Calendar doesn't allow update Start Date for all events in
   * the Series.)
   *
   * @param series   series to be updated
   * @param startAt  start date and time of the target event in the Series
   * @param newValue new start time
   * @throws UnsupportedOperationException if try to update start date
   */
  private void handleSeriesAllUpdateEventStartDateTimeHelper(SeriesInterface series,
                                                             LocalDateTime startAt,
                                                             String newValue)
      throws UnsupportedOperationException {
    LocalDateTime newStartDateTime = LocalDateTime.parse(newValue);
    if (!startAt.toLocalDate().equals(newStartDateTime.toLocalDate())) {
      throw new UnsupportedOperationException(
          "Google Calendar doesn't allow modify event start date for the entire series");
    }

    Duration shift = Duration.between(startAt, newStartDateTime);

    List<EventInterface> eventsToBeUpdated = this.eventManager.filterEventsInSeries(series);
    List<EventInterface> updatedEvents = new ArrayList<>();
    for (EventInterface oldEvent : eventsToBeUpdated) {
      LocalDateTime shiftedStart = oldEvent.getStartDateTime().plus(shift);
      LocalDateTime shiftedEnd = oldEvent.getEndDateTime().plus(shift);

      EventInterface newEvent =
          this.eventEditor.editEvent(oldEvent, EventProperty.START, shiftedStart.toString());
      newEvent = this.eventEditor.editEvent(newEvent, EventProperty.END, shiftedEnd.toString());
      updatedEvents.add(newEvent);
    }

    SeriesImpl seriesImpl = (SeriesImpl) series;
    SeriesInterface newSeries = seriesImpl.copy();

    this.eventManager.removeEvents(eventsToBeUpdated);
    this.eventManager.removeSeries(series);
    this.eventManager.attachEventsToNewSeries(newSeries, updatedEvents);
  }


  /**
   * Handle updating the end time a SingleEvent starting at a given date and time, along with
   * all events in the Series as well. This process involves replacing old events with updated
   * ones in the original Series. (Our design doesn't allow Series Event to span more than 1 day,
   * so updating end date is not supported.)
   *
   * @param series   series to be updated
   * @param startAt  start date and time of the target event in the Series
   * @param newValue new end time
   * @throws UnsupportedOperationException if try to update end date
   */
  private void handleSeriesAllUpdateEventEndDateTimeHelper(SeriesInterface series,
                                                           LocalDateTime startAt,
                                                           String newValue)
      throws UnsupportedOperationException {
    LocalDateTime newEndDateTime = LocalDateTime.parse(newValue);
    if (!startAt.toLocalDate().equals(newEndDateTime.toLocalDate())) {
      throw new UnsupportedOperationException("A series event must not span more than one day");
    }
    List<EventInterface> eventsToBeUpdated = this.eventManager.filterEventsInSeries(series);
    updateSeriesEventEndHelper(newEndDateTime, eventsToBeUpdated);
  }

  /**
   * Handle updating the property other than START and END of a SingleEvent starting at a given
   * date and time, along with future events in the Series as well. This process simply involves
   * replacing original events with updated ones in the same Series.
   *
   * @param series   series to be updated
   * @param property property to be updated
   * @param newValue new property value
   */
  private void handleSeriesAllUpdateEventPropertyHelper(SeriesInterface series,
                                                        EventProperty property,
                                                        String newValue) {
    List<EventInterface> eventsToBeUpdated = this.eventManager.filterEventsInSeries(series);
    this.eventManager.updateEventsProperty(eventsToBeUpdated, property, newValue, this.eventEditor);
  }

  /**
   * Help update the date of {@param events} to match recurring weekdays of {@param series} starting
   * from {@param startDate}.
   *
   * @param series    series to be attached
   * @param events    events to be shifted to match weekdays
   * @param startDate series start date
   * @return list of matched events
   */
  public List<EventInterface> matchSeriesEventsWeekdays(SeriesInterface series,
                                                        List<EventInterface> events,
                                                        LocalDate startDate) {
    events.sort(Comparator.comparing(EventInterface::getStartDateTime));
    Set<Weekday> weekdays = series.getWeekdays();
    List<EventInterface> updatedEvents = new ArrayList<>();

    LocalDate currentDate = startDate;

    for (EventInterface event : events) {
      LocalTime startTime = event.getStartDateTime().toLocalTime();
      LocalTime endTime = event.getEndDateTime().toLocalTime();

      while (!weekdays.contains(Weekday.valueOf(currentDate.getDayOfWeek().toString()))) {
        currentDate = currentDate.plusDays(1);
      }

      LocalDateTime newStart = LocalDateTime.of(currentDate, startTime);
      LocalDateTime newEnd = LocalDateTime.of(currentDate, endTime);

      EventInterface updatedEvent =
          eventEditor.editEvent(event, EventProperty.START, newStart.toString());
      updatedEvent = eventEditor.editEvent(updatedEvent, EventProperty.END, newEnd.toString());
      updatedEvents.add(updatedEvent);

      currentDate = currentDate.plusDays(1);
    }
    return updatedEvents;
  }
}
