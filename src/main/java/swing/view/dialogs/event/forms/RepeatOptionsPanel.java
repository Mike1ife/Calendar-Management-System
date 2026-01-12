package swing.view.dialogs.event.forms;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import model.calendar.Weekday;

/**
 * Panel for configuring repeat options of an event. Supports selecting weekdays and specifying end
 * conditions (number of occurrences or a final date).
 */
public class RepeatOptionsPanel extends JPanel {
  private JCheckBox mondayCheck;
  private JCheckBox tuesdayCheck;
  private JCheckBox wednesdayCheck;
  private JCheckBox thursdayCheck;
  private JCheckBox fridayCheck;
  private JCheckBox saturdayCheck;
  private JCheckBox sundayCheck;

  private JRadioButton occurrencesRadio;
  private JRadioButton untilDateRadio;

  private JSpinner occurrencesSpinner;
  private JSpinner untilDateSpinner;

  /**
   * Create the repeat options panel including weekday selection and repeat end-condition controls.
   */
  public RepeatOptionsPanel() {
    setLayout(new BorderLayout(10, 10));
    setBorder(new EmptyBorder(10, 15, 10, 15));

    add(createWeekdaysPanel(), BorderLayout.NORTH);
    add(createEndConditionPanel(), BorderLayout.CENTER);
  }

  /**
   * Create a row of weekday toggle checkboxes (M–U).
   *
   * @return panel containing weekday checkboxes
   */
  private JPanel createWeekdaysPanel() {
    mondayCheck = new JCheckBox("M");
    tuesdayCheck = new JCheckBox("T");
    wednesdayCheck = new JCheckBox("W");
    thursdayCheck = new JCheckBox("R");
    fridayCheck = new JCheckBox("F");
    saturdayCheck = new JCheckBox("S");
    sundayCheck = new JCheckBox("U");

    JPanel weekdaysPanel = new JPanel(new GridLayout(1, 7, 5, 5));
    weekdaysPanel.add(mondayCheck);
    weekdaysPanel.add(tuesdayCheck);
    weekdaysPanel.add(wednesdayCheck);
    weekdaysPanel.add(thursdayCheck);
    weekdaysPanel.add(fridayCheck);
    weekdaysPanel.add(saturdayCheck);
    weekdaysPanel.add(sundayCheck);

    return weekdaysPanel;
  }

