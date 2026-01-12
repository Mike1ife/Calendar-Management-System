import controller.CalendarController;
import controller.MultiCalendarController;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import model.calendar.CalendarManager;
import model.calendar.CalendarManagerInterface;
import swing.controller.CalendarGuiController;
import swing.view.CalendarGuiView;
import view.CalendarTextView;
import view.CalendarViewInterface;

/**
 * Provides an entry point to run the Calendar application in various modes including GUI,
 * interactive text-based, or headless mode. The specific mode is determined based on
 * command-line arguments passed by the user.
 */
public class CalendarRunner {

  /**
   * The entry point of the application. Determines the mode of operation for the application
   * based on the provided command-line arguments and invokes the appropriate mode.
   * If no arguments are provided, the application starts in GUI mode by default.
   * Supported modes include GUI, interactive, and headless mode.
   *
   * @param args command-line arguments to configure the application behavior:
   *             {@code --mode <gui | interactive | headless>} specifies the mode of operation.
   *             For headless mode, an additional argument {@code <filename>} is required,
   *             which specifies the file to read commands from.
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      runGuiMode();
      return;
    }

    String mode = null;
    String filename = null;

    for (int i = 0; i < args.length; i++) {
      if (args[i].equalsIgnoreCase("--mode") && i + 1 < args.length) {
        mode = args[i + 1].toLowerCase();
        i++;
      }
    }

    if (mode == null) {
      System.err.println("Error: --mode argument is required");
      printUsage();
      System.exit(1);
    }

    if (mode.equals("headless")) {
      for (int i = args.length - 1; i >= 0; i--) {
        if (!args[i].equalsIgnoreCase("--mode")
            && (i == 0 || !args[i - 1].equalsIgnoreCase("--mode"))) {
          filename = args[i];
          break;
        }
      }
    }

    try {
      switch (mode) {
        case "gui":
          runGuiMode();
          break;
        case "interactive":
          runInteractiveMode();
          break;
        case "headless":
          runHeadlessMode(filename);
          break;
        default:
          System.err.println("Error: mode must be 'gui', 'interactive', or 'headless'");
          printUsage();
          System.exit(1);
      }
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(1);
    }
  }

  /**
   * Run the application in GUI mode.
   */
  private static void runGuiMode() {
    CalendarManagerInterface model = new CalendarManager();
    CalendarGuiView view = new CalendarGuiView();
    CalendarGuiController controller = new CalendarGuiController(model, view);
    controller.go();
  }

  /**
   * Run the application in interactive text mode.
   */
  private static void runInteractiveMode() {
    CalendarManagerInterface calendarManager = new CalendarManager();
    CalendarViewInterface view = new CalendarTextView(System.out);

    CalendarController controller =
        new MultiCalendarController(calendarManager, view, new InputStreamReader(System.in));

    System.out.println("Calendar Application - Interactive Mode");
    System.out.println("Type 'exit' to quit");
    controller.go();
  }

  /**
   * Run the application in headless mode (reading from a file).
   *
   * @param filename the file to read commands from
   */
  private static void runHeadlessMode(String filename) {
    if (filename == null) {
      System.err.println("Error: filename required for headless mode");
      System.err.println("Usage: java CalendarRunner --mode headless <filename>");
      System.exit(1);
    }

    CalendarManagerInterface calendarManager = new CalendarManager();
    CalendarViewInterface view = new CalendarTextView(System.out);

    try (FileReader fileReader = new FileReader(filename)) {
      CalendarController controller =
          new MultiCalendarController(calendarManager, view, fileReader);

      System.out.println("Calendar Application - Headless Mode");
      System.out.println("Reading commands from: " + filename);
      controller.go();
    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
      System.exit(1);
    }
  }

  /**
   * Print usage information.
   */
  private static void printUsage() {
    System.err.println("Usage:");
    System.err.println("  GUI mode (default):     java CalendarRunner");
    System.err.println("  GUI mode (explicit):    java CalendarRunner --mode gui");
    System.err.println("  Interactive mode:       java CalendarRunner --mode interactive");
    System.err.println("  Headless mode:          java CalendarRunner --mode headless <filename>");
  }
}