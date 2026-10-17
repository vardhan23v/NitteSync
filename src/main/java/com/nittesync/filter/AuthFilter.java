package com.nittesync.filter;

import java.io.IOException;

import com.nittesync.model.User;
import com.nittesync.util.SessionUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Server side gate in front of every *.xhtml page.
 *
 * Two checks happen here, before JSF even starts working:
 *   1. is somebody logged in?
 *   2. does that user have the role the page requires?
 *
 * The same rules are checked again inside the service classes before any data
 * is changed, because a page is never the place where security lives.
 */
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contextPath = httpRequest.getContextPath();
        String path = httpRequest.getRequestURI().substring(contextPath.length());

        // Tomcat appends a path parameter such as ;jsessionid=... to the URI
        // while the browser has no session cookie yet. It is not part of the
        // page name, so it must be removed before comparing.
        int pathParameter = path.indexOf(';');
        if (pathParameter != -1) {
            path = path.substring(0, pathParameter);
        }
        if (path.isEmpty() || "/".equals(path)) {
            path = "/index.xhtml";   // the welcome file
        }

        // Public pages, and JSF resources such as the stylesheet: no login needed.
        if ("/index.xhtml".equals(path) || "/login.xhtml".equals(path)
                || "/register.xhtml".equals(path)
                || path.startsWith("/jakarta.faces.resource/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        User user = session == null ? null : (User) session.getAttribute(SessionUtil.USER_KEY);

        if (user == null) {
            httpResponse.sendRedirect(contextPath + "/login.xhtml");
            return;
        }

        String requiredRole = requiredRole(path);
        if (requiredRole != null && !requiredRole.equals(user.getRole())) {
            session.setAttribute(SessionUtil.AUTH_MESSAGE_KEY, "You are not allowed to open that page.");
            httpResponse.sendRedirect(contextPath + "/dashboard.xhtml");
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Which role may open which page.
     *
     * @return the required role, or null when any logged-in user may open the page
     */
    private String requiredRole(String path) {
        if ("/my-registrations.xhtml".equals(path)) {
            return User.ROLE_STUDENT;
        }
        if ("/club-events.xhtml".equals(path) || "/event-form.xhtml".equals(path)) {
            return User.ROLE_CLUB;
        }
        if ("/admin-approval.xhtml".equals(path) || "/admin-events.xhtml".equals(path)) {
            return User.ROLE_ADMIN;
        }
        return null;   // dashboard, events, event-details: any logged-in user
    }
}