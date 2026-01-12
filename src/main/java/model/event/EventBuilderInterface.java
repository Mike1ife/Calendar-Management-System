package model.event;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * This class represent general purposes of a builder of event.
 */
public interface EventBuilderInterface {
  /**
   * Set event subject for builder.
   *
   * @param subject subject
   * @return updated builder
   */
  EventBuilderInterface setSubject(String subject);

  /**
   * Set event start date and time for builder.
   *
   * @param startDateTime start date and time
   * @return updated builder
   */
  EventBuilderInterface setStartDateTime(LocalDateTime startDateTime);

  /**
   * Set event end date and time for builder.
   *
   * @param endDateTime end date and time
   * @return updated builder
   */
  EventBuilderInterface setEndDateTime(LocalDateTime endDateTime);

  /**
   * Set event end time for builder.
   *
   * @param endTime end time
   * @return updated builder
   */
  EventBuilderInterface setEndTime(LocalTime endTime);

  /**
   * Set description for builder.
   *
   * @param description event description
   * @return updated builder
   */
  EventBuilderInterface setDescription(String description);

  /**
   * Set location for builder.
   *
   * @param location event location
   * @return updated builder
   */
  EventBuilderInterface setLocation(String location);

  /**
   * Set status for builder.
   *
   * @param status event status
   * @return updated builder
   */
  EventBuilderInterface setStatus(EventStatus status);


  /**
   * Build SingleEvent.
   *
   * @return SingleEvent
   */
  EventInterface build();
}
