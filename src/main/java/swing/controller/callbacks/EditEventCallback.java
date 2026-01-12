package swing.controller.callbacks;

import java.time.LocalDateTime;
import model.calendar.CalendarModelInterface;
import model.event.EventReadOnlyInterface;
import swing.view.dialogs.event.EditEventOptionDialog;
import swing.view.dialogs.event.EditSeriesDialog;
import swing.view.dialogs.event.data.BaseData;

/**
 * Callback for editing an existing event.
 * Handles saving updated event data and determining whether the edit applies
 * to a single event or a series.
 */
public class EditEventCallback extends DialogCallbackImpl<BaseData> {
  private final EventReadOnlyInterface originalEvent;
  private final boolean isSeriesEvent;
  private final EditEventOptionDialog editEventOptionDialog;
  private final EditSeriesDialog seriesDialog;

  /**
   * Constructs an EditEventCallback for editing a specific event. The constructor determines
   * whether the event is part of a series so that the appropriate dialog can be used during
   * {@link #onSave(BaseData)}.
   *
   * @param calendar              the calendar used to query whether the event is part of a series
   * @param originalEvent         the event the user intends to edit
   * @param editEventOptionDialog dialog used when editing a non-series (single) event
   * @param seriesDialog          dialog used when editing a series or series occurrence
   */
  public EditEventCallback(CalendarModelInterface calendar, EventReadOnlyInterface originalEvent,
                           EditEventOptionDialog editEventOptionDialog,
                           EditSeriesDialog seriesDialog) {
    this.originalEvent = originalEvent;
    this.isSeriesEvent = calendar.isSeriesEvent(originalEvent);
    this.editEventOptionDialog = editEventOptionDialog;
    this.seriesDialog = seriesDialog;
  }

  @Override
  public void onSave(BaseData data) {
    try {
      if (!isSeriesEvent) {
        editEventOptionDialog.setEventEditData(data);
        editEventOptionDialog.setVisible(true);
      } else {
        editSeriesHelper(data);
        seriesDialog.setEventEditData(data);
        seriesDialog.setVisible(true);
      }
    } catch (Exception e) {
      showError("Error creating event: " + e.getMessage());
    }
  }

  @Override
  public void onCancel() {
    System.out.println("User cancelled event edition");
  }

  /**
   * Validate updated series event data and configure the series dialog accordingly.
   *
   * @param data updated event data
   * @throws UnsupportedOperationException if updated event spans more than one day
   */
  private void editSeriesHelper(BaseData data) {
    LocalDateTime newStart = LocalDateTime.parse(data.getStartDateTime());
    LocalDateTime newEnd = LocalDateTime.parse(data.getEndDateTime());
    if (!newStart.toLocalDate().equals(newEnd.toLocalDate())) {
      throw new UnsupportedOperationException("Series event cannot span more than one day");
    }
    if (!LocalDateTime.parse(data.getStartDateTime()).toLocalDate()
        .equals(originalEvent.getStartDateTime().toLocalDate())) {
      seriesDialog.disableEditSeries();
    }
  }
}
