package swing.view.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JCheckBox;

/**
 * Checkbox component with a customizable colored check box. Provides a hand cursor, anti-aliased
 * rendering, and a custom checkmark style.
 */
public class ColoredCheckBox extends JCheckBox {
  private final Color checkColor;

  /**
   * Create a checkbox with a colored checkbox.
   *
   * @param text       checkbox label
   * @param checkColor color of the checkbox
   */
  public ColoredCheckBox(String text, Color checkColor) {
    super(text);
    this.checkColor = checkColor;
    setOpaque(false);
    setCursor(new Cursor(Cursor.HAND_CURSOR));
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int boxSize = 16;
    int boxY = (getHeight() - boxSize) / 2;

    g2.setColor(checkColor);
    g2.fillRoundRect(0, boxY, boxSize, boxSize, 3, 3);

    if (isSelected()) {
      double luminance =
          (299.0 * checkColor.getRed() + 587 * checkColor.getGreen() + 114 * checkColor.getBlue())
              / 1000;
      g2.setColor(luminance >= 128 ? Color.BLACK : Color.WHITE);
      g2.setStroke(new BasicStroke(2));
      g2.drawLine(3, boxY + 8, 6, boxY + 11);
      g2.drawLine(6, boxY + 11, 13, boxY + 4);
    }

    g2.dispose();

    g.setColor(getForeground());
    g.setFont(getFont());
    g.drawString(getText(), 22, getHeight() / 2 + 5);
  }
}