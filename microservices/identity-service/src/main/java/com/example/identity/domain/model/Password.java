package com.example.identity.domain.model;

import java.util.Objects;

/**
 * Value object representing a password.
 */
public class Password {
    private final String value;
    
    private Password(String value) {
        this.value = value;
    }
    
    /**
     * Creates a new Password instance after validating the password.
     *
     * @param password the password string to validate
     * @return a new Password instance
     * @throws IllegalArgumentException if the password is invalid
     */
    public static Password of(String password) {
        validatePassword(password);
        return new Password(password);
    }
    
    private static void validatePassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        
        if (password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        
        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        
        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        
        // Check for at least one digit
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }
        
        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password = (Password) o;
        return Objects.equals(value, password.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    // Note: We don't override toString() for security reasons
    // to avoid accidentally logging passwords
    @Override
    public String toString() {
        return "********";
    }
}