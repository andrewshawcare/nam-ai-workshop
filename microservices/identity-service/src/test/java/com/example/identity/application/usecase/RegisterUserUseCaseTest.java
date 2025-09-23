package com.example.identity.application.usecase;

import com.example.identity.domain.model.Email;
import com.example.identity.domain.model.Password;
import com.example.identity.domain.model.PasswordHash;
import com.example.identity.domain.model.User;
import com.example.identity.domain.service.UserDomainService;
import com.example.identity.port.input.RegisterUserPort.UserAlreadyExistsException;
import com.example.identity.port.output.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegisterUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;
    
    @Mock
    private UserDomainService userDomainService;

    private RegisterUserUseCase registerUserUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registerUserUseCase = new RegisterUserUseCase(userDomainService, userRepository);
    }

    @Test
    void shouldRegisterNewUser() {
        // Given
        String email = "user@example.com";
        String password = "Password123!";
        
        UUID userId = UUID.randomUUID();
        User savedUser = User.reconstitute(
                userId,
                Email.of(email),
                PasswordHash.fromHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"),
                java.time.LocalDateTime.now().minusDays(1),
                java.time.LocalDateTime.now()
        );
        
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userDomainService.registerUser(any(Email.class), any(Password.class))).thenReturn(savedUser);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        User result = registerUserUseCase.registerUser(email, password);
        
        // Then
        assertNotNull(result);
        assertEquals(email, result.getEmail().getValue());
        
        // Verify interactions
        verify(userRepository).existsByEmail(any(Email.class));
        verify(userDomainService).registerUser(any(Email.class), any(Password.class));
        verify(userRepository).save(any(User.class));
        
        // Capture the User object passed to save
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        
        User capturedUser = userCaptor.getValue();
        assertEquals(email, capturedUser.getEmail().getValue());
        assertNotNull(capturedUser.getPasswordHash());
        assertNotNull(capturedUser.getId());
        assertNotNull(capturedUser.getCreatedAt());
        assertNotNull(capturedUser.getUpdatedAt());
    }
    
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        String email = "existing@example.com";
        String password = "Password123!";
        
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);
        // No need to mock userDomainService.registerUser() since we're expecting an exception before that
        
        // When & Then
        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> {
            registerUserUseCase.registerUser(email, password);
        });
        
        assertEquals("User with email " + email + " already exists", exception.getMessage());
        
        // Verify interactions
        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Given
        String invalidEmail = "invalid-email";
        String password = "Password123!";
        
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            registerUserUseCase.registerUser(invalidEmail, password);
        });
        
        assertEquals("Invalid email format", exception.getMessage());
        
        // Verify interactions
        verify(userRepository, never()).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        // Given
        String email = "user@example.com";
        String invalidPassword = "weak";
        
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            registerUserUseCase.registerUser(email, invalidPassword);
        });
        
        assertEquals("Password must be at least 8 characters long", exception.getMessage());
        
        // Verify interactions
        verify(userDomainService, never()).registerUser(any(Email.class), any(Password.class));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenPasswordHasNoUppercase() {
        // Given
        String email = "user@example.com";
        String invalidPassword = "password123!";
        
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            registerUserUseCase.registerUser(email, invalidPassword);
        });
        
        assertEquals("Password must contain at least one uppercase letter", exception.getMessage());
        
        // Verify interactions
        verify(userDomainService, never()).registerUser(any(Email.class), any(Password.class));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenPasswordHasNoLowercase() {
        // Given
        String email = "user@example.com";
        String invalidPassword = "PASSWORD123!";
        
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            registerUserUseCase.registerUser(email, invalidPassword);
        });
        
        assertEquals("Password must contain at least one lowercase letter", exception.getMessage());
        
        // Verify interactions
        verify(userDomainService, never()).registerUser(any(Email.class), any(Password.class));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenPasswordHasNoDigit() {
        // Given
        String email = "user@example.com";
        String invalidPassword = "Password!";
        
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            registerUserUseCase.registerUser(email, invalidPassword);
        });
        
        assertEquals("Password must contain at least one digit", exception.getMessage());
        
        // Verify interactions
        verify(userDomainService, never()).registerUser(any(Email.class), any(Password.class));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenPasswordHasNoSpecialChar() {
        // Given
        String email = "user@example.com";
        String invalidPassword = "Password123";
        
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            registerUserUseCase.registerUser(email, invalidPassword);
        });
        
        assertEquals("Password must contain at least one special character", exception.getMessage());
        
        // Verify interactions
        verify(userDomainService, never()).registerUser(any(Email.class), any(Password.class));
        verify(userRepository, never()).save(any(User.class));
    }
}