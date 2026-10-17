package com.nittesync.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import com.nittesync.model.Registration;
import com.nittesync.util.DBConnection;
import com.nittesync.util.DataAccessException;

/**
 * All SQL that touches the registrations table.
 *
 * Duplicate registrations are stopped twice: first by exists() in this class
 * and, as the final guarantee, by the UNIQUE constraint uq_student_event in
 * the database.
 */
public class RegistrationDAO {

    /** Is this student already registered for this event? */
    public boolean exists(int studentId, int eventId) {
        String sql = "SELECT 1 FROM registrations WHERE student_id = ? AND event_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);
            statement.setInt(2, eventId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not check the existing registrations.", e);
        }
    }

    /**
     * Inserts one registration.
     *
     * @return true when the row was inserted, false when the UNIQUE constraint
     *         rejected it because the student is already registered
     */
    public boolean insert(int studentId, int eventId) {
        String sql = "INSERT INTO registrations (student_id, event_id) VALUES (?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);
            statement.setInt(2, eventId);

            return statement.executeUpdate() == 1;
        } catch (SQLIntegrityConstraintViolationException e) {
            // Duplicate key: this student already has a row for this event.
            return false;
        } catch (SQLException e) {
            throw new DataAccessException("Could not save the registration.", e);
        }
    }

    /** The registrations of one student, with the event details filled in. */
    public List<Registration> findByStudent(int studentId) {
        // student_name is selected here as well, because mapRow() reads it for
        // both list queries.
        String sql = "SELECT r.id, r.student_id, r.event_id, r.registration_date, "
                   + "u.name AS student_name, e.title AS event_title, e.event_date, e.venue "
                   + "FROM registrations r "
                   + "JOIN users u ON u.id = r.student_id "
                   + "JOIN events e ON e.id = r.event_id "
                   + "WHERE r.student_id = ? ORDER BY e.event_date";
        List<Registration> registrations = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    registrations.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load your registrations.", e);
        }
        return registrations;
    }

    /** Every registration, with student and event names - for the administrator. */
    public List<Registration> findAll() {
        String sql = "SELECT r.id, r.student_id, r.event_id, r.registration_date, "
                   + "u.name AS student_name, e.title AS event_title, e.event_date, e.venue "
                   + "FROM registrations r "
                   + "JOIN users u ON u.id = r.student_id "
                   + "JOIN events e ON e.id = r.event_id "
                   + "ORDER BY r.registration_date DESC";
        List<Registration> registrations = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                registrations.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load the registrations.", e);
        }
        return registrations;
    }

    /** How many events one student has registered for - shown on the dashboard. */
    public int countByStudent(int studentId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE student_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not count your registrations.", e);
        }
    }

    /**
     * Copies the current row of the ResultSet into a Registration object.
     * Both list queries above join users and events, so student_name,
     * event_title, event_date and venue are always present.
     */
    private Registration mapRow(ResultSet resultSet) throws SQLException {
        Registration registration = new Registration();
        registration.setId(resultSet.getInt("id"));
        registration.setStudentId(resultSet.getInt("student_id"));
        registration.setEventId(resultSet.getInt("event_id"));

        java.sql.Timestamp registrationDate = resultSet.getTimestamp("registration_date");
        if (registrationDate != null) {
            registration.setRegistrationDate(registrationDate.toLocalDateTime());
        }

        registration.setEventTitle(resultSet.getString("event_title"));

        java.sql.Date eventDate = resultSet.getDate("event_date");
        if (eventDate != null) {
            registration.setEventDate(eventDate.toLocalDate());
        }

        registration.setVenue(resultSet.getString("venue"));
        registration.setStudentName(resultSet.getString("student_name"));
        return registration;
    }
}