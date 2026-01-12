package swing.view.dialogs.calendar;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.ZoneId;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import swing.controller.callbacks.DialogCallbackInterface;
import swing.view.dialogs.event.forms.DialogButtonPanel;

/**
 * Dialog for editing an existing calendar. Pre-fills current calendar data and notifies the
 * callback on save or cancel.
 */
public class EditCalendarDialog extends JDialog {

  private final BasicCalendarFormPanel formPanel;
  private final DialogCallbackInterface<CalendarData> callback;

  private final String calendarName;
  private final ZoneId timezone;

  /**
   * Create a modal dialog for editing a calendar.
   *
   * @param parent       parent frame
   * @param callback     callback for handling save and cancel actions
   * @param calendarName current calendar name
   * @param timezone     current calendar timezone
   */
  public EditCalendarDialog(JFrame parent, DialogCallbackInterface<CalendarData> callback,
                            String calendarName, ZoneId timezone) {
    super(parent, "Edit Calendar", true);
    this.callback = callback;
    this.calendarName = calendarName;
    this.timezone = timezone;

    setSize(500, 550);
    setLocationRelativeTo(parent);
    setLayout(new BorderLayout(10, 10));

    formPanel = new BasicCalendarFormPanel();
    add(formPanel, BorderLayout.NORTH);

    JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    checkboxPanel.setBorder(new EmptyBorder(0, 15, 10, 15));

    DialogButtonPanel buttonPanel = new DialogButtonPanel();
    buttonPanel.getSaveButton().addActionListener(e -> handleSave());
    buttonPanel.getCancelButton().addActionListener(e -> handleCancel());
    add(buttonPanel, BorderLayout.SOUTH);

    preFillCalendarData();
  }

  /**
   * Handle save action by validating input and passing updated data to callback.
   */
  private void handleSave() {
    if (!validateInput()) {
      return;
    }

    CalendarData calendarData = collectCalendarData();
    callback.onSave(calendarData);
    dispose();
  }

  /**
   * Handle dialog cancellation.
   */
  private void handleCancel() {
    callback.onCancel();
    dispose();
  }

  /**
   * Validate user input before saving.
   *
   * @return true if valid, false otherwise
   */
  private boolean validateInput() {
    if (formPanel.getName().isEmpty()) {
      JOptionPane.showMessageDialog(this, "Name is required!",
          "Validation Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  /**
   * Collect updated calendar data from form fields.
   *
   * @return updated calendar data
   */
  private CalendarData collectCalendarData() {
    String name = formPanel.getName();
    String timezone = formPanel.getTimezone();
    return new CalendarData(name, timezone);
  }

  /**
   * Pre-fill form fields with existing calendar information.
   */
  private void preFillCalendarData() {
    this.formPanel.setName(this.calendarName);
    this.formPanel.setTimezone(this.timezone.toString());
  }
}
