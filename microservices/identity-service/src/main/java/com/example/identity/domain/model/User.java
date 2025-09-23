package com.example.identity.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a user.
 */
public class User {
    private final UUID id;
    private final Email email;
    private final PasswordHash passwordHash;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    private User(UUID id, Email email, PasswordHash passwordHash, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    /**
     * Creates a new user with the given email and password.
     *
     * @param email the user's email
     * @param password the user's password
     * @return a new User instance
     */
    public static User create(Email email, Password password) {
        if (email == null) {
            throw new NullPointerException("Email cannot be null");
        }
        if (password == null) {
            throw new NullPointerException("Password cannot be null");
        }
        
        // Use the same timestamp for both createdAt and updatedAt
        LocalDateTime now = LocalDateTime.now();
        
        return new User(
            UUID.randomUUID(),
            email,
            PasswordHash.fromRawPassword(password),
            now,
            now
        );
    }
    
    /**
     * Reconstructs a user from persistence.
     *
     * @param id the user's ID
     * @param email the user's email
     * @param passwordHash the user's password hash
     * @param createdAt when the user was created
     * @param updatedAt when the user was last updated
     * @return a User instance
     */
    public static User reconstitute(UUID id, Email email, PasswordHash passwordHash, 
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new User(id, email, passwordHash, createdAt, updatedAt);
    }
    
    /**
     * Verifies if the provided password matches the user's password.
     *
     * @param password the password to verify
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(Password password) {
        return this.passwordHash.verify(password);
    }
    
    // Getters
    
    public UUID getId() {
        return id;
    }
    
    public Email getEmail() {
        return email;
    }
    
    public PasswordHash getPasswordHash() {
        return passwordHash;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email=" + email +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}