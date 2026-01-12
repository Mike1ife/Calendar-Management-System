package model.calendar;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import model.event.EventEditor;
import model.event.EventExistException;
import model.event.EventFactory;
import model.event.EventInterface;
import model.event.EventManager;
import model.event.EventNotFoundException;
import model.event.EventPrinter;
import model.event.EventProperty;
import model.event.EventReadOnlyInterface;
import model.event.SeriesInterface;
import model.event.SeriesOccurrence;
import model.event.SeriesUntilEnd;
import model.event.SeriesUpdater;

/**
 * This class represents general purposes of a Calendar, which contains a number of  single events
 * and series of events. It can conduct event creation, modification, presentation, and exportation.
 */
public class Calendar implements CalendarModelInterface {
  protected final EventManager eventManager;
  protected final EventEditor eventEditor;
  protected final SeriesUpdater seriesUpdater;

  /**
   * Create a Calendar with its own Manager and Editor of events.
   */
  public Calendar() {
    this.eventManager = new EventManager();
    this.eventEditor = new EventEditor();
    this.seriesUpdater = new SeriesUpdater(this.eventManager, this.eventEditor);
  }

  @Override
  public void createSingleEventWithTime(String subject, String startDateTime,
                                        String endDateTime)
      throws DateTimeParseException, EventExistException, UnsupportedOperationException {
    LocalDateTime eventStartDateTime = LocalDateTime.parse(startDateTime);
    LocalDateTime eventEndDateTime = LocalDateTime.parse(endDateTime);

    if (eventEndDateTime.isBefore(eventStartDateTime)) {
      throw new UnsupportedOperationException("Event ends before starting");
    }

    EventInterface event =
        EventFactory.createSingleEvent(subject, eventStartDateTime, eventEndDateTime);
    this.eventManager.addSingleEvent(event);
  }

  @Override
  public void createAllDaySingleEvent(String subject, String startDate)
      throws DateTimeParseException, EventExistException {
    EventInterface event = EventFactory.createSingleEvent(subject,
        LocalDateTime.of(LocalDate.parse(startDate), LocalTime.parse("08:00")),
        LocalDateTime.of(LocalDate.parse(startDate), LocalTime.parse("17:00"))
    );
    this.eventManager.addSingleEvent(event);
  }

  @Override
  public void createSeriesEventWithOccurrence(String subject, String startDateTime,
                                              String endDateTime, Set<Weekday> weekdays,
                                              int numberOfOccurrences)
      throws DateTimeParseException, EventExistException, UnsupportedOperationException {
    LocalDateTime eventStartDateTime = LocalDateTime.parse(startDateTime);
    LocalDateTime eventEndDateTime = LocalDateTime.parse(endDateTime);

    if (!eventStartDateTime.toLocalDate().equals(eventEndDateTime.toLocalDate())) {
      throw new UnsupportedOperationException("Series events cannot span more than one days");
    }

    SeriesOccurrence series = EventFactory.createSeriesWithOccurrence(weekdays,
        numberOfOccurrences);
    this.eventManager.addSeries(series, subject, eventStartDateTime,
        eventEndDateTime.toLocalTime());
  }

  @Override
  public void createAllDaySeriesEventWithOccurrence(String subject, String startDate,
                                                    Set<Weekday> weekdays, int numberOfOccurrences)
      throws DateTimeParseException, EventExistException {
    SeriesOccurrence series = EventFactory.createSeriesWithOccurrence(weekdays,
        numberOfOccurrences);
    this.eventManager.addSeries(series, subject, LocalDateTime.parse(startDate + "T08:00"),
        LocalTime.parse("17:00"));
  }

