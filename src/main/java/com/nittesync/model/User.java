package com.nittesync.model;

import java.time.LocalDateTime;

/**
 * A person who can log in: a student, a club account or an administrator.
 * Plain data holder - all behaviour lives in the service and bean classes.
 */
public class User {

    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_CLUB = "CLUB";
    public static final String ROLE_ADMIN = "ADMIN";

    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private String role;
    private LocalDateTime createdAt;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}