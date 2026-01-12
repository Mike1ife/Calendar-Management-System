package swing;

import static org.junit.Assert.assertEquals;

import model.calendar.CalendarManagerInterface;
import org.junit.Before;
import org.junit.Test;
import swing.controller.CalendarGuiController;
import swing.mock.MockCalendarGuiView;
import swing.mock.MockCalendarManager;
import swing.view.CalendarGuiViewInterface;

/**
 * Tests for {@link CalendarGuiController} using mock view and mock calendar manager objects.
 * Each test verifies that the controller invokes the expected sequence of method calls on
 * both the model and the view. All interactions are captured via log buffers supplied to
 * the mock objects.
 */
public class CalendarGuiControllerTest {
  private StringBuilder calendarManagerLog;
  private StringBuilder guiViewLog;
  private CalendarGuiController calendarController;

  /**
   * Initializes fresh mock instances and a controller before each test. Logs are cleared
   * and the controller is prepared in its initial state.
   */
  @Before
  public void setUp() {
    calendarManagerLog = new StringBuilder();
    guiViewLog = new StringBuilder();
    CalendarManagerInterface mockCalendarManager = new MockCalendarManager(calendarManagerLog);
    CalendarGuiViewInterface mockCalendarGuiView = new MockCalendarGuiView(guiViewLog);
    calendarController = new CalendarGuiController(mockCalendarManager, mockCalendarGuiView);
  }

  @Test
  public void testGo() {
    calendarController.go();
    String calendarResult = "addCalendar: Default\n";
    String guiViewResult = "addViewListener\n"
        + "setEventActionListener\n"
        + "setCalendarSelectListener\n"
        + "addCalendar: Default\n"
        + "clearAllEvents\n"
        + "getCurrentMonth\n"
        + "getSelectedCalendars\n"
        + "display\n";
    assertEquals(calendarResult, calendarManagerLog.toString());
    assertEquals(guiViewResult, guiViewLog.toString());
  }

  @Test
  public void testHandleToday() {
    calendarController.handleToday();
    String calendarResult = "addCalendar: Default\n";
    String guiViewResult = "addViewListener\n"
        + "setEventActionListener\n"
        + "setCalendarSelectListener\n"
        + "addCalendar: Default\n"
        + "goToToday\n"
        + "clearAllEvents\n"
        + "getCurrentMonth\n"
        + "getSelectedCalendars\n";
    assertEquals(calendarResult, calendarManagerLog.toString());
    assertEquals(guiViewResult, guiViewLog.toString());
  }

  @Test
  public void testHandlePreviousMonth() {
    calendarController.handlePreviousMonth();
    String calendarResult = "addCalendar: Default\n";
    String guiViewResult = "addViewListener\n"
        + "setEventActionListener\n"
        + "setCalendarSelectListener\n"
        + "addCalendar: Default\n"
        + "goToPreviousMonth\n"
        + "clearAllEvents\n"
        + "getCurrentMonth\n"
        + "getSelectedCalendars\n";
    assertEquals(calendarResult, calendarManagerLog.toString());
    assertEquals(guiViewResult, guiViewLog.toString());
  }

  @Test
  public void testHandleNextMonth() {
    calendarController.handleNextMonth();
    String calendarResult = "addCalendar: Default\n";
    String guiViewResult = "addViewListener\n"
        + "setEventActionListener\n"
        + "setCalendarSelectListener\n"
        + "addCalendar: Default\n"
        + "goToNextMonth\n"
        + "clearAllEvents\n"
        + "getCurrentMonth\n"
        + "getSelectedCalendars\n";
    assertEquals(calendarResult, calendarManagerLog.toString());
    assertEquals(guiViewResult, guiViewLog.toString());
  }

  @Test
  public void testOnToggle() {
    calendarController.onToggle();
    String calendarResult = "addCalendar: Default\n";
    String guiViewResult = "addViewListener\n"
        + "setEventActionListener\n"
        + "setCalendarSelectListener\n"
        + "addCalendar: Default\n"
        + "clearAllEvents\n"
        + "getCurrentMonth\n"
        + "getSelectedCalendars\n";
    assertEquals(calendarResult, calendarManagerLog.toString());
    assertEquals(guiViewResult, guiViewLog.toString());
  }
}
