package com.example.task.adapter.input.rest.dto;

import com.example.task.domain.model.Task;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for task responses.
 */
public class TaskResponse {
    
    private UUID id;
    private UUID userId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public TaskResponse() {
    }
    
    public TaskResponse(
        UUID id,
        UUID userId,
        String title,
        String description,
        LocalDateTime dueDate,
        boolean completed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    /**
     * Creates a TaskResponse from a Task domain object.
     *
     * @param task the task domain object
     * @return a new TaskResponse
     */
    public static TaskResponse fromTask(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getUserId(),
            task.getTitle(),
            task.getDescription(),
            task.getDueDate(),
            task.isCompleted(),
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }
    
    // Getters and setters
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
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