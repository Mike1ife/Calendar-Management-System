import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.Permission;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for CalendarRunner.
 */
public class CalendarRunnerTest {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;
  private final InputStream originalIn = System.in;
  private File testCommandsFile;
  private SecurityManager originalSecurityManager;

  /**
   * Set up test environment.
   */
  @Before
  public void setUp() throws IOException {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
    originalSecurityManager = System.getSecurityManager();
    System.setSecurityManager(new NoExitSecurityManager());

    testCommandsFile = new File("test_commands.txt");
    try (FileWriter writer = new FileWriter(testCommandsFile)) {
      writer.write("create event \"Test\" from 2025-10-24T10:00 to 2025-10-24T11:00\n");
      writer.write("exit\n");
    }
  }

  /**
   * Tear down the test environment.
   */
  @After
  public void tearDown() {
    System.setOut(originalOut);
    System.setErr(originalErr);
    System.setIn(originalIn);
    System.setSecurityManager(originalSecurityManager);

    deleteFileIfExists(testCommandsFile);
    deleteFileIfExists(new File("invalid_commands.txt"));
    deleteFileIfExists(new File("multiple_commands.txt"));
    deleteFileIfExists(new File("calendar_commands.txt"));
    deleteFileIfExists(new File("empty.txt"));
    deleteFileIfExists(new File("exit_only.txt"));
    deleteFileIfExists(new File("test file.txt"));
  }

  /**
   * Delete a file if it exists.
   */
  private void deleteFileIfExists(File file) {
    if (file != null && file.exists()) {
      if (!file.delete()) {
        System.err.println("Warning: Could not delete file: " + file.getPath());
      }
    }
  }

  @Test
  public void testMissingModeArgument() {
    try {
      CalendarRunner.main(new String[] {"test_commands.txt"});
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit"));
    }

    String errOutput = errContent.toString();
    assertTrue(errOutput.contains("--mode argument is required"));
  }

  @Test
  public void testInvalidMode() {
    try {
      CalendarRunner.main(new String[] {"--mode", "invalid"});
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit"));
    }

    String errOutput = errContent.toString();
    assertTrue(errOutput.contains("Error: mode must be 'gui', 'interactive', or 'headless'"));
  }

  @Test
  public void testHeadlessModeWithoutFilename() {
    try {
      CalendarRunner.main(new String[] {"--mode", "headless"});
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit"));
    }

    String errOutput = errContent.toString();
    assertTrue(errOutput.contains("filename required for headless mode"));
  }

  @Test
  public void testHeadlessModeWithNonExistentFile() {
    try {
      CalendarRunner.main(new String[] {"--mode", "headless", "nonexistent.txt"});
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit"));
    }

    String errOutput = errContent.toString();
    assertTrue(errOutput.contains("Error reading file"));
  }

  @Test
  public void testHeadlessModeWithValidFile() throws IOException {
    try {
      CalendarRunner.main(new String[] {"--mode", "headless", "test_commands.txt"});
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit"));
    }

    String output = outContent.toString();
    assertTrue(output.contains("Calendar Application - Headless Mode"));
    assertTrue(output.contains("Reading commands from: test_commands.txt"));
  }

  @Test
  public void testHeadlessModeCaseInsensitive() throws IOException {
    try {
      CalendarRunner.main(new String[] {"--mode", "HEADLESS", "test_commands.txt"});
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit"));
    }

    String output = outContent.toString();
    assertTrue(output.contains("Calendar Application - Headless Mode"));
  }

  @Test
  public void testModeFlagCaseInsensitive() throws IOException {
    try {
      CalendarRunner.main(new String[] {"--MODE", "headless", "test_commands.txt"});
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit"));
    }

    String output = outContent.toString();
    assertTrue(output.contains("Calendar Application - Headless Mode"));
  }

  @Test
  public void testHeadlessModeArgumentOrder() throws IOException {
    try {
      CalendarRunner.main(new String[] {"test_commands.txt", "--mode", "headless"});
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit"));
    }

    String output = outContent.toString();
    assertTrue(output.contains("Calendar Application - Headless Mode"));
  }

