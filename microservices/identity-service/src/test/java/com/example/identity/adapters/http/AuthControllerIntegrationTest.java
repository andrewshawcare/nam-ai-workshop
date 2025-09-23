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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepositoryPort userRepository;

    @MockBean
    private TokenGenerationPort tokenGenerator;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(userRepository, tokenGenerator);
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        // Given
        String email = "test@example.com";
        String password = "Password123!";

        UserRegistrationRequest request = new UserRegistrationRequest(email, password);

        UUID userId = UUID.randomUUID();
        User savedUser = User.reconstitute(
                userId,
                Email.of(email),
                PasswordHash.fromHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"),
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When & Then
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.email").value(email));

        // Verify interactions
        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldReturnBadRequestWhenEmailAlreadyExists() throws Exception {
        // Given
        String email = "existing@example.com";
        String password = "Password123!";

        UserRegistrationRequest request = new UserRegistrationRequest(email, password);

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists: " + email));

        // Verify interactions
        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        // Given
        String invalidEmail = "invalid-email";
        String password = "Password123!";

        UserRegistrationRequest request = new UserRegistrationRequest(invalidEmail, password);

        // When & Then
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid email format"));

        // Verify interactions
        verify(userRepository, never()).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsInvalid() throws Exception {
        // Given
        String email = "test@example.com";
        String invalidPassword = "weak";

        UserRegistrationRequest request = new UserRegistrationRequest(email, invalidPassword);

        // When & Then
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character"));

        // Verify interactions
        verify(userRepository, never()).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        // Given
        String email = "test@example.com";
        String password = "Password123!";
        UUID userId = UUID.randomUUID();
        String token = "jwt.token.here";

        LoginRequest request = new LoginRequest(email, password);

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getEmail()).thenReturn(Email.of(email));
        when(user.verifyPassword(any())).thenReturn(true);

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(tokenGenerator.generateToken(userId.toString())).thenReturn(token);

        // When & Then
        MvcResult result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        TokenResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                TokenResponse.class
        );

        assertEquals(token, response.getToken());

        // Verify interactions
        verify(userRepository).findByEmail(any(Email.class));
        verify(tokenGenerator).generateToken(userId.toString());
    }

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
        // Given
        String email = "test@example.com";
        String password = "Password123!";

        LoginRequest request = new LoginRequest(email, password);

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));

        // Verify interactions
        verify(userRepository).findByEmail(any(Email.class));
        verify(tokenGenerator, never()).generateToken(anyString());
    }

    // Helper classes to match the request/response DTOs
    static class UserRegistrationRequest {
        private String email;
        private String password;

        public UserRegistrationRequest() {
        }

        public UserRegistrationRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }

    static class LoginRequest {
        private String email;
        private String password;

        public LoginRequest() {
        }

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }

    static class TokenResponse {
        private String token;

        public TokenResponse() {
        }

        public String getToken() {
            return token;
        }
    }
}