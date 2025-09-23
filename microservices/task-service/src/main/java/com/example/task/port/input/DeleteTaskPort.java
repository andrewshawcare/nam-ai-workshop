package com.example.task.port.input;

/**
 * Input port for deleting a task.
 */
public interface DeleteTaskPort {
    
    /**
     * Deletes a task.
     *
     * @param userId the ID of the user who owns the task
     * @param taskId the ID of the task to delete
     * @throws TaskNotFoundException if the task is not found
     * @throws UnauthorizedAccessException if the user does not own the task
     */
    void deleteTask(String userId, String taskId) 
        throws TaskNotFoundException, UnauthorizedAccessException;
    
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