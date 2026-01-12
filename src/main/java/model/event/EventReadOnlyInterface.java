package model.event;

import java.time.LocalDateTime;

/**
 * The IEventReadOnly interface serves as a marker or functional contract for read-only access
 * to event data in the context of the application. Classes implementing this interface are
 * expected to provide read-only operations related to event details without allowing
 * modifications to the event state.
 *
 * <p>This interface facilitates the separation of concerns and ensures that only read-related
 * operations are exposed, supporting immutability and safeguarding data integrity for event
 * objects.
 */
public interface EventReadOnlyInterface {
  /**
   * Get the subject of this event.
   *
   * @return event subject
   */
  String getSubject();

  /**
   * Get the start date and time of this event.
   *
   * @return start date and time
   */
  LocalDateTime getStartDateTime();

  /**
   * Get the end date and time of this event.
   *
   * @return end date and time
   */
  LocalDateTime getEndDateTime();

  /**
   * Get the location of this event.
   *
   * @return event location, or {@code null} if none is set
   */
  String getLocation();

  /**
   * Get the description of this event.
   *
   * @return event description, or {@code null} if none is set
   */
  String getDescription();

  /**
   * Get the status of this event (e.g., confirmed, tentative).
   *
   * @return event status, or {@code null} if none is set
   */
  EventStatus getStatus();
}
