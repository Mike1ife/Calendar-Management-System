package controller.mock;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import model.calendar.CalendarModelInterface;
import model.calendar.CalendarStatus;
import model.calendar.Weekday;
import model.event.EventReadOnlyInterface;

/**
 * Mock implementation of ICalendarModel for testing.
 * Logs all method calls instead of actually executing them.
 */
public class MockCalendarModel implements CalendarModelInterface {
  private final StringBuilder log;

  /**
   * Create a MockCalendarModel.
   *
   * @param log StringBuilder to log method calls
   */
  public MockCalendarModel(StringBuilder log) {
    this.log = log;
  }

  @Override
  public void createSingleEventWithTime(String subject, String startDateTime, String endDateTime) {
    log.append("createSingleEventWithTime: ")
        .append(subject).append(", ")
        .append(startDateTime).append(", ")
        .append(endDateTime).append("\n");
  }

  @Override
  public void createAllDaySingleEvent(String subject, String date) {
    log.append("createAllDaySingleEvent: ")
        .append(subject).append(", ")
        .append(date).append("\n");
  }

  @Override
  public void createSeriesEventWithOccurrence(String subject, String startDateTime,
                                              String endDateTime, Set<Weekday> weekdays,
                                              int occurrences) {
    log.append("createSeriesEventWithOccurrence: ")
        .append(subject).append(", ")
        .append(startDateTime).append(", ")
        .append(endDateTime).append(", ")
        .append(weekdays).append(", ")
        .append(occurrences).append("\n");
  }

  @Override
  public void createSeriesEventWithEndDate(String subject, String startDateTime,
                                           String endDateTime, Set<Weekday> weekdays,
                                           String untilDate) {
    log.append("createSeriesEventWithEndDate: ")
        .append(subject).append(", ")
        .append(startDateTime).append(", ")
        .append(endDateTime).append(", ")
        .append(weekdays).append(", ")
        .append(untilDate).append("\n");
  }

  @Override
  public void createAllDaySeriesEventWithOccurrence(String subject, String date,
                                                    Set<Weekday> weekdays, int occurrences) {
    log.append("createAllDaySeriesEventWithOccurrence: ")
        .append(subject).append(", ")
        .append(date).append(", ")
        .append(weekdays).append(", ")
        .append(occurrences).append("\n");
  }

  @Override
  public void createAllDaySeriesEventWithEndDate(String subject, String date,
                                                 Set<Weekday> weekdays, String untilDate) {
    log.append("createAllDaySeriesEventWithEndDate: ")
        .append(subject).append(", ")
        .append(date).append(", ")
        .append(weekdays).append(", ")
        .append(untilDate).append("\n");
  }

  @Override
  public void editSingleEvent(List<String> args) {
    String subject = args.get(0);
    String property = args.get(1);
    String startDateTime = args.get(2);
    String endDateTime = args.get(3);
    String newValue = args.get(4);
    log.append("editSingleEvent: ")
        .append(subject).append(", ")
        .append(property).append(", ")
        .append(startDateTime).append(", ")
        .append(endDateTime).append(", ")
        .append(newValue).append("\n");
  }

  @Override
  public void editEventStartFrom(String subject, String property, String startDateTime,
                                 String newValue) {
    log.append("editEventStartFrom: ")
        .append(subject).append(", ")
        .append(property).append(", ")
        .append(startDateTime).append(", ")
        .append(newValue).append("\n");
  }

  @Override
  public void editSeriesStartFrom(String subject, String property, String startDateTime,
                                  String newValue) {
    log.append("editSeriesStartFrom: ")
        .append(subject).append(", ")
        .append(property).append(", ")
        .append(startDateTime).append(", ")
        .append(newValue).append("\n");
  }

  @Override
  public String getEventsOnDate(String date) {
    log.append("getEventsOnDate: ").append(date).append("\n");
    return "Events on " + date;
  }

  @Override
  public String getEventsInRange(String startDateTime, String endDateTime) {
    log.append("getEventsInRange: ")
        .append(startDateTime).append(", ")
        .append(endDateTime).append("\n");
    return "Events from " + startDateTime + " to " + endDateTime;
  }

  @Override
  public CalendarStatus isBusy(String startDateTime) {
    log.append("isBusy: ").append(startDateTime).append("\n");
    return CalendarStatus.BUSY;
  }

  @Override
  public List<EventReadOnlyInterface> getAllEventsReadOnly() {
    log.append("getAllEvents\n");
    return List.of();
  }

  @Override
  public boolean isSeriesEvent(EventReadOnlyInterface event) {
    log.append("Check").append(event.getSubject()).append("is a series or not");
    return false;
  }

  @Override
  public Set<Weekday> getSeriesWeekdays(EventReadOnlyInterface event) {
    log.append("getSeriesWeekdays of series of").append(event.getSubject());
    return Set.of();
  }

  @Override
  public LocalDate getSeriesUntilEnd(EventReadOnlyInterface event) {
    log.append("getSeriesUntilEnd of series of").append(event.getSubject());
    return null;
  }

  @Override
  public Integer getSeriesOccurrence(EventReadOnlyInterface event) {
    log.append("getSeriesOccurrence of series of").append(event.getSubject());
    return 0;
  }
}