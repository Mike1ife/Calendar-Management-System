package swing.view.dialogs.calendar;

import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import swing.controller.callbacks.DialogCallbackInterface;
import swing.view.dialogs.event.forms.DialogButtonPanel;

/**
 * Dialog for creating a new calendar. Collects calendar name and timezone, validates input, and
 * notifies the callback on save or cancel.
 */
public class CreateCalendarDialog extends JDialog {
  private final BasicCalendarFormPanel formPanel;
  private final DialogCallbackInterface<CalendarData> callback;

  /**
   * Create a modal dialog for calendar creation.
   *
   * @param parent   parent frame
   * @param callback callback for handling save and cancel actions
   */
  public CreateCalendarDialog(JFrame parent, DialogCallbackInterface<CalendarData> callback) {
    super(parent, "Create Calendar", true);
    this.callback = callback;

    setSize(500, 550);
    setLocationRelativeTo(parent);
    setLayout(new BorderLayout(10, 10));

    formPanel = new BasicCalendarFormPanel();
    add(formPanel, BorderLayout.NORTH);

    DialogButtonPanel buttonPanel = new DialogButtonPanel();
    buttonPanel.getSaveButton().addActionListener(e -> handleSave());
    buttonPanel.getCancelButton().addActionListener(e -> handleCancel());
    add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Handle save action by validating input and sending collected data to callback.
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
   * Validate form input before saving.
   *
   * @return true if input is valid, false otherwise
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
   * Collect calendar data from form fields.
   *
   * @return calendar data
   */
  private CalendarData collectCalendarData() {
    String name = formPanel.getName();
    String timezone = formPanel.getTimezone();
    return new CalendarData(name, timezone);
  }
}
