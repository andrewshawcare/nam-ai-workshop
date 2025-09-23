package com.example.identity.port.output;

/**
 * Port for JWT token generation and validation.
 */
public interface TokenGenerationPort {
    
    /**
     * Generates a JWT token for the given user ID.
     *
     * @param userId the user ID to include in the token
     * @return the generated JWT token
     */
    String generateToken(String userId);
    
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
     */
    String getUserIdFromToken(String token);
}