package com.nittesync.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * One row of the registrations table: a student registered for an event.
 *
 * studentName, eventTitle, eventDate and venue are not columns of the
 * registrations table - the DAO fills them from the JOIN with users and
 * events so that the same object can be shown on the student page and on
 * the administrator page.
 */
public class Registration {

    private int id;
    private int studentId;
    private int eventId;
    private LocalDateTime registrationDate;

    /** Display only: name of the student who registered. */
    private String studentName;

    /** Display only: title of the registered event. */
    private String eventTitle;

    /** Display only: date of the registered event. */
    private LocalDate eventDate;

    /** Display only: venue of the registered event. */
    private String venue;

    public Registration() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
}