package com.example.identity.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateValidEmail() {
        // Given
        String validEmailAddress = "user@example.com";

        // When
        Email email = Email.of(validEmailAddress);

        // Then
        assertEquals(validEmailAddress, email.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "invalid-email",
        "missing@tld",
        "@missing-local-part.com",
        "spaces in@email.com",
        ""
    })
    void shouldThrowExceptionForInvalidEmail(String invalidEmail) {
        // Then
        assertThrows(IllegalArgumentException.class, () -> Email.of(invalidEmail));
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Given
        Email email1 = Email.of("user@example.com");
        Email email2 = Email.of("user@example.com");

        // Then
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        Email email1 = Email.of("user1@example.com");
        Email email2 = Email.of("user2@example.com");

        // Then
        assertNotEquals(email1, email2);
    }
}