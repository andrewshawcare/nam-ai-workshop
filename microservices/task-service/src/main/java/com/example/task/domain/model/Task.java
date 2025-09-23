package com.example.task.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a task.
 */
public class Task {
    private final UUID id;
    private final UUID userId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private boolean completed;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Task(
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
     * Creates a new task with the given details.
     *
     * @param userId the ID of the user who owns the task
     * @param title the task title
     * @param description the task description (optional)
     * @param dueDate the task due date (optional)
     * @return a new Task instance
     */
    public static Task create(
        UUID userId,
        String title,
        String description,
        LocalDateTime dueDate
    ) {
        validateTitle(title);
        
        return new Task(
            UUID.randomUUID(),
            userId,
            title,
            description,
            dueDate,
            false,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
    
    /**
     * Reconstructs a task from persistence.
     *
     * @param id the task ID
     * @param userId the ID of the user who owns the task
     * @param title the task title
     * @param description the task description
     * @param dueDate the task due date
     * @param completed whether the task is completed
     * @param createdAt when the task was created
     * @param updatedAt when the task was last updated
     * @return a Task instance
     */
    public static Task reconstitute(
        UUID id,
        UUID userId,
        String title,
        String description,
        LocalDateTime dueDate,
        boolean completed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        return new Task(
            id,
            userId,
            title,
            description,
            dueDate,
            completed,
            createdAt,
            updatedAt
        );
    }
    
    /**
     * Updates the task details.
     *
     * @param title the new title
     * @param description the new description
     * @param dueDate the new due date
     */
    public void update(String title, String description, LocalDateTime dueDate) {
        validateTitle(title);
        
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Marks the task as completed.
     */
    public void markAsCompleted() {
        this.completed = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Marks the task as not completed.
     */
    public void markAsNotCompleted() {
        this.completed = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Checks if the task belongs to the specified user.
     *
     * @param userId the user ID to check
     * @return true if the task belongs to the user, false otherwise
     */
    public boolean belongsToUser(UUID userId) {
        return this.userId.equals(userId);
    }
    
    private static void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
    }
    
    // Getters
    
    public UUID getId() {
        return id;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}