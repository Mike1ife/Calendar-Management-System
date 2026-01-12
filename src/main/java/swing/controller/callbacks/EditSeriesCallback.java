package swing.controller.callbacks;

import model.calendar.CalendarModelInterface;
import model.event.EventReadOnlyInterface;
import swing.view.dialogs.event.data.SeriesData;

/**
 * Callback for editing events that belong to a series.
 * Determines whether to edit a single instance, all following instances,
 * or the entire series.
 */
public class EditSeriesCallback extends DialogCallbackImpl<SeriesData> {
  private final CalendarModelInterface calendar;
  private final EventReadOnlyInterface originalEvent;

  /**
   * Create a callback handler for editing a series event.
   *
   * @param calendar      calendar model
   * @param originalEvent event being edited
   */
  public EditSeriesCallback(CalendarModelInterface calendar, EventReadOnlyInterface originalEvent) {
    this.calendar = calendar;
    this.originalEvent = originalEvent;
  }

  @Override
  public void onSave(SeriesData data) {
    try {
      if (data.isEditStartFrom()) {
        SeriesEventEditor.handleEditEventStartFrom(calendar, originalEvent, data);
      } else if (data.isEditSeries()) {
        SeriesEventEditor.handleEditSeries(calendar, originalEvent, data);
      } else {
        SingleEventEditor.handleEditSingleEvent(calendar, originalEvent, data);
      }
    } catch (Exception e) {
      showError("Error creating event: " + e.getMessage());
    }
  }

  @Override
  public void onCancel() {
    System.out.println("User cancelled series edition");
  }
}
