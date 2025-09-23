package com.example.identity.port.input;

import com.example.identity.domain.model.User;

/**
 * Input port for registering a new user.
 */
public interface RegisterUserPort {
    
    /**
     * Registers a new user with the given email and password.
     *
     * @param email the user's email
     * @param password the user's password
     * @return the registered user
     * @throws UserAlreadyExistsException if a user with the email already exists
     */
    User registerUser(String email, String password) throws UserAlreadyExistsException;
    
    /**
     * Exception thrown when attempting to register a user with an email that already exists.
     */
    class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }
}