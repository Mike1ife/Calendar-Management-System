package controller.command.event;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import controller.command.evet.ExportCommand;
import controller.mock.MockCalendarModel;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import view.CalendarTextView;
import view.CalendarViewInterface;

/**
 * Test class for ExportCommand with multiple export formats.
 */
public class ExportCommandTest {
  private ExportCommand command;
  private StringBuilder modelLog;
  private MockCalendarModel mockModel;
  private StringBuilder viewOutput;
  private CalendarViewInterface view;

  /**
   * Create command, log, model, and view before every test.
   */
  @Before
  public void setUp() {
    command = new ExportCommand();
    modelLog = new StringBuilder();
    mockModel = new MockCalendarModel(modelLog);
    viewOutput = new StringBuilder();
    view = new CalendarTextView(viewOutput);
  }

  @Test
  public void testCanHandle() {
    assertTrue(command.canHandle("export cal calendar.csv"));
    assertTrue(command.canHandle("export cal events.ics"));
    assertTrue(command.canHandle("EXPORT CAL test.csv"));
    assertTrue(command.canHandle("export xyz cal abc calendar.csv"));
    assertFalse(command.canHandle("create event"));
    assertFalse(command.canHandle("print events"));
  }

  @Test
  public void testExportCsvCommand() {
    String cmd = "export cal test.csv";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("getAllEvents"));
    assertTrue(viewOutput.toString().contains("Exported to:"));
    assertTrue(viewOutput.toString().contains("test.csv"));
  }

  @Test
  public void testExportIcsCommand() {
    String cmd = "export cal test.ics";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("getAllEvents"));
    assertTrue(viewOutput.toString().contains("Exported to:"));
    assertTrue(viewOutput.toString().contains("test.ics"));
  }

  @Test
  public void testExportIcalCommand() {
    String cmd = "export cal test.ical";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("getAllEvents"));
    assertTrue(viewOutput.toString().contains("Exported to:"));
    assertTrue(viewOutput.toString().contains("test.ical"));
  }

  @Test
  public void testExportWithExtraWords() {
    String cmd = "export xyz cal abc calendar.csv";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("getAllEvents"));
    assertTrue(viewOutput.toString().contains("calendar.csv"));
  }

  @Test
  public void testAutoDetectCsvFormat() {
    String cmd = "export cal events.csv";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("getAllEvents"));

    assertTrue(viewOutput.toString().contains("Exported to:"));
  }

  @Test
  public void testAutoDetectIcsFormat() {
    String cmd = "export cal events.ics";
    command.execute(cmd, mockModel, view);

    assertTrue(modelLog.toString().contains("getAllEvents"));
    assertTrue(viewOutput.toString().contains("Exported to:"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExportCommandNoFilename() {
    String cmd = "export cal";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExportCommandUnsupportedFormat() {
    String cmd = "export cal test.txt";
    command.execute(cmd, mockModel, view);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExportCommandNoExtension() {
    String cmd = "export cal testFile";
    command.execute(cmd, mockModel, view);
  }

  @Test
  public void testExportCommandMultipleFormats() {
    String csvCmd = "export cal test1.csv";
    command.execute(csvCmd, mockModel, view);
    assertTrue(viewOutput.toString().contains("test1.csv"));

    viewOutput.setLength(0);
    modelLog.setLength(0);

    String icsCmd = "export cal test2.ics";
    command.execute(icsCmd, mockModel, view);
    assertTrue(viewOutput.toString().contains("test2.ics"));
  }

  @Test
  public void testExportAbsolutePath() {
    String cmd = "export cal myEvents.csv";
    command.execute(cmd, mockModel, view);

    String output = viewOutput.toString();
    assertTrue(output.contains("Exported to:"));
    assertTrue(output.contains("myEvents.csv"));
    assertTrue(output.contains(File.separator) || output.contains("/"));
  }
}