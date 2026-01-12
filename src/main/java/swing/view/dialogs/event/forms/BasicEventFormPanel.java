package swing.view.dialogs.event.forms;

import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;

/**
 * Form panel for entering basic event details including subject, date/time, location, description,
 * status, and calendar selection.
 */
public class BasicEventFormPanel extends JPanel {

  private final JComboBox<String> calendarName;
  private final JTextField subject;
  private final JSpinner startDate;
  private final JSpinner startTime;
  private final JSpinner endDate;
  private final JSpinner endTime;
  private final JTextField location;
  private final JTextField description;
  private final JComboBox<String> status;

  /**
   * Create an event form populated with available calendar names.
   *
   * @param calendarNames list of calendar names
   */
  public BasicEventFormPanel(List<String> calendarNames) {
    setLayout(new GridLayout(9, 2, 10, 10));
    setBorder(new EmptyBorder(15, 15, 15, 15));

    add(new JLabel("Calendar:"));
    calendarName = new JComboBox<>(calendarNames.toArray(new String[0]));
    add(calendarName);

    add(new JLabel("Subject:"));
    subject = new JTextField();
    add(subject);

    add(new JLabel("Start Date:"));
    startDate = createDateSpinner();
    add(startDate);

    add(new JLabel("Start Time:"));
    startTime = createTimeSpinner();
    editTimeSpinner(startTime, 12, 0);
    add(startTime);

    add(new JLabel("End Date:"));
    endDate = createDateSpinner();
    add(endDate);

    add(new JLabel("End Time:"));
    endTime = createTimeSpinner();
    editTimeSpinner(endTime, 13, 0);
    add(endTime);

    add(new JLabel("Location:"));
    location = new JTextField();
    add(location);

    add(new JLabel("Description:"));
    description = new JTextField();
    add(description);

    add(new JLabel("Status:"));
    status = new JComboBox<>(new String[] {"", "Public", "Private"});
    add(status);
  }

  /**
   * Get selected calendar name.
   *
   * @return calendar name
   */
  public String getSelectedCalendarName() {
    return (String) calendarName.getSelectedItem();
  }

  /**
   * Set the calendar name and disable selection.
   *
   * @param name calendar name
   */
  public void setCalendarName(String name) {
    calendarName.setSelectedItem(name);
    calendarName.setEnabled(false);
  }

  /**
   * Get event subject.
   *
   * @return subject text
   */
  public String getSubject() {
    return subject.getText().trim();
  }

  /**
   * Set event subject.
   *
   * @param text subject text
   */
  public void setSubject(String text) {
    subject.setText(text);
  }

  /**
   * Get start date-time string.
   *
   * @return start date-time
   */
  public String getStartDateTime() {
    return getDateTimeFromSpinners(startDate, startTime);
  }

  /**
   * Set start date and time spinners.
   *
   * @param startDateTime date-time value
   */
  public void setStartDateTime(LocalDateTime startDateTime) {
    LocalDate date = startDateTime.toLocalDate();
    LocalTime time = startDateTime.toLocalTime();

    editDateSpinner(startDate, date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    editTimeSpinner(startTime, time.getHour(), time.getMinute());
  }

  /**
   * Get end date-time string.
   *
   * @return end date-time
   */
  public String getEndDateTime() {
    return getDateTimeFromSpinners(endDate, endTime);
  }

  /**
   * Set end date and time spinners.
   *
   * @param endDateTime date-time value
   */
  public void setEndDateTime(LocalDateTime endDateTime) {
    LocalDate date = endDateTime.toLocalDate();
    LocalTime time = endDateTime.toLocalTime();

    editDateSpinner(endDate, date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    editTimeSpinner(endTime, time.getHour(), time.getMinute());
  }

  /**
   * Get location text.
   *
   * @return location
   */
  public String getLocationText() {
    return location.getText().trim();
  }

  /**
   * Set location text.
   *
   * @param text location
   */
  public void setLocationText(String text) {
    location.setText(text);
  }

  /**
   * Get description text.
   *
   * @return description
   */
  public String getDescriptionText() {
    return description.getText().trim();
  }

  /**
   * Set description text.
   *
   * @param text description
   */
  public void setDescriptionText(String text) {
    description.setText(text);
  }

  /**
   * Get selected status text.
   *
   * @return status
   */
  public String getStatusText() {
    return (String) status.getSelectedItem();
  }

  /**
   * Set status selection.
   *
   * @param statusText status string
   */
  public void setStatus(String statusText) {
    removeBlankStatusOption();

    String normalized = statusText.substring(0, 1).toUpperCase()
        + statusText.substring(1).toLowerCase();

    for (int i = 0; i < status.getItemCount(); i++) {
      if (status.getItemAt(i).equals(normalized)) {
        status.setSelectedIndex(i);
        return;
      }
    }
  }

  /**
   * Remove blank status option if present.
   */
  private void removeBlankStatusOption() {
    if (status.getItemCount() > 0 && status.getItemAt(0).isEmpty()) {
      status.removeItemAt(0);
    }
  }

  /**
   * Enable or disable time selectors.
   *
   * @param enabled whether time selection is enabled
   */
  public void setTimeEnabled(boolean enabled) {
    if (!enabled) {
      editTimeSpinner(startTime, 8, 0);
      editTimeSpinner(endTime, 17, 0);
    }
    startTime.setEnabled(enabled);
    endTime.setEnabled(enabled);
  }

  /**
   * Sync end date to match start date.
   */
  public void syncEndDate() {
    endDate.setValue(startDate.getValue());
  }

  /**
   * Create a date spinner.
   *
   * @return spinner component
   */
  private JSpinner createDateSpinner() {
    SpinnerDateModel dateModel = new SpinnerDateModel();
    JSpinner spinner = new JSpinner(dateModel);
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
    spinner.setEditor(dateEditor);
    return spinner;
  }

  /**
   * Create a time spinner.
   *
   * @return spinner component
   */
  private JSpinner createTimeSpinner() {
    SpinnerDateModel timeModel = new SpinnerDateModel();
    JSpinner spinner = new JSpinner(timeModel);
    JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spinner, "HH:mm");
    spinner.setEditor(timeEditor);
    return spinner;
  }

  /**
   * Edit date spinner value.
   */
  private void editDateSpinner(JSpinner spinner, int year, int month, int day) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.DAY_OF_MONTH, day);
    spinner.setValue(calendar.getTime());
  }

  /**
   * Edit time spinner value.
   */
  private void editTimeSpinner(JSpinner spinner, int hour, int minute) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    spinner.setValue(calendar.getTime());
  }

  /**
   * Combine date and time from spinners into ISO-like string.
   *
   * @return date-time string
   */
  private String getDateTimeFromSpinners(JSpinner dateSpinner, JSpinner timeSpinner) {
    Date dateValue = (Date) dateSpinner.getValue();
    Date timeValue = (Date) timeSpinner.getValue();

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String datePart = dateFormat.format(dateValue);

    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    String timePart = timeFormat.format(timeValue);

    return datePart + "T" + timePart;
  }

  /**
   * Disable calendar selection dropdown.
   */
  public void disableCalendarSelection() {
    calendarName.setEnabled(false);
  }
}
