package com.example.task.application.usecase;

import com.example.task.domain.model.Task;
import com.example.task.domain.service.TaskDomainService;
import com.example.task.port.input.UpdateTaskPort;
import com.example.task.port.output.TaskRepositoryPort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Use case for updating a task.
 */
@Service
public class UpdateTaskUseCase implements UpdateTaskPort {
    
    private final TaskDomainService taskDomainService;
    private final TaskRepositoryPort taskRepository;
    
    public UpdateTaskUseCase(TaskDomainService taskDomainService, TaskRepositoryPort taskRepository) {
        this.taskDomainService = taskDomainService;
        this.taskRepository = taskRepository;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Task updateTask(
        String userId,
        String taskId,
        String title,
        String description,
        LocalDateTime dueDate,
        Boolean completed
    ) throws TaskNotFoundException, UnauthorizedAccessException {
        try {
            UUID userUUID = UUID.fromString(userId);
            UUID taskUUID = UUID.fromString(taskId);
            
            // Find task
            Task task = taskRepository.findById(taskUUID)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));
            
            // Verify ownership
            if (!taskDomainService.verifyTaskOwnership(task, userUUID)) {
                throw new UnauthorizedAccessException("User does not have access to this task");
            }
            
            // Update task details
            task = taskDomainService.updateTask(task, title, description, dueDate);
            
            // Update completion status if provided
            if (completed != null) {
                task = taskDomainService.toggleTaskCompletion(task, completed);
            }
            
            // Save updated task
            return taskRepository.save(task);
        } catch (IllegalArgumentException e) {
            // This catches invalid UUID format and invalid task data
            if (e.getMessage().contains("UUID")) {
                throw new IllegalArgumentException("Invalid ID format", e);
            }
            throw e;
        }
    }
}