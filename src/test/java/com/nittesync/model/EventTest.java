package com.nittesync.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EventTest {

    @Test
    @DisplayName("Event status constants match lifecycle stages")
    void testEventStatusConstants() {
        assertEquals("DRAFT", Event.STATUS_DRAFT);
        assertEquals("PENDING", Event.STATUS_PENDING);
        assertEquals("APPROVED", Event.STATUS_APPROVED);
        assertEquals("REJECTED", Event.STATUS_REJECTED);
    }

    @Test
    @DisplayName("Event getters and setters preserve field values")
    void testEventGettersAndSetters() {
        Event event = new Event();
        event.setId(101);
        event.setTitle("Hackathon 2026");
        event.setDescription("Annual 24-hour campus hackathon.");
        event.setVenue("Auditorium B");
        LocalDate eventDate = LocalDate.of(2026, 12, 1);
        event.setEventDate(eventDate);
        event.setOrganizerId(5);
        event.setStatus(Event.STATUS_DRAFT);
        LocalDateTime now = LocalDateTime.now();
        event.setCreatedAt(now);
        event.setOrganizerName("Coding Club");
        event.setRegistrationCount(45);

        assertEquals(101, event.getId());
        assertEquals("Hackathon 2026", event.getTitle());
        assertEquals("Annual 24-hour campus hackathon.", event.getDescription());
        assertEquals("Auditorium B", event.getVenue());
        assertEquals(eventDate, event.getEventDate());
        assertEquals(5, event.getOrganizerId());
        assertEquals(Event.STATUS_DRAFT, event.getStatus());
        assertEquals(now, event.getCreatedAt());
        assertEquals("Coding Club", event.getOrganizerName());
        assertEquals(45, event.getRegistrationCount());
        assertNotNull(event.toString());
    }
}
