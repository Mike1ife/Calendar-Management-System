package swing.controller;

import controller.CalendarControllerInterface;
import java.awt.Color;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.JFrame;
import model.calendar.CalendarManagerInterface;
import model.calendar.CalendarModelInterface;
import model.event.EventReadOnlyInterface;
import swing.controller.callbacks.CreateCalendarCallback;
import swing.controller.callbacks.CreateEventCallback;
import swing.controller.callbacks.EditCalendarCallback;
import swing.controller.callbacks.EditEventCallback;
import swing.controller.callbacks.EditEventOptionCallback;
import swing.controller.callbacks.EditSeriesCallback;
import swing.view.CalendarGuiViewInterface;
import swing.view.dialogs.calendar.CreateCalendarDialog;
import swing.view.dialogs.calendar.EditCalendarDialog;
import swing.view.dialogs.event.CreateEventDialog;
import swing.view.dialogs.event.DayEventsDialog;
import swing.view.dialogs.event.EditEventDialog;
import swing.view.dialogs.event.EditEventOptionDialog;
import swing.view.dialogs.event.EditSeriesDialog;
import swing.view.listeners.CalendarSelectListener;
import swing.view.listeners.EventActionListener;
import swing.view.listeners.ViewListener;

/**
 * GUI controller for the calendar application. Handles user interactions, updates the view, and
 * communicates with the model.
 */
