package com.nittesync.service;

import com.nittesync.dao.UserDAO;
import com.nittesync.model.User;
import com.nittesync.util.PasswordUtil;

/**
 * Login logic.
 *
 * The service returns null for every kind of failure (unknown email, wrong
 * password, empty input) so the login page can show one single message and
 * an attacker learns nothing about which emails exist.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * @param email    what the user typed in the email field
     * @param password what the user typed in the password field
     * @return the logged-in User, or null when the credentials are not valid
     */
    public User login(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        String trimmedEmail = email.trim();
        if (trimmedEmail.isEmpty() || password.isEmpty()) {
            return null;
        }

        User user = userDAO.findByEmail(trimmedEmail);
        if (user == null) {
            return null;
        }

        // The typed password is hashed again and compared with the stored hash.
        if (!PasswordUtil.matches(password, user.getPasswordHash())) {
            return null;
        }
        return user;
    }
}