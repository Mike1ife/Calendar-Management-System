package swing.view.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import swing.view.components.CalendarCheckBox;
import swing.view.listeners.CalendarSelectListener;

/**
 * A panel that displays a list of calendars with corresponding checkboxes and edit controls. This
 * panel allows users to toggle calendar visibility and invoke edit actions. It also provides a
 * button for creating new calendars.
 *
 * <p>The panel is responsible only for presenting the calendar rows and
 * delegating user actions to the assigned {@link CalendarSelectListener}.
 */
public class CalendarSelectPanel extends JPanel {
  private final JButton createCalendarButton;
  private final JPanel calendarsContainer;
  private final Map<String, CalendarCheckBox> calendarRows;
  private CalendarSelectListener listener;

  /**
   * Constructs a CalendarSelectPanel with UI components for displaying calendars, toggling
   * visibility, and creating new calendar entries.
   */
  public CalendarSelectPanel() {
    this.createCalendarButton = new JButton("+ Create Calendar");
    this.calendarRows = new LinkedHashMap<>();
    this.calendarsContainer = new JPanel();

    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(0, 5, 10, 5));

    this.createCalendarButton.setForeground(Color.black);
    this.createCalendarButton.setFont(new Font("Arial", Font.BOLD, 15));
    this.createCalendarButton.setPreferredSize(new Dimension(150, 35));
    this.createCalendarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    buttonPanel.add(createCalendarButton);
    add(buttonPanel, BorderLayout.NORTH);

    calendarsContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
    add(calendarsContainer, BorderLayout.CENTER);
  }

  /**
   * Retrieves the button used to trigger calendar creation.
   *
   * @return the create calendar button
   */
  public JButton getCreateCalendarButton() {
    return this.createCalendarButton;
  }

  /**
   * Adds a new calendar row to the panel. Each row contains a checkbox for toggling visibility and
   * a button for editing the calendar.
   *
   * @param calendarName name of the calendar to display
   * @param color        the color used for the checkbox indicator
   */
  public void addCalendar(String calendarName, Color color) {
    Icon editIcon = UIManager.getIcon("FileView.directoryIcon");
    CalendarCheckBox calendarCheckBox = new CalendarCheckBox(calendarName, editIcon, color);
    calendarCheckBox.setListener(this.listener);

    JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    rowPanel.add(calendarCheckBox.getCheckBox());
    rowPanel.add(calendarCheckBox.getButton());

    this.calendarRows.put(calendarName, calendarCheckBox);
    calendarsContainer.add(rowPanel);

    revalidate();
    repaint();
  }

  /**
   * Updates the label of a calendar row when a calendar name is changed.
   *
   * @param originalName the previous name of the calendar
   * @param newName      the updated calendar name
   */
  public void updateCalendarName(String originalName, String newName) {
    this.calendarRows.put(newName, this.calendarRows.remove(originalName));
    this.calendarRows.get(newName).setLabel(newName);
  }

  /**
   * Removes all displayed calendars from the panel.
   */
  public void clearAllCalendars() {
    this.calendarRows.clear();
    this.calendarsContainer.removeAll();
    revalidate();
    repaint();
  }

  /**
   * Assigns a {@link CalendarSelectListener} to handle user interactions
   * such as toggling visibility and editing calendars.
   *
   * @param listener the listener to assign
   */
  public void setListener(CalendarSelectListener listener) {
    this.listener = listener;
    for (CalendarCheckBox calendarCheckBox : calendarRows.values()) {
      calendarCheckBox.setListener(listener);
    }
  }

  /**
   * Retrieves a list of calendar names that are currently selected
   * (i.e., their checkboxes are checked).
   *
   * @return list of selected calendar names
   */
  public List<String> getSelectedCalendarNames() {
    List<String> selectedCalendarNames = new ArrayList<>();
    for (Map.Entry<String, CalendarCheckBox> entry : this.calendarRows.entrySet()) {
      if (entry.getValue().getCheckBox().isSelected()) {
        selectedCalendarNames.add(entry.getKey());
      }
    }
    return selectedCalendarNames;
  }

  /**
   * Sets the selected state for specified calendars.
   *
   * @param calendarNames list of calendar names to select
   */
  public void setSelectedCalendars(List<String> calendarNames) {
    for (String name : calendarNames) {
      CalendarCheckBox checkbox = calendarRows.get(name);
      if (checkbox != null) {
        checkbox.getCheckBox().setSelected(true);
      }
    }
  }
}
