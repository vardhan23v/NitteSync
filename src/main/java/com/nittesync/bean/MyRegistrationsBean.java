package com.nittesync.bean;

import java.util.ArrayList;
import java.util.List;

import com.nittesync.model.Registration;
import com.nittesync.model.User;
import com.nittesync.service.RegistrationService;
import com.nittesync.util.DataAccessException;
import com.nittesync.util.FacesUtil;
import com.nittesync.util.SessionUtil;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean of my-registrations.xhtml: the events the logged-in student
 * has registered for.
 */
@Named("myRegistrationsBean")
@RequestScoped
public class MyRegistrationsBean {

    private final RegistrationService registrationService = new RegistrationService();

    private List<Registration> registrations = new ArrayList<>();

    @PostConstruct
    public void load() {
        User user = SessionUtil.getCurrentUser();
        if (user == null) {
            return;
        }
        try {
            registrations = registrationService.getRegistrationsForStudent(user.getId());
        } catch (DataAccessException e) {
            FacesUtil.addError(e.getMessage());
        }
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }
}