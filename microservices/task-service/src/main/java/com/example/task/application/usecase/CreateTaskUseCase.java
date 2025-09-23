package com.example.task.application.usecase;

import com.example.task.domain.model.Task;
import com.example.task.domain.service.TaskDomainService;
import com.example.task.port.input.CreateTaskPort;
import com.example.task.port.output.TaskRepositoryPort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Use case for creating a new task.
 */
@Service
public class CreateTaskUseCase implements CreateTaskPort {
    
    private final TaskDomainService taskDomainService;
    private final TaskRepositoryPort taskRepository;
    
    public CreateTaskUseCase(TaskDomainService taskDomainService, TaskRepositoryPort taskRepository) {
        this.taskDomainService = taskDomainService;
        this.taskRepository = taskRepository;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Task createTask(String userId, String title, String description, LocalDateTime dueDate) {
        try {
            UUID userUUID = UUID.fromString(userId);
            
            // Create task using domain service
            Task task = taskDomainService.createTask(userUUID, title, description, dueDate);
            
            // Save task to repository
            return taskRepository.save(task);
        } catch (IllegalArgumentException e) {
            // This catches both invalid UUID format and invalid task data (e.g., empty title)
            throw new IllegalArgumentException("Invalid input: " + e.getMessage(), e);
        }
    }
}