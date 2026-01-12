package swing.controller.callbacks;

import javax.swing.JOptionPane;

/**
 * Abstract base class for dialog callbacks.
 * Provides common helper methods for callback implementations.
 *
 * @param <T> type of data supplied when saving
 */
abstract class DialogCallbackImpl<T> implements DialogCallbackInterface<T> {
  /**
   * Display an error message dialog.
   *
   * @param message error message
   */
  protected void showError(String message) {
    JOptionPane.showMessageDialog(null,
        message,
        "Error",
        JOptionPane.ERROR_MESSAGE);
  }
}
