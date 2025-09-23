package com.example.task.port.input;

import com.example.task.domain.model.Task;

import java.time.LocalDateTime;

/**
 * Input port for updating a task.
 */
public interface UpdateTaskPort {
    
    /**
     * Updates an existing task.
     *
     * @param userId the ID of the user who owns the task
     * @param taskId the ID of the task to update
     * @param title the new title
     * @param description the new description
     * @param dueDate the new due date
     * @param completed the new completion status
     * @return the updated task
     * @throws TaskNotFoundException if the task is not found
     * @throws UnauthorizedAccessException if the user does not own the task
     * @throws IllegalArgumentException if the title is empty or null
     */
    Task updateTask(
        String userId,
        String taskId,
        String title,
        String description,
        LocalDateTime dueDate,
        Boolean completed
    ) throws TaskNotFoundException, UnauthorizedAccessException;
    
    /**
     * Exception thrown when a task is not found.
     */
    class TaskNotFoundException extends RuntimeException {
        public TaskNotFoundException(String message) {
            super(message);
        }
    }
    
    /**
     * Exception thrown when a user attempts to access a task they don't own.
     */
    class UnauthorizedAccessException extends RuntimeException {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
    }
}