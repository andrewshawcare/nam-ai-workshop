package com.example.task.infrastructure.persistence.mappers;

import com.example.task.domain.model.Task;
import com.example.task.infrastructure.persistence.entities.TaskEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Mapper for converting between Task domain entity and TaskEntity JPA entity
 */
@Component
public class TaskMapper {

    /**
     * Convert a domain Task to a JPA TaskEntity
     *
     * @param task The domain Task to convert
     * @return The corresponding TaskEntity
     */
    public TaskEntity toEntity(Task task) {
        if (task == null) {
            return null;
        }
        
        return TaskEntity.builder()
                .id(task.getId())
                .userId(task.getUserId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate() != null ? OffsetDateTime.of(task.getDueDate(), ZoneOffset.UTC) : null)
                .completed(task.isCompleted())
                .createdAt(OffsetDateTime.of(task.getCreatedAt(), ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.of(task.getUpdatedAt(), ZoneOffset.UTC))
                .build();
    }
    
    /**
     * Convert a JPA TaskEntity to a domain Task
     *
     * @param entity The TaskEntity to convert
     * @return The corresponding domain Task
     */
    public Task toDomain(TaskEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Task.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getDueDate() != null ? entity.getDueDate().toLocalDateTime() : null,
                entity.isCompleted(),
                entity.getCreatedAt().toLocalDateTime(),
                entity.getUpdatedAt().toLocalDateTime()
        );
    }
}