package com.nittesync.bean;

import java.util.ArrayList;
import java.util.List;

import com.nittesync.model.Event;
import com.nittesync.service.EventService;
import com.nittesync.util.DataAccessException;
import com.nittesync.util.FacesUtil;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean of events.xhtml: the list of approved events a student can
 * register for.
 */
@Named("eventListBean")
@RequestScoped
public class EventListBean {

    private final EventService eventService = new EventService();

    private List<Event> events = new ArrayList<>();

    @PostConstruct
    public void load() {
        try {
            events = eventService.getApprovedEvents();
        } catch (DataAccessException e) {
            FacesUtil.addError(e.getMessage());
        }
    }

    public List<Event> getEvents() {
        return events;
    }
}