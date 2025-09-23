package com.example.task.adapter.input.rest.controller;

import com.example.task.adapter.input.rest.dto.ErrorResponse;
import com.example.task.adapter.input.rest.dto.TaskRequest;
import com.example.task.adapter.input.rest.dto.TaskResponse;
import com.example.task.domain.model.Task;
import com.example.task.infrastructure.security.CurrentUser;
import com.example.task.port.input.CreateTaskPort;
import com.example.task.port.input.DeleteTaskPort;
import com.example.task.port.input.GetTasksPort;
import com.example.task.port.input.UpdateTaskPort;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for task operations.
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    private final CreateTaskPort createTaskPort;
    private final GetTasksPort getTasksPort;
    private final UpdateTaskPort updateTaskPort;
    private final DeleteTaskPort deleteTaskPort;
    
    public TaskController(
        CreateTaskPort createTaskPort,
        GetTasksPort getTasksPort,
        UpdateTaskPort updateTaskPort,
        DeleteTaskPort deleteTaskPort
    ) {
        this.createTaskPort = createTaskPort;
        this.getTasksPort = getTasksPort;
        this.updateTaskPort = updateTaskPort;
        this.deleteTaskPort = deleteTaskPort;
    }
    
    /**
     * Creates a new task.
     *
     * @param request the task request
     * @return the created task
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@CurrentUser UUID userId, @Valid @RequestBody TaskRequest request) {
        Task task = createTaskPort.createTask(
            userId.toString(),
            request.getTitle(),
            request.getDescription(),
            request.getDueDate()
        );
        
        return TaskResponse.fromTask(task);
    }
    
    /**
     * Retrieves all tasks for the current user.
     *
     * @return the list of tasks
     */
    @GetMapping
    public List<TaskResponse> getTasks(@CurrentUser UUID userId) {
        List<Task> tasks = getTasksPort.getTasks(userId.toString());
        
        return tasks.stream()
            .map(TaskResponse::fromTask)
            .collect(Collectors.toList());
    }
    
    /**
     * Updates an existing task.
     *
     * @param id the task ID
     * @param request the task request
     * @return the updated task
     */
    @PutMapping("/{id}")
    public TaskResponse updateTask(@CurrentUser UUID userId, @PathVariable String id, @Valid @RequestBody TaskRequest request) {
        Task task = updateTaskPort.updateTask(
            userId.toString(),
            id,
            request.getTitle(),
            request.getDescription(),
            request.getDueDate(),
            request.getCompleted()
        );
        
        return TaskResponse.fromTask(task);
    }
    
    /**
     * Deletes a task.
     *
     * @param id the task ID
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@CurrentUser UUID userId, @PathVariable String id) {
        deleteTaskPort.deleteTask(userId.toString(), id);
    }
    
    /**
     * Handles validation exceptions.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors);
    }
    
    /**
     * Handles task not found exceptions.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler({UpdateTaskPort.TaskNotFoundException.class, DeleteTaskPort.TaskNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTaskNotFoundException(RuntimeException ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }
    
    /**
     * Handles unauthorized access exceptions.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler({UpdateTaskPort.UnauthorizedAccessException.class, DeleteTaskPort.UnauthorizedAccessException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleUnauthorizedAccessException(RuntimeException ex) {
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    }
}