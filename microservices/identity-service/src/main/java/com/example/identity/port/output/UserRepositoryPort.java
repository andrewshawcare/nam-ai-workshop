package com.example.identity.port.output;

import com.example.identity.domain.model.Email;
import com.example.identity.domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Port for user repository operations.
 */
public interface UserRepositoryPort {
    
    /**
     * Saves a user to the repository.
     *
     * @param user the user to save
     * @return the saved user
     */
    User save(User user);
    
    /**
     * Finds a user by ID.
     *
     * @param id the user ID
     * @return the user, if found
     */
    Optional<User> findById(UUID id);
    
    /**
     * Finds a user by email.
     *
     * @param email the user email
     * @return the user, if found
     */
    Optional<User> findByEmail(Email email);
    
    /**
     * Checks if a user with the given email exists.
     *
     * @param email the email to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(Email email);
}