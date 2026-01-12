package swing.view.dialogs.event;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import swing.controller.callbacks.DialogCallbackInterface;
import swing.view.dialogs.event.data.BaseData;
import swing.view.dialogs.event.data.EventEditData;
import swing.view.dialogs.event.forms.DialogButtonPanel;

/**
 * A dialog that allows the user to choose how an existing event should be edited. The user may
 * choose to update only the selected event, update the selected event and all future occurrences
 * in the same series, or update an entire recurring series. This dialog does not modify events
 * directly but instead delivers user-selected edit options back to a callback for further
 * processing.
 */
public class EditEventOptionDialog extends JDialog {
  private final JRadioButton editStartFrom;
  private final JRadioButton editSeries;
  private final DialogCallbackInterface<EventEditData> callback;
  private BaseData editData;

  /**
   * Constructs an EditEventOptionDialog, initializing radio buttons, explanations, and action
   * controls that allow the user to choose how event edits should be applied.
   *
   * @param parent   the parent window used to center this dialog
   * @param callback the callback invoked when the user confirms or cancels editing choices
   */
  public EditEventOptionDialog(JFrame parent, DialogCallbackInterface<EventEditData> callback) {
    super(parent, "Edit Event Option", true);
    this.callback = callback;

    setSize(400, 400);
    setLocationRelativeTo(parent);
    setLayout(new BorderLayout(10, 10));

    JLabel title = new JLabel("Edit Single Event");
    title.setBorder(new EmptyBorder(10, 10, 10, 10));
    add(title, BorderLayout.NORTH);

    JRadioButton editSingle = new JRadioButton("This event only");
    editSingle.setSelected(true);

    editStartFrom = new JRadioButton("This and similar events (update future in series)");
    editSeries = new JRadioButton("This and similar events (update entire series)");

    ButtonGroup group = new ButtonGroup();
    group.add(editSingle);
    group.add(editStartFrom);
    group.add(editSeries);

    JLabel explainSingle = new JLabel("<html>• Only this event will be edited.</html>");
    JLabel explainStartFrom = new JLabel(
        "<html>• If a similar event is in a series → update future events in that series</html>");
    JLabel explainSeries = new JLabel(
        "<html>• If a similar event is in a series → update all events in that series</html>");

    explainSingle.setForeground(java.awt.Color.GRAY);
    explainStartFrom.setForeground(java.awt.Color.GRAY);
    explainSeries.setForeground(java.awt.Color.GRAY);

    explainSingle.setBorder(new EmptyBorder(0, 30, 5, 10));
    explainStartFrom.setBorder(new EmptyBorder(0, 30, 5, 10));
    explainSeries.setBorder(new EmptyBorder(0, 30, 5, 10));

    JPanel optionsPanel = new JPanel(new GridLayout(0, 1, 3, 3));
    optionsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

    optionsPanel.add(editSingle);
    optionsPanel.add(explainSingle);

    optionsPanel.add(editStartFrom);
    optionsPanel.add(explainStartFrom);

    optionsPanel.add(editSeries);
    optionsPanel.add(explainSeries);

    JLabel hintLabel = new JLabel("Similar = same subject and start date/time");
    hintLabel.setBorder(new EmptyBorder(0, 20, 10, 20));

    JPanel centerWrapper = new JPanel(new BorderLayout());
    centerWrapper.add(optionsPanel, BorderLayout.CENTER);
    centerWrapper.add(hintLabel, BorderLayout.SOUTH);

    add(centerWrapper, BorderLayout.CENTER);

    DialogButtonPanel buttonPanel = new DialogButtonPanel();
    buttonPanel.getSaveButton().addActionListener(e -> handleSave());
    buttonPanel.getCancelButton().addActionListener(e -> handleCancel());
    add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Attach the event edit data produced by the previous dialog.
   *
   * @param editData event edition data
   */
  public void setEventEditData(BaseData editData) {
    this.editData = editData;
  }

  /**
   * Handle save helper.
   */
  private void handleSave() {
    EventEditData eventEditData = new EventEditData(
        this.editData.getSubject(),
        this.editData.getStartDateTime(),
        this.editData.getEndDateTime(),
        this.editData.getLocation(),
        this.editData.getDescription(),
        this.editData.getStatus(),
        this.editData.isAllDay(),
        this.editData.getCalendarName(),
        editStartFrom.isSelected(),
        editSeries.isSelected()
    );
    callback.onSave(eventEditData);
    dispose();
  }

  /**
   * Handle cancel helper.
   */
  private void handleCancel() {
    callback.onCancel();
    dispose();
  }
}
