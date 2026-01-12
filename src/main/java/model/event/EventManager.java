package model.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import model.calendar.Weekday;

/**
 * This class represents a manager for events and series in Calendar. It contains Mapping from
 * event identifier to SingleEvent, series id to Series, and event identifier to series id to track
 * Series some SingleEvents associated with.
 */
public class EventManager {
  private final Set<EventProperty> eventIdentifier;
  private final Set<EventInterface> eventSet;
  private final Map<String, SeriesInterface> seriesMap;
  private final Map<EventInterface, String> eventToSeriesMap;

  /**
   * Create an EventManager.
   */
  public EventManager() {
    this.eventIdentifier = Set.of(EventProperty.SUBJECT, EventProperty.START, EventProperty.END);
    this.eventSet = new HashSet<>();
    this.seriesMap = new HashMap<>();
    this.eventToSeriesMap = new HashMap<>();
  }

  /**
   * Add a new SingleEvent to Calendar.
   *
   * @param event event to be added
   * @throws EventExistException if event has already existed
   */
  public void addSingleEvent(EventInterface event) throws EventExistException {
    if (this.eventSet.contains(event)) {
      throw new EventExistException("Event already exists!");
    }
    this.eventSet.add(event);
  }

  /**
   * Add list of SingleEvents to Calendar.
   *
   * @param events list of events
   * @throws EventExistException if event(s) has already existed
   */
  public void addSingleEventFromList(List<EventInterface> events) throws EventExistException {
    for (EventInterface event : events) {
      if (this.eventSet.contains(event)) {
        throw new EventExistException("Event already exists!");
      }
    }

    this.eventSet.addAll(events);
  }

  /**
   * Add a new Series to Calendar, and generate a series of SingleEvents associated with it.
   *
   * @param series        new series
   * @param subject       event subject
   * @param startDateTime event start time
   * @param endTime       event start date
   * @throws EventExistException if generated events have already existed
   */
  public void addSeries(SeriesInterface series, String subject, LocalDateTime startDateTime,
                        LocalTime endTime)
      throws EventExistException {
    List<EventInterface> seriesEvents = series.generateEvents(subject, startDateTime, endTime);

    for (EventInterface event : seriesEvents) {
      if (this.eventSet.contains(event)) {
        throw new EventExistException("Event already exists!");
      }
    }

    for (EventInterface event : seriesEvents) {
      this.eventSet.add(event);
      this.eventToSeriesMap.put(event, series.getSeriesId());
    }
    this.seriesMap.put(series.getSeriesId(), series);
  }

  /**
   * Attach a list of SingleEvents to a new Series. These SingleEvents can be old and new ones.
   *
   * @param newSeries new series
   * @param events    list of events to be attached
   */
  public void attachEventsToNewSeries(SeriesInterface newSeries, List<EventInterface> events) {
    this.seriesMap.put(newSeries.getSeriesId(), newSeries);

    for (EventInterface event : events) {
      this.eventSet.add(event);
      this.eventToSeriesMap.put(event, newSeries.getSeriesId());
    }
  }

  /**
   * Remove all events in {@code events} from Calendar.
   *
   * @param events list of events to be removed
   */
  public void removeEvents(List<EventInterface> events) {
    for (EventInterface event : events) {
      this.eventSet.remove(event);
      this.eventToSeriesMap.remove(event);
    }
  }

  /**
   * Remove a Series and all events associated with it.
   *
   * @param series series to be removed
   */
  public void removeSeries(SeriesInterface series) {
    this.seriesMap.remove(series.getSeriesId());
    this.eventToSeriesMap.entrySet()
        .removeIf(entry -> entry.getValue().equals(series.getSeriesId()));
  }

