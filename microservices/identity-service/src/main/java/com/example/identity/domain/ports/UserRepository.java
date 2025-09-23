package com.example.identity.domain.ports;

import com.example.identity.domain.model.Email;
import com.example.identity.domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for User domain entity
 */
public interface UserRepository {
    
    /**
     * Save a user to the repository
     * 
     * @param user The user to save
     * @return The saved user
     */
    User save(User user);
    
    /**
     * Find a user by their ID
     * 
     * @param id The user ID
     * @return An Optional containing the user if found, or empty if not found
     */
    Optional<User> findById(UUID id);
    
    /**
     * Find a user by their email
     * 
     * @param email The user's email
     * @return An Optional containing the user if found, or empty if not found
     */
    Optional<User> findByEmail(Email email);
    
    /**
     * Check if a user with the given email exists
     * 
     * @param email The email to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(Email email);
}