package com.example.task.adapter.input.rest.controller;

import com.example.task.adapter.input.rest.dto.TaskRequest;
import com.example.task.domain.model.Task;
import com.example.task.domain.ports.TaskRepository;
import com.example.task.infrastructure.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskRepository taskRepository;

    private UUID userId;
    private UUID taskId;
    private Task task;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(taskRepository);
        
        // Set up common test data
        userId = UUID.randomUUID();
        taskId = UUID.randomUUID();
        task = Task.builder()
                .id(taskId)
                .userId(userId)
                .title("Test Task")
                .description("Test Description")
                .dueDate(OffsetDateTime.now().plusDays(1))
                .completed(false)
                .createdAt(OffsetDateTime.now().minusDays(1))
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    private MockHttpServletRequestBuilder addAuthHeader(MockHttpServletRequestBuilder request) {
        return request.header("Authorization", "Bearer valid.jwt.token")
                .requestAttr(JwtAuthenticationFilter.USER_ID_ATTRIBUTE, userId);
    }

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        // Given
        TaskRequest request = new TaskRequest(
                "Test Task",
                "Test Description",
                OffsetDateTime.now().plusDays(1),
                false
        );

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // When & Then
        mockMvc.perform(addAuthHeader(post("/tasks"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.completed").value(false));

        // Verify interactions
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldReturnBadRequestWhenTitleIsEmpty() throws Exception {
        // Given
        TaskRequest request = new TaskRequest(
                "",
                "Test Description",
                OffsetDateTime.now().plusDays(1),
                false
        );

        // When & Then
        mockMvc.perform(addAuthHeader(post("/tasks"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Verify interactions
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldGetAllTasksSuccessfully() throws Exception {
        // Given
        Task task2 = Task.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title("Test Task 2")
                .description("Test Description 2")
                .completed(true)
                .createdAt(OffsetDateTime.now().minusDays(2))
                .updatedAt(OffsetDateTime.now())
                .build();

        when(taskRepository.findAllByUserId(userId)).thenReturn(Arrays.asList(task, task2));

        // When & Then
        mockMvc.perform(addAuthHeader(get("/tasks")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(taskId.toString()))
                .andExpect(jsonPath("$[0].title").value("Test Task"))
                .andExpect(jsonPath("$[1].title").value("Test Task 2"));

        // Verify interactions
        verify(taskRepository).findAllByUserId(userId);
    }

    @Test
    void shouldReturnEmptyListWhenNoTasksFound() throws Exception {
        // Given
        when(taskRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(addAuthHeader(get("/tasks")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // Verify interactions
        verify(taskRepository).findAllByUserId(userId);
    }

    @Test
    void shouldUpdateTaskSuccessfully() throws Exception {
        // Given
        TaskRequest request = new TaskRequest(
                "Updated Task",
                "Updated Description",
                OffsetDateTime.now().plusDays(2),
                true
        );

        Task updatedTask = Task.builder()
                .id(taskId)
                .userId(userId)
                .title("Updated Task")
                .description("Updated Description")
                .dueDate(OffsetDateTime.now().plusDays(2))
                .completed(true)
                .createdAt(task.getCreatedAt())
                .updatedAt(OffsetDateTime.now())
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.existsByIdAndUserId(taskId, userId)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // When & Then
        mockMvc.perform(addAuthHeader(put("/tasks/{id}", taskId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.completed").value(true));

        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository).existsByIdAndUserId(taskId, userId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentTask() throws Exception {
        // Given
        TaskRequest request = new TaskRequest(
                "Updated Task",
                "Updated Description",
                OffsetDateTime.now().plusDays(2),
                true
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(addAuthHeader(put("/tasks/{id}", taskId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).existsByIdAndUserId(any(), any());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldReturnForbiddenWhenUpdatingTaskOfAnotherUser() throws Exception {
        // Given
        TaskRequest request = new TaskRequest(
                "Updated Task",
                "Updated Description",
                OffsetDateTime.now().plusDays(2),
                true
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.existsByIdAndUserId(taskId, userId)).thenReturn(false);

        // When & Then
        mockMvc.perform(addAuthHeader(put("/tasks/{id}", taskId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository).existsByIdAndUserId(taskId, userId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldDeleteTaskSuccessfully() throws Exception {
        // Given
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.existsByIdAndUserId(taskId, userId)).thenReturn(true);

        // When & Then
        mockMvc.perform(addAuthHeader(delete("/tasks/{id}", taskId)))
                .andExpect(status().isNoContent());

        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository).existsByIdAndUserId(taskId, userId);
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentTask() throws Exception {
        // Given
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(addAuthHeader(delete("/tasks/{id}", taskId)))
                .andExpect(status().isNotFound());

        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).existsByIdAndUserId(any(), any());
        verify(taskRepository, never()).deleteById(any());
    }

    @Test
    void shouldReturnForbiddenWhenDeletingTaskOfAnotherUser() throws Exception {
        // Given
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.existsByIdAndUserId(taskId, userId)).thenReturn(false);

        // When & Then
        mockMvc.perform(addAuthHeader(delete("/tasks/{id}", taskId)))
                .andExpect(status().isForbidden());

        // Verify interactions
        verify(taskRepository).findById(taskId);
        verify(taskRepository).existsByIdAndUserId(taskId, userId);
        verify(taskRepository, never()).deleteById(any());
    }

    @Test
    void shouldReturnUnauthorizedWhenNoAuthenticationProvided() throws Exception {
        // When & Then
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isUnauthorized());

        // Verify interactions
        verify(taskRepository, never()).findAllByUserId(any());
    }

    // Helper class to match the TaskRequest DTO
    static class TaskRequest {
        private String title;
        private String description;
        private OffsetDateTime dueDate;
        private Boolean completed;

        public TaskRequest() {
        }

        public TaskRequest(String title, String description, OffsetDateTime dueDate, Boolean completed) {
            this.title = title;
            this.description = description;
            this.dueDate = dueDate;
            this.completed = completed;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public OffsetDateTime getDueDate() {
            return dueDate;
        }

        public Boolean getCompleted() {
            return completed;
        }
    }
}