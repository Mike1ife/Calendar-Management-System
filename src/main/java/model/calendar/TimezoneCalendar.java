package model.calendar;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.event.EventExistException;
import model.event.EventFactory;
import model.event.EventInterface;
import model.event.EventNotFoundException;
import model.event.EventProperty;
import model.event.SeriesImpl;
import model.event.SeriesInterface;

/**
 * This class represents general purposes of a Calendar, which contains a number of  single events
 * and series of events. It can conduct event creation, modification, presentation, and exportation.
 */
class TimezoneCalendar extends Calendar implements TimezoneCalendarModelInterface {
  /**
   * Create a Calendar with its own Manager and Editor of events.
   */
  public TimezoneCalendar() {
    super();
  }

  @Override
  public List<EventInterface> getShiftedEvent(String subject, LocalDateTime start,
                                              LocalDateTime target)
      throws EventNotFoundException {
    List<EventInterface> eventsToBeUpdated = this.eventManager.filter(
        event -> event.getSubject().equals(subject)
            && event.getStartDateTime().equals(start));
    if (eventsToBeUpdated.isEmpty()) {
      throw new EventNotFoundException("Event not found");
    }

    List<EventInterface> result = new ArrayList<>();
    for (EventInterface event : eventsToBeUpdated) {
      Duration duration = Duration.between(event.getStartDateTime(), event.getEndDateTime());
      LocalDateTime targetEnd = target.plus(duration);
      EventInterface shiftedEvent;
      shiftedEvent = this.eventEditor.editEvent(event, EventProperty.START, target.toString());
      shiftedEvent =
          this.eventEditor.editEvent(shiftedEvent, EventProperty.END, targetEnd.toString());
      result.add(shiftedEvent);
    }
    return result;
  }

  @Override
  public void addSingleEventFromList(List<EventInterface> events) throws EventExistException {
    this.eventManager.addSingleEventFromList(events);
  }

  @Override
  public void addSeriesEventsFromMap(Map<SeriesInterface, List<EventInterface>> seriesEventsBetween)
      throws EventExistException {
    List<EventInterface> eventsToBeAdded = new ArrayList<>();
    for (List<EventInterface> events : seriesEventsBetween.values()) {
      eventsToBeAdded.addAll(events);
    }
    this.eventManager.addSingleEventFromList(eventsToBeAdded);

    for (SeriesInterface series : seriesEventsBetween.keySet()) {
      List<EventInterface> events = seriesEventsBetween.get(series);
      this.eventManager.attachEventsToNewSeries(series, events);
    }
  }

  @Override
  public void shiftTimeZone(ZoneId oldTimeZoneId, ZoneId newTimeZoneId) {
    List<EventInterface> eventsToBeUpdated = this.eventManager.getAllEvents();
    List<EventInterface> updatedEvents = new ArrayList<>();
    for (EventInterface event : eventsToBeUpdated) {
      ZonedDateTime oldStart = event.getStartDateTime().atZone(oldTimeZoneId);
      ZonedDateTime oldEnd = event.getEndDateTime().atZone(oldTimeZoneId);

      LocalDateTime newStart = oldStart.withZoneSameInstant(newTimeZoneId).toLocalDateTime();
      LocalDateTime newEnd = oldEnd.withZoneSameInstant(newTimeZoneId).toLocalDateTime();

      EventInterface shiftedEvent;
      shiftedEvent = this.eventEditor.editEvent(event, EventProperty.START, newStart.toString());
      shiftedEvent = this.eventEditor.editEvent(shiftedEvent, EventProperty.END, newEnd.toString());
      updatedEvents.add(shiftedEvent);
    }
    this.eventManager.updateAllEventsWithList(eventsToBeUpdated, updatedEvents);
  }

  @Override
  public List<EventInterface> getShiftedEventsOnDate(LocalDate original, LocalDate target,
                                                     ZoneId oldTimeZoneId, ZoneId newTimeZoneId)
      throws EventNotFoundException {
    List<EventInterface> eventsOnDate = this.eventManager.filter(event -> {
      LocalDate start = event.getStartDateTime().toLocalDate();
      LocalDate end = event.getEndDateTime().toLocalDate();
      return !original.isBefore(start) && !original.isAfter(end);
    });

    if (eventsOnDate.isEmpty()) {
      throw new EventNotFoundException("Event not found");
    }

    return shiftEventsTimezoneHelper(
        Duration.between(original.atStartOfDay(), target.atStartOfDay()), oldTimeZoneId,
        newTimeZoneId, false, eventsOnDate);
  }

