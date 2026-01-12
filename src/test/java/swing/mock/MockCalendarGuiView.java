package swing.mock;

import java.awt.Color;
import java.time.YearMonth;
import java.util.List;
import model.event.EventReadOnlyInterface;
import swing.view.CalendarGuiViewInterface;
import swing.view.listeners.CalendarSelectListener;
import swing.view.listeners.EventActionListener;
import swing.view.listeners.ViewListener;

/**
 * A mock implementation of {@link CalendarGuiViewInterface} used for testing controller behavior
 * without relying on Swing UI components. Each method call appends a descriptive entry into the
 * provided log, enabling tests to verify that the correct view interactions occur.
 */
public class MockCalendarGuiView implements CalendarGuiViewInterface {
  private final StringBuilder log;

  /**
   * Constructs a MockCalendarGuiView that writes all invoked method names and parameters into the
   * given log.
   *
   * @param log the StringBuilder used to record method calls
   */
  public MockCalendarGuiView(StringBuilder log) {
    this.log = log;
  }

  @Override
  public void display() {
    log.append("display\n");
  }

  @Override
  public void addViewListener(ViewListener viewListener) {
    log.append("addViewListener\n");
  }

  @Override
  public void setCalendarSelectListener(CalendarSelectListener listener) {
    log.append("setCalendarSelectListener\n");
  }

  @Override
  public void setEventActionListener(EventActionListener listener) {
    log.append("setEventActionListener\n");
  }

  @Override
  public void renderCalendar(String calendarName, Color color) {
    log.append("renderCalendar ").append(calendarName).append("\n");
  }

  @Override
  public void renderEvent(EventReadOnlyInterface event, Color color) {
    log.append("renderEvent ").append(event.getSubject()).append('\n');
  }

  @Override
  public List<String> getSelectedCalendars() {
    log.append("getSelectedCalendars\n");
    return List.of();
  }

  @Override
  public void setSelectedCalendars(List<String> calendarNames) {
    log.append("setSelectedCalendars ").append(calendarNames).append('\n');
  }

  @Override
  public void goToToday() {
    log.append("goToToday\n");
  }

  @Override
  public void goToPreviousMonth() {
    log.append("goToPreviousMonth\n");
  }

  @Override
  public void goToNextMonth() {
    log.append("goToNextMonth\n");
  }

  @Override
  public void addCalendar(String calendarName, Color color) {
    log.append("addCalendar: ").append(calendarName).append("\n");
  }

  @Override
  public void updateCalendarName(String originalName, String newName) {
    log.append("updateCalendarName: ").append(originalName).append(" ").append(newName)
        .append("\n");
  }

  @Override
  public void clearAllCalenders() {
    log.append("clearAllCalenders\n");
  }

  @Override
  public void clearAllEvents() {
    log.append("clearAllEvents\n");
  }

  @Override
  public YearMonth getCurrentMonth() {
    log.append("getCurrentMonth\n");
    return YearMonth.now();
  }
}
