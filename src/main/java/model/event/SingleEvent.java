package model.event;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * This class represents a SingleEvent.
 */
public class SingleEvent implements EventInterface {
  private final String subject;
  private final LocalDateTime startDateTime;
  private final LocalDateTime endDateTime;
  private final String description;
  private final String location;
  private final EventStatus status;

  /**
   * Create an SingleEvent with given subject, start date/time, end date/time, description,
   * location, and status.
   *
   * @param subject       event subject
   * @param startDateTime event start date and time
   * @param endDateTime   event end date and time
   * @param description   event description
   * @param location      event location
   * @param status        event status
   */
  private SingleEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                      String description, String location, EventStatus status) {
    this.subject = subject;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.description = description;
    this.location = location;
    this.status = status;
  }

  @Override
  public String getSubject() {
    return this.subject;
  }

  @Override
  public LocalDateTime getStartDateTime() {
    return this.startDateTime;
  }

  @Override
  public LocalDateTime getEndDateTime() {
    return this.endDateTime;
  }

  @Override
  public String getLocation() {
    return this.location;
  }

  @Override
  // TODO: test it with Event Read Only
  public String getDescription() {
    return this.description;
  }

  @Override
  // TODO: test it with Event Read Only
  public EventStatus getStatus() {
    return this.status;
  }

  @Override
  public SingleEventBuilder toBuilder() {
    return new SingleEventBuilder()
        .setSubject(this.subject)
        .setStartDateTime(this.startDateTime)
        .setEndDateTime(this.endDateTime)
        .setDescription(this.description)
        .setLocation(this.location)
        .setStatus(this.status);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    SingleEvent that = (SingleEvent) o;
    return this.subject.equals(that.subject)
        && this.startDateTime.equals(that.startDateTime)
        && this.endDateTime.equals(that.endDateTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.subject, this.startDateTime, this.endDateTime);
  }

  /**
   * This class extends EventBuilder to build SingleEvent.
   */
  public static class SingleEventBuilder implements EventBuilderInterface {
    private String subject;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String description;
    private String location;
    private EventStatus status;

    /**
     * Create an empty builder.
     */
    public SingleEventBuilder() {
    }

    @Override
    public SingleEventBuilder setSubject(String subject) {
      this.subject = subject;
      return this;
    }

    @Override
    public SingleEventBuilder setStartDateTime(LocalDateTime startDateTime) {
      this.startDateTime = startDateTime;
      return this;
    }

    @Override
    public SingleEventBuilder setEndDateTime(LocalDateTime endDateTime) {
      this.endDateTime = endDateTime;
      return this;
    }

    @Override
    public SingleEventBuilder setEndTime(LocalTime endTime) {
      this.endDateTime = LocalDateTime.of(this.endDateTime.toLocalDate(), endTime);
      return this;
    }

    @Override
    public SingleEventBuilder setDescription(String description) {
      this.description = description;
      return this;
    }

    @Override
    public SingleEventBuilder setLocation(String location) {
      this.location = location;
      return this;
    }

    @Override
    public SingleEventBuilder setStatus(EventStatus status) {
      this.status = status;
      return this;
    }


    @Override
    public EventInterface build() {
      return new SingleEvent(this.subject, this.startDateTime, this.endDateTime, this.description,
          this.location, this.status);
    }
  }
}