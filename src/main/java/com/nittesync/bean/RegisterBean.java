package com.nittesync.bean;

import java.util.Locale;

import com.nittesync.model.User;
import com.nittesync.service.AuthService;
import com.nittesync.util.DataAccessException;
import com.nittesync.util.FacesUtil;
import com.nittesync.util.SessionUtil;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean of register.xhtml.
 *
 * Creates a student or club account, signs the new user in and sends them to
 * the dashboard. Administrators are provisioned, never self-registered.
 */
@Named("registerBean")
@RequestScoped
public class RegisterBean {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final AuthService authService = new AuthService();

    private String name;
    private String email;
    private String password;
    private String confirmPassword;
    private String role = User.ROLE_STUDENT;

    /**
     * Action method of the registration form.
     *
     * Required fields and lengths are checked by JSF first; this method adds
     * the business rules that need the database or two fields together.
     *
     * @return the dashboard after a successful registration, or null to stay
     *         on the form and show the error
     */
    public String register() {
        if (SessionUtil.getCurrentUser() != null) {
            return "dashboard?faces-redirect=true";   // already signed in
        }

        String cleanName = name == null ? "" : name.trim();
        String cleanEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);

        if (cleanName.length() < 2) {
            FacesUtil.addError("Please enter your name.");
            return null;
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            FacesUtil.addError("Choose a password with at least 8 characters.");
            return null;
        }
        if (!password.equals(confirmPassword)) {
            FacesUtil.addError("The two passwords do not match.");
            return null;
        }

        try {
            User user = authService.register(cleanName, cleanEmail, password, role);
            SessionUtil.setCurrentUser(user);
            password = null;
            confirmPassword = null;
            FacesUtil.keepMessagesForRedirect();
            FacesUtil.addInfo("Welcome to NitteSync, " + user.getName() + ".");
            return "dashboard?faces-redirect=true";
        } catch (IllegalArgumentException e) {
            FacesUtil.addError(e.getMessage());
            return null;
        } catch (DataAccessException e) {
            FacesUtil.addError(e.getMessage());
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /** STUDENT or CLUB; the service rejects anything else. */
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