public class CalendarGuiController implements CalendarControllerInterface, ViewListener,
    EventActionListener, CalendarSelectListener {
  protected final CalendarManagerInterface model;
  protected final CalendarGuiViewInterface view;
  private final Map<String, Color> colorMap;
  private final Random generator;

  /**
   * Create a GUI controller with the specified model and view. Initializes the default calendar and
   * registers listeners.
   *
   * @param model calendar manager model
   * @param view  calendar GUI view
   */
  public CalendarGuiController(CalendarManagerInterface model, CalendarGuiViewInterface view) {
    this.colorMap = new HashMap<>();
    this.generator = new Random();

    this.model = model;
    model.addCalendar("Default", ZoneId.systemDefault().getId());
    this.view = view;
    this.view.addViewListener(this);
    this.view.setEventActionListener(this);
    this.view.setCalendarSelectListener(this);
    assignColorHelper("Default");
    view.addCalendar("Default", this.colorMap.get("Default"));
  }

  @Override
  public void go() {
    refreshEvents();
    this.view.display();
  }

  @Override
  public void handleToday() {
    view.goToToday();
    refreshEvents();
  }

  @Override
  public void handlePreviousMonth() {
    view.goToPreviousMonth();
    refreshEvents();
  }

  @Override
  public void handleNextMonth() {
    view.goToNextMonth();
    refreshEvents();
  }

  @Override
  public void handleCreateCalendar() {
    CreateCalendarCallback callback = new CreateCalendarCallback(model, view, this.colorMap);
    CreateCalendarDialog dialog = new CreateCalendarDialog((JFrame) view, callback);
    dialog.setVisible(true);

    String createdCalendarName = callback.getCalendarName();
    if (createdCalendarName != null) {
      assignColorHelper(createdCalendarName);
    }

    refreshView();
  }

  /**
   * Assign a random color to a calendar.
   *
   * @param calendarName name of calendar
   */
  private void assignColorHelper(String calendarName) {
    Color color = new Color(
        this.generator.nextInt(256),
        this.generator.nextInt(256),
        this.generator.nextInt(256)
    );

    this.colorMap.putIfAbsent(calendarName, color);
  }

  @Override
  public void handleCreateEvent() {
    List<String> calendarNames = new ArrayList<>(model.getAllCalendarNames());
    CreateEventCallback callback = new CreateEventCallback(model);
    CreateEventDialog dialog = new CreateEventDialog((JFrame) view, callback, calendarNames);
    dialog.setVisible(true);

    refreshEvents();
  }

  @Override
  public void handleEditCalendar(String currentCalendarName) {
    EditCalendarCallback callback = new EditCalendarCallback(model, view, currentCalendarName);
    EditCalendarDialog dialog = new EditCalendarDialog((JFrame) view, callback, currentCalendarName,
        model.getCalendarTimezone(currentCalendarName));
    dialog.setVisible(true);

    String newCalendarName = callback.getNewCalendarName();
    if (newCalendarName != null) {
      this.colorMap.put(newCalendarName, this.colorMap.remove(currentCalendarName));
    }

    refreshEvents();
  }

  @Override
  public void handleEditEvent(String calendarName, EventReadOnlyInterface event) {
    model.activateCalendar(calendarName);
    CalendarModelInterface calendar = model.getActiveCalendar();

    EditEventOptionCallback editEventOptionCallback = new EditEventOptionCallback(calendar, event);
    EditEventOptionDialog editEventOptionDialog =
        new EditEventOptionDialog((JFrame) view, editEventOptionCallback);

    EditSeriesCallback seriesCallback = new EditSeriesCallback(calendar, event);
    EditSeriesDialog seriesDialog = new EditSeriesDialog((JFrame) view, seriesCallback);

    EditEventCallback callback =
        new EditEventCallback(calendar, event, editEventOptionDialog, seriesDialog);
    EditEventDialog dialog =
        new EditEventDialog((JFrame) view, event, callback, calendar, calendarName);
    dialog.setVisible(true);

    refreshEvents();
  }

  @Override
  public void handleDayClick(LocalDate date) {
    List<EventReadOnlyInterface> events = new ArrayList<>();
    Map<EventReadOnlyInterface, String> eventCalendarNames = new HashMap<>();

    for (String calendarName : view.getSelectedCalendars()) {
      model.activateCalendar(calendarName);
      CalendarModelInterface calendar = model.getActiveCalendar();

      for (EventReadOnlyInterface event : calendar.getAllEventsReadOnly()) {
        LocalDate eventStart = event.getStartDateTime().toLocalDate();
        LocalDate eventEnd = event.getEndDateTime().toLocalDate();

        if (!date.isBefore(eventStart) && !date.isAfter(eventEnd)) {
          events.add(event);
          eventCalendarNames.put(event, calendarName);
        }
      }
    }

    DayEventsDialog dialog =
        new DayEventsDialog((JFrame) view, date, events, eventCalendarNames, model, this.colorMap);
    dialog.setListener(this);
    dialog.setVisible(true);

    refreshEvents();
  }

  /**
   * Refresh the calendar list and re-render all calendars and events.
   */
  public void refreshView() {
    List<String> previousSelections = new ArrayList<>(view.getSelectedCalendars());

    view.clearAllCalenders();
    for (String calendarName : model.getAllCalendarNames()) {
      view.renderCalendar(calendarName, this.colorMap.get(calendarName));
    }
    view.setSelectedCalendars(previousSelections);

    refreshEvents();
  }

  /**
   * Refresh all events in the current month view.
   */
  public void refreshEvents() {
    view.clearAllEvents();

    YearMonth currentMonth = view.getCurrentMonth();
    LocalDate monthStart = currentMonth.atDay(1);
    LocalDate monthEnd = currentMonth.atEndOfMonth();

    for (String calendarName : view.getSelectedCalendars()) {
      model.activateCalendar(calendarName);
      CalendarModelInterface calendar = model.getActiveCalendar();
      Color color = this.colorMap.get(calendarName);
      for (EventReadOnlyInterface event : calendar.getAllEventsReadOnly()) {
        LocalDate eventDate = event.getStartDateTime().toLocalDate();
        if (!eventDate.isBefore(monthStart) && !eventDate.isAfter(monthEnd)) {
          view.renderEvent(event, color);
        }
      }
    }
  }

  @Override
  public void onEditEvent(EventReadOnlyInterface event, Color color) {
    for (Map.Entry<String, Color> entry : colorMap.entrySet()) {
      if (entry.getValue().equals(color)) {
        handleEditEvent(entry.getKey(), event);
        break;
      }
    }
  }

  @Override
  public void onToggle() {
    refreshEvents();
  }

  @Override
  public void onEdit(String currentCalendarName) {
    handleEditCalendar(currentCalendarName);
  }

  @Override
  public void onDayClicked(LocalDate date) {
    handleDayClick(date);
  }
}
