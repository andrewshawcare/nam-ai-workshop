package com.example.task.application.usecase;

import com.example.task.domain.model.Task;
import com.example.task.domain.ports.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetTasksUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    private GetTasksUseCase getTasksUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        getTasksUseCase = new GetTasksUseCase(taskRepository);
    }

    @Test
    void shouldGetTasksForUserSuccessfully() {
        // Given
        UUID userId = UUID.randomUUID();
        
        Task task1 = Task.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title("Task 1")
                .description("Description 1")
                .completed(false)
                .createdAt(OffsetDateTime.now().minusDays(2))
                .updatedAt(OffsetDateTime.now().minusDays(1))
                .build();
                
        Task task2 = Task.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title("Task 2")
                .description("Description 2")
                .completed(true)
                .createdAt(OffsetDateTime.now().minusDays(1))
                .updatedAt(OffsetDateTime.now())
                .build();
        
        List<Task> tasks = Arrays.asList(task1, task2);
        
        when(taskRepository.findAllByUserId(userId)).thenReturn(tasks);
        
        // When
        List<Task> result = getTasksUseCase.execute(userId.toString());
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(task1.getId(), result.get(0).getId());
        assertEquals(task2.getId(), result.get(1).getId());
        
        // Verify interactions
        verify(taskRepository).findAllByUserId(userId);
    }
    
    @Test
    void shouldReturnEmptyListWhenNoTasksFound() {
        // Given
        UUID userId = UUID.randomUUID();
        
        when(taskRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());
        
        // When
        List<Task> result = getTasksUseCase.execute(userId.toString());
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        // Verify interactions
        verify(taskRepository).findAllByUserId(userId);
    }
    
    @Test
    void shouldThrowExceptionWhenUserIdIsInvalid() {
        // Given
        String invalidUserId = "invalid-uuid";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            getTasksUseCase.execute(invalidUserId);
        });
        
        // Verify interactions
        verify(taskRepository, never()).findAllByUserId(any());
    }
    
    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            getTasksUseCase.execute(null);
        });
        
        // Verify interactions
        verify(taskRepository, never()).findAllByUserId(any());
    }
}