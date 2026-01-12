package swing.view.dialogs.calendar;

import java.awt.GridLayout;
import java.time.ZoneId;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Form panel for entering calendar details such as name and timezone. Used in calendar creation and
 * editing dialogs.
 */
public class BasicCalendarFormPanel extends JPanel {
  private final JTextField name;
  private final JComboBox<String> timezone;

  /**
   * Create a form panel with fields for calendar name and timezone.
   */
  public BasicCalendarFormPanel() {
    setLayout(new GridLayout(2, 2, 10, 10));
    setBorder(new EmptyBorder(15, 15, 15, 15));

    add(new JLabel("Name:"));
    name = new JTextField();
    add(name);

    add(new JLabel("Timezone:"));
    String[] zones = ZoneId.getAvailableZoneIds().stream().sorted().toArray(String[]::new);
    timezone = new JComboBox<>(zones);
    timezone.setSelectedItem(ZoneId.systemDefault().getId());
    add(timezone);
  }

  /**
   * Get the entered calendar name.
   *
   * @return calendar name
   */
  public String getName() {
    return name.getText().trim();
  }

  /**
   * Set the calendar name field.
   *
   * @param name calendar name
   */
  public void setName(String name) {
    this.name.setText(name);
  }

  /**
   * Get the selected timezone.
   *
   * @return timezone string
   */
  public String getTimezone() {
    return (String) timezone.getSelectedItem();
  }

  /**
   * Set the selected timezone field.
   *
   * @param timezone timezone string
   */
  public void setTimezone(String timezone) {
    this.timezone.setSelectedItem(timezone);
  }
}