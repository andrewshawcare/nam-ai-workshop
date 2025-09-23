package com.example.identity.domain.model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

/**
 * Value object representing a hashed password.
 */
public class PasswordHash {
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder(12);
    
    private final String hash;
    
    private PasswordHash(String hash) {
        this.hash = hash;
    }
    
    /**
     * Creates a new PasswordHash instance from a raw password.
     *
     * @param password the raw password to hash
     * @return a new PasswordHash instance
     */
    public static PasswordHash fromRawPassword(Password password) {
        String hashedPassword = hashPassword(password.getValue());
        return new PasswordHash(hashedPassword);
    }
    
    /**
     * Creates a new PasswordHash instance from an existing hash.
     *
     * @param hash the existing hash
     * @return a new PasswordHash instance
     */
    public static PasswordHash fromHash(String hash) {
        if (hash == null || hash.isEmpty()) {
            throw new IllegalArgumentException("Hash cannot be null or empty");
        }
        return new PasswordHash(hash);
    }
    
    private static String hashPassword(String rawPassword) {
        return PASSWORD_ENCODER.encode(rawPassword);
    }
    
    /**
     * Verifies if the provided password matches this hash.
     *
     * @param password the password to verify
     * @return true if the password matches, false otherwise
     */
    public boolean verify(Password password) {
        return PASSWORD_ENCODER.matches(password.getValue(), this.hash);
    }
    
    public String getHash() {
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordHash that = (PasswordHash) o;
        return Objects.equals(hash, that.hash);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }
    
    // Note: We don't expose the actual hash in toString() for security reasons
    @Override
    public String toString() {
        return "PasswordHash{...}";
    }
}