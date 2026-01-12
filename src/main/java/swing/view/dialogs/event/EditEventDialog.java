package swing.view.dialogs.event;


import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;
import model.calendar.CalendarModelInterface;
import model.calendar.Weekday;
import model.event.EventReadOnlyInterface;
import swing.controller.callbacks.DialogCallbackInterface;
import swing.view.dialogs.event.data.BaseData;

/**
 * A dialog for editing an existing event. Extends {@link CreateEventDialog} and pre-fills the form
 * fields based on the selected event. If the event belongs to a series, the repeat options are
 * shown and populated.
 */
public class EditEventDialog extends CreateEventDialog {
  private final CalendarModelInterface calendar;
  private final String calendarName;
  private final EventReadOnlyInterface originalEvent;
  private final DialogCallbackInterface<BaseData> editCallback;

  /**
   * Create an Edit Event dialog for the given event.
   *
   * @param parent        parent frame
   * @param originalEvent event being edited
   * @param callback      callback to handle the edited event data
   * @param calendar      active calendar containing the event
   * @param calendarName  name of the calendar the event belongs to
   */
  public EditEventDialog(JFrame parent,
                         EventReadOnlyInterface originalEvent,
                         DialogCallbackInterface<BaseData> callback,
                         CalendarModelInterface calendar,
                         String calendarName) {

    super(parent, null, List.of(calendarName));

    this.calendar = calendar;
    this.calendarName = calendarName;
    this.originalEvent = originalEvent;
    this.editCallback = callback;

    setTitle("Edit Event");
    preFillEventData();
    formPanel.setCalendarName(calendarName);
    formPanel.disableCalendarSelection();

    isRepeat.setEnabled(false);
    boolean isSeriesEvent = calendar.isSeriesEvent(originalEvent);
    if (isSeriesEvent) {
      preFillSeriesData();
    } else {
      repeatOptionsPanel.setVisible(false);
    }

    for (ActionListener al : buttonPanel.getSaveButton().getActionListeners()) {
      buttonPanel.getSaveButton().removeActionListener(al);
    }
    buttonPanel.getSaveButton().addActionListener(e -> handleEditSave());
  }

  /**
   * Handle save action for an edited event.
   * Validates input, constructs updated event data,
   * and passes it to the callback.
   */
  private void handleEditSave() {
    if (invalidateInput()) {
      return;
    }

    BaseData data = new BaseData(
        formPanel.getSubject(),
        formPanel.getStartDateTime(),
        formPanel.getEndDateTime(),
        formPanel.getLocationText(),
        formPanel.getDescriptionText(),
        formPanel.getStatusText(),
        isAllDay.isSelected(),
        calendarName
    );

    editCallback.onSave(data);
    dispose();
  }

  /**
   * Pre-fill the form with the original event's basic details.
   */
  private void preFillEventData() {
    formPanel.setSubject(originalEvent.getSubject());
    formPanel.setStartDateTime(originalEvent.getStartDateTime());
    formPanel.setEndDateTime(originalEvent.getEndDateTime());
    formPanel.setLocationText(originalEvent.getLocation());
    formPanel.setDescriptionText(originalEvent.getDescription());

    if (originalEvent.getStatus() != null) {
      formPanel.setStatus(originalEvent.getStatus().toString());
    }
  }

  /**
   * Pre-fill repeat options for a series event, including weekdays, end date, and occurrence count.
   */
  private void preFillSeriesData() {
    isRepeat.setSelected(true);
    handleRepeatToggle();

    Set<Weekday> weekdays = calendar.getSeriesWeekdays(originalEvent);
    LocalDate until = calendar.getSeriesUntilEnd(originalEvent);
    Integer occurrence = calendar.getSeriesOccurrence(originalEvent);

    repeatOptionsPanel.setSelectedWeekdays(weekdays);
    repeatOptionsPanel.setUntilDate(until);
    repeatOptionsPanel.setOccurrences(occurrence);
  }
}
