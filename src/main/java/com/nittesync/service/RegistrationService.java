package com.nittesync.service;

import java.util.List;

import com.nittesync.dao.EventDAO;
import com.nittesync.dao.RegistrationDAO;
import com.nittesync.model.Event;
import com.nittesync.model.Registration;
import com.nittesync.model.User;

/**
 * Business rules for registrations.
 *
 * register() is where the "no duplicate registration" rule lives. It checks
 * the table first and the UNIQUE constraint in the database catches anything
 * that slips through two simultaneous requests.
 */
public class RegistrationService {

    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private final EventDAO eventDAO = new EventDAO();

    /**
     * Registers a student for an approved event.
     *
     * @throws IllegalStateException when the caller is not a student, when the
     *         event is not open for registration, or when the student is
     *         already registered for it
     */
    public void register(User student, int eventId) {
        if (student == null || !User.ROLE_STUDENT.equals(student.getRole())) {
            throw new IllegalStateException("Only student accounts can register for events.");
        }

        Event event = eventDAO.findById(eventId);
        if (event == null) {
            throw new IllegalStateException("That event no longer exists.");
        }
        if (!Event.STATUS_APPROVED.equals(event.getStatus())) {
            throw new IllegalStateException("This event is not open for registration.");
        }

        // Duplicate check before the INSERT ...
        if (registrationDAO.exists(student.getId(), eventId)) {
            throw new IllegalStateException("You are already registered for this event.");
        }

        // ... and the UNIQUE constraint as the final guarantee.
        if (!registrationDAO.insert(student.getId(), eventId)) {
            throw new IllegalStateException("You are already registered for this event.");
        }
    }

    /** Is this student already registered for this event? */
    public boolean isRegistered(int studentId, int eventId) {
        return registrationDAO.exists(studentId, eventId);
    }

    /** The registrations of one student, with the event details. */
    public List<Registration> getRegistrationsForStudent(int studentId) {
        return registrationDAO.findByStudent(studentId);
    }

    /** Every registration, for the administrator page. */
    public List<Registration> getAllRegistrations() {
        return registrationDAO.findAll();
    }

    /** How many events one student registered for - shown on the dashboard. */
    public int countForStudent(int studentId) {
        return registrationDAO.countByStudent(studentId);
    }
}