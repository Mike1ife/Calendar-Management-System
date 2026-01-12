package swing.view.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import model.event.EventReadOnlyInterface;
import swing.view.components.EventWidget;
import swing.view.listeners.EventActionListener;

/**
 * DayCellPanel represents a single day within a calendar grid. The panel displays the day number
 * and a list of events occurring on that day. It is divided into two sections: a header area
 * showing the date, and a vertical container displaying each event as an EventWidget.The panel also
 * supports user interaction. When clicked, the associated EventActionListener is notified so that
 * higher-level components can open daily event views or perform other actions. If the represented
 * date is the current date, the panel highlights itself automatically.
 */
public class DayCellPanel extends JPanel {
  private final List<EventWidget> eventWidgets;
  private final JLabel dateLabel;
  private final JPanel eventsContainer;
  private LocalDate date;

  private EventActionListener listener;

  /**
   * Constructs a DayCellPanel and initializes the visual layout used to represent a day in the
   * calendar. The panel includes a header label for the day number and a container for event
   * widgets. Background color, border styling, and mouse interaction are also configured here.
   */
  public DayCellPanel() {
    this.eventWidgets = new ArrayList<>();

    setLayout(new BorderLayout());
    setPreferredSize(new Dimension(120, 90));
    setBorder(new LineBorder(Color.GRAY, 1));
    setBackground(Color.WHITE);

    dateLabel = new JLabel("", JLabel.RIGHT);
    dateLabel.setBorder(new javax.swing.border.EmptyBorder(5, 5, 5, 5));
    add(dateLabel, BorderLayout.NORTH);

    eventsContainer = new JPanel();
    eventsContainer.setLayout(new BoxLayout(eventsContainer, BoxLayout.Y_AXIS));
    eventsContainer.setBackground(Color.WHITE);
    add(eventsContainer, BorderLayout.CENTER);

    setCursor(new Cursor(Cursor.HAND_CURSOR));
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
        if (date != null && listener != null) {
          listener.onDayClicked(date);
        }
      }
    });
  }

  /**
   * Assigns an EventActionListener so that day-click events can be forwarded to higher-level
   * handlers.
   *
   * @param listener the listener responsible for handling day interactions
   */
  public void setListener(EventActionListener listener) {
    this.listener = listener;
  }

  /**
   * Sets the numeric day label shown at the top of this cell.
   *
   * @param day the day number text to display
   */
  public void setDateNumber(String day) {
    this.dateLabel.setText(day);
  }

  /**
   * Returns the LocalDate associated with this day cell.
   *
   * @return the date for this panel, or null if none is set
   */
  public LocalDate getDate() {
    return this.date;
  }

  /**
   * Assigns the date represented by this cell and updates its appearance. If the date is today, the
   * background is highlighted.
   *
   * @param date the LocalDate to assign to this cell
   */
  public void setDate(LocalDate date) {
    this.date = date;

    if (date != null && date.equals(LocalDate.now())) {
      setBackground(new Color(230, 240, 255));
      eventsContainer.setBackground(new Color(230, 240, 255));
    } else {
      setBackground(Color.WHITE);
      eventsContainer.setBackground(Color.WHITE);
    }
  }

  /**
   * Adds an event to this day cell. The event is wrapped in an EventWidget and displayed inside the
   * internal event container. The panel is refreshed to show the new event visually.
   *
   * @param event the event to display
   * @param color the color associated with the calendar the event belongs to
   */
  public void addEvent(EventReadOnlyInterface event, Color color) {
    EventWidget widget = new EventWidget(event, color);
    widget.setListener(this.listener);
    this.eventWidgets.add(widget);

    updateEventDisplay();
  }

  /**
   * Removes all events from this day cell. This includes clearing the internal list of event
   * widgets and removing all components from the event container. The panel is refreshed afterward.
   */
  public void clearAllEvents() {
    this.eventWidgets.clear();
    this.eventsContainer.removeAll();
    revalidate();
    repaint();
  }

  /**
   * Fully resets the DayCellPanel to its default state. This includes:
   * 1. Clearing the date label.
   * 2. Removing all displayed events.
   * 3. Resetting the stored date to null.
   * 4. Restoring the background to white.
   */
  public void clear() {
    dateLabel.setText("");
    clearAllEvents();
    date = null;
    setBackground(Color.WHITE);
    eventsContainer.setBackground(Color.WHITE);
  }

  /**
   * Updates the event display within the day cell panel by refreshing the list of visible events
   * and adding a summary for additional hidden events, if applicable. This method operates as
   * follows:
   * 1. Clears all existing components from the event container.
   * 2. Determines the maximum number of event widgets to display (default is 3).
   * 3. Adds up to the specified maximum number of event widgets to the container.
   * 4. If there are more events than the maximum specified, appends a "more" label showing
   * the number of hidden events. This label interacts with user clicks to notify listeners
   * through the associated event listener if defined.
   * 5. Revalidates and repaints the panel to reflect the updates.
   */
  private void updateEventDisplay() {
    eventsContainer.removeAll();

    int maxVisible = 2;
    int totalEvents = eventWidgets.size();

    for (int i = 0; i < Math.min(maxVisible, totalEvents); i++) {
      if (i > 0) {
        eventsContainer.add(Box.createRigidArea(new Dimension(0, 2)));
      }
      eventsContainer.add(eventWidgets.get(i));
    }

    if (totalEvents > maxVisible) {
      JLabel moreLabel = new JLabel("+" + (totalEvents - maxVisible) + " more");
      moreLabel.setFont(new Font("Arial", Font.PLAIN, 13));
      moreLabel.setForeground(Color.GRAY);
      moreLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
      moreLabel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
          if (listener != null) {
            listener.onDayClicked(date);
          }
        }
      });
      eventsContainer.add(Box.createRigidArea(new Dimension(0, 2)));
      eventsContainer.add(moreLabel);
    }

    revalidate();
    repaint();
  }
}