  /**
   * Update property of a SingleEvent by replacing it with an updated one. If old event is in a
   * series, we only attach the new one to the series if we are not updating start time. (Google
   * Calendar allows modifying start date of event in series.)
   *
   * @param oldEvent original event
   * @param newEvent updated event
   * @param property property benn updated
   * @throws EventExistException           if we update event identifiers and updated event has
   *                                       existed
   * @throws UnsupportedOperationException if new series event spans more than one day
   */
  public void updateSingleEvent(EventInterface oldEvent, EventInterface newEvent,
                                EventProperty property)
      throws EventExistException, UnsupportedOperationException {
    if (this.eventIdentifier.contains(property)) {
      if (this.eventSet.contains(newEvent)) {
        throw new EventExistException("Event already exists!");
      }
    }

    if (this.eventToSeriesMap.containsKey(oldEvent)
        && !newEvent.getStartDateTime().toLocalDate()
        .equals(newEvent.getEndDateTime().toLocalDate())) {
      throw new UnsupportedOperationException("Series event cannot span more than one day");
    }

    updateSingleEventHelper(oldEvent, newEvent);
  }

  /**
   * Update properties of multiple SingleEvents at once.
   * First ensures that none of the new events already exist (when identifiers change).
   * If all checks pass, replaces old events with new ones.
   *
   * @param oldEvents list of original events
   * @param newEvents list of updated events
   * @param property  property being updated
   * @throws EventExistException if any new event already exists
   */
  public void updateSingleEventsWithList(List<EventInterface> oldEvents,
                                         List<EventInterface> newEvents,
                                         EventProperty property) throws EventExistException {
    if (this.eventIdentifier.contains(property)) {
      for (EventInterface newEvent : newEvents) {
        if (this.eventSet.contains(newEvent)) {
          throw new EventExistException("Event already exists: " + newEvent);
        }
      }
    }

    for (int i = 0; i < oldEvents.size(); i++) {
      EventInterface oldEvent = oldEvents.get(i);
      EventInterface newEvent = newEvents.get(i);

      updateSingleEventHelper(oldEvent, newEvent);
    }
  }

  /**
   * Replace all original events with list of new events.
   *
   * @param originalEvents list of original events
   * @param updatedEvents  list of updated events
   */
  public void updateAllEventsWithList(List<EventInterface> originalEvents,
                                      List<EventInterface> updatedEvents) {
    for (int i = 0; i < originalEvents.size(); i++) {
      EventInterface oldEvent = originalEvents.get(i);
      EventInterface newEvent = updatedEvents.get(i);

      this.eventSet.remove(oldEvent);

      if (this.eventToSeriesMap.containsKey(oldEvent)) {
        String seriesId = this.eventToSeriesMap.remove(oldEvent);
        this.eventToSeriesMap.put(newEvent, seriesId);
      }

      this.eventSet.add(newEvent);
    }
  }

  /**
   * Help replace original event with an updated one.
   *
   * @param oldEvent original event
   * @param newEvent updated event
   */
  private void updateSingleEventHelper(EventInterface oldEvent, EventInterface newEvent) {
    this.eventSet.remove(oldEvent);
    this.eventSet.add(newEvent);

    if (this.eventToSeriesMap.containsKey(oldEvent)) {
      String seriesId = this.eventToSeriesMap.remove(oldEvent);
      if (oldEvent.getStartDateTime().toLocalTime()
          .equals(newEvent.getStartDateTime().toLocalTime())) {
        this.eventToSeriesMap.put(newEvent, seriesId);
      } else {
        this.seriesMap.get(seriesId).decrementNumberOfOccurrences();
      }
    }
  }

  /**
   * Filter all events in Calendar with a given predicate.
   *
   * @param predicate filter predicate
   * @return filtered events
   */
  public List<EventInterface> filter(Predicate<EventInterface> predicate) {
    List<EventInterface> result = new ArrayList<>();
    for (EventInterface event : this.eventSet) {
      if (predicate.test(event)) {
        result.add(event);
      }
    }
    return result;
  }

