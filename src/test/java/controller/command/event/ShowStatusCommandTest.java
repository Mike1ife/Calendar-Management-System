package controller.command.event;

import static org.junit.Assert.assertTrue;

import controller.command.evet.ShowStatusCommand;
import controller.mock.MockCalendarModel;
import model.calendar.CalendarModelInterface;
import org.junit.Before;
import org.junit.Test;
import view.CalendarTextView;
import view.CalendarViewInterface;

/**
 * Test class for ShowStatusCommand.
 */
public class ShowStatusCommandTest {
  private ShowStatusCommand command;
  private StringBuilder modelLog;
  private CalendarModelInterface mockModel;
  private StringBuilder viewOutput;
  private CalendarViewInterface view;

  /**
   * Create command, log, model, and view before every test.
   */
  @Before
  public void setUp() {
    command = new ShowStatusCommand();
    modelLog = new StringBuilder();
    mockModel = new MockCalendarModel(modelLog);
    viewOutput = new StringBuilder();
    view = new CalendarTextView(viewOutput);
  }

  @Test
  public void testShowStatus() {
    String cmd = "show status on 2025-10-24T10:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("isBusy"));
    assertTrue(modelLog.toString().contains("2025-10-24T10:00"));
    assertTrue(viewOutput.toString().contains("BUSY"));
  }

  @Test
  public void testShowStatusWithExtraWords() {
    String cmd = "show xyz status abc on def 2025-10-24T10:00";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("isBusy"));
    assertTrue(modelLog.toString().contains("2025-10-24T10:00"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidShowStatusCommand() {
    String cmd = "show status on invalid-date";
    command.execute(cmd, mockModel, view);
  }
}