package swing.controller.callbacks;

import java.awt.Color;
import java.util.Map;
import model.calendar.CalendarManagerInterface;
import swing.view.CalendarGuiViewInterface;
import swing.view.dialogs.calendar.CalendarData;

/**
 * Callback for creating a new calendar.
 * Handles saving calendar data from the dialog and updating the model and view.
 */
public class CreateCalendarCallback extends DialogCallbackImpl<CalendarData>
    implements DialogCallbackInterface<CalendarData> {
  private final CalendarManagerInterface model;
  private final CalendarGuiViewInterface view;
  private final Map<String, Color> colorMap;
  private String createdCalendarName;

  /**
   * Create a callback handler for calendar creation.
   *
   * @param model    calendar manager model
   * @param view     calendar GUI view
   * @param colorMap map of calendar names to colors
   */
  public CreateCalendarCallback(CalendarManagerInterface model, CalendarGuiViewInterface view,
                                Map<String, Color> colorMap) {
    this.model = model;
    this.view = view;
    this.colorMap = colorMap;
  }

  @Override
  public void onSave(CalendarData calendarData) {
    try {
      handleCreateCalendar(calendarData);
    } catch (Exception e) {
      showError("Error creating calendar: " + e.getMessage());
    }
  }

  @Override
  public void onCancel() {
    System.out.println("User cancelled calendar creation");
  }


  /**
   * Get the name of the calendar created in this callback.
   *
   * @return created calendar name
   */
  public String getCalendarName() {
    return this.createdCalendarName;
  }

  /**
   * Create a calendar in the model and add it to the view.
   *
   * @param calendarData calendar data from the dialog
   */
  private void handleCreateCalendar(CalendarData calendarData) {
    this.model.addCalendar(calendarData.getName(), calendarData.getTimezone());
    this.view.addCalendar(calendarData.getName(), this.colorMap.get(calendarData.getName()));
    this.createdCalendarName = calendarData.getName();
  }
}
