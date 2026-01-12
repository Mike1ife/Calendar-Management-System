package swing.controller.callbacks;

import model.calendar.CalendarManagerInterface;
import swing.view.CalendarGuiViewInterface;
import swing.view.dialogs.calendar.CalendarData;

/**
 * Callback for editing an existing calendar. Handles saving updated calendar data and applying
 * changes to the model and view.
 */
public class EditCalendarCallback extends DialogCallbackImpl<CalendarData> {
  private final CalendarManagerInterface model;
  private final CalendarGuiViewInterface view;
  private final String currentCalendarName;
  private String newCalendarName;

  /**
   * Create a callback handler for calendar editing.
   *
   * @param model               calendar manager model
   * @param view                calendar GUI view
   * @param currentCalendarName name of the calendar being edited
   */
  public EditCalendarCallback(CalendarManagerInterface model, CalendarGuiViewInterface view,
                              String currentCalendarName) {
    this.model = model;
    this.view = view;
    this.currentCalendarName = currentCalendarName;
  }

  @Override
  public void onSave(CalendarData data) {
    try {
      handleEditCalendar(data);
    } catch (Exception e) {
      showError("Error creating event: " + e.getMessage());
    }
  }

  @Override
  public void onCancel() {
    System.out.println("User cancelled calendar edition");
  }

  public String getNewCalendarName() {
    return this.newCalendarName;
  }

  /**
   * Apply calendar name and timezone changes in the model, then update the view accordingly.
   *
   * @param data updated calendar data
   */
  private void handleEditCalendar(CalendarData data) {
    if (!this.currentCalendarName.equals(data.getName())) {
      this.model.editCalendar(this.currentCalendarName, "name", data.getName());
    }
    this.model.editCalendar(data.getName(), "timezone", data.getTimezone());
    this.newCalendarName = data.getName();
    this.view.updateCalendarName(this.currentCalendarName, this.newCalendarName);
  }
}
