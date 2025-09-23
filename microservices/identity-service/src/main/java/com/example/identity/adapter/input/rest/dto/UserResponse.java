package com.example.identity.adapter.input.rest.dto;

import com.example.identity.domain.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for user responses.
 */
public class UserResponse {
    
    private UUID id;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public UserResponse() {
    }
    
    public UserResponse(UUID id, String email, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    /**
     * Creates a UserResponse from a User domain object.
     *
     * @param user the user domain object
     * @return a new UserResponse
     */
    public static UserResponse fromUser(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail().getValue(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
    
    // Getters and setters
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}