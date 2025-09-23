package com.example.identity.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserWithAllFields() {
        // Given
        UUID id = UUID.randomUUID();
        Email email = Email.of("user@example.com");
        PasswordHash passwordHash = PasswordHash.fromHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG");
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        
        // When
        User user = User.reconstitute(id, email, passwordHash, createdAt, updatedAt);
        
        // Then
        assertEquals(id, user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(passwordHash, user.getPasswordHash());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
    }
    
    @Test
    void shouldCreateUserWithEmailAndPassword() {
        // Given
        Email email = Email.of("user@example.com");
        Password password = Password.of("Password123!");
        
        // When
        User user = User.create(email, password);
        
        // Then
        assertNotNull(user.getId());
        assertEquals(email, user.getEmail());
        assertNotNull(user.getPasswordHash());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        // Created at and updated at should be the same when first created
        assertEquals(user.getCreatedAt(), user.getUpdatedAt());
    }
    
    @Test
    void shouldVerifyCorrectPassword() {
        // Given
        Email email = Email.of("user@example.com");
        Password password = Password.of("Password123!");
        User user = User.create(email, password);
        
        // When & Then
        assertTrue(user.verifyPassword(password));
    }
    
    @Test
    void shouldNotVerifyIncorrectPassword() {
        // Given
        Email email = Email.of("user@example.com");
        Password correctPassword = Password.of("Password123!");
        Password wrongPassword = Password.of("WrongPassword456@");
        User user = User.create(email, correctPassword);
        
        // When & Then
        assertFalse(user.verifyPassword(wrongPassword));
    }
    
    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        // Given
        Password password = Password.of("Password123!");
        
        // Then
        assertThrows(NullPointerException.class, () ->
            User.create(null, password)
        );
    }
    
    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        // Given
        Email email = Email.of("user@example.com");
        
        // Then
        assertThrows(NullPointerException.class, () ->
            User.create(email, null)
        );
    }
    
    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        // Given
        UUID id = UUID.randomUUID();
        
        User user1 = User.reconstitute(
                id,
                Email.of("user1@example.com"),
                PasswordHash.fromHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
                
        User user2 = User.reconstitute(
                id,
                Email.of("user2@example.com"),  // Different email
                PasswordHash.fromHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/XX"),  // Different hash
                LocalDateTime.now().minusDays(1),  // Different dates
                LocalDateTime.now().minusHours(1)
        );
        
        // Then
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        // Given
        User user1 = User.reconstitute(
                UUID.randomUUID(),
                Email.of("user@example.com"),
                PasswordHash.fromHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
                
        User user2 = User.reconstitute(
                UUID.randomUUID(),
                Email.of("user@example.com"),  // Same email
                PasswordHash.fromHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"),  // Same hash
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        
        // Then
        assertNotEquals(user1, user2);
    }
}