  /**
   * Create panel containing "For X times" or "Until YYYY-MM-DD", repeat-end selection controls.
   *
   * @return panel containing end-condition widgets
   */
  private JPanel createEndConditionPanel() {
    occurrencesRadio = new JRadioButton("For");
    untilDateRadio = new JRadioButton("Until");

    ButtonGroup endGroup = new ButtonGroup();
    endGroup.add(occurrencesRadio);
    endGroup.add(untilDateRadio);
    occurrencesRadio.setSelected(true);

    occurrencesSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));

    SpinnerDateModel dateModel = new SpinnerDateModel();
    untilDateSpinner = new JSpinner(dateModel);
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(untilDateSpinner, "yyyy-MM-dd");
    untilDateSpinner.setEditor(dateEditor);
    untilDateSpinner.setEnabled(false);

    occurrencesRadio.addActionListener(e -> {
      occurrencesSpinner.setEnabled(true);
      untilDateSpinner.setEnabled(false);
    });

    untilDateRadio.addActionListener(e -> {
      occurrencesSpinner.setEnabled(false);
      untilDateSpinner.setEnabled(true);
    });

    JPanel occPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    occPanel.add(occurrencesRadio);
    occPanel.add(occurrencesSpinner);
    occPanel.add(new JLabel("times"));

    JPanel untilPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    untilPanel.add(untilDateRadio);
    untilPanel.add(untilDateSpinner);

    JPanel endPanel = new JPanel(new GridLayout(2, 1, 5, 5));
    endPanel.add(occPanel);
    endPanel.add(untilPanel);

    return endPanel;
  }

  /**
   * Collect selected weekdays as a single string of letters (e.g., {@code "MWF"}).
   *
   * @return weekday code string
   */
  public String getSelectedWeekdays() {
    StringBuilder sb = new StringBuilder();

    if (mondayCheck.isSelected()) {
      sb.append("M");
    }
    if (tuesdayCheck.isSelected()) {
      sb.append("T");
    }
    if (wednesdayCheck.isSelected()) {
      sb.append("W");
    }
    if (thursdayCheck.isSelected()) {
      sb.append("R");
    }
    if (fridayCheck.isSelected()) {
      sb.append("F");
    }
    if (saturdayCheck.isSelected()) {
      sb.append("S");
    }
    if (sundayCheck.isSelected()) {
      sb.append("U");
    }

    return sb.toString();
  }

  /**
   * Mark weekday checkboxes based on the series weekdays and disables them.
   *
   * @param weekdays set of weekdays to select
   */
  public void setSelectedWeekdays(Set<Weekday> weekdays) {
    disableWeekdayCheckHelper();
    for (Weekday weekday : weekdays) {
      selectWeekdayHelper(weekday);
    }
  }

  /**
   * Get the number of occurrences if the "For X times" option is selected.
   *
   * @return occurrence count or null
   */
  public Integer getOccurrences() {
    if (occurrencesRadio.isSelected()) {
      return (Integer) occurrencesSpinner.getValue();
    }
    return null;
  }

  /**
   * Prefill and disable the occurrences control for editing a series.
   *
   * @param occurrences occurrence count to show
   */
  public void setOccurrences(Integer occurrences) {
    occurrencesRadio.setEnabled(false);
    occurrencesSpinner.setEnabled(false);

    if (occurrences != null) {
      occurrencesRadio.setSelected(true);
      occurrencesSpinner.setValue(occurrences);
    }
  }

  /**
   * Get the until-date string if "Until" is selected.
   *
   * @return ISO date string or null
   */
  public String getUntilDate() {
    if (untilDateRadio.isSelected()) {
      return getUntilDateHelper();
    }
    return null;
  }

  /**
   * Prefill and disable the until-date control for editing a series.
   *
   * @param untilDate repeat-end date
   */
  public void setUntilDate(LocalDate untilDate) {
    untilDateRadio.setEnabled(false);
    untilDateSpinner.setEnabled(false);

    if (untilDate != null) {
      Date date = java.sql.Date.valueOf(untilDate);
      untilDateRadio.setSelected(true);
      untilDateSpinner.setValue(date);
    }
  }

  /**
   * Format the selected date in {@code yyyy-MM-dd}.
   *
   * @return formatted date string
   */
  private String getUntilDateHelper() {
    Date dateValue = (Date) untilDateSpinner.getValue();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    return dateFormat.format(dateValue);
  }

  /**
   * Disable weekday checkboxes so user cannot change series pattern.
   */
  private void disableWeekdayCheckHelper() {
    mondayCheck.setEnabled(false);
    tuesdayCheck.setEnabled(false);
    wednesdayCheck.setEnabled(false);
    thursdayCheck.setEnabled(false);
    fridayCheck.setEnabled(false);
    saturdayCheck.setEnabled(false);
    sundayCheck.setEnabled(false);
  }

  /**
   * Select the checkbox corresponding to the given weekday.
   *
   * @param weekday weekday enum to mark as selected
   */
  private void selectWeekdayHelper(Weekday weekday) {
    switch (weekday) {
      case MONDAY:
        mondayCheck.setSelected(true);
        break;
      case TUESDAY:
        tuesdayCheck.setSelected(true);
        break;
      case WEDNESDAY:
        wednesdayCheck.setSelected(true);
        break;
      case THURSDAY:
        thursdayCheck.setSelected(true);
        break;
      case FRIDAY:
        fridayCheck.setSelected(true);
        break;
      case SATURDAY:
        saturdayCheck.setSelected(true);
        break;
      case SUNDAY:
        sundayCheck.setSelected(true);
        break;
      default:
        break;
    }
  }
}
