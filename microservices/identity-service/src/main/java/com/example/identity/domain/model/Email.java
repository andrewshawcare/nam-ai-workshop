package com.example.identity.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing an email address.
 */
public class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    
    private final String value;
    
    private Email(String value) {
        this.value = value;
    }
    
    /**
     * Creates a new Email instance after validating the email format.
     *
     * @param email the email string to validate
     * @return a new Email instance
     * @throws IllegalArgumentException if the email format is invalid
     */
    public static Email of(String email) {
        validateEmail(email);
        return new Email(email);
    }
    
    private static void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}