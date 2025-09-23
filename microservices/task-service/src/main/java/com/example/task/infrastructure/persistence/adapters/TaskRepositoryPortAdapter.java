package com.example.task.infrastructure.persistence.adapters;

import com.example.task.domain.model.Task;
import com.example.task.domain.ports.TaskRepository;
import com.example.task.port.output.TaskRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter that implements TaskRepositoryPort and delegates to TaskRepository
 */
@Component
public class TaskRepositoryPortAdapter implements TaskRepositoryPort {

    private final TaskRepository taskRepository;

    public TaskRepositoryPortAdapter(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return taskRepository.findById(id);
    }

    @Override
    public List<Task> findByUserId(UUID userId) {
        return taskRepository.findAllByUserId(userId);
    }

    @Override
    public void delete(UUID id) {
        taskRepository.deleteById(id);
    }
}