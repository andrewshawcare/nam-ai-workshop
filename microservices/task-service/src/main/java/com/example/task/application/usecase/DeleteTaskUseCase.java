package com.example.task.application.usecase;

import com.example.task.domain.model.Task;
import com.example.task.domain.service.TaskDomainService;
import com.example.task.port.input.DeleteTaskPort;
import com.example.task.port.output.TaskRepositoryPort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case for deleting a task.
 */
@Service
public class DeleteTaskUseCase implements DeleteTaskPort {
    
    private final TaskDomainService taskDomainService;
    private final TaskRepositoryPort taskRepository;
    
    public DeleteTaskUseCase(TaskDomainService taskDomainService, TaskRepositoryPort taskRepository) {
        this.taskDomainService = taskDomainService;
        this.taskRepository = taskRepository;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteTask(String userId, String taskId) 
        throws TaskNotFoundException, UnauthorizedAccessException {
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
            
            // Delete task
            taskRepository.delete(taskUUID);
        } catch (IllegalArgumentException e) {
            // This catches invalid UUID format
            if (e.getMessage().contains("UUID")) {
                throw new IllegalArgumentException("Invalid ID format", e);
            }
            throw e;
        }
    }
}