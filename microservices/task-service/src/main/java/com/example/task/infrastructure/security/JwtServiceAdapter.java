package com.example.task.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

/**
 * Enhanced JWT service that properly validates tokens
 */
@Service
public class JwtServiceAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtServiceAdapter.class);

    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Extract the user ID from the token
     * 
     * @param token The JWT token
     * @return The user ID
     */
    public UUID extractUserId(String token) {
        String userIdStr = extractClaim(token, Claims::getSubject);
        return UUID.fromString(userIdStr);
    }

    /**
     * Extract a specific claim from the token
     * 
     * @param token The JWT token
     * @param claimsResolver Function to extract a specific claim
     * @return The extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Check if a token is valid (not expired)
     * 
     * @param token The JWT token
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            logger.debug("Validating token: {}", token);
            
            // Extract user ID to verify the token is well-formed
            UUID userId = extractUserId(token);
            logger.debug("Extracted user ID: {}", userId);
            
            // Check if the token is expired
            boolean isExpired = isTokenExpired(token);
            logger.debug("Token expired: {}", isExpired);
            
            return !isExpired;
        } catch (Exception e) {
            logger.error("Error validating token", e);
            return false;
        }
    }

    /**
     * Check if a token is expired
     * 
     * @param token The JWT token
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extract the expiration date from a token
     * 
     * @param token The JWT token
     * @return The expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract all claims from a token
     * 
     * @param token The JWT token
     * @return All claims in the token
     */
    private Claims extractAllClaims(String token) {
        // Ensure the secret key is properly encoded
        String encodedSecret = getEncodedSecret(secretKey);
        logger.debug("Using encoded secret: {}", encodedSecret);
        
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(encodedSecret))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            logger.debug("Extracted claims: {}", claims);
            return claims;
        } catch (Exception e) {
            logger.error("Error extracting claims from token", e);
            throw e;
        }
    }

    /**
     * Get the signing key for JWT
     * 
     * @param encodedSecret The encoded secret key
     * @return The signing key
     */
    private Key getSigningKey(String encodedSecret) {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(encodedSecret);
            logger.debug("Decoded key bytes length: {}", keyBytes.length);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            logger.error("Error getting signing key", e);
            throw e;
        }
    }
    
    /**
     * Ensure the secret key is properly Base64 encoded
     * 
     * @param secret The secret key
     * @return The Base64 encoded secret key
     */
    private String getEncodedSecret(String secret) {
        try {
            logger.debug("Original secret: {}", secret);
            // Try to decode the secret to check if it's already Base64 encoded
            Decoders.BASE64.decode(secret);
            logger.debug("Secret is already Base64 encoded");
            return secret;
        } catch (Exception e) {
            // If decoding fails, the secret is not Base64 encoded, so encode it
            logger.debug("Secret is not Base64 encoded, encoding it");
            String encodedSecret = Base64.getEncoder().encodeToString(secret.getBytes());
            logger.debug("Encoded secret: {}", encodedSecret);
            return encodedSecret;
        }
    }
}