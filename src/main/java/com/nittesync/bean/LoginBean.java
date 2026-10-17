package com.nittesync.bean;

import com.nittesync.model.User;
import com.nittesync.service.AuthService;
import com.nittesync.util.DataAccessException;
import com.nittesync.util.FacesUtil;
import com.nittesync.util.SessionUtil;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Backing bean of login.xhtml and of the navigation menu.
 *
 * @RequestScoped: the form fields only live for one request. The logged-in
 * user is kept in the HTTP session (see SessionUtil), not in this bean.
 */
@Named("loginBean")
@RequestScoped
public class LoginBean {

    private final AuthService authService = new AuthService();

    private String email;
    private String password;

    /** One-time message from AuthFilter, shown after an unauthorized page request. */
    private String authMessage;
    private boolean authMessageLoaded;

    /**
     * Action method of the login form.
     *
     * @return "loginSuccess" or "loginFailure" - both outcomes are mapped to
     *         pages in faces-config.xml
     */
    public String login() {
        try {
            User user = authService.login(email, password);
            if (user == null) {
                FacesUtil.addError("Invalid email or password.");
                return "loginFailure";
            }

            SessionUtil.setCurrentUser(user);
            password = null;   // never keep the password in memory
            FacesUtil.keepMessagesForRedirect();
            FacesUtil.addInfo("Welcome back, " + user.getName() + ".");
            return "loginSuccess";
        } catch (DataAccessException e) {
            // For example: MySQL is not running.
            FacesUtil.addError(e.getMessage());
            return "loginFailure";
        }
    }

    /** Action method of the Logout link: drops the whole session. */
    public String logout() {
        SessionUtil.clearSession();
        return "logout";
    }

    // ------------------------------------------------- used by the template

    public boolean isLoggedIn() {
        return SessionUtil.getCurrentUser() != null;
    }

    public User getCurrentUser() {
        return SessionUtil.getCurrentUser();
    }

    public boolean isStudent() {
        return SessionUtil.hasRole(User.ROLE_STUDENT);
    }

    public boolean isClub() {
        return SessionUtil.hasRole(User.ROLE_CLUB);
    }

    public boolean isAdmin() {
        return SessionUtil.hasRole(User.ROLE_ADMIN);
    }

    /**
     * The message AuthFilter left behind when somebody opened a page without
     * the required role. It is read once and then removed from the session.
     */
    public String getAuthMessage() {
        if (!authMessageLoaded) {
            authMessage = SessionUtil.consumeAuthMessage();
            authMessageLoaded = true;
        }
        return authMessage;
    }

    // ------------------------------------------------------ form properties

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
}