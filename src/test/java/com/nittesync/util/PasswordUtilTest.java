package com.nittesync.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {

    @Test
    @DisplayName("sha256Hex produces a 64-character lowercase hex string")
    void testSha256HexProducesValid64CharHex() {
        String hash = PasswordUtil.sha256Hex("password");
        assertNotNull(hash);
        assertEquals(64, hash.length());
        assertTrue(hash.matches("^[0-9a-f]{64}$"));
        // Known SHA-256 for "password"
        assertEquals("5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8", hash);
    }

    @Test
    @DisplayName("sha256Hex is deterministic across multiple invocations")
    void testSha256HexDeterministic() {
        String hash1 = PasswordUtil.sha256Hex("secret123");
        String hash2 = PasswordUtil.sha256Hex("secret123");
        assertEquals(hash1, hash2);
    }

    @Test
    @DisplayName("matches returns true when password matches hash")
    void testMatchesSuccess() {
        String hash = PasswordUtil.sha256Hex("mypassword");
        assertTrue(PasswordUtil.matches("mypassword", hash));
    }

    @Test
    @DisplayName("matches returns false for incorrect password")
    void testMatchesFailure() {
        String hash = PasswordUtil.sha256Hex("mypassword");
        assertFalse(PasswordUtil.matches("wrongpassword", hash));
    }

    @Test
    @DisplayName("matches handles null or whitespace gracefully")
    void testMatchesNullAndWhitespace() {
        String hash = PasswordUtil.sha256Hex("test");
        assertFalse(PasswordUtil.matches(null, hash));
        assertFalse(PasswordUtil.matches("test", null));
        assertFalse(PasswordUtil.matches(null, null));
    }

    @Test
    @DisplayName("matches is case-insensitive and trims stored hash")
    void testMatchesCaseInsensitiveAndTrimmed() {
        String hash = PasswordUtil.sha256Hex("test").toUpperCase();
        assertTrue(PasswordUtil.matches("test", "  " + hash + "  "));
    }
}
