package com.example.task.port.input;

import com.example.task.domain.model.Task;

import java.util.List;

/**
 * Input port for retrieving tasks.
 */
public interface GetTasksPort {
    
    /**
     * Retrieves all tasks for the specified user.
     *
     * @param userId the ID of the user whose tasks to retrieve
     * @return the list of tasks
     */
    List<Task> getTasks(String userId);
}