package com.nittesync.service;

import java.util.Locale;
import java.util.regex.Pattern;

import com.nittesync.dao.UserDAO;
import com.nittesync.model.User;
import com.nittesync.util.DataAccessException;
import com.nittesync.util.PasswordUtil;

/**
 * Login and account-creation logic.
 *
 * Login returns null for every kind of failure (unknown email, wrong password,
 * empty input) so the login page can show one single message and an attacker
 * learns nothing about which emails exist. Registration validates its input
 * again here: the HTML form is only a convenience, never the security check.
 */
public class AuthService {

    private static final Pattern EMAIL =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");

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

    /**
     * Creates a student or club account and returns it, ready to be put in the
     * session. Administrators cannot be self-registered.
     *
     * @throws IllegalArgumentException with a message safe to show to the user
     * @throws DataAccessException      when the database rejects the insert
     */
    public User register(String name, String email, String password, String role) {
        String cleanName = name == null ? "" : name.trim();
        String cleanEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);

        if (cleanName.length() < 2 || cleanName.length() > 100) {
            throw new IllegalArgumentException("Please enter your name (2 to 100 characters).");
        }
        if (cleanEmail.isEmpty() || cleanEmail.length() > 150 || !EMAIL.matcher(cleanEmail).matches()) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Choose a password with at least 8 characters.");
        }
        if (!User.ROLE_STUDENT.equals(role) && !User.ROLE_CLUB.equals(role)) {
            throw new IllegalArgumentException("Choose a valid account type.");
        }
        if (userDAO.findByEmail(cleanEmail) != null) {
            throw new IllegalArgumentException("An account with that email already exists.");
        }

        User user = new User();
        user.setName(cleanName);
        user.setEmail(cleanEmail);
        user.setPasswordHash(PasswordUtil.sha256Hex(password));
        user.setRole(role);
        userDAO.create(user);
        return user;
    }
}