package com.example.identity.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PasswordTest {

    @Test
    void shouldCreateValidPassword() {
        // Given
        String validPassword = "Password123!";

        // When
        Password password = Password.of(validPassword);

        // Then
        assertEquals(validPassword, password.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "short",      // Too short
        "nodigits!",  // No digits
        "NOLOWER123", // No lowercase
        "nouppercase123", // No uppercase
        "NoSpecialChars123" // No special characters
    })
    void shouldThrowExceptionForInvalidPassword(String invalidPassword) {
        // Then
        assertThrows(IllegalArgumentException.class, () -> Password.of(invalidPassword));
    }

    @Test
    void shouldThrowExceptionForNullPassword() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> Password.of(null));
    }

    @Test
    void shouldThrowExceptionForEmptyPassword() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> Password.of(""));
    }

    @Test
    void shouldHashPasswordCorrectly() {
        // Given
        String plainPassword = "Password123!";
        Password password = Password.of(plainPassword);

        // When
        PasswordHash hash = PasswordHash.fromRawPassword(password);

        // Then
        assertNotNull(hash);
        assertNotEquals(plainPassword, hash.getHash());
        assertTrue(hash.getHash().startsWith("$2a$")); // BCrypt hash format
    }

    @Test
    void shouldVerifyCorrectPassword() {
        // Given
        String plainPassword = "Password123!";
        Password password = Password.of(plainPassword);
        PasswordHash hash = PasswordHash.fromRawPassword(password);

        // When
        boolean isValid = hash.verify(password);

        // Then
        assertTrue(isValid);
    }

    @Test
    void shouldNotVerifyIncorrectPassword() {
        // Given
        Password password1 = Password.of("Password123!");
        Password password2 = Password.of("DifferentPassword123!");
        PasswordHash hash = PasswordHash.fromRawPassword(password1);

        // When
        boolean isValid = hash.verify(password2);

        // Then
        assertFalse(isValid);
    }
}