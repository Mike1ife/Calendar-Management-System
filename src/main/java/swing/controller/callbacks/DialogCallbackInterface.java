package swing.controller.callbacks;

/**
 * Callback interface for handling dialog actions.
 * Provides methods for saving data and cancelling the dialog.
 *
 * @param <T> type of data supplied when saving
 */
public interface DialogCallbackInterface<T> {

  /**
   * Save the given data provided by the dialog.
   *
   * @param data dialog data to be saved
   */
  void onSave(T data);

  /**
   * Handle dialog cancellation without saving.
   */
  void onCancel();
}
