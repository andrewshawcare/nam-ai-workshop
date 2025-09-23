package com.example.identity.application.usecase;

import com.example.identity.domain.model.Email;
import com.example.identity.domain.model.PasswordHash;
import com.example.identity.domain.model.User;
import com.example.identity.port.input.GetUserInfoPort.UserNotFoundException;
import com.example.identity.port.output.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetUserInfoUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;
    
    private GetUserInfoUseCase getUserInfoUseCase;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        getUserInfoUseCase = new GetUserInfoUseCase(userRepository);
    }
    
    @Test
    void shouldGetUserInfoSuccessfully() {
        // Given
        UUID userId = UUID.randomUUID();
        String email = "user@example.com";
        
        User user = User.reconstitute(
                userId,
                Email.of(email),
                PasswordHash.fromHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        // When
        User result = getUserInfoUseCase.getUserInfo(userId.toString());
        
        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(email, result.getEmail().getValue());
        
        // Verify interactions
        verify(userRepository).findById(userId);
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // When & Then
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            getUserInfoUseCase.getUserInfo(userId.toString());
        });
        
        assertEquals("User not found with id: " + userId, exception.getMessage());
        
        // Verify interactions
        verify(userRepository).findById(userId);
    }
    
    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            getUserInfoUseCase.getUserInfo(null);
        });
        
        assertTrue(exception instanceof UserNotFoundException || exception instanceof NullPointerException);
        
        // Verify interactions
        verify(userRepository, never()).findById(any());
    }
}