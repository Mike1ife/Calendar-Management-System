package swing.view.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * WeekHeaderPanel displays a single-row header containing the names of the seven days of the week.
 * Each day is shown as a labeled cell with a consistent background color, border, and centered
 * text. The panel uses a 1x7 GridLayout to ensure even spacing and alignment across all day labels.
 */
public class WeekHeaderPanel extends JPanel {
  /**
   * Constructs a new WeekHeaderPanel and initializes the header row. The panel displays labels for
   * Sunday through Saturday, styled with uniform colors, borders, and fixed dimensions. Labels are
   * centered and arranged in a single horizontal row.
   */
  public WeekHeaderPanel() {
    setLayout(new GridLayout(1, 7, 2, 2));
    setBorder(new EmptyBorder(10, 10, 0, 10));

    String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    for (String dayName : dayNames) {
      JLabel dayLabel = new JLabel(dayName, SwingConstants.CENTER);
      dayLabel.setOpaque(true);
      dayLabel.setBackground(new Color(240, 240, 240));
      dayLabel.setBorder(new LineBorder(Color.GRAY, 1));
      dayLabel.setPreferredSize(new Dimension(120, 30));
      add(dayLabel);
    }
  }
}
