package com.nittesync.model;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserTest {

    @Test
    @DisplayName("User role constants match application expectations")
    void testUserRoleConstants() {
        assertEquals("STUDENT", User.ROLE_STUDENT);
        assertEquals("CLUB", User.ROLE_CLUB);
        assertEquals("ADMIN", User.ROLE_ADMIN);
    }

    @Test
    @DisplayName("User getters and setters preserve field values")
    void testUserGettersAndSetters() {
        User user = new User();
        user.setId(42);
        user.setName("Test Student");
        user.setEmail("student@nitte.edu.in");
        user.setPasswordHash("hash123");
        user.setRole(User.ROLE_STUDENT);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);

        assertEquals(42, user.getId());
        assertEquals("Test Student", user.getName());
        assertEquals("student@nitte.edu.in", user.getEmail());
        assertEquals("hash123", user.getPasswordHash());
        assertEquals(User.ROLE_STUDENT, user.getRole());
        assertEquals(now, user.getCreatedAt());
        assertNotNull(user.toString());
    }
}
