package com.nittesync.bean;

import java.util.ArrayList;
import java.util.List;

import com.nittesync.model.Event;
import com.nittesync.service.EventService;
import com.nittesync.util.DataAccessException;
import com.nittesync.util.FacesUtil;
import com.nittesync.util.SessionUtil;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean of admin-approval.xhtml: the events waiting for the
 * administrator's decision.
 */
@Named("adminApprovalBean")
@RequestScoped
public class AdminApprovalBean {

    private final EventService eventService = new EventService();

    private List<Event> pendingEvents = new ArrayList<>();

    @PostConstruct
    public void load() {
        try {
            pendingEvents = eventService.getPendingEvents();
        } catch (DataAccessException e) {
            FacesUtil.addError(e.getMessage());
        }
    }

    /** Action of the "Approve" button. */
    public String approve(int eventId) {
        try {
            eventService.approve(eventId, SessionUtil.getCurrentUser());
            FacesUtil.keepMessagesForRedirect();
            FacesUtil.addInfo("Event approved. Students can now see it.");
            return "admin-approval?faces-redirect=true";
        } catch (IllegalStateException | DataAccessException e) {
            FacesUtil.addError(e.getMessage());
            return null;
        }
    }

    /** Action of the "Reject" button. */
    public String reject(int eventId) {
        try {
            eventService.reject(eventId, SessionUtil.getCurrentUser());
            FacesUtil.keepMessagesForRedirect();
            FacesUtil.addInfo("Event rejected. The club can edit and submit it again.");
            return "admin-approval?faces-redirect=true";
        } catch (IllegalStateException | DataAccessException e) {
            FacesUtil.addError(e.getMessage());
            return null;
        }
    }

    public List<Event> getPendingEvents() {
        return pendingEvents;
    }
}