  @Override
  public void createSeriesEventWithEndDate(String subject, String startDateTime,
                                           String endDateTime, Set<Weekday> weekdays,
                                           String seriesEndDate)
      throws DateTimeParseException, EventExistException {
    LocalDateTime eventStartDateTime = LocalDateTime.parse(startDateTime);
    LocalDateTime eventEndDateTime = LocalDateTime.parse(endDateTime);

    if (!eventStartDateTime.toLocalDate().equals(eventEndDateTime.toLocalDate())) {
      throw new UnsupportedOperationException("Series events cannot span more than one days");
    }

    SeriesUntilEnd series = EventFactory.createSeriesWithEndDate(weekdays,
        LocalDate.parse(seriesEndDate));
    this.eventManager.addSeries(series, subject, eventStartDateTime,
        eventEndDateTime.toLocalTime());
  }

  @Override
  public void createAllDaySeriesEventWithEndDate(String subject, String startDate,
                                                 Set<Weekday> weekdays, String seriesEndDate)
      throws DateTimeParseException, EventExistException {
    SeriesUntilEnd series = EventFactory.createSeriesWithEndDate(weekdays,
        LocalDate.parse(seriesEndDate));
    this.eventManager.addSeries(series, subject, LocalDateTime.parse(startDate + "T08:00"),
        LocalTime.parse("17:00"));
  }

  @Override
  public void editSingleEvent(List<String> args)
      throws IllegalArgumentException, DateTimeParseException, EventNotFoundException,
      UnsupportedOperationException, EventExistException {
    if (args.size() != 5) {
      throw new IllegalArgumentException("Invalid number of parameters");
    }

    String subject = args.get(0);
    String property = args.get(1);
    String startDateTime = args.get(2);
    String endDateTime = args.get(3);
    String newValue = args.get(4);

    EventProperty eventProperty = EventProperty.valueOf(property.toUpperCase());

    EventInterface oldEvent =
        this.eventManager.findEvent(subject, LocalDateTime.parse(startDateTime),
            LocalDateTime.parse(endDateTime));

    EventInterface newEvent = this.eventEditor.editEvent(oldEvent, eventProperty, newValue);

    if (eventProperty == EventProperty.START) {
      Duration eventDuration = Duration.between(
          oldEvent.getStartDateTime(),
          oldEvent.getEndDateTime()
      );
      LocalDateTime newStartDateTime = newEvent.getStartDateTime();
      LocalDateTime newEndDateTime = newStartDateTime.plus(eventDuration);

      newEvent = this.eventEditor.editEvent(newEvent, EventProperty.END, newEndDateTime.toString());
    } else if (eventProperty == EventProperty.END) {
      if (newEvent.getEndDateTime().isBefore(newEvent.getStartDateTime())) {
        throw new UnsupportedOperationException("Event end time cannot be before start time!");
      }
    }

    this.eventManager.updateSingleEvent(oldEvent, newEvent, eventProperty);
  }

  @Override
  public void editEventStartFrom(String subject, String property, String startDateTime,
                                 String newValue)
      throws IllegalArgumentException, DateTimeParseException, EventNotFoundException,
      EventExistException, UnsupportedOperationException {
    EventProperty eventProperty = EventProperty.valueOf(property.toUpperCase());

    LocalDateTime startFrom = LocalDateTime.parse(startDateTime);
    List<SeriesInterface> seriesList =
        editSingleEventsAndGetSeriesStartFromHelper(subject, eventProperty, startFrom, newValue);

    for (SeriesInterface series : seriesList) {
      this.seriesUpdater.updateSeriesStartFrom(series, eventProperty, startFrom, newValue);
    }
  }

  @Override
  public void editSeriesStartFrom(String subject, String property, String startDateTime,
                                  String newValue)
      throws IllegalArgumentException, DateTimeParseException, EventNotFoundException,
      EventExistException, UnsupportedOperationException {
    EventProperty eventProperty = EventProperty.valueOf(property.toUpperCase());
    LocalDateTime startingFrom = LocalDateTime.parse(startDateTime);
    List<SeriesInterface> seriesList =
        editSingleEventsAndGetSeriesStartFromHelper(subject, eventProperty, startingFrom, newValue);

    for (SeriesInterface series : seriesList) {
      this.seriesUpdater.updateSeriesAll(series, eventProperty, startingFrom, newValue);
    }
  }

