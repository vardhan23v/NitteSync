package com.nittesync.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;

import com.nittesync.model.User;
import com.nittesync.util.DBConnection;
import com.nittesync.util.DataAccessException;

/**
 * All SQL that touches the users table.
 *
 * Accounts are created by the public registration page (or seeded by
 * sample-data.sql); the application still never updates an existing user.
 */
public class UserDAO {

    private static final String FIND_BY_EMAIL =
            "SELECT id, name, email, password_hash, role, created_at FROM users WHERE email = ?";

    private static final String INSERT_USER =
            "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";

    /**
     * Looks up one account by email address.
     *
     * @return the User, or null when no account uses that email
     */
    public User findByEmail(String email) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_EMAIL)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not look up the user account.", e);
        }
    }

    /**
     * Inserts a new account and copies the generated id back into the object.
     *
     * The UNIQUE constraint on users.email is the final guard: if two requests
     * race past the "email already exists" check, the second INSERT fails here.
     *
     * @throws DataAccessException with a readable message when the email is
     *         already taken or the database is unavailable
     */
    public void create(User user) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DataAccessException("An account with that email already exists.", e);
        } catch (SQLException e) {
            throw new DataAccessException("Could not create the account.", e);
        }
    }

    /** Copies the current row of the ResultSet into a User object. */
    private User mapRow(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setName(resultSet.getString("name"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setRole(resultSet.getString("role"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        return user;
    }
}