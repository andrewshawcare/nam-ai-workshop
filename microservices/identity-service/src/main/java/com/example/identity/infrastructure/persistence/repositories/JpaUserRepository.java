package com.example.identity.infrastructure.persistence.repositories;

import com.example.identity.infrastructure.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for UserEntity
 */
@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
    
    /**
     * Find a user by their email
     * 
     * @param email The user's email
     * @return An Optional containing the user if found, or empty if not found
     */
    Optional<UserEntity> findByEmail(String email);
    
    /**
     * Check if a user with the given email exists
     * 
     * @param email The email to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
}