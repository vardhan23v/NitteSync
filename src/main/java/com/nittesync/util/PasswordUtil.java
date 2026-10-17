package com.nittesync.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Turns a plain password into the value stored in users.password_hash.
 *
 * The stored value is the SHA-256 digest written as 64 hexadecimal
 * characters, which is why the column is VARCHAR(64).
 *
 * Note for the viva: a real application would use a slow, salted algorithm
 * such as BCrypt. SHA-256 keeps this lab project free of extra libraries.
 */
public final class PasswordUtil {

    private PasswordUtil() {
        // utility class: never instantiated
    }

    /** @return the SHA-256 digest of the password as 64 lower case hex characters */
    public static String sha256Hex(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // Every JVM is required to provide SHA-256, so this cannot normally happen.
            throw new IllegalStateException("SHA-256 is not available in this JVM.", e);
        }
    }

    /**
     * Compares a typed password with the hash stored in the database.
     *
     * @param rawPassword  what the user typed on the login page
     * @param storedHash   the value of the password_hash column
     */
    public static boolean matches(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }
        return sha256Hex(rawPassword).equalsIgnoreCase(storedHash.trim());
    }
}