  @Override
  public String getEventsOnDate(String date) throws DateTimeParseException {
    LocalDate targetDate = LocalDate.parse(date);

    List<EventInterface> eventsOnDate = this.eventManager.filter(event -> {
      LocalDate start = event.getStartDateTime().toLocalDate();
      LocalDate end = event.getEndDateTime().toLocalDate();
      return !targetDate.isBefore(start) && !targetDate.isAfter(end);
    });

    if (eventsOnDate.isEmpty()) {
      return "No events scheduled on this date";
    }

    return EventPrinter.print(eventsOnDate);
  }

  @Override
  public String getEventsInRange(String startDateTime, String endDateTime)
      throws DateTimeParseException {
    LocalDateTime start = LocalDateTime.parse(startDateTime);
    LocalDateTime end = LocalDateTime.parse(endDateTime);

    List<EventInterface> eventInRange = this.eventManager.filter(event ->
        !event.getStartDateTime().isAfter(end)
            && !event.getEndDateTime().isBefore(start));

    if (eventInRange.isEmpty()) {
      return "No events scheduled between this range";
    }

    return EventPrinter.print(eventInRange);
  }

  @Override
  public CalendarStatus isBusy(String dateTime) throws DateTimeParseException {
    LocalDateTime eventDateTime = LocalDateTime.parse(dateTime);
    List<EventInterface> eventAtDateTime = this.eventManager.filter(event ->
        !event.getStartDateTime().isAfter(eventDateTime)
            && !event.getEndDateTime().isBefore(eventDateTime));
    if (eventAtDateTime.isEmpty()) {
      return CalendarStatus.AVAILABLE;
    } else {
      return CalendarStatus.BUSY;
    }
  }

  @Override
  public List<EventReadOnlyInterface> getAllEventsReadOnly() {
    return this.eventManager.getAllEventsReadOnly();
  }

  @Override
  public boolean isSeriesEvent(EventReadOnlyInterface event) {
    return this.eventManager.isSeriesEvent(Objects.requireNonNull(event));
  }

  @Override
  public Set<Weekday> getSeriesWeekdays(EventReadOnlyInterface event) {
    return this.eventManager.getSeriesWeekdays(Objects.requireNonNull(event));
  }

  @Override
  public LocalDate getSeriesUntilEnd(EventReadOnlyInterface event) {
    return this.eventManager.getSeriesUntilEnd(Objects.requireNonNull(event));
  }

  @Override
  public Integer getSeriesOccurrence(EventReadOnlyInterface event) {
    return this.eventManager.getSeriesOccurrence(Objects.requireNonNull(event));
  }

  /**
   * Update SingleEvents starting at given date and time. Then return Series starting at that time.
   *
   * @param subject      event subject
   * @param property     property to be updated
   * @param startingFrom given date and time
   * @param newValue     new property value
   * @return list of Series starting at that time
   * @throws EventNotFoundException if no events and no series starting from given date and time
   * @throws EventExistException    if event(s) has already existed
   */
  private List<SeriesInterface> editSingleEventsAndGetSeriesStartFromHelper(
      String subject, EventProperty property, LocalDateTime startingFrom, String newValue)
      throws EventNotFoundException, EventExistException {
    List<EventInterface> eventsStartAt = this.eventManager.filter(event ->
        event.getSubject().equals(subject)
            && event.getStartDateTime().equals(startingFrom));

    List<EventInterface> eventsToBeUpdated =
        this.eventManager.filterSingleEventsFromList(eventsStartAt);
    this.eventManager.updateEventsProperty(eventsToBeUpdated, property, newValue, this.eventEditor);

    List<SeriesInterface> seriesList = this.eventManager.filterSeriesListFromList(eventsStartAt);

    if (eventsStartAt.isEmpty()) {
      throw new EventNotFoundException("Events starting from not found!");
    }

    return List.copyOf(seriesList);
  }
}