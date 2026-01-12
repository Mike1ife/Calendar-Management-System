package swing.view.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import model.event.EventReadOnlyInterface;
import swing.view.listeners.EventActionListener;

/**
 * Component representing a single event in the calendar view. Displays the event subject with a
 * background color and provides additional details via tooltip.
 */
public class EventWidget extends JLabel {
  private final EventReadOnlyInterface event;
  private EventActionListener listener;

  /**
   * Create an event widget with the given event data and background color.
   * The text color is automatically chosen based on contrast.
   *
   * @param event event data
   * @param color background color for the widget
   */
  public EventWidget(EventReadOnlyInterface event, Color color) {
    this.event = event;

    setText(event.getSubject());

    setOpaque(true);
    setBackground(color);
    double luminance =
        (299.0 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
    setForeground(luminance >= 128 ? Color.BLACK : Color.WHITE);
    setBorder(new EmptyBorder(2, 4, 2, 4));

    setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

    setToolTipText(createTooltip());

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
        if (listener != null) {
          listener.onDayClicked(event.getStartDateTime().toLocalDate());
        }
      }
    });
  }

  /**
   * Sets the listener for handling event-related actions in the calendar view.
   *
   * @param listener the listener to handle actions such as editing events or selecting specific
   *                 days
   */
  public void setListener(EventActionListener listener) {
    this.listener = listener;
  }

  /**
   * Build the tooltip text for this event.
   *
   * @return tooltip string
   */
  private String createTooltip() {
    StringBuilder tooltip = new StringBuilder();
    tooltip.append(event.getSubject());
    tooltip.append(" ");
    tooltip.append(event.getStartDateTime().toLocalTime());
    tooltip.append(" - ");
    tooltip.append(event.getEndDateTime().toLocalTime());

    if (event.getLocation() != null && !event.getLocation().isEmpty()) {
      tooltip.append(" at ").append(event.getLocation());
    }

    return tooltip.toString();
  }

  /**
   * Get the event associated with this widget.
   *
   * @return event data
   */
  public EventReadOnlyInterface getEvent() {
    return this.event;
  }
}
