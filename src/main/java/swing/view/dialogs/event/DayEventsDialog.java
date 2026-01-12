package swing.view.dialogs.event;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import model.calendar.CalendarManagerInterface;
import model.calendar.Weekday;
import model.event.EventReadOnlyInterface;
import swing.view.listeners.EventActionListener;

/**
 * Dialog displaying all events scheduled on a given day. Shows event cards with subject, time,
 * location, and an edit option.
 */
public class DayEventsDialog extends JDialog {
  private final LocalDate date;
  private final List<EventReadOnlyInterface> events;
  private final Map<EventReadOnlyInterface, String> eventCalendarNames;
  private final CalendarManagerInterface calendarManager;
  private final Map<String, Color> colorMap;
  private final JPanel mainContainer;
  private EventActionListener listener;

  /**
   * Constructs a dialog that displays all events scheduled on a given date.
   *
   * @param parent             the parent frame used for modality and positioning
   * @param date               the date for which events are being displayed
   * @param events             the list of events occurring on the given date
   * @param eventCalendarNames mapping of each event to the calendar it belongs to
   * @param calendarManager    the calendar manager used to determine recurring details
   * @param colorMap           mapping of calendar names to display colors for event cards
   */
  public DayEventsDialog(JFrame parent, LocalDate date,
                         List<EventReadOnlyInterface> events,
                         Map<EventReadOnlyInterface, String> eventCalendarNames,
                         CalendarManagerInterface calendarManager, Map<String, Color> colorMap) {
    super(parent, "Events on " + date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")), true);
    this.date = date;
    this.events = events;
    this.eventCalendarNames = eventCalendarNames;
    this.calendarManager = calendarManager;
    this.colorMap = colorMap;

    setSize(600, 500);
    setLocationRelativeTo(parent);
    setLayout(new BorderLayout(10, 10));

    mainContainer = new JPanel(new BorderLayout(10, 10));
    add(mainContainer, BorderLayout.CENTER);

    refresh();
  }

  /**
   * Set listener for handling event edit actions.
   *
   * @param listener event action listener
   */
  public void setListener(EventActionListener listener) {
    this.listener = listener;
  }

  /**
   * Rebuilds and repaints all UI components in the dialog.
   */
  private void refresh() {
    mainContainer.removeAll();

    mainContainer.add(createHeaderPanel(), BorderLayout.NORTH);
    mainContainer.add(createEventsPanel(), BorderLayout.CENTER);
    mainContainer.add(createButtonPanel(), BorderLayout.SOUTH);

    mainContainer.revalidate();
    mainContainer.repaint();
  }

  /**
   * Creates the header panel displaying the formatted date.
   *
   * @return a panel containing the date label
   */
  private JPanel createHeaderPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.setBorder(new EmptyBorder(10, 10, 0, 10));

    JLabel dateLabel = new JLabel(date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
    dateLabel.setFont(new Font("Arial", Font.BOLD, 18));
    panel.add(dateLabel);

    return panel;
  }

  /**
   * Create the scrollable panel containing all event cards.
   */
  private JScrollPane createEventsPanel() {
    JPanel container = new JPanel();
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
    container.setBorder(new EmptyBorder(10, 15, 10, 10));

    if (events.isEmpty()) {
      JLabel noEventsLabel = new JLabel("No events scheduled for this day");
      noEventsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
      noEventsLabel.setForeground(Color.GRAY);
      container.add(noEventsLabel);
    } else {
      for (EventReadOnlyInterface event : events) {
        container.add(
            createEventCard(event, colorMap.get(eventCalendarNames.get(event))));
        container.add(Box.createRigidArea(new Dimension(0, 10)));
      }
    }

    JScrollPane scrollPane = new JScrollPane(container);
    scrollPane.setBorder(null);
    return scrollPane;
  }

  /**
   * Create a card displaying event details and edit button.
   *
   * @param event event data
   * @param color event color
   * @return event card panel
   */
  private JPanel createEventCard(EventReadOnlyInterface event, Color color) {
    JPanel card = new JPanel(new BorderLayout(10, 10));
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
        new EmptyBorder(10, 10, 10, 10)
    ));
    card.setBackground(Color.WHITE);
    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

    JPanel colorStrip = new JPanel();
    colorStrip.setBackground(color);
    colorStrip.setPreferredSize(new Dimension(8, 0));
    card.add(colorStrip, BorderLayout.WEST);

    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setOpaque(false);

    updateEventEssentialCard(event, infoPanel);
    updateEventOptionalCard(event, infoPanel);

    card.add(infoPanel, BorderLayout.CENTER);

    JButton editButton = new JButton("Edit");
    editButton.setPreferredSize(new Dimension(70, 30));
    editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    editButton.addActionListener(e -> handleEdit(event, color));

    JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonWrapper.setOpaque(false);
    buttonWrapper.add(editButton);

    card.add(buttonWrapper, BorderLayout.EAST);

