package com.example.task.domain.model;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void shouldCreateTaskWithAllFields() {
        // Given
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String title = "Test Task";
        String description = "This is a test task";
        OffsetDateTime dueDate = OffsetDateTime.now().plusDays(1);
        boolean completed = false;
        OffsetDateTime createdAt = OffsetDateTime.now().minusDays(1);
        OffsetDateTime updatedAt = OffsetDateTime.now();
        
        // When
        Task task = Task.builder()
                .id(id)
                .userId(userId)
                .title(title)
                .description(description)
                .dueDate(dueDate)
                .completed(completed)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
        
        // Then
        assertEquals(id, task.getId());
        assertEquals(userId, task.getUserId());
        assertEquals(title, task.getTitle());
        assertEquals(description, task.getDescription());
        assertEquals(dueDate, task.getDueDate());
        assertEquals(completed, task.isCompleted());
        assertEquals(createdAt, task.getCreatedAt());
        assertEquals(updatedAt, task.getUpdatedAt());
    }
    
    @Test
    void shouldGenerateIdWhenNotProvided() {
        // Given
        UUID userId = UUID.randomUUID();
        String title = "Test Task";
        
        // When
        Task task = Task.builder()
                .userId(userId)
                .title(title)
                .build();
        
        // Then
        assertNotNull(task.getId());
    }
    
    @Test
    void shouldSetCreatedAtAndUpdatedAtWhenNotProvided() {
        // Given
        UUID userId = UUID.randomUUID();
        String title = "Test Task";
        
        // When
        Task task = Task.builder()
                .userId(userId)
                .title(title)
                .build();
        
        // Then
        assertNotNull(task.getCreatedAt());
        assertNotNull(task.getUpdatedAt());
        // Created at and updated at should be the same when first created
        assertEquals(task.getCreatedAt(), task.getUpdatedAt());
    }
    
    @Test
    void shouldSetCompletedToFalseByDefault() {
        // Given
        UUID userId = UUID.randomUUID();
        String title = "Test Task";
        
        // When
        Task task = Task.builder()
                .userId(userId)
                .title(title)
                .build();
        
        // Then
        assertFalse(task.isCompleted());
    }
    
    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        // Given
        String title = "Test Task";
        
        // Then
        assertThrows(NullPointerException.class, () -> 
            Task.builder()
                .userId(null)
                .title(title)
                .build()
        );
    }
    
    @Test
    void shouldThrowExceptionWhenTitleIsNull() {
        // Given
        UUID userId = UUID.randomUUID();
        
        // Then
        assertThrows(NullPointerException.class, () -> 
            Task.builder()
                .userId(userId)
                .title(null)
                .build()
        );
    }
    
    @Test
    void shouldThrowExceptionWhenTitleIsEmpty() {
        // Given
        UUID userId = UUID.randomUUID();
        
        // Then
        assertThrows(IllegalArgumentException.class, () -> 
            Task.builder()
                .userId(userId)
                .title("")
                .build()
        );
    }
    
    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        // Given
        UUID id = UUID.randomUUID();
        
        Task task1 = Task.builder()
                .id(id)
                .userId(UUID.randomUUID())
                .title("Task 1")
                .build();
                
        Task task2 = Task.builder()
                .id(id)
                .userId(UUID.randomUUID())  // Different user ID
                .title("Task 2")  // Different title
                .build();
        
        // Then
        assertEquals(task1, task2);
        assertEquals(task1.hashCode(), task2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        // Given
        Task task1 = Task.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .title("Same Title")
                .build();
                
        Task task2 = Task.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .title("Same Title")  // Same title
                .build();
        
        // Then
        assertNotEquals(task1, task2);
    }
    
    @Test
    void shouldMarkTaskAsCompleted() {
        // Given
        Task task = Task.builder()
                .userId(UUID.randomUUID())
                .title("Test Task")
                .completed(false)
                .build();
        
        // When
        Task completedTask = task.toBuilder()
                .completed(true)
                .build();
        
        // Then
        assertTrue(completedTask.isCompleted());
    }
}