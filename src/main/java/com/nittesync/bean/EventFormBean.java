package com.nittesync.bean;

import java.io.Serializable;
import java.time.LocalDate;

import com.nittesync.model.Event;
import com.nittesync.model.User;
import com.nittesync.service.EventService;
import com.nittesync.util.DataAccessException;
import com.nittesync.util.FacesUtil;
import com.nittesync.util.SessionUtil;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Backing bean of event-form.xhtml, used for both creating and editing.
 *
 * @ViewScoped keeps the typed values alive while the page is open, which is
 * exactly what a form needs. The same bean handles both cases:
 *   event-form.xhtml              -> create a new event
 *   event-form.xhtml?eventId=3    -> edit the existing event
 */
@Named("eventFormBean")
@ViewScoped
public class EventFormBean implements Serializable {

    // @ViewScoped is a passivating scope: Weld requires the bean, and everything
    // it holds, to be serializable so the view can be stored between requests.
    private static final long serialVersionUID = 1L;

    private final EventService eventService = new EventService();

    private Integer eventId;
    private String title;
    private String description;
    private String venue;
    private LocalDate eventDate;

    /** True when an existing event was loaded for editing. */
    private boolean editing;

    /** Called by <f:viewAction> when the page is opened with ?eventId=... */
    public void loadForEdit() {
        if (eventId == null) {
            return;   // create mode
        }

        User user = SessionUtil.getCurrentUser();
        if (user == null || !User.ROLE_CLUB.equals(user.getRole())) {
            FacesUtil.addError("Only club accounts can edit events.");
            return;
        }

        try {
            Event event = eventService.getEvent(eventId);
            if (event == null) {
                FacesUtil.addError("That event does not exist.");
                return;
            }
            if (event.getOrganizerId() != user.getId()) {
                FacesUtil.addError("You can only edit events created by your own club.");
                return;
            }

            title = event.getTitle();
            description = event.getDescription();
            venue = event.getVenue();
            eventDate = event.getEventDate();
            editing = true;
        } catch (DataAccessException e) {
            FacesUtil.addError(e.getMessage());
        }
    }

    /** Action method of the Save button. */
    public String save() {
        User user = SessionUtil.getCurrentUser();

        Event event = new Event();
        event.setId(eventId == null ? 0 : eventId);
        event.setTitle(trim(title));
        event.setDescription(trim(description));
        event.setVenue(trim(venue));
        event.setEventDate(eventDate);

        try {
            if (editing) {
                eventService.updateEvent(event, user);
                FacesUtil.keepMessagesForRedirect();
                FacesUtil.addInfo("Event updated. Submit it again for approval.");
            } else {
                eventService.createEvent(event, user);
                FacesUtil.keepMessagesForRedirect();
                FacesUtil.addInfo("Event created. Submit it for approval when you are ready.");
            }
            return "club-events?faces-redirect=true";
        } catch (IllegalStateException | DataAccessException e) {
            FacesUtil.addError(e.getMessage());
            return null;   // stay on the form so the input is not lost
        }
    }

    /** Action method of the Cancel button. */
    public String cancel() {
        return "club-events?faces-redirect=true";
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    // ------------------------------------------------------ form properties

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public boolean isEditing() {
        return editing;
    }
}