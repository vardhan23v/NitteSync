package com.nittesync.util;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

/**
 * Small helpers around FacesMessage, so the beans do not repeat the same
 * four lines every time they want to show something to the user.
 */
public final class FacesUtil {

    private FacesUtil() {
        // utility class: never instantiated
    }

    /** Shows a green "everything worked" message. */
    public static void addInfo(String message) {
        add(FacesMessage.SEVERITY_INFO, message);
    }

    /** Shows a red error message. */
    public static void addError(String message) {
        add(FacesMessage.SEVERITY_ERROR, message);
    }

    /**
     * Adds a global message: it is displayed by <h:messages globalOnly="true"/>
     * in the page template instead of next to one input field.
     */
    private static void add(FacesMessage.Severity severity, String message) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            context.addMessage(null, new FacesMessage(severity, message, message));
        }
    }

    /**
     * Keeps the current messages for the next request.
     *
     * FacesMessages normally die with the request they were created in, so a
     * message added just before returning "somepage.xhtml?faces-redirect=true"
     * would never be seen. Calling this method first stores the messages in
     * the flash scope, where they survive exactly one redirect.
     */
    public static void keepMessagesForRedirect() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            context.getExternalContext().getFlash().setKeepMessages(true);
        }
    }
}