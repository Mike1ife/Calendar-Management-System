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
import swing.view.dialogs.event.data.SeriesData;
import swing.view.dialogs.event.forms.DialogButtonPanel;

/**
 * A dialog for selecting how a recurring event should be edited.
 * Provides options to edit a single occurrence, the selected occurrence
 * and all following ones, or the entire series.
 */
public class EditSeriesDialog extends JDialog {
  private final JRadioButton editStartFrom;
  private final JRadioButton editSeries;
  private final DialogCallbackInterface<SeriesData> callback;
  private BaseData editData;

  /**
   * Create an Edit Series dialog.
   *
   * @param parent   parent frame
   * @param callback callback to receive user selection
   */
  public EditSeriesDialog(JFrame parent, DialogCallbackInterface<SeriesData> callback) {
    super(parent, "Edit Series", true);
    this.callback = callback;

    setSize(400, 250);
    setLocationRelativeTo(parent);
    setLayout(new BorderLayout(10, 10));

    JLabel title = new JLabel("Edit Recurring Event");
    title.setBorder(new EmptyBorder(10, 10, 10, 10));
    add(title, BorderLayout.NORTH);

    JRadioButton editSingle = new JRadioButton("This event");
    editSingle.setSelected(true);
    editStartFrom = new JRadioButton("This and following events");
    editSeries = new JRadioButton("All events");

    ButtonGroup group = new ButtonGroup();
    group.add(editSingle);
    group.add(editStartFrom);
    group.add(editSeries);

    JPanel optionsPanel = new JPanel(new GridLayout(3, 1, 8, 8));
    optionsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
    optionsPanel.add(editSingle);
    optionsPanel.add(editStartFrom);
    optionsPanel.add(editSeries);

    add(optionsPanel, BorderLayout.CENTER);

    DialogButtonPanel buttonPanel = new DialogButtonPanel();
    buttonPanel.getSaveButton().addActionListener(e -> handleSave());
    buttonPanel.getCancelButton().addActionListener(e -> handleCancel());
    add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Attach the event edit data produced by the previous dialog.
   *
   * @param editData base event data
   */
  public void setEventEditData(BaseData editData) {
    this.editData = editData;
  }

  /**
   * Disable the "edit series" option when the change cannot be applied to the whole series
   * (e.g., start date changed).
   */
  public void disableEditSeries() {
    editSeries.setEnabled(false);
  }

  /**
   * Handle save helper.
   */
  private void handleSave() {
    SeriesData seriesData = new SeriesData(
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
    callback.onSave(seriesData);
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
