package swing.controller.callbacks;

import model.calendar.CalendarModelInterface;
import model.event.EventReadOnlyInterface;
import swing.view.dialogs.event.data.EventEditData;

/**
 * Callback for applying edits to an existing event once the user has specified how the event
 * should be modified. This callback determines whether the update applies to only the selected
 * event, to the event and all following occurrences, or to the entire series. Based on this
 * decision, it delegates the actual update logic to the appropriate static editor helper.
 */
public class EditEventOptionCallback extends DialogCallbackImpl<EventEditData> {
  private final CalendarModelInterface calendar;
  private final EventReadOnlyInterface originalEvent;

  /**
   * Constructs an EditEventOptionCallback for processing user-selected edit options on an event.
   *
   * @param calendar      the calendar model through which event updates are performed
   * @param originalEvent the event being edited; used as a reference point for all modifications
   */
  public EditEventOptionCallback(CalendarModelInterface calendar,
                                 EventReadOnlyInterface originalEvent) {
    this.calendar = calendar;
    this.originalEvent = originalEvent;
  }

  @Override
  public void onSave(EventEditData data) {
    try {
      if (data.isStartFrom()) {
        SeriesEventEditor.handleEditEventStartFrom(calendar, originalEvent, data);
      } else if (data.isAllSeries()) {
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
