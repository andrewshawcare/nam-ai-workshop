package com.example.identity.application.usecase;

import com.example.identity.domain.model.Email;
import com.example.identity.domain.model.Password;
import com.example.identity.domain.model.PasswordHash;
import com.example.identity.domain.model.User;
import com.example.identity.domain.service.UserDomainService;
import com.example.identity.port.input.LoginPort.AuthenticationFailedException;
import com.example.identity.port.output.TokenGenerationPort;
import com.example.identity.port.output.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoginUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;
    
    @Mock
    private TokenGenerationPort tokenGenerator;
    
    @Mock
    private UserDomainService userDomainService;
    
    private LoginUseCase loginUseCase;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginUseCase = new LoginUseCase(userDomainService, userRepository, tokenGenerator);
    }
    
    @Test
    void shouldLoginSuccessfully() {
        // Given
        String email = "user@example.com";
        String password = "Password123!";
        
        // Create a password hash that will verify successfully
        Password userPassword = Password.of(password);
        PasswordHash passwordHash = PasswordHash.fromRawPassword(userPassword);
        
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        User user = User.reconstitute(
                userId,
                Email.of(email),
                passwordHash,
                now,
                now
        );
        
        String expectedToken = "jwt.token.here";
        
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(userDomainService.authenticateUser(any(User.class), any(Password.class))).thenReturn(true);
        when(tokenGenerator.generateToken(userId.toString())).thenReturn(expectedToken);
        
        // When
        String token = loginUseCase.login(email, password);
        
        // Then
        assertEquals(expectedToken, token);
        
        // Verify interactions
        verify(userRepository).findByEmail(any(Email.class));
        verify(tokenGenerator).generateToken(userId.toString());
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        String email = "nonexistent@example.com";
        String password = "Password123!";
        
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());
        // No need to mock userDomainService.authenticateUser() since we're expecting an exception before that
        
        // When & Then
        Exception exception = assertThrows(AuthenticationFailedException.class, () -> {
            loginUseCase.login(email, password);
        });
        
        assertEquals("Invalid credentials", exception.getMessage());
        
        // Verify interactions
        verify(userRepository).findByEmail(any(Email.class));
        verify(tokenGenerator, never()).generateToken(anyString());
    }
    
    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        // Given
        String email = "user@example.com";
        String correctPassword = "Password123!";
        String incorrectPassword = "WrongPassword123!";
        
        // Create a password hash for the correct password
        Password userPassword = Password.of(correctPassword);
        PasswordHash passwordHash = PasswordHash.fromRawPassword(userPassword);
        
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        User user = User.reconstitute(
                userId,
                Email.of(email),
                passwordHash,
                now,
                now
        );
        
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(userDomainService.authenticateUser(any(User.class), any(Password.class))).thenReturn(false);
        
        // When & Then
        Exception exception = assertThrows(AuthenticationFailedException.class, () -> {
            loginUseCase.login(email, incorrectPassword);
        });
        
        assertEquals("Invalid credentials", exception.getMessage());
        
        // Verify interactions
        verify(userRepository).findByEmail(any(Email.class));
        verify(tokenGenerator, never()).generateToken(anyString());
    }
    
    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Given
        String invalidEmail = "invalid-email";
        String password = "Password123!";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            loginUseCase.login(invalidEmail, password);
        });
        
        // Verify interactions
        verify(userRepository, never()).findByEmail(any(Email.class));
        verify(tokenGenerator, never()).generateToken(anyString());
    }
}