package com.example.task.port.output;

import com.example.task.domain.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port for task repository operations.
 */
public interface TaskRepositoryPort {
    
    /**
     * Saves a task to the repository.
     *
     * @param task the task to save
     * @return the saved task
     */
    Task save(Task task);
    
    /**
     * Finds a task by ID.
     *
     * @param id the task ID
     * @return the task, if found
     */
    Optional<Task> findById(UUID id);
    
    /**
     * Finds all tasks for a user.
     *
     * @param userId the user ID
     * @return the list of tasks
     */
    List<Task> findByUserId(UUID userId);
    
    /**
     * Deletes a task by ID.
     *
     * @param id the task ID
     */
    void delete(UUID id);
}