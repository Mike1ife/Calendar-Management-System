package model.event;

/**
 * This class represent the general purposes of an event containing subject, start, end, location,
 * description, and status.
 */
public interface EventInterface extends EventReadOnlyInterface {
  /**
   * Get a builder with current property values.
   *
   * @return builder
   */
  EventBuilderInterface toBuilder();
}
