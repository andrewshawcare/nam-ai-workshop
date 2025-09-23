package com.example.task.port.output;

/**
 * Port for JWT token validation.
 */
public interface TokenValidationPort {
    
    /**
     * Validates a JWT token.
     *
     * @param token the token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);
    
    /**
     * Extracts the user ID from a JWT token.
     *
     * @param token the token to extract from
     * @return the user ID
     * @throws IllegalArgumentException if the token is invalid or the user ID cannot be extracted
     */
    String getUserIdFromToken(String token);
}