  /**
   * Filter SingleEvents of a Series in Calendar starting from given date and time.
   *
   * @param series        series events belong to
   * @param startDateTime starting date and time
   * @return list of SeriesEvents
   */
  public List<EventInterface> filterEventsInSeriesStartingFrom(SeriesInterface series,
                                                               LocalDateTime startDateTime) {
    return filter(event -> this.eventToSeriesMap.containsKey(event)
        && this.eventToSeriesMap.get(event).equals(series.getSeriesId())
        && !event.getStartDateTime().isBefore(startDateTime)
    );
  }

  /**
   * Filter SingleEvents of a Series in Calendar before given date and time.
   *
   * @param series   series events belong to
   * @param dateTime date and time we need events to happen before
   * @return list of SeriesEvents
   */
  public List<EventInterface> filterEventsInSeriesBefore(SeriesInterface series,
                                                         LocalDateTime dateTime) {
    return filter(event -> this.eventToSeriesMap.containsKey(event)
        && this.eventToSeriesMap.get(event).equals(series.getSeriesId())
        && event.getStartDateTime().isBefore(dateTime)
    );
  }

  /**
   * Filter a SingleEvent of a Series in Calendar starting at given date and time.
   *
   * @param series   series events belong to
   * @param dateTime event start date and time
   * @return event in {@code series} starting at {@code dateTIme}
   */
  public EventInterface filterEventsInSeriesStartAt(SeriesInterface series,
                                                    LocalDateTime dateTime) {
    List<EventInterface> eventsInSeries = filterEventsInSeries(series);
    EventInterface result = null;
    for (EventInterface event : eventsInSeries) {
      if (event.getStartDateTime().equals(dateTime)) {
        result = event;
        break;
      }
    }
    return result;
  }

  /**
   * Filter all SingleEvents of a Series in Calendar.
   *
   * @param series series events belong to
   * @return list of SeriesEvents
   */
  public List<EventInterface> filterEventsInSeries(SeriesInterface series) {
    return filter(event -> this.eventToSeriesMap.containsKey(event)
        && this.eventToSeriesMap.get(event).equals(series.getSeriesId())
    );
  }

  /**
   * Filter SingleEvents from a list of events.
   *
   * @param events list of events
   * @return list of SingleEvents
   */
  public List<EventInterface> filterSingleEventsFromList(List<EventInterface> events) {
    List<EventInterface> result = new ArrayList<>();
    for (EventInterface event : events) {
      if (!this.eventToSeriesMap.containsKey(event)) {
        result.add(event);
      }
    }
    return result;
  }

  /**
   * Filter Series from a list of events.
   *
   * @param events list of events
   * @return list of Series
   */
  public List<SeriesInterface> filterSeriesListFromList(List<EventInterface> events) {
    Set<SeriesInterface> seriesList = new HashSet<>();
    for (EventInterface event : events) {
      if (this.eventToSeriesMap.containsKey(event)) {
        String seriesId = this.eventToSeriesMap.get(event);
        seriesList.add(this.seriesMap.get(seriesId));
      }
    }
    return new ArrayList<>(seriesList);
  }

  /**
   * Filter SingleEvents (not in Series) between {@param start} and {@param end}.
   *
   * @param start interval start date
   * @param end   interval end date
   * @return list of SingleEvents
   */
  public List<EventInterface> filterSingleEventsBetween(LocalDate start, LocalDate end) {
    return filter(event ->
        !event.getStartDateTime().toLocalDate().isAfter(end)
            && !event.getEndDateTime().toLocalDate().isBefore(start)
            && !this.eventToSeriesMap.containsKey(event));
  }

