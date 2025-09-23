package com.example.identity.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=testSecretKeyForJwtThatIsLongEnoughToBeSecure",
    "jwt.expiration=3600000" // 1 hour
})
class JwtServiceIntegrationTest {

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Test
    void shouldGenerateValidToken() {
        // Given
        UUID userId = UUID.randomUUID();

        // When
        String token = jwtService.generateToken(userId);

        // Then
        assertNotNull(token);
        assertTrue(token.length() > 0);

        // Verify token can be parsed
        Claims claims = extractAllClaims(token);
        assertEquals(userId.toString(), claims.getSubject());
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void shouldExtractUserIdFromToken() {
        // Given
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId);

        // When
        UUID extractedUserId = jwtService.extractUserId(token);

        // Then
        assertEquals(userId, extractedUserId);
    }

    @Test
    void shouldValidateTokenSuccessfully() {
        // Given
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId);

        // When
        boolean isValid = jwtService.isTokenValid(token, userId);

        // Then
        assertTrue(isValid);
    }

    @Test
    void shouldReturnFalseWhenTokenIsForDifferentUser() {
        // Given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        String token = jwtService.generateToken(userId1);

        // When
        boolean isValid = jwtService.isTokenValid(token, userId2);

        // Then
        assertFalse(isValid);
    }

    @Test
    void shouldReturnFalseWhenTokenIsExpired() {
        // Given
        UUID userId = UUID.randomUUID();
        
        // Create an expired token
        String expiredToken = Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // Expired 1 second ago
                .signWith(getSigningKey(), io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();

        // When
        boolean isValid = jwtService.isTokenValid(expiredToken, userId);

        // Then
        assertFalse(isValid);
    }

    @Test
    void shouldThrowExceptionWhenTokenIsInvalid() {
        // Given
        String invalidToken = "invalid.token.format";

        // When & Then
        assertThrows(Exception.class, () -> jwtService.extractUserId(invalidToken));
    }

    // Helper methods to parse and validate tokens
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}