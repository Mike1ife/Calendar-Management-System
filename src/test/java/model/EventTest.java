package model;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import model.event.EventInterface;
import model.event.EventStatus;
import model.event.SingleEvent;
import org.junit.Test;

/**
 * This class represents test for EventInterface and EventBuilderInterface.
 */
public class EventTest {
  @Test
  public void testBuildEvent() {
    EventInterface event = new SingleEvent.SingleEventBuilder()
        .setSubject("PDP")
        .setStartDateTime(LocalDateTime.parse("2025-11-11T13:35"))
        .setEndDateTime(LocalDateTime.parse("2025-11-11T15:15"))
        .setDescription("Programming Design Paradigm")
        .setLocation("SN")
        .setStatus(EventStatus.PRIVATE)
        .build();

    assertEquals("PDP", event.getSubject());
    assertEquals(LocalDateTime.parse("2025-11-11T13:35"), event.getStartDateTime());
    assertEquals(LocalDateTime.parse("2025-11-11T15:15"), event.getEndDateTime());
    assertEquals("Programming Design Paradigm", event.getDescription());
    assertEquals("SN", event.getLocation());
    assertEquals(EventStatus.PRIVATE, event.getStatus());
  }
}
