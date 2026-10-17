package com.nittesync.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.nittesync.model.Event;
import com.nittesync.util.DBConnection;
import com.nittesync.util.DataAccessException;

/**
 * All SQL that touches the events table: SELECT, INSERT, UPDATE and DELETE.
 *
 * Every statement uses ? placeholders filled in with PreparedStatement, so
 * user input is never concatenated into SQL.
 */
public class EventDAO implements Serializable {

    // Serializable because EventService (held by a view scoped bean) holds a
    // reference to this DAO. The class has no state of its own.
    private static final long serialVersionUID = 1L;

    /**
     * The column list shared by all read queries.
     * organizer_name comes from the JOIN with users, registration_count from
     * a COUNT over the registrations table.
     */
    private static final String SELECT_EVENTS =
            "SELECT e.id, e.title, e.description, e.venue, e.event_date, e.organizer_id, e.status, e.created_at, "
          + "u.name AS organizer_name, "
          + "(SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.id) AS registration_count "
          + "FROM events e JOIN users u ON u.id = e.organizer_id ";

    private static final String ORDER_BY_EVENT_DATE = " ORDER BY e.event_date";
    private static final String ORDER_BY_CREATED_AT = " ORDER BY e.created_at DESC";

    /** Events a student is allowed to see. */
    public List<Event> findApproved() {
        String sql = SELECT_EVENTS + "WHERE e.status = ?" + ORDER_BY_EVENT_DATE;
        List<Event> events = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, Event.STATUS_APPROVED);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    events.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load the approved events.", e);
        }
        return events;
    }

    /** Every event, whatever its status - used by the administrator page. */
    public List<Event> findAll() {
        String sql = SELECT_EVENTS + ORDER_BY_CREATED_AT;
        List<Event> events = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                events.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load the events.", e);
        }
        return events;
    }

    /** The events created by one club. */
    public List<Event> findByOrganizer(int organizerId) {
        String sql = SELECT_EVENTS + "WHERE e.organizer_id = ?" + ORDER_BY_CREATED_AT;
        List<Event> events = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, organizerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    events.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load the events of this club.", e);
        }
        return events;
    }

    /** Events in one status, for example all events waiting for approval. */
    public List<Event> findByStatus(String status) {
        String sql = SELECT_EVENTS + "WHERE e.status = ?" + ORDER_BY_CREATED_AT;
        List<Event> events = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    events.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load the events with status " + status + ".", e);
        }
        return events;
    }

    /**
     * Loads one event.
     *
     * @return the Event, or null when the id does not exist
     */
    public Event findById(int eventId) {
        String sql = SELECT_EVENTS + "WHERE e.id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, eventId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load the event.", e);
        }
    }

    /** Inserts a new event. */
    public boolean insert(Event event) {
        String sql = "INSERT INTO events (title, description, venue, event_date, organizer_id, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, event.getTitle());
            statement.setString(2, event.getDescription());
            statement.setString(3, event.getVenue());
            setDate(statement, 4, event.getEventDate());
            statement.setInt(5, event.getOrganizerId());
            statement.setString(6, event.getStatus());

            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException("Could not save the new event.", e);
        }
    }

    /**
     * Updates an event owned by the given club and puts it back to DRAFT,
     * so that an edited event has to be approved again.
     *
     * The organizer_id in the WHERE clause means a club can never modify
     * another club's event, even if someone tampers with the page.
     */
    public boolean update(Event event, int organizerId) {
        String sql = "UPDATE events SET title = ?, description = ?, venue = ?, event_date = ?, status = ? "
                   + "WHERE id = ? AND organizer_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, event.getTitle());
            statement.setString(2, event.getDescription());
            statement.setString(3, event.getVenue());
            setDate(statement, 4, event.getEventDate());
            statement.setString(5, Event.STATUS_DRAFT);
            statement.setInt(6, event.getId());
            statement.setInt(7, organizerId);

            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException("Could not update the event.", e);
        }
    }

    /** Changes only the status: used to submit, approve or reject an event. */
    public boolean updateStatus(int eventId, String status) {
        String sql = "UPDATE events SET status = ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, eventId);

            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException("Could not change the event status.", e);
        }
    }

    /** Deletes an event owned by the given club. */
    public boolean delete(int eventId, int organizerId) {
        String sql = "DELETE FROM events WHERE id = ? AND organizer_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, eventId);
            statement.setInt(2, organizerId);

            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete the event.", e);
        }
    }

    /** LocalDate has no direct JDBC setter, so it is converted to java.sql.Date. */
    private void setDate(PreparedStatement statement, int index, LocalDate date) throws SQLException {
        if (date == null) {
            statement.setNull(index, Types.DATE);
        } else {
            statement.setDate(index, java.sql.Date.valueOf(date));
        }
    }

    /** Copies the current row of the ResultSet into an Event object. */
    private Event mapRow(ResultSet resultSet) throws SQLException {
        Event event = new Event();
        event.setId(resultSet.getInt("id"));
        event.setTitle(resultSet.getString("title"));
        event.setDescription(resultSet.getString("description"));
        event.setVenue(resultSet.getString("venue"));

        java.sql.Date eventDate = resultSet.getDate("event_date");
        if (eventDate != null) {
            event.setEventDate(eventDate.toLocalDate());
        }

        event.setOrganizerId(resultSet.getInt("organizer_id"));
        event.setStatus(resultSet.getString("status"));

        java.sql.Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            event.setCreatedAt(createdAt.toLocalDateTime());
        }

        event.setOrganizerName(resultSet.getString("organizer_name"));
        event.setRegistrationCount(resultSet.getInt("registration_count"));
        return event;
    }
}