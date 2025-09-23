package com.example.task.infrastructure.persistence.adapters;

import com.example.task.domain.model.Task;
import com.example.task.domain.ports.TaskRepository;
import com.example.task.infrastructure.persistence.entities.TaskEntity;
import com.example.task.infrastructure.persistence.mappers.TaskMapper;
import com.example.task.infrastructure.persistence.repositories.JpaTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the TaskRepository port that uses JPA
 */
@Repository
@RequiredArgsConstructor
public class TaskRepositoryAdapter implements TaskRepository {

    private final JpaTaskRepository jpaTaskRepository;
    private final TaskMapper taskMapper;

    @Override
    public Task save(Task task) {
        TaskEntity entity = taskMapper.toEntity(task);
        TaskEntity savedEntity = jpaTaskRepository.save(entity);
        return taskMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return jpaTaskRepository.findById(id)
                .map(taskMapper::toDomain);
    }

    @Override
    public List<Task> findAllByUserId(UUID userId) {
        return jpaTaskRepository.findAllByUserId(userId)
                .stream()
                .map(taskMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaTaskRepository.deleteById(id);
    }

    @Override
    public boolean existsByIdAndUserId(UUID id, UUID userId) {
        return jpaTaskRepository.existsByIdAndUserId(id, userId);
    }
}