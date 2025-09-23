package com.example.identity.infrastructure.security.adapters;

import com.example.identity.infrastructure.security.JwtService;
import com.example.identity.port.output.TokenGenerationPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adapter that implements TokenGenerationPort and delegates to JwtService
 */
@Component
public class TokenGenerationPortAdapter implements TokenGenerationPort {

    private final JwtService jwtService;

    public TokenGenerationPortAdapter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public String generateToken(String userId) {
        return jwtService.generateToken(UUID.fromString(userId));
    }

    @Override
    public boolean validateToken(String token) {
        try {
            // Extract user ID from token
            UUID userId = jwtService.extractUserId(token);
            // Check if token is valid for this user ID
            return jwtService.isTokenValid(token, userId);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getUserIdFromToken(String token) {
        return jwtService.extractUserId(token).toString();
    }
}