package com.example.task.port.input;

import com.example.task.domain.model.Task;

import java.time.LocalDateTime;

/**
 * Input port for creating a new task.
 */
public interface CreateTaskPort {
    
    /**
     * Creates a new task for the specified user.
     *
     * @param userId the ID of the user who owns the task
     * @param title the task title
     * @param description the task description (optional)
     * @param dueDate the task due date (optional)
     * @return the created task
     * @throws IllegalArgumentException if the title is empty or null
     */
    Task createTask(String userId, String title, String description, LocalDateTime dueDate);
}