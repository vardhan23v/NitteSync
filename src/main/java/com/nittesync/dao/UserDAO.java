package com.nittesync.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.nittesync.model.User;
import com.nittesync.util.DBConnection;
import com.nittesync.util.DataAccessException;

/**
 * All SQL that touches the users table.
 *
 * The application never inserts or updates users: the accounts come from
 * sample-data.sql. Only lookups are needed here.
 */
public class UserDAO {

    private static final String FIND_BY_EMAIL =
            "SELECT id, name, email, password_hash, role, created_at FROM users WHERE email = ?";

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