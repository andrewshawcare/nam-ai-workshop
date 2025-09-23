package com.example.task.application.usecase;

import com.example.task.domain.model.Task;
import com.example.task.domain.ports.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateTaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    private CreateTaskUseCase createTaskUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        createTaskUseCase = new CreateTaskUseCase(taskRepository);
    }

    @Test
    void shouldCreateTaskSuccessfully() {
        // Given
        UUID userId = UUID.randomUUID();
        String title = "Test Task";
        String description = "This is a test task";
        OffsetDateTime dueDate = OffsetDateTime.now().plusDays(1);
        
        Task savedTask = Task.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title(title)
                .description(description)
                .dueDate(dueDate)
                .completed(false)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        
        // When
        Task result = createTaskUseCase.execute(userId.toString(), title, description, dueDate);
        
        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(title, result.getTitle());
        assertEquals(description, result.getDescription());
        assertEquals(dueDate, result.getDueDate());
        assertFalse(result.isCompleted());
        
        // Verify interactions
        verify(taskRepository).save(any(Task.class));
        
        // Capture the Task object passed to save
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        
        Task capturedTask = taskCaptor.getValue();
        assertEquals(userId, capturedTask.getUserId());
        assertEquals(title, capturedTask.getTitle());
        assertEquals(description, capturedTask.getDescription());
        assertEquals(dueDate, capturedTask.getDueDate());
        assertFalse(capturedTask.isCompleted());
        assertNotNull(capturedTask.getId());
        assertNotNull(capturedTask.getCreatedAt());
        assertNotNull(capturedTask.getUpdatedAt());
    }
    
    @Test
    void shouldCreateTaskWithoutOptionalFields() {
        // Given
        UUID userId = UUID.randomUUID();
        String title = "Test Task";
        
        Task savedTask = Task.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title(title)
                .completed(false)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        
        // When
        Task result = createTaskUseCase.execute(userId.toString(), title, null, null);
        
        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(title, result.getTitle());
        assertNull(result.getDescription());
        assertNull(result.getDueDate());
        assertFalse(result.isCompleted());
        
        // Verify interactions
        verify(taskRepository).save(any(Task.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUserIdIsInvalid() {
        // Given
        String invalidUserId = "invalid-uuid";
        String title = "Test Task";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            createTaskUseCase.execute(invalidUserId, title, null, null);
        });
        
        // Verify interactions
        verify(taskRepository, never()).save(any(Task.class));
    }
    
    @Test
    void shouldThrowExceptionWhenTitleIsNull() {
        // Given
        UUID userId = UUID.randomUUID();
        
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            createTaskUseCase.execute(userId.toString(), null, null, null);
        });
        
        // Verify interactions
        verify(taskRepository, never()).save(any(Task.class));
    }
    
    @Test
    void shouldThrowExceptionWhenTitleIsEmpty() {
        // Given
        UUID userId = UUID.randomUUID();
        String emptyTitle = "";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            createTaskUseCase.execute(userId.toString(), emptyTitle, null, null);
        });
        
        // Verify interactions
        verify(taskRepository, never()).save(any(Task.class));
    }
}