package com.example.task.infrastructure.persistence.repositories;

import com.example.task.infrastructure.persistence.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for TaskEntity
 */
@Repository
public interface JpaTaskRepository extends JpaRepository<TaskEntity, UUID> {
    
    /**
     * Find all tasks for a specific user
     * 
     * @param userId The user ID
     * @return A list of tasks belonging to the user
     */
    List<TaskEntity> findAllByUserId(UUID userId);
    
    /**
     * Check if a task exists and belongs to a specific user
     * 
     * @param id The task ID
     * @param userId The user ID
     * @return true if the task exists and belongs to the user, false otherwise
     */
    boolean existsByIdAndUserId(UUID id, UUID userId);
}