  @Test
  public void testHeadlessModeWithInvalidCommands() throws IOException {
    File invalidFile = new File("invalid_commands.txt");
    try (FileWriter writer = new FileWriter(invalidFile)) {
      writer.write("invalid command\n");
      writer.write("exit\n");
    }

    try {
      CalendarRunner.main(new String[] {"--mode", "headless", "invalid_commands.txt"});
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit"));
    }

    String output = outContent.toString();
    assertTrue(output.contains("Calendar Application - Headless Mode"));
  }

  @Test
  public void testHeadlessModeWithMultipleCommands() throws IOException {
    File multipleCommandsFile = new File("multiple_commands.txt");
    try (FileWriter writer = new FileWriter(multipleCommandsFile)) {
      writer.write("create event \"Meeting\" from 2025-10-24T10:00 to 2025-10-24T11:00\n");
      writer.write("create event \"Birthday\" on 2025-10-25\n");
      writer.write("print events on 2025-10-24\n");
      writer.write("exit\n");
    }

    try {
      CalendarRunner.main(new String[] {"--mode", "headless", "multiple_commands.txt"});
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit"));
    }

    String output = outContent.toString();
    assertTrue(output.contains("Calendar Application - Headless Mode"));
  }

  @Test
  public void testHeadlessModeWithCalendarCommands() throws IOException {
    File calendarCommandsFile = new File("calendar_commands.txt");
    try (FileWriter writer = new FileWriter(calendarCommandsFile)) {
      writer.write("create calendar --name Work --timezone America/New_York\n");
      writer.write("use calendar --name Work\n");
      writer.write("create event \"Meeting\" from 2025-10-24T10:00 to 2025-10-24T11:00\n");
      writer.write("exit\n");
    }

    try {
      CalendarRunner.main(new String[] {"--mode", "headless", "calendar_commands.txt"});
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit"));
    }

    String output = outContent.toString();
    assertTrue(output.contains("Calendar Application - Headless Mode"));
  }

  @Test
  public void testModeFlagPosition() throws IOException {
    try {
      CalendarRunner.main(
          new String[] {"random", "arg", "--mode", "headless", "test_commands.txt"});
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit"));
    }

    String output = outContent.toString();
    assertTrue(output.contains("Calendar Application - Headless Mode"));
  }

  @Test
  public void testHeadlessModeWithRelativePath() throws IOException {
    try {
      CalendarRunner.main(new String[] {"--mode", "headless", "./test_commands.txt"});
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit"));
    }

    String output = outContent.toString();
    assertTrue(output.contains("Calendar Application - Headless Mode"));
  }

  @Test
  public void testEmptyCommandFile() throws IOException {
    File emptyFile = new File("empty.txt");
    try (FileWriter writer = new FileWriter(emptyFile)) {
      writer.write("");
    }

    try {
      CalendarRunner.main(new String[] {"--mode", "headless", "empty.txt"});
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit"));
    }

    String output = outContent.toString();
    assertTrue(output.contains("Calendar Application - Headless Mode"));
  }

  @Test
  public void testCommandFileWithOnlyExit() throws IOException {
    File exitOnlyFile = new File("exit_only.txt");
    try (FileWriter writer = new FileWriter(exitOnlyFile)) {
      writer.write("exit\n");
    }

    try {
      CalendarRunner.main(new String[] {"--mode", "headless", "exit_only.txt"});
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit"));
    }

    String output = outContent.toString();
    assertTrue(output.contains("Calendar Application - Headless Mode"));
  }

  @Test
  public void testHeadlessModeWithMixedCaseMode() throws IOException {
    try {
      CalendarRunner.main(new String[] {"--mode", "HeAdLeSs", "test_commands.txt"});
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit"));
    }

    String output = outContent.toString();
    assertTrue(output.contains("Calendar Application - Headless Mode"));
  }

  @Test
  public void testModeArgumentWithoutDashes() {
    try {
      CalendarRunner.main(new String[] {"mode", "headless", "test_commands.txt"});
    } catch (SecurityException e) {
      assertTrue(e.getMessage().contains("System.exit"));
    }

    String errOutput = errContent.toString();
    assertTrue(errOutput.contains("--mode argument is required"));
  }

  private static class NoExitSecurityManager extends SecurityManager {
    @Override
    public void checkPermission(Permission perm) {
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
    }

    @Override
    public void checkExit(int status) {
      super.checkExit(status);
      throw new SecurityException("System.exit(" + status + ")");
    }
  }
}