package com.example.identity.port.input;

import com.example.identity.domain.model.User;

/**
 * Input port for retrieving user information.
 */
public interface GetUserInfoPort {
    
    /**
     * Retrieves information about the user with the given ID.
     *
     * @param userId the ID of the user to retrieve
     * @return the user information
     * @throws UserNotFoundException if no user with the ID exists
     */
    User getUserInfo(String userId) throws UserNotFoundException;
    
    /**
     * Exception thrown when a user is not found.
     */
    class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
}