  /**
   * Filter Series Events between {@param start} and {@param end} and return a map mapping series to
   * its event list.
   *
   * @param start interval start date
   * @param end   interval end date
   * @return map of series to list of series events
   */
  public Map<SeriesInterface, List<EventInterface>> filterSeriesEventsBetween(LocalDate start,
                                                                              LocalDate end) {
    Map<SeriesInterface, List<EventInterface>> result = new HashMap<>();
    for (EventInterface event : this.eventSet) {
      if (!event.getStartDateTime().toLocalDate().isAfter(end)
          && !event.getEndDateTime().toLocalDate().isBefore(start)
          && this.eventToSeriesMap.containsKey(event)) {
        String seriesId = this.eventToSeriesMap.get(event);
        SeriesInterface series = this.seriesMap.get(seriesId);
        result.putIfAbsent(series, new ArrayList<>());
        result.get(series).add(event);
      }
    }
    return result;
  }

  /**
   * Find a SingleEvent with subject, start date and time, end date and time.
   *
   * @param subject       event subject
   * @param startDateTime event start date and time
   * @param endDateTime   event end date and time
   * @return target event
   * @throws EventNotFoundException if we cannot find such an event
   */
  public EventInterface findEvent(String subject, LocalDateTime startDateTime,
                                  LocalDateTime endDateTime) throws EventNotFoundException {
    EventInterface result = null;
    for (EventInterface event : this.eventSet) {
      if (event.getSubject().equals(subject)) {
        if (event.getStartDateTime().equals(startDateTime)) {
          if (event.getEndDateTime().equals(endDateTime)) {
            result = event;
            break;
          }
        }
      }
    }
    if (result == null) {
      throw new EventNotFoundException("Event not found!");
    }
    return result;
  }

  /**
   * Export all SingleEvents.
   *
   * @return list of events
   */
  public List<EventInterface> getAllEvents() {
    return new ArrayList<>(this.eventSet);
  }

  /**
   * Export all SingleEvents Readonly.
   *
   * @return list of readonly events
   */
  public List<EventReadOnlyInterface> getAllEventsReadOnly() {
    return new ArrayList<>(this.eventSet);
  }

  /**
   * Update the property of a list of events with {@param newValue}.
   *
   * @param events   list events to be updated
   * @param property property to be updated
   * @param newValue new property value
   * @param editor   event editor to update old events
   */
  public void updateEventsProperty(List<EventInterface> events, EventProperty property,
                                   String newValue, EventEditor editor) {
    List<EventInterface> updated = new ArrayList<>();
    for (EventInterface oldEvent : events) {
      updated.add(editor.editEvent(oldEvent, property, newValue));
    }
    this.updateSingleEventsWithList(events, updated, property);
  }

  /**
   * Check whether the given event is part of a series.
   *
   * @param event event to be checked
   * @return true if event belongs to a series, false otherwise
   */
  public boolean isSeriesEvent(EventReadOnlyInterface event) {
    return this.eventToSeriesMap.containsKey((EventInterface) event);
  }

  /**
   * Get the set of weekdays on which the series associated with the given event repeats.
   *
   * @param event event belonging to a series
   * @return set of weekdays
   */
  public Set<Weekday> getSeriesWeekdays(EventReadOnlyInterface event) {
    String seriesId = this.eventToSeriesMap.get((EventInterface) event);
    return Set.copyOf(this.seriesMap.get(seriesId).getWeekdays());
  }

  /**
   * Get the end date of the series associated with the given event.
   *
   * @param event event belonging to a series
   * @return series end date
   */
  public LocalDate getSeriesUntilEnd(EventReadOnlyInterface event) {
    String seriesId = this.eventToSeriesMap.get((EventInterface) event);
    return this.seriesMap.get(seriesId).getEndDate();
  }

  /**
   * Get the number of occurrences of the series associated with the given event.
   *
   * @param event event belonging to a series
   * @return number of occurrences, or null if series is end-date based
   */
  public Integer getSeriesOccurrence(EventReadOnlyInterface event) {
    String seriesId = this.eventToSeriesMap.get((EventInterface) event);
    return this.seriesMap.get(seriesId).getNumberOfOccurrences();
  }
}
