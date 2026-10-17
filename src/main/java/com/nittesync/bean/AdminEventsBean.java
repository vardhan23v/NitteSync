package com.nittesync.bean;

import java.util.ArrayList;
import java.util.List;

import com.nittesync.model.Event;
import com.nittesync.model.Registration;
import com.nittesync.service.EventService;
import com.nittesync.service.RegistrationService;
import com.nittesync.util.DataAccessException;
import com.nittesync.util.FacesUtil;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean of admin-events.xhtml: every event with its status, and every
 * registration in the system.
 */
@Named("adminEventsBean")
@RequestScoped
public class AdminEventsBean {

    private final EventService eventService = new EventService();
    private final RegistrationService registrationService = new RegistrationService();

    private List<Event> allEvents = new ArrayList<>();
    private List<Registration> registrations = new ArrayList<>();

    @PostConstruct
    public void load() {
        try {
            allEvents = eventService.getAllEvents();
            registrations = registrationService.getAllRegistrations();
        } catch (DataAccessException e) {
            FacesUtil.addError(e.getMessage());
        }
    }

    public List<Event> getAllEvents() {
        return allEvents;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }
}