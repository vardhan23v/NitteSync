package com.nittesync.bean;

import java.util.ArrayList;
import java.util.List;

import com.nittesync.model.Event;
import com.nittesync.model.User;
import com.nittesync.service.EventService;
import com.nittesync.service.RegistrationService;
import com.nittesync.util.DataAccessException;
import com.nittesync.util.FacesUtil;
import com.nittesync.util.SessionUtil;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean of dashboard.xhtml.
 *
 * Shows different numbers depending on the role of the logged-in user.
 * @PostConstruct runs once, right after JSF creates the bean, which is where
 * the data is loaded - the getters then only read fields.
 */
@Named("dashboardBean")
@RequestScoped
public class DashboardBean {

    private static final int MAX_UPCOMING_EVENTS = 5;

    private final EventService eventService = new EventService();
    private final RegistrationService registrationService = new RegistrationService();

    private List<Event> upcomingEvents = new ArrayList<>();
    private int myRegistrationCount;
    private int myEventCount;
    private int pendingEventCount;

    @PostConstruct
    public void load() {
        User user = SessionUtil.getCurrentUser();
        if (user == null) {
            return;   // nobody is logged in, so there is nothing to show
        }

        try {
            // The next approved events, at most five of them.
            List<Event> approvedEvents = eventService.getApprovedEvents();
            for (Event event : approvedEvents) {
                if (upcomingEvents.size() == MAX_UPCOMING_EVENTS) {
                    break;
                }
                upcomingEvents.add(event);
            }

            if (User.ROLE_STUDENT.equals(user.getRole())) {
                myRegistrationCount = registrationService.countForStudent(user.getId());
            } else if (User.ROLE_CLUB.equals(user.getRole())) {
                myEventCount = eventService.getEventsByOrganizer(user.getId()).size();
            } else if (User.ROLE_ADMIN.equals(user.getRole())) {
                pendingEventCount = eventService.getPendingEvents().size();
            }
        } catch (DataAccessException e) {
            FacesUtil.addError(e.getMessage());
        }
    }

    public List<Event> getUpcomingEvents() {
        return upcomingEvents;
    }

    public int getUpcomingEventCount() {
        return upcomingEvents.size();
    }

    public int getMyRegistrationCount() {
        return myRegistrationCount;
    }

    public int getMyEventCount() {
        return myEventCount;
    }

    public int getPendingEventCount() {
        return pendingEventCount;
    }
}