  @Override
  public List<EventInterface> getShiftedSingleEventsBetween(LocalDate intervalStart,
                                                            LocalDate intervalEnd,
                                                            LocalDate targetIntervalStart,
                                                            ZoneId oldTimeZoneId,
                                                            ZoneId newTimeZoneId) {
    List<EventInterface> singleEventsBetween =
        this.eventManager.filterSingleEventsBetween(intervalStart, intervalEnd);

    return shiftEventsTimezoneHelper(
        Duration.between(intervalStart.atStartOfDay(), targetIntervalStart.atStartOfDay()),
        oldTimeZoneId, newTimeZoneId, false, singleEventsBetween);
  }

  @Override
  public Map<SeriesInterface, List<EventInterface>> getShiftedSeriesEventsBetween(
      LocalDate intervalStart,
      LocalDate intervalEnd,
      LocalDate targetIntervalStart,
      ZoneId oldTimeZoneId,
      ZoneId newTimeZoneId) {
    Map<SeriesInterface, List<EventInterface>> seriesEventsBetween =
        this.eventManager.filterSeriesEventsBetween(intervalStart, intervalEnd);

    LocalDate targetIntervalEnd = targetIntervalStart.plusDays(
        ChronoUnit.DAYS.between(intervalStart, targetIntervalStart)
    );

    Map<SeriesInterface, List<EventInterface>> result = new HashMap<>();
    for (SeriesInterface series : seriesEventsBetween.keySet()) {
      List<EventInterface> shiftedEvents = shiftEventsTimezoneHelper(
          Duration.between(intervalStart.atStartOfDay(), targetIntervalStart.atStartOfDay()),
          oldTimeZoneId, newTimeZoneId, true, seriesEventsBetween.get(series)
      );

      SeriesInterface newSeries;
      SeriesImpl seriesImpl = (SeriesImpl) series;
      if (seriesImpl.getEndDate() == null) {
        newSeries =
            EventFactory.createSeriesWithOccurrence(series.getWeekdays(), shiftedEvents.size());
      } else {
        newSeries = EventFactory.createSeriesWithEndDate(series.getWeekdays(), targetIntervalEnd);
      }

      result.put(newSeries,
          this.seriesUpdater.matchSeriesEventsWeekdays(series, shiftedEvents, targetIntervalStart))
      ;
    }
    return result;
  }

  /**
   * Help shift a list of events time by {@param shift} and change timezone from
   * {@param oldTimeZoneId} to {@param newTimeZoneId}.
   *
   * @param shift         time shifted
   * @param oldTimeZoneId original timezone
   * @param newTimeZoneId target timezone
   * @param events        events to be shifted
   * @return shifted events
   */
  private List<EventInterface> shiftEventsTimezoneHelper(Duration shift, ZoneId oldTimeZoneId,
                                                         ZoneId newTimeZoneId, boolean isSeries,
                                                         List<EventInterface> events)
      throws UnsupportedOperationException {
    List<EventInterface> result = new ArrayList<>();
    for (EventInterface event : events) {
      ZonedDateTime oldStart = event.getStartDateTime().atZone(oldTimeZoneId);
      ZonedDateTime oldEnd = event.getEndDateTime().atZone(oldTimeZoneId);

      LocalDateTime newStart =
          oldStart.withZoneSameInstant(newTimeZoneId).toLocalDateTime().plus(shift);
      LocalDateTime newEnd =
          oldEnd.withZoneSameInstant(newTimeZoneId).toLocalDateTime().plus(shift);

      if (isSeries && !newStart.toLocalDate().equals(newEnd.toLocalDate())) {
        throw new UnsupportedOperationException("Series events cannot span more than one day");
      }

      EventInterface shiftedEvent;
      shiftedEvent = this.eventEditor.editEvent(event, EventProperty.START, newStart.toString());
      shiftedEvent = this.eventEditor.editEvent(shiftedEvent, EventProperty.END, newEnd.toString());
      result.add(shiftedEvent);
    }
    return result;
  }
}