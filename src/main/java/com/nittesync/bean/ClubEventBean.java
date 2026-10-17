package com.nittesync.bean;

import java.util.ArrayList;
import java.util.List;

import com.nittesync.model.Event;
import com.nittesync.model.User;
import com.nittesync.service.EventService;
import com.nittesync.util.DataAccessException;
import com.nittesync.util.FacesUtil;
import com.nittesync.util.SessionUtil;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean of club-events.xhtml: the events created by the logged-in club,
 * with the Edit / Submit for approval / Delete actions.
 */
@Named("clubEventBean")
@RequestScoped
public class ClubEventBean {

    private final EventService eventService = new EventService();

    private List<Event> events = new ArrayList<>();

    @PostConstruct
    public void load() {
        User user = SessionUtil.getCurrentUser();
        if (user == null) {
            return;
        }
        try {
            events = eventService.getEventsByOrganizer(user.getId());
        } catch (DataAccessException e) {
            FacesUtil.addError(e.getMessage());
        }
    }

    /** Action of the "Submit for approval" button. */
    public String submit(int eventId) {
        try {
            eventService.submitForApproval(eventId, SessionUtil.getCurrentUser());
            FacesUtil.keepMessagesForRedirect();
            FacesUtil.addInfo("Event submitted for approval.");
            return "club-events?faces-redirect=true";
        } catch (IllegalStateException | DataAccessException e) {
            FacesUtil.addError(e.getMessage());
            return null;
        }
    }

    /** Action of the "Delete" button. */
    public String delete(int eventId) {
        try {
            eventService.deleteEvent(eventId, SessionUtil.getCurrentUser());
            FacesUtil.keepMessagesForRedirect();
            FacesUtil.addInfo("Event deleted.");
            return "club-events?faces-redirect=true";
        } catch (IllegalStateException | DataAccessException e) {
            FacesUtil.addError(e.getMessage());
            return null;
        }
    }

    /** DRAFT and REJECTED events may still be changed. */
    public boolean canEdit(Event event) {
        return event != null
                && (Event.STATUS_DRAFT.equals(event.getStatus()) || Event.STATUS_REJECTED.equals(event.getStatus()));
    }

    /** DRAFT and REJECTED events can be (re-)submitted. */
    public boolean canSubmit(Event event) {
        return canEdit(event);
    }

    public List<Event> getEvents() {
        return events;
    }
}