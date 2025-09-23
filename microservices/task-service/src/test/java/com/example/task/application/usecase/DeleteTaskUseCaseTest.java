package com.example.task.application.usecase;

import com.example.task.domain.model.Task;
import com.example.task.domain.ports.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeleteTaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    private DeleteTaskUseCase deleteTaskUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        deleteTaskUseCase = new DeleteTaskUseCase(taskRepository);
    }

    @Test
    void shouldDeleteTaskSuccessfully() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        
        Task existingTask = Task.builder()
                .id(taskId)
                .userId(userId)
                .title("Task to Delete")
                .createdAt(OffsetDateTime.now().minusDays(1))
                .updatedAt(OffsetDateTime.now())
                .build();
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.existsByIdAndUserId(taskId, userId)).thenReturn(true);
        
        // When
        deleteTaskUseCase.execute(userId.toString(), taskId.toString());
        
        // Then
        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository).existsByIdAndUserId(taskId, userId);
        verify(taskRepository).deleteById(taskId);
    }
    
    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        
        // When & Then
        Exception exception = assertThrows(DeleteTaskUseCase.TaskNotFoundException.class, () -> {
            deleteTaskUseCase.execute(userId.toString(), taskId.toString());
        });
        
        assertEquals("Task not found with id: " + taskId, exception.getMessage());
        
        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).existsByIdAndUserId(any(), any());
        verify(taskRepository, never()).deleteById(any());
    }
    
    @Test
    void shouldThrowExceptionWhenUserDoesNotOwnTask() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        
        Task existingTask = Task.builder()
                .id(taskId)
                .userId(UUID.randomUUID())  // Different user ID
                .title("Task to Delete")
                .build();
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.existsByIdAndUserId(taskId, userId)).thenReturn(false);
        
        // When & Then
        Exception exception = assertThrows(DeleteTaskUseCase.UnauthorizedAccessException.class, () -> {
            deleteTaskUseCase.execute(userId.toString(), taskId.toString());
        });
        
        assertEquals("User does not have access to task with id: " + taskId, exception.getMessage());
        
        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository).existsByIdAndUserId(taskId, userId);
        verify(taskRepository, never()).deleteById(any());
    }
    
    @Test
    void shouldThrowExceptionWhenUserIdIsInvalid() {
        // Given
        String invalidUserId = "invalid-uuid";
        UUID taskId = UUID.randomUUID();
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            deleteTaskUseCase.execute(invalidUserId, taskId.toString());
        });
        
        // Verify interactions
        verify(taskRepository, never()).findById(any());
        verify(taskRepository, never()).existsByIdAndUserId(any(), any());
        verify(taskRepository, never()).deleteById(any());
    }
    
    @Test
    void shouldThrowExceptionWhenTaskIdIsInvalid() {
        // Given
        UUID userId = UUID.randomUUID();
        String invalidTaskId = "invalid-uuid";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            deleteTaskUseCase.execute(userId.toString(), invalidTaskId);
        });
        
        // Verify interactions
        verify(taskRepository, never()).findById(any());
        verify(taskRepository, never()).existsByIdAndUserId(any(), any());
        verify(taskRepository, never()).deleteById(any());
    }
}