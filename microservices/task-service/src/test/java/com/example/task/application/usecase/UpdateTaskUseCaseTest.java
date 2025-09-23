package com.example.task.application.usecase;

import com.example.task.domain.model.Task;
import com.example.task.domain.ports.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateTaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    private UpdateTaskUseCase updateTaskUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        updateTaskUseCase = new UpdateTaskUseCase(taskRepository);
    }

    @Test
    void shouldUpdateTaskSuccessfully() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        
        String originalTitle = "Original Title";
        String originalDescription = "Original Description";
        OffsetDateTime originalDueDate = OffsetDateTime.now().plusDays(1);
        boolean originalCompleted = false;
        
        String updatedTitle = "Updated Title";
        String updatedDescription = "Updated Description";
        OffsetDateTime updatedDueDate = OffsetDateTime.now().plusDays(2);
        boolean updatedCompleted = true;
        
        Task existingTask = Task.builder()
                .id(taskId)
                .userId(userId)
                .title(originalTitle)
                .description(originalDescription)
                .dueDate(originalDueDate)
                .completed(originalCompleted)
                .createdAt(OffsetDateTime.now().minusDays(1))
                .updatedAt(OffsetDateTime.now().minusHours(1))
                .build();
        
        Task updatedTask = Task.builder()
                .id(taskId)
                .userId(userId)
                .title(updatedTitle)
                .description(updatedDescription)
                .dueDate(updatedDueDate)
                .completed(updatedCompleted)
                .createdAt(existingTask.getCreatedAt())
                .updatedAt(OffsetDateTime.now())
                .build();
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.existsByIdAndUserId(taskId, userId)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        
        // When
        Task result = updateTaskUseCase.execute(
                userId.toString(),
                taskId.toString(),
                updatedTitle,
                updatedDescription,
                updatedDueDate,
                updatedCompleted
        );
        
        // Then
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(updatedTitle, result.getTitle());
        assertEquals(updatedDescription, result.getDescription());
        assertEquals(updatedDueDate, result.getDueDate());
        assertEquals(updatedCompleted, result.isCompleted());
        
        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository).existsByIdAndUserId(taskId, userId);
        verify(taskRepository).save(any(Task.class));
        
        // Capture the Task object passed to save
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        
        Task capturedTask = taskCaptor.getValue();
        assertEquals(taskId, capturedTask.getId());
        assertEquals(userId, capturedTask.getUserId());
        assertEquals(updatedTitle, capturedTask.getTitle());
        assertEquals(updatedDescription, capturedTask.getDescription());
        assertEquals(updatedDueDate, capturedTask.getDueDate());
        assertEquals(updatedCompleted, capturedTask.isCompleted());
        assertEquals(existingTask.getCreatedAt(), capturedTask.getCreatedAt());
        assertNotNull(capturedTask.getUpdatedAt());
    }
    
    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        
        // When & Then
        Exception exception = assertThrows(UpdateTaskUseCase.TaskNotFoundException.class, () -> {
            updateTaskUseCase.execute(
                    userId.toString(),
                    taskId.toString(),
                    "Updated Title",
                    "Updated Description",
                    OffsetDateTime.now().plusDays(1),
                    true
            );
        });
        
        assertEquals("Task not found with id: " + taskId, exception.getMessage());
        
        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).existsByIdAndUserId(any(), any());
        verify(taskRepository, never()).save(any(Task.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUserDoesNotOwnTask() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        
        Task existingTask = Task.builder()
                .id(taskId)
                .userId(UUID.randomUUID())  // Different user ID
                .title("Original Title")
                .build();
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.existsByIdAndUserId(taskId, userId)).thenReturn(false);
        
        // When & Then
        Exception exception = assertThrows(UpdateTaskUseCase.UnauthorizedAccessException.class, () -> {
            updateTaskUseCase.execute(
                    userId.toString(),
                    taskId.toString(),
                    "Updated Title",
                    "Updated Description",
                    OffsetDateTime.now().plusDays(1),
                    true
            );
        });
        
        assertEquals("User does not have access to task with id: " + taskId, exception.getMessage());
        
        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository).existsByIdAndUserId(taskId, userId);
        verify(taskRepository, never()).save(any(Task.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUserIdIsInvalid() {
        // Given
        String invalidUserId = "invalid-uuid";
        UUID taskId = UUID.randomUUID();
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            updateTaskUseCase.execute(
                    invalidUserId,
                    taskId.toString(),
                    "Updated Title",
                    "Updated Description",
                    OffsetDateTime.now().plusDays(1),
                    true
            );
        });
        
        // Verify interactions
        verify(taskRepository, never()).findById(any());
        verify(taskRepository, never()).existsByIdAndUserId(any(), any());
        verify(taskRepository, never()).save(any(Task.class));
    }
    
    @Test
    void shouldThrowExceptionWhenTaskIdIsInvalid() {
        // Given
        UUID userId = UUID.randomUUID();
        String invalidTaskId = "invalid-uuid";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            updateTaskUseCase.execute(
                    userId.toString(),
                    invalidTaskId,
                    "Updated Title",
                    "Updated Description",
                    OffsetDateTime.now().plusDays(1),
                    true
            );
        });
        
        // Verify interactions
        verify(taskRepository, never()).findById(any());
        verify(taskRepository, never()).existsByIdAndUserId(any(), any());
        verify(taskRepository, never()).save(any(Task.class));
    }
    
    @Test
    void shouldThrowExceptionWhenTitleIsNull() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            updateTaskUseCase.execute(
                    userId.toString(),
                    taskId.toString(),
                    null,
                    "Updated Description",
                    OffsetDateTime.now().plusDays(1),
                    true
            );
        });
        
        // Verify interactions
        verify(taskRepository, never()).findById(any());
        verify(taskRepository, never()).existsByIdAndUserId(any(), any());
        verify(taskRepository, never()).save(any(Task.class));
    }
    
    @Test
    void shouldThrowExceptionWhenTitleIsEmpty() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            updateTaskUseCase.execute(
                    userId.toString(),
                    taskId.toString(),
                    "",
                    "Updated Description",
                    OffsetDateTime.now().plusDays(1),
                    true
            );
        });
        
        // Verify interactions
        verify(taskRepository, never()).findById(any());
        verify(taskRepository, never()).existsByIdAndUserId(any(), any());
        verify(taskRepository, never()).save(any(Task.class));
    }
}