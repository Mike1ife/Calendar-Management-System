package swing.view.components;

import java.awt.Color;
import java.awt.Cursor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import swing.view.listeners.CalendarSelectListener;

/**
 * Component representing a calendar checkbox with an accompanying edit button. Used for selecting
 * and editing calendars in the GUI.
 */
public class CalendarCheckBox {
  private final ColoredCheckBox checkBox;
  private final JButton editButton;

  /**
   * Create a calendar checkbox with a colored label and an edit icon.
   *
   * @param calendarName name of the calendar
   * @param icon         edit button icon
   * @param checkColor   color for the checkbox label
   */
  public CalendarCheckBox(String calendarName, Icon icon, Color checkColor) {
    this.checkBox = new ColoredCheckBox(calendarName, checkColor);
    this.checkBox.setOpaque(false);
    this.editButton = new JButton(icon);
    this.editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    this.editButton.setBorderPainted(false);
    this.editButton.setContentAreaFilled(false);
  }

  /**
   * Get the checkbox component.
   *
   * @return checkbox
   */
  public JCheckBox getCheckBox() {
    return this.checkBox;
  }

  /**
   * Update the checkbox label.
   *
   * @param newName new label text
   */
  public void setLabel(String newName) {
    this.checkBox.setText(newName);
  }

  /**
   * Get the edit button component.
   *
   * @return edit button
   */
  public JButton getButton() {
    return this.editButton;
  }

  /**
   * Register a listener for checkbox toggle and edit actions.
   *
   * @param listener calendar selection listener
   */
  public void setListener(CalendarSelectListener listener) {
    this.checkBox.addActionListener(e -> listener.onToggle());
    this.editButton.addActionListener(e -> listener.onEdit(checkBox.getText()));
  }
}