package swing.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JPanel;
import model.event.EventReadOnlyInterface;
import swing.view.listeners.CalendarSelectListener;
import swing.view.listeners.EventActionListener;
import swing.view.listeners.ViewListener;
import swing.view.panels.CalendarSelectPanel;
import swing.view.panels.DayCellPanel;
import swing.view.panels.MonthViewPanel;
import swing.view.panels.NavigationPanel;
import swing.view.panels.WeekHeaderPanel;

/**
 * A Swing-based implementation of CalendarGuiViewInterface. This class constructs and manages the
 * graphical user interface for the calendar application. It displays navigation controls, a month
 * grid, weekday headers, and a calendar-selection panel. User actions, such as navigation or event
 * creation, are forwarded to registered ViewListener instances.
 */
public class CalendarGuiView extends JFrame implements CalendarGuiViewInterface, ActionListener {
  private final NavigationPanel navigationPanel;
  private final MonthViewPanel monthViewPanel;
  private final CalendarSelectPanel calendarSelectPanel;

  private final List<ViewListener> viewListeners;

  /**
   * Constructs a CalendarGuiView and initializes all UI components, including the navigation bar,
   * month grid, weekday header, and calendar-selection area. The window is prepared but not
   * displayed until display() is called.
   */
  public CalendarGuiView() {
    super("Simple Calendar");

    this.viewListeners = new ArrayList<>();

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1000, 800);
    setLayout(new BorderLayout());

    this.navigationPanel = new NavigationPanel();
    WeekHeaderPanel weekHeaderPanel = new WeekHeaderPanel();
    this.monthViewPanel = new MonthViewPanel();

    JPanel bottomSection = new JPanel(new BorderLayout());
    bottomSection.add(weekHeaderPanel, BorderLayout.NORTH);
    bottomSection.add(monthViewPanel, BorderLayout.SOUTH);

    this.calendarSelectPanel = new CalendarSelectPanel();

    add(navigationPanel, BorderLayout.NORTH);
    add(bottomSection, BorderLayout.CENTER);
    add(calendarSelectPanel, BorderLayout.SOUTH);

    updateMonthLabel();
    setUpButtonListener();
  }

  @Override
  public void display() {
    setVisible(true);
  }

  @Override
  public void addViewListener(ViewListener viewListener) {
    this.viewListeners.add(viewListener);
  }

  @Override
  public void setCalendarSelectListener(CalendarSelectListener listener) {
    calendarSelectPanel.setListener(listener);
  }

  @Override
  public void setEventActionListener(EventActionListener listener) {
    monthViewPanel.setListener(listener);
  }

  @Override
  public void renderCalendar(String calendarName, Color color) {
    this.calendarSelectPanel.addCalendar(calendarName, color);
  }

  @Override
  public void renderEvent(EventReadOnlyInterface event, Color color) {
    LocalDate eventStart = event.getStartDateTime().toLocalDate();
    LocalDate eventEnd = event.getEndDateTime().toLocalDate();

    for (DayCellPanel cell : monthViewPanel.getDayCells()) {
      LocalDate cellDate = cell.getDate();
      if (cellDate != null) {
        if (!cellDate.isBefore(eventStart) && !cellDate.isAfter(eventEnd)) {
          cell.addEvent(event, color);
        }
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
      case "today":
        for (ViewListener listener : this.viewListeners) {
          listener.handleToday();
        }
        break;
      case "previous":
        for (ViewListener listener : this.viewListeners) {
          listener.handlePreviousMonth();
        }
        break;
      case "next":
        for (ViewListener listener : this.viewListeners) {
          listener.handleNextMonth();
        }
        break;
      case "create calendar":
        for (ViewListener listener : this.viewListeners) {
          listener.handleCreateCalendar();
        }
        break;
      case "create event":
        for (ViewListener listener : this.viewListeners) {
          listener.handleCreateEvent();
        }
        break;
      default:
        throw new IllegalArgumentException("Invalid action command: " + e.getActionCommand());
    }
  }

  @Override
  public List<String> getSelectedCalendars() {
    return calendarSelectPanel.getSelectedCalendarNames();
  }

  @Override
  public void setSelectedCalendars(List<String> calendarNames) {
    this.calendarSelectPanel.setSelectedCalendars(calendarNames);
  }

  @Override
  public void goToToday() {
    monthViewPanel.goToToday();
    updateMonthLabel();
  }

  @Override
  public void goToPreviousMonth() {
    monthViewPanel.previousMonth();
    updateMonthLabel();
  }

  @Override
  public void goToNextMonth() {
    monthViewPanel.nextMonth();
    updateMonthLabel();
  }

  @Override
  public void addCalendar(String calendarName, Color color) {
    this.calendarSelectPanel.addCalendar(calendarName, color);
  }

  @Override
  public void updateCalendarName(String originalName, String newName) {
    this.calendarSelectPanel.updateCalendarName(originalName, newName);
  }

  @Override
  public void clearAllCalenders() {
    calendarSelectPanel.clearAllCalendars();
  }

  @Override
  public void clearAllEvents() {
    for (DayCellPanel cell : monthViewPanel.getDayCells()) {
      cell.clearAllEvents();
    }
  }

  @Override
  public YearMonth getCurrentMonth() {
    return monthViewPanel.getCurrentMonth();
  }

  /**
   * Updates the month label in the navigation bar to show the current month and year.
   */
  private void updateMonthLabel() {
    String monthName = monthViewPanel.getCurrentMonth()
        .getMonth()
        .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

    int year = monthViewPanel.getCurrentMonth().getYear();

    navigationPanel.setMonthYearText(monthName + " " + year);
  }

  /**
   * Sets action listeners and assigns action commands to navigation buttons.
   */
  private void setUpButtonListener() {
    navigationPanel.getTodayButton().addActionListener(this);
    navigationPanel.getTodayButton().setActionCommand("today");

    navigationPanel.getPreviousButton().addActionListener(this);
    navigationPanel.getPreviousButton().setActionCommand("previous");

    navigationPanel.getNextButton().addActionListener(this);
    navigationPanel.getNextButton().setActionCommand("next");

    navigationPanel.getCreateEventButton().addActionListener(this);
    navigationPanel.getCreateEventButton().setActionCommand("create event");

    calendarSelectPanel.getCreateCalendarButton().addActionListener(this);
    calendarSelectPanel.getCreateCalendarButton().setActionCommand("create calendar");
  }
}
