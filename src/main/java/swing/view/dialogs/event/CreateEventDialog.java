package swing.view.dialogs.event;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import swing.controller.callbacks.DialogCallbackInterface;
import swing.view.dialogs.event.data.EventData;
import swing.view.dialogs.event.forms.BasicEventFormPanel;
import swing.view.dialogs.event.forms.DialogButtonPanel;
import swing.view.dialogs.event.forms.RepeatOptionsPanel;

/**
 * Dialog for creating a new event. Provides fields for event details along with options for all-day
 * and recurring events.
 */
public class CreateEventDialog extends JDialog {
  protected final BasicEventFormPanel formPanel;
  protected final RepeatOptionsPanel repeatOptionsPanel;
  protected final DialogButtonPanel buttonPanel;

  protected final JCheckBox isAllDay;
  protected final JCheckBox isRepeat;

  protected final DialogCallbackInterface<EventData> callback;

  /**
   * Create a modal dialog for event creation.
   *
   * @param parent        parent frame
   * @param callback      callback for handling save and cancel actions
   * @param calendarNames list of calendar names to choose from
   */
  public CreateEventDialog(JFrame parent, DialogCallbackInterface<EventData> callback,
                           List<String> calendarNames) {
    super(parent, "Create Event", true);
    this.callback = callback;

    setSize(500, 600);
    setLocationRelativeTo(parent);
    setLayout(new BorderLayout(10, 10));

    formPanel = new BasicEventFormPanel(calendarNames);
    add(formPanel, BorderLayout.NORTH);

    JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    checkboxPanel.setBorder(new EmptyBorder(0, 15, 10, 15));

    isAllDay = new JCheckBox("All-day event");
    isRepeat = new JCheckBox("Repeat");
    isAllDay.addActionListener(e -> handleAllDayToggle());
    isRepeat.addActionListener(e -> handleRepeatToggle());

    checkboxPanel.add(isAllDay);
    checkboxPanel.add(isRepeat);

    JPanel centerSection = new JPanel(new BorderLayout(5, 5));
    centerSection.add(checkboxPanel, BorderLayout.NORTH);

    repeatOptionsPanel = new RepeatOptionsPanel();
    repeatOptionsPanel.setVisible(false);
    centerSection.add(repeatOptionsPanel, BorderLayout.SOUTH);

    add(centerSection, BorderLayout.CENTER);

    buttonPanel = new DialogButtonPanel();
    buttonPanel.getSaveButton().addActionListener(e -> handleSave());
    buttonPanel.getCancelButton().addActionListener(e -> handleCancel());
    add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Toggle time selection when the all-day option is selected.
   */
  private void handleAllDayToggle() {
    boolean allDay = isAllDay.isSelected();
    formPanel.setTimeEnabled(!allDay);

    if (allDay) {
      formPanel.syncEndDate();
    }
  }

  /**
   * Toggle repeat options visibility.
   */
  protected void handleRepeatToggle() {
    boolean repeat = isRepeat.isSelected();
    repeatOptionsPanel.setVisible(repeat);
  }

  /**
   * Handle save action by validating input and sending data to callback.
   */
  private void handleSave() {
    if (invalidateInput()) {
      return;
    }

    EventData eventData = collectEventData();
    callback.onSave(eventData);
    dispose();
  }

  /**
   * Handle cancel action.
   */
  private void handleCancel() {
    if (callback != null) {
      callback.onCancel();
    }
    dispose();
  }

  /**
   * Validate user input.
   *
   * @return true if invalid, false otherwise
   */
  protected boolean invalidateInput() {
    if (formPanel.getSubject().isEmpty()) {
      showError("Subject is required!");
      return true;
    }

    if (isRepeat.isSelected() && repeatOptionsPanel.getSelectedWeekdays().isEmpty()) {
      showError("Please select at least one day for repeat");
      return true;
    }

    return false;
  }

  /**
   * Collect all event data from the form and repeat options.
   *
   * @return event data object
   */
  private EventData collectEventData() {
    String calendarName = formPanel.getSelectedCalendarName();
    boolean repeat = isRepeat.isSelected();

    String weekdays = null;
    Integer occurrences = null;
    String untilDate = null;

    if (repeat) {
      weekdays = repeatOptionsPanel.getSelectedWeekdays();
      occurrences = repeatOptionsPanel.getOccurrences();
      untilDate = repeatOptionsPanel.getUntilDate();
    }

    return new EventData(
        formPanel.getSubject(),
        formPanel.getStartDateTime(),
        formPanel.getEndDateTime(),
        formPanel.getLocationText(),
        formPanel.getDescriptionText(),
        formPanel.getStatusText(),
        isAllDay.isSelected(),
        repeat,
        weekdays,
        occurrences,
        untilDate,
        calendarName
    );
  }

  /**
   * Show a validation error dialog.
   *
   * @param message error message
   */
  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message,
        "Validation Error", JOptionPane.ERROR_MESSAGE);
  }
}
