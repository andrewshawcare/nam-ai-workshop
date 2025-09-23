package com.example.task.application.usecase;

import com.example.task.domain.model.Task;
import com.example.task.port.input.GetTasksPort;
import com.example.task.port.output.TaskRepositoryPort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Use case for retrieving tasks for a user.
 */
@Service
public class GetTasksUseCase implements GetTasksPort {
    
    private final TaskRepositoryPort taskRepository;
    
    public GetTasksUseCase(TaskRepositoryPort taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasks(String userId) {
        try {
            UUID userUUID = UUID.fromString(userId);
            
            // Retrieve tasks for the user
            return taskRepository.findByUserId(userUUID);
        } catch (IllegalArgumentException e) {
            // Invalid UUID format
            throw new IllegalArgumentException("Invalid user ID format: " + userId, e);
        }
    }
}