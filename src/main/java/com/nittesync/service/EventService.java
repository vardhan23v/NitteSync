package com.nittesync.service;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.nittesync.dao.EventDAO;
import com.nittesync.model.Event;
import com.nittesync.model.User;

/**
 * Business rules for events.
 *
 * The beans call these methods instead of talking to the DAO directly, so
 * every rule (who may do what, which status change is allowed, which input
 * is valid) is enforced on the server in one place.
 *
 * A rule that the user breaks is reported with IllegalStateException carrying
 * a message that can be shown on the page. A database failure arrives as a
 * DataAccessException from the DAO.
 */
public class EventService implements Serializable {

    // Serializable so that the view scoped EventFormBean, which holds a
    // reference to this service, stays passivation capable.
    private static final long serialVersionUID = 1L;

    private final EventDAO eventDAO = new EventDAO();

    // ---------------------------------------------------------------- reads

    /** Events visible to students. */
    public List<Event> getApprovedEvents() {
        return eventDAO.findApproved();
    }

    /** Every event, for the administrator. */
    public List<Event> getAllEvents() {
        return eventDAO.findAll();
    }

    /** The events created by one club. */
    public List<Event> getEventsByOrganizer(int organizerId) {
        return eventDAO.findByOrganizer(organizerId);
    }

    /** Events waiting for the administrator's decision. */
    public List<Event> getPendingEvents() {
        return eventDAO.findByStatus(Event.STATUS_PENDING);
    }

    /** @return the event, or null when the id does not exist */
    public Event getEvent(int eventId) {
        return eventDAO.findById(eventId);
    }

    // -------------------------------------------------------------- writes

    /** A club creates a new event. It starts as DRAFT. */
    public void createEvent(Event event, User organizer) {
        requireRole(organizer, User.ROLE_CLUB, "Only club accounts can create events.");
        validate(event);

        event.setOrganizerId(organizer.getId());
        event.setStatus(Event.STATUS_DRAFT);

        if (!eventDAO.insert(event)) {
            throw new IllegalStateException("The event could not be saved. Please try again.");
        }
    }

    /** A club edits its own event. Editing puts the event back to DRAFT. */
    public void updateEvent(Event event, User organizer) {
        requireRole(organizer, User.ROLE_CLUB, "Only club accounts can edit events.");
        validate(event);

        Event existing = loadOwnedEvent(event.getId(), organizer);
        if (Event.STATUS_APPROVED.equals(existing.getStatus())) {
            throw new IllegalStateException("Approved events cannot be edited.");
        }

        if (!eventDAO.update(event, organizer.getId())) {
            throw new IllegalStateException("The event could not be updated. Please try again.");
        }
    }

    /** A club deletes its own event, unless it has already been approved. */
    public void deleteEvent(int eventId, User organizer) {
        requireRole(organizer, User.ROLE_CLUB, "Only club accounts can delete events.");

        Event existing = loadOwnedEvent(eventId, organizer);
        if (Event.STATUS_APPROVED.equals(existing.getStatus())) {
            throw new IllegalStateException("Approved events cannot be deleted.");
        }

        if (!eventDAO.delete(eventId, organizer.getId())) {
            throw new IllegalStateException("The event could not be deleted. Please try again.");
        }
    }

    /** A club sends a DRAFT or REJECTED event to the administrator. */
    public void submitForApproval(int eventId, User organizer) {
        requireRole(organizer, User.ROLE_CLUB, "Only club accounts can submit events.");

        Event existing = loadOwnedEvent(eventId, organizer);
        if (!(Event.STATUS_DRAFT.equals(existing.getStatus()) || Event.STATUS_REJECTED.equals(existing.getStatus()))) {
            throw new IllegalStateException("Only draft or rejected events can be submitted for approval.");
        }

        eventDAO.updateStatus(eventId, Event.STATUS_PENDING);
    }

    /** The administrator approves an event that is waiting for approval. */
    public void approve(int eventId, User admin) {
        requireRole(admin, User.ROLE_ADMIN, "Only an administrator can approve events.");
        changeStatusOfPendingEvent(eventId, Event.STATUS_APPROVED, "approved");
    }

    /** The administrator rejects an event that is waiting for approval. */
    public void reject(int eventId, User admin) {
        requireRole(admin, User.ROLE_ADMIN, "Only an administrator can reject events.");
        changeStatusOfPendingEvent(eventId, Event.STATUS_REJECTED, "rejected");
    }

    // ------------------------------------------------------------- helpers

    /** Loads an event and checks that the given club is its organizer. */
    private Event loadOwnedEvent(int eventId, User organizer) {
        Event existing = eventDAO.findById(eventId);
        if (existing == null) {
            throw new IllegalStateException("That event no longer exists.");
        }
        if (existing.getOrganizerId() != organizer.getId()) {
            throw new IllegalStateException("You can only change events created by your own club.");
        }
        return existing;
    }

    private void changeStatusOfPendingEvent(int eventId, String newStatus, String action) {
        Event existing = eventDAO.findById(eventId);
        if (existing == null) {
            throw new IllegalStateException("That event no longer exists.");
        }
        if (!Event.STATUS_PENDING.equals(existing.getStatus())) {
            throw new IllegalStateException("Only events waiting for approval can be " + action + ".");
        }
        eventDAO.updateStatus(eventId, newStatus);
    }

    /** Server side input validation: the page can be bypassed, this cannot. */
    private void validate(Event event) {
        if (event.getTitle() == null || event.getTitle().trim().length() < 3) {
            throw new IllegalStateException("The title must be at least 3 characters long.");
        }
        if (event.getTitle().trim().length() > 150) {
            throw new IllegalStateException("The title must not be longer than 150 characters.");
        }
        if (event.getVenue() == null || event.getVenue().trim().isEmpty()) {
            throw new IllegalStateException("The venue is required.");
        }
        if (event.getVenue().trim().length() > 150) {
            throw new IllegalStateException("The venue must not be longer than 150 characters.");
        }
        if (event.getEventDate() == null) {
            throw new IllegalStateException("The event date is required.");
        }
        if (event.getEventDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("The event date cannot be in the past.");
        }
    }

    private void requireRole(User user, String role, String message) {
        if (user == null || !role.equals(user.getRole())) {
            throw new IllegalStateException(message);
        }
    }
}