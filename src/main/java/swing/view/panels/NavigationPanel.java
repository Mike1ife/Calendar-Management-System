package swing.view.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * NavigationPanel provides the top navigation bar for the calendar view. This
 * panel contains
 * controls for moving between months, returning to the current day, and
 * creating new events. It
 * also displays the current month and year as part of the application header.
 * The panel is divided into two main sections:
 * - A left section displaying an icon and the current month-year label.
 * - A right section containing navigation and action buttons.
 * All buttons are styled consistently for appearance and usability.
 */
public class NavigationPanel extends JPanel {

  private final JButton previousButton;
  private final JButton nextButton;
  private final JLabel currentMonthYearLabel;
  private final JButton createEventButton;
  private final JButton todayButton;

  /**
   * Constructs a new NavigationPanel, initializing all navigation and action
   * controls. The panel is
   * composed of two internal subpanels: one for the display label and icon, and
   * one for the
   * interactive controls such as month navigation, "Today", and event creation.
   * Visual styling,
   * fonts, and background colors are applied during initialization to provide a
   * consistent header
   * appearance.
   */
  public NavigationPanel() {
    setLayout(new BorderLayout());
    setBackground(new Color(60, 60, 60));
    setBorder(new EmptyBorder(10, 20, 10, 20));

    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    leftPanel.setOpaque(false);

    ImageIcon originalIcon = new ImageIcon("src/main/source/NEU.png");
    Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
    ImageIcon icon = new ImageIcon(scaledImage);
    JLabel iconLabel = new JLabel(icon);
    leftPanel.add(iconLabel);

    currentMonthYearLabel = new JLabel("November 2025");
    currentMonthYearLabel.setFont(new Font("Arial", Font.BOLD, 28));
    currentMonthYearLabel.setForeground(Color.WHITE);
    leftPanel.add(currentMonthYearLabel);

    add(leftPanel, BorderLayout.WEST);

    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.setOpaque(false);

    JPanel buttonsContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonsContainer.setOpaque(false);

    previousButton = new JButton("<");
    styleButton(previousButton);
    previousButton.setPreferredSize(new Dimension(50, 35));
    buttonsContainer.add(previousButton);

    todayButton = new JButton("Today");
    styleButton(todayButton);
    todayButton.setPreferredSize(new Dimension(80, 35));
    buttonsContainer.add(todayButton);

    nextButton = new JButton(">");
    styleButton(nextButton);
    nextButton.setPreferredSize(new Dimension(50, 35));
    buttonsContainer.add(nextButton);

    createEventButton = new JButton("+ Create Event");
    styleButton(createEventButton);
    createEventButton.setPreferredSize(new Dimension(150, 35));
    buttonsContainer.add(createEventButton);

    rightPanel.add(buttonsContainer, BorderLayout.CENTER);

    add(rightPanel, BorderLayout.EAST);
  }

  /**
   * Applies a consistent visual style to a button, including font, cursor type,
   * and text color.
   * This helper method is used internally to configure all action buttons in the
   * navigation bar.
   *
   * @param button the JButton instance to be styled
   */
  private void styleButton(JButton button) {
    button.setForeground(Color.black);
    button.setFont(new Font("Arial", Font.BOLD, 15));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
  }

  /**
   * Updates the label showing the current month and year.
   *
   * @param monthYear a formatted string representing the targeted month and year
   */
  public void setMonthYearText(String monthYear) {
    currentMonthYearLabel.setText(monthYear);
  }

  /**
   * Returns the button used to navigate to the previous month.
   *
   * @return the "previous month" button
   */
  public JButton getPreviousButton() {
    return previousButton;
  }

  /**
   * Returns the button used to navigate to the next month.
   *
   * @return the "next month" button
   */
  public JButton getNextButton() {
    return nextButton;
  }

  /**
   * Returns the button used to open the event creation dialog.
   *
   * @return the create-event button
   */
  public JButton getCreateEventButton() {
    return createEventButton;
  }

  /**
   * Returns the button that resets the calendar view to the current day.
   *
   * @return the "Today" button
   */
  public AbstractButton getTodayButton() {
    return todayButton;
  }
}
