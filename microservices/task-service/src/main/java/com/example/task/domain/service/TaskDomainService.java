package com.example.task.domain.service;

import com.example.task.domain.model.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain service containing business logic related to tasks.
 */
@Service
public class TaskDomainService {
    
    /**
     * Creates a new task with the given details.
     *
     * @param userId the ID of the user who owns the task
     * @param title the task title
     * @param description the task description (optional)
     * @param dueDate the task due date (optional)
     * @return a new Task instance
     */
    public Task createTask(UUID userId, String title, String description, LocalDateTime dueDate) {
        return Task.create(userId, title, description, dueDate);
    }
    
    /**
     * Updates an existing task with new details.
     *
     * @param task the task to update
     * @param title the new title
     * @param description the new description
     * @param dueDate the new due date
     * @return the updated task
     */
    public Task updateTask(Task task, String title, String description, LocalDateTime dueDate) {
        task.update(title, description, dueDate);
        return task;
    }
    
    /**
     * Toggles the completion status of a task.
     *
     * @param task the task to update
     * @param completed the new completion status
     * @return the updated task
     */
    public Task toggleTaskCompletion(Task task, boolean completed) {
        if (completed) {
            task.markAsCompleted();
        } else {
            task.markAsNotCompleted();
        }
        return task;
    }
    
    /**
     * Verifies if a task belongs to a specific user.
     *
     * @param task the task to check
     * @param userId the user ID to verify against
     * @return true if the task belongs to the user, false otherwise
     */
    public boolean verifyTaskOwnership(Task task, UUID userId) {
        return task.belongsToUser(userId);
    }
}