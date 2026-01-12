package swing;

import static org.junit.Assert.assertEquals;

import model.calendar.CalendarManagerInterface;
import org.junit.Before;
import org.junit.Test;
import swing.controller.callbacks.EditCalendarCallback;
import swing.mock.MockCalendarGuiView;
import swing.mock.MockCalendarManager;
import swing.view.CalendarGuiViewInterface;
import swing.view.dialogs.calendar.CalendarData;

/**
 * Tests for {@link EditCalendarCallback}, ensuring that editing a calendar's name or time zone
 * triggers the correct sequence of interactions with both the calendar manager and the GUI view.
 * All interactions are recorded through mock objects and verified by comparing expected and
 * actual log output.
 */
public class EditCalendarCallbackTest {
  private StringBuilder calendarManagerLog;
  private StringBuilder guiViewLog;
  private CalendarManagerInterface mockCalendarManager;
  private CalendarGuiViewInterface mockCalendarGuiView;

  /**
   * Initializes fresh mock objects and log buffers before each test, ensuring test isolation
   * and accuracy of method call tracking.
   */
  @Before
  public void setUp() {
    calendarManagerLog = new StringBuilder();
    guiViewLog = new StringBuilder();
    mockCalendarManager = new MockCalendarManager(calendarManagerLog);
    mockCalendarGuiView = new MockCalendarGuiView(guiViewLog);
  }

  @Test
  public void testOnSave1() {
    EditCalendarCallback callback =
        new EditCalendarCallback(mockCalendarManager, mockCalendarGuiView, "Taipei");
    CalendarData data = new CalendarData("Boston", "America/New_York");
    // current name = Taipei
    // data.name = Taiwan
    // data.timezone = Asia/Taipei
    callback.onSave(data);
    assertEquals(
        "editCalendar: Taipei name Boston\n"
            + "editCalendar: Boston timezone America/New_York",
        calendarManagerLog.toString().trim());
    assertEquals("updateCalendarName: Taipei Boston", guiViewLog.toString().trim());
    assertEquals("Boston", callback.getNewCalendarName());
  }

  @Test
  public void testOnSave2() {
    EditCalendarCallback callback =
        new EditCalendarCallback(mockCalendarManager, mockCalendarGuiView, "Taipei");
    CalendarData data = new CalendarData("Taipei", "America/New_York");
    callback.onSave(data);
    assertEquals(
        "editCalendar: Taipei timezone America/New_York",
        calendarManagerLog.toString().trim());
    assertEquals("updateCalendarName: Taipei Taipei", guiViewLog.toString().trim());
    assertEquals("Taipei", callback.getNewCalendarName());
  }

  @Test
  public void testOnCancel() {
    EditCalendarCallback callback =
        new EditCalendarCallback(mockCalendarManager, mockCalendarGuiView, "Taipei");

    callback.onCancel();
  }
}
