package com.nittesync.service;

import com.nittesync.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServiceValidationTest {

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService();
    }

    @Test
    @DisplayName("login returns null immediately when credentials are null or blank")
    void testLoginWithNullOrBlankCredentials() {
        assertNull(authService.login(null, "password"));
        assertNull(authService.login("user@nitte.edu.in", null));
        assertNull(authService.login("   ", "password"));
        assertNull(authService.login("user@nitte.edu.in", ""));
    }

    @Test
    @DisplayName("register rejects names shorter than 2 or longer than 100 characters")
    void testRegisterNameValidation() {
        IllegalArgumentException shortEx = assertThrows(IllegalArgumentException.class, () ->
            authService.register("A", "test@nitte.edu.in", "password123", User.ROLE_STUDENT)
        );
        assertTrue(shortEx.getMessage().contains("2 to 100 characters"));

        IllegalArgumentException longEx = assertThrows(IllegalArgumentException.class, () ->
            authService.register("A".repeat(101), "test@nitte.edu.in", "password123", User.ROLE_STUDENT)
        );
        assertTrue(longEx.getMessage().contains("2 to 100 characters"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"plainaddress", "missingatsign.com", "@missingusername.com", "two@@signs.com", "missingdomain@.com"})
    @DisplayName("register rejects invalid email formats")
    void testRegisterInvalidEmailFormats(String invalidEmail) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            authService.register("John Doe", invalidEmail, "password123", User.ROLE_STUDENT)
        );
        assertTrue(ex.getMessage().contains("valid email address"));
    }

    @Test
    @DisplayName("register rejects passwords with fewer than 8 characters")
    void testRegisterPasswordLength() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            authService.register("Valid Name", "valid@nitte.edu.in", "short", User.ROLE_STUDENT)
        );
        assertTrue(ex.getMessage().contains("at least 8 characters"));
    }

    @Test
    @DisplayName("register prevents self-registering as ADMIN or arbitrary role")
    void testRegisterDisallowsAdminRole() {
        IllegalArgumentException adminEx = assertThrows(IllegalArgumentException.class, () ->
            authService.register("Valid Name", "valid@nitte.edu.in", "password123", User.ROLE_ADMIN)
        );
        assertTrue(adminEx.getMessage().contains("valid account type"));

        IllegalArgumentException arbitraryEx = assertThrows(IllegalArgumentException.class, () ->
            authService.register("Valid Name", "valid@nitte.edu.in", "password123", "SUPERUSER")
        );
        assertTrue(arbitraryEx.getMessage().contains("valid account type"));
    }
}
