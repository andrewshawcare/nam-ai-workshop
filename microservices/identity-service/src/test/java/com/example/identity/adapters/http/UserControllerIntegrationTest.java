package com.example.identity.adapters.http;

import com.example.identity.domain.model.Email;
import com.example.identity.domain.model.PasswordHash;
import com.example.identity.domain.model.User;
import com.example.identity.port.output.UserRepositoryPort;
import com.example.identity.port.output.TokenGenerationPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepositoryPort userRepository;

    @MockBean
    private TokenGenerationPort tokenGenerator;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(userRepository, tokenGenerator);
        
        // Set up common test data
        userId = UUID.randomUUID();
        user = User.reconstitute(
                userId,
                Email.of("user@example.com"),
                PasswordHash.fromHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
    }

    @Test
    @WithMockUser(username = "user-id")
    void shouldGetCurrentUserSuccessfully() throws Exception {
        // Given
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));

        // When & Then
        mockMvc.perform(get("/me")
                .header("Authorization", "Bearer valid.jwt.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("user@example.com"));

        // Verify interactions
        verify(userRepository).findById(any(UUID.class));
    }

    @Test
    void shouldReturnUnauthorizedWhenNoAuthenticationProvided() throws Exception {
        // When & Then
        mockMvc.perform(get("/me"))
                .andExpect(status().isUnauthorized());

        // Verify interactions
        verify(userRepository, never()).findById(any(UUID.class));
    }

    @Test
    @WithMockUser(username = "non-existent-user-id")
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        // Given
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/me")
                .header("Authorization", "Bearer valid.jwt.token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));

        // Verify interactions
        verify(userRepository).findById(any(UUID.class));
    }
}