package swing.view.dialogs.event.forms;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * A simple panel containing Save and Cancel buttons for dialog actions. Provides direct access to
 * both buttons for attaching event listeners.
 */
public class DialogButtonPanel extends JPanel {

  private final JButton saveButton;
  private final JButton cancelButton;

  /**
   * Create a panel with Save and Cancel buttons arranged in a flow layout.
   */
  public DialogButtonPanel() {
    setLayout(new FlowLayout());

    saveButton = new JButton("Save");
    cancelButton = new JButton("Cancel");

    add(saveButton);
    add(cancelButton);
  }

  /**
   * Get the Save button.
   *
   * @return save button
   */
  public JButton getSaveButton() {
    return saveButton;
  }

  /**
   * Get the Cancel button.
   *
   * @return cancel button
   */
  public JButton getCancelButton() {
    return cancelButton;
  }
}
