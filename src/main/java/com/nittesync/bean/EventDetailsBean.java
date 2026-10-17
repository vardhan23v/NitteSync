package com.nittesync.bean;

import com.nittesync.model.Event;
import com.nittesync.model.User;
import com.nittesync.service.EventService;
import com.nittesync.service.RegistrationService;
import com.nittesync.util.DataAccessException;
import com.nittesync.util.FacesUtil;
import com.nittesync.util.SessionUtil;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean of event-details.xhtml.
 *
 * The page is opened as event-details.xhtml?eventId=3. The <f:viewParam> in
 * the page stores that number in the eventId property, and the <f:viewAction>
 * then calls load() once, after the parameter has arrived.
 */
@Named("eventDetailsBean")
@RequestScoped
public class EventDetailsBean {

    private final EventService eventService = new EventService();
    private final RegistrationService registrationService = new RegistrationService();

    private Integer eventId;
    private Event event;
    private boolean alreadyRegistered;

    /** Loads the event shown on the page. Called by <f:viewAction>. */
    public void load() {
        if (eventId == null) {
            FacesUtil.addError("No event was selected.");
            return;
        }

        try {
            event = eventService.getEvent(eventId);
            if (event == null) {
                FacesUtil.addError("That event does not exist.");
                return;
            }

            User user = SessionUtil.getCurrentUser();
            if (user != null && User.ROLE_STUDENT.equals(user.getRole())) {
                alreadyRegistered = registrationService.isRegistered(user.getId(), event.getId());
            }
        } catch (DataAccessException e) {
            FacesUtil.addError(e.getMessage());
        }
    }

    /**
     * Action method of the Register button.
     *
     * The button is only rendered for students, but the check is repeated in
     * RegistrationService - the real protection is on the server.
     */
    public String register() {
        User user = SessionUtil.getCurrentUser();
        try {
            registrationService.register(user, eventId == null ? 0 : eventId);
            FacesUtil.keepMessagesForRedirect();
            FacesUtil.addInfo("You are registered for this event.");
            // Reload the page so the button disappears and the state is fresh.
            return "event-details?faces-redirect=true&includeViewParams=true";
        } catch (IllegalStateException | DataAccessException e) {
            FacesUtil.addError(e.getMessage());
            return null;   // stay on the page and show the message
        }
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Event getEvent() {
        return event;
    }

    public boolean isAlreadyRegistered() {
        return alreadyRegistered;
    }
}