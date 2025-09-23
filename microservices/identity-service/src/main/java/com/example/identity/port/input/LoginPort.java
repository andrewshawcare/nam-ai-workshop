package com.example.identity.port.input;

/**
 * Input port for user authentication.
 */
public interface LoginPort {
    
    /**
     * Authenticates a user with the given credentials and returns a JWT token.
     *
     * @param email the user's email
     * @param password the user's password
     * @return a JWT token if authentication is successful
     * @throws AuthenticationFailedException if authentication fails
     */
    String login(String email, String password) throws AuthenticationFailedException;
    
    /**
     * Exception thrown when authentication fails.
     */
    class AuthenticationFailedException extends RuntimeException {
        public AuthenticationFailedException(String message) {
            super(message);
        }
    }
}