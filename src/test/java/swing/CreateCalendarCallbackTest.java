package swing;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import model.calendar.CalendarManagerInterface;
import org.junit.Before;
import org.junit.Test;
import swing.controller.callbacks.CreateCalendarCallback;
import swing.mock.MockCalendarGuiView;
import swing.mock.MockCalendarManager;
import swing.view.CalendarGuiViewInterface;
import swing.view.dialogs.calendar.CalendarData;

/**
 * Tests for {@link CreateCalendarCallback}, verifying that saving or canceling a calendar
 * creation request triggers the correct interactions with the calendar manager and the GUI view.
 * All method calls are recorded using mock objects, and assertions check the expected outcomes.
 */
public class CreateCalendarCallbackTest {
  private StringBuilder calendarManagerLog;
  private StringBuilder guiViewLog;
  private CalendarManagerInterface mockCalendarManager;
  private CalendarGuiViewInterface mockCalendarGuiView;

  /**
   * Initializes fresh log buffers and mock dependencies before each test case. This ensures
   * that logs reflect only the interactions triggered by an individual test.
   */
  @Before
  public void setUp() {
    calendarManagerLog = new StringBuilder();
    guiViewLog = new StringBuilder();
    mockCalendarManager = new MockCalendarManager(calendarManagerLog);
    mockCalendarGuiView = new MockCalendarGuiView(guiViewLog);
  }

  @Test
  public void testOnSave() {
    CalendarData data = new CalendarData("Taipei", "Asia/Taipei");
    Map<String, Color> colorMap = new HashMap<>();
    colorMap.put("Taipei", Color.GREEN);
    CreateCalendarCallback callback =
        new CreateCalendarCallback(mockCalendarManager, mockCalendarGuiView, colorMap);

    callback.onSave(data);

    assertEquals("addCalendar: Taipei", calendarManagerLog.toString().trim());
    assertEquals("addCalendar: Taipei", guiViewLog.toString().trim());
    assertEquals("Taipei", callback.getCalendarName());
  }

  @Test
  public void testOnCancel() {
    Map<String, Color> colorMap = new HashMap<>();
    colorMap.put("Taipei", Color.GREEN);
    CreateCalendarCallback callback =
        new CreateCalendarCallback(mockCalendarManager, mockCalendarGuiView, colorMap);

    callback.onCancel();
  }
}
