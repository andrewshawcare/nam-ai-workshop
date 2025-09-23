package com.example.task.adapter.output.security;

import com.example.task.infrastructure.security.JwtService;
import com.example.task.port.output.TokenValidationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adapter for JWT token validation
 */
@Component
@RequiredArgsConstructor
public class TokenValidationAdapter implements TokenValidationPort {

    private final JwtService jwtService;

    @Override
    public boolean validateToken(String token) {
        try {
            return jwtService.isTokenValid(token);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getUserIdFromToken(String token) {
        try {
            UUID userId = jwtService.extractUserId(token);
            return userId.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token or user ID cannot be extracted", e);
        }
    }
}