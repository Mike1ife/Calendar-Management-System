package swing.view.panels;

import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import swing.view.listeners.EventActionListener;

/**
 * MonthViewPanel represents a calendar grid view for a specific month. The panel is arranged as a
 * 6-by-7 grid, allowing all days of the month to be displayed along with any necessary padding
 * cells. Each cell in the grid is represented by a DayCellPanel, which displays the day number and
 * the events associated with that date. The panel provides methods for navigating between months
 * and for updating the view to reflect the current system month. Rendering logic automatically
 * assigns dates to the correct cells based on the month's structure.
 */
public class MonthViewPanel extends JPanel {
  private final List<DayCellPanel> dayCells;
  private YearMonth currentMonth;

  /**
   * Constructs a MonthViewPanel and initializes the underlying 6-by-7 grid. Each cell in the grid
   * is represented by a DayCellPanel, and together they form a visual representation of a monthly
   * calendar. The current month is initially set to the system's current month. The panel layout
   * includes spacing and padding for readability, and the month is rendered immediately after
   * construction.
   */
  public MonthViewPanel() {
    this.dayCells = new ArrayList<>();
    this.currentMonth = YearMonth.now();

    setLayout(new GridLayout(6, 7, 2, 2));
    setBorder(new EmptyBorder(10, 10, 10, 10));

    for (int i = 0; i < 42; i++) {
      DayCellPanel dayCell = new DayCellPanel();
      dayCells.add(dayCell);
      add(dayCell);
    }

    renderMonth();
  }

  /**
   * Moves the calendar to the next month and refreshes the grid to display the new month's
   * structure and dates.
   */
  public void nextMonth() {
    this.currentMonth = this.currentMonth.plusMonths(1);
    renderMonth();
  }

  /**
   * Moves the calendar to the previous month and refreshes the grid to show the updated month.
   */
  public void previousMonth() {
    this.currentMonth = this.currentMonth.minusMonths(1);
    renderMonth();
  }

  /**
   * Resets the calendar to the system's current month and updates the displayed grid to match.
   */
  public void goToToday() {
    this.currentMonth = YearMonth.now();
    renderMonth();
  }

  /**
   * Returns the month currently displayed by the panel.
   *
   * @return the YearMonth representing the currently active month
   */
  public YearMonth getCurrentMonth() {
    return currentMonth;
  }

  /**
   * Returns a copy of the list of DayCellPanel objects used in the grid. This method provides
   * access to the individual day cells without allowing direct modification of the internal list.
   *
   * @return a new list containing all DayCellPanel instances
   */
  public List<DayCellPanel> getDayCells() {
    return new ArrayList<>(dayCells);
  }

  /**
   * Renders the month currently stored in the currentMonth field.
   * This method performs the following steps:
   * 1. Clears all existing day cells.
   * 2. Determines the day of the week on which the first day of the month falls.
   * 3. Populates the appropriate cells with the correct date numbers.
   * 4. Leaves any unused cells blank as padding.
   * After assigning all dates, the panel is revalidated and repainted to visually update the grid.
   */
  private void renderMonth() {
    LocalDate firstOfMonth = currentMonth.atDay(1);

    int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;

    int daysInMonth = currentMonth.lengthOfMonth();

    for (DayCellPanel cell : dayCells) {
      cell.clear();
    }

    for (int day = 1; day <= daysInMonth; day++) {
      int cellIndex = dayOfWeek + day - 1;
      LocalDate date = firstOfMonth.plusDays(day - 1);

      dayCells.get(cellIndex).setDateNumber(String.valueOf(day));
      dayCells.get(cellIndex).setDate(date);
    }

    revalidate();
    repaint();
  }

  /**
   * Assigns an EventActionListener to all DayCellPanel instances in this grid. The listener will be
   * notified when a user clicks on a day cell.
   *
   * @param listener the EventActionListener for day cell interactions
   */
  public void setListener(EventActionListener listener) {
    for (DayCellPanel dayCell : dayCells) {
      dayCell.setListener(listener);
    }
  }
}
