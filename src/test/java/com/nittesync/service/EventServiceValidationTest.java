package com.nittesync.service;

import java.time.LocalDate;
import com.nittesync.model.Event;
import com.nittesync.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventServiceValidationTest {

    private EventService eventService;
    private User clubUser;
    private User studentUser;

    @BeforeEach
    void setUp() {
        eventService = new EventService();

        clubUser = new User();
        clubUser.setId(10);
        clubUser.setName("Coding Club");
        clubUser.setRole(User.ROLE_CLUB);

        studentUser = new User();
        studentUser.setId(20);
        studentUser.setName("Student User");
        studentUser.setRole(User.ROLE_STUDENT);
    }

    @Test
    @DisplayName("createEvent disallows non-club accounts")
    void testCreateEventNonClubRole() {
        Event event = new Event();
        event.setTitle("Tech Fest");
        event.setVenue("Auditorium");
        event.setEventDate(LocalDate.now().plusDays(10));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
            eventService.createEvent(event, studentUser)
        );
        assertTrue(ex.getMessage().contains("Only club accounts can create events"));
    }

    @Test
    @DisplayName("createEvent validates title length minimum and maximum")
    void testCreateEventTitleValidation() {
        Event shortTitleEvent = new Event();
        shortTitleEvent.setTitle("AB");
        shortTitleEvent.setVenue("Main Hall");
        shortTitleEvent.setEventDate(LocalDate.now().plusDays(5));

        IllegalStateException ex1 = assertThrows(IllegalStateException.class, () ->
            eventService.createEvent(shortTitleEvent, clubUser)
        );
        assertTrue(ex1.getMessage().contains("at least 3 characters"));

        Event longTitleEvent = new Event();
        longTitleEvent.setTitle("A".repeat(151));
        longTitleEvent.setVenue("Main Hall");
        longTitleEvent.setEventDate(LocalDate.now().plusDays(5));

        IllegalStateException ex2 = assertThrows(IllegalStateException.class, () ->
            eventService.createEvent(longTitleEvent, clubUser)
        );
        assertTrue(ex2.getMessage().contains("longer than 150 characters"));
    }

    @Test
    @DisplayName("createEvent validates venue requirement")
    void testCreateEventVenueValidation() {
        Event event = new Event();
        event.setTitle("Valid Title");
        event.setVenue("   ");
        event.setEventDate(LocalDate.now().plusDays(5));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
            eventService.createEvent(event, clubUser)
        );
        assertTrue(ex.getMessage().contains("venue is required"));
    }

    @Test
    @DisplayName("createEvent rejects dates in the past")
    void testCreateEventPastDateValidation() {
        Event event = new Event();
        event.setTitle("Past Event");
        event.setVenue("Campus Ground");
        event.setEventDate(LocalDate.now().minusDays(1));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
            eventService.createEvent(event, clubUser)
        );
        assertTrue(ex.getMessage().contains("cannot be in the past"));
    }

    @Test
    @DisplayName("updateEvent and deleteEvent require club role")
    void testUpdateAndDeleteRequireClubRole() {
        Event event = new Event();
        event.setId(1);
        event.setTitle("Valid Title");
        event.setVenue("Main Hall");
        event.setEventDate(LocalDate.now().plusDays(5));

        IllegalStateException updateEx = assertThrows(IllegalStateException.class, () ->
            eventService.updateEvent(event, studentUser)
        );
        assertTrue(updateEx.getMessage().contains("Only club accounts can edit events"));

        IllegalStateException deleteEx = assertThrows(IllegalStateException.class, () ->
            eventService.deleteEvent(1, studentUser)
        );
        assertTrue(deleteEx.getMessage().contains("Only club accounts can delete events"));
    }
}
