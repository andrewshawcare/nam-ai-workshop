package com.example.identity.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHashTest {

    @Test
    void shouldCreatePasswordHash() {
        // Given
        String hash = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

        // When
        PasswordHash passwordHash = PasswordHash.fromHash(hash);

        // Then
        assertEquals(hash, passwordHash.getHash());
    }

    @Test
    void shouldThrowExceptionForNullHash() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> PasswordHash.fromHash(null));
    }

    @Test
    void shouldThrowExceptionForEmptyHash() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> PasswordHash.fromHash(""));
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Given
        PasswordHash hash1 = PasswordHash.fromHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG");
        PasswordHash hash2 = PasswordHash.fromHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG");

        // Then
        assertEquals(hash1, hash2);
        assertEquals(hash1.hashCode(), hash2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        PasswordHash hash1 = PasswordHash.fromHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG");
        PasswordHash hash2 = PasswordHash.fromHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/XX");

        // Then
        assertNotEquals(hash1, hash2);
    }
}