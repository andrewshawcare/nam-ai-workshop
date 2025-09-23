package com.example.task.domain.ports;

import com.example.task.domain.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Task domain entity
 */
public interface TaskRepository {
    
    /**
     * Save a task to the repository
     * 
     * @param task The task to save
     * @return The saved task
     */
    Task save(Task task);
    
    /**
     * Find a task by its ID
     * 
     * @param id The task ID
     * @return An Optional containing the task if found, or empty if not found
     */
    Optional<Task> findById(UUID id);
    
    /**
     * Find all tasks for a specific user
     * 
     * @param userId The user ID
     * @return A list of tasks belonging to the user
     */
    List<Task> findAllByUserId(UUID userId);
    
    /**
     * Delete a task by its ID
     * 
     * @param id The task ID
     */
    void deleteById(UUID id);
    
    /**
     * Check if a task exists and belongs to a specific user
     * 
     * @param id The task ID
     * @param userId The user ID
     * @return true if the task exists and belongs to the user, false otherwise
     */
    boolean existsByIdAndUserId(UUID id, UUID userId);
}