    return card;
  }

  /**
   * Adds the essential fields of an event—subject and time range—to the specified panel.
   *
   * @param event     the event whose essential information is added
   * @param infoPanel the panel to which UI components are appended
   */
  private void updateEventEssentialCard(EventReadOnlyInterface event, JPanel infoPanel) {
    JLabel subjectLabel = new JLabel(event.getSubject());
    subjectLabel.setFont(new Font("Arial", Font.BOLD, 16));
    infoPanel.add(subjectLabel);

    infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));

    String start =
        event.getStartDateTime().format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
    String timezone =
        " (" + calendarManager.getCalendarTimezone(eventCalendarNames.get(event)).toString() + ")";

    String end;
    if (event.getStartDateTime().toLocalDate().equals(event.getEndDateTime().toLocalDate())) {
      end =
          event.getEndDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    } else {
      end = event.getEndDateTime().format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
    }
    String timeText = start + " - " + end + timezone;
    JLabel timeLabel = new JLabel(timeText);
    timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    timeLabel.setForeground(Color.DARK_GRAY);
    infoPanel.add(timeLabel);
  }

  /**
   * Adds optional and conditional event fields to the specified panel.
   *
   * @param event     the event whose optional and recurring information is added
   * @param infoPanel the panel to which UI components are appended
   */
  private void updateEventOptionalCard(EventReadOnlyInterface event, JPanel infoPanel) {
    if (event.getLocation() != null && !event.getLocation().isEmpty()) {
      infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
      JLabel locationLabel = new JLabel("Location: " + event.getLocation());
      locationLabel.setFont(new Font("Arial", Font.PLAIN, 13));
      locationLabel.setForeground(Color.GRAY);
      infoPanel.add(locationLabel);
    }

    if (event.getDescription() != null && !event.getDescription().isEmpty()) {
      infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
      JLabel descLabel = new JLabel("Description: " + event.getDescription());
      descLabel.setFont(new Font("Arial", Font.PLAIN, 13));
      descLabel.setForeground(Color.GRAY);
      infoPanel.add(descLabel);
    }

    if (event.getStatus() != null) {
      infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
      JLabel statusLabel = new JLabel("Status: " + event.getStatus());
      statusLabel.setFont(new Font("Arial", Font.PLAIN, 13));
      statusLabel.setForeground(Color.GRAY);
      infoPanel.add(statusLabel);
    }

    calendarManager.activateCalendar(eventCalendarNames.get(event));
    boolean isSeries = calendarManager.getActiveCalendar().isSeriesEvent(event);

    if (isSeries) {
      infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));

      Set<Weekday> weekdays = calendarManager.getActiveCalendar().getSeriesWeekdays(event);
      String weekdayString = weekdaysToString(weekdays);

      Integer occurrences = calendarManager.getActiveCalendar().getSeriesOccurrence(event);
      LocalDate until = calendarManager.getActiveCalendar().getSeriesUntilEnd(event);

      String recurringText;
      if (occurrences != null) {
        recurringText = "Recurring on " + weekdayString + " for " + occurrences + " times";
      } else {
        recurringText = "Recurring on " + weekdayString + " until " + until;
      }

      JLabel recurLabel = new JLabel(recurringText);
      recurLabel.setFont(new Font("Arial", Font.PLAIN, 13));
      recurLabel.setForeground(Color.GRAY);
      infoPanel.add(recurLabel);
    }
  }

  /**
   * Convert weekday set to M/T/W/R/F/S/U string in correct order.
   */
  private String weekdaysToString(Set<Weekday> weekdays) {
    Weekday[] order = {
        Weekday.MONDAY, Weekday.TUESDAY, Weekday.WEDNESDAY,
        Weekday.THURSDAY, Weekday.FRIDAY,
        Weekday.SATURDAY, Weekday.SUNDAY
    };

    StringBuilder sb = new StringBuilder();

    for (Weekday w : order) {
      String code = null;

      switch (w) {
        case MONDAY:
          if (weekdays.contains(Weekday.MONDAY)) {
            code = "M";
          }
          break;
        case TUESDAY:
          if (weekdays.contains(Weekday.TUESDAY)) {
            code = "T";
          }
          break;
        case WEDNESDAY:
          if (weekdays.contains(Weekday.WEDNESDAY)) {
            code = "W";
          }
          break;
        case THURSDAY:
          if (weekdays.contains(Weekday.THURSDAY)) {
            code = "R";
          }
          break;
        case FRIDAY:
          if (weekdays.contains(Weekday.FRIDAY)) {
            code = "F";
          }
          break;
        case SATURDAY:
          if (weekdays.contains(Weekday.SATURDAY)) {
            code = "S";
          }
          break;
        case SUNDAY:
          if (weekdays.contains(Weekday.SUNDAY)) {
            code = "U";
          }
          break;
        default:
          break;
      }

      if (code != null) {
        if (sb.length() > 0) {
          sb.append("/");
        }
        sb.append(code);
      }
    }

    return sb.toString();
  }


  /**
   * Create the bottom panel containing the Close button.
   */
  private JPanel createButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.setBorder(new EmptyBorder(0, 10, 10, 10));

    JButton closeButton = new JButton("Close");
    closeButton.setPreferredSize(new Dimension(80, 35));
    closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    closeButton.addActionListener(e -> dispose());

    panel.add(closeButton);

    return panel;
  }

  /**
   * Notify listener to edit an event.
   */
  private void handleEdit(EventReadOnlyInterface event, Color color) {
    dispose();
    if (listener != null) {
      listener.onEditEvent(event, color);
      listener.onDayClicked(date);
    }
  }
}
