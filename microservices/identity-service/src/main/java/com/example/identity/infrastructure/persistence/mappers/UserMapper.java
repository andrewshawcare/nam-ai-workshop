package com.example.identity.infrastructure.persistence.mappers;

import com.example.identity.domain.model.Email;
import com.example.identity.domain.model.PasswordHash;
import com.example.identity.domain.model.User;
import com.example.identity.infrastructure.persistence.entities.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Mapper for converting between User domain entity and UserEntity JPA entity
 */
@Component
public class UserMapper {

    /**
     * Convert a domain User to a JPA UserEntity
     * 
     * @param user The domain User to convert
     * @return The corresponding UserEntity
     */
    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        
        return UserEntity.builder()
                .id(user.getId())
                .email(user.getEmail().getValue())
                .passwordHash(user.getPasswordHash().getHash())
                .createdAt(OffsetDateTime.of(user.getCreatedAt(), ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.of(user.getUpdatedAt(), ZoneOffset.UTC))
                .build();
    }
    
    /**
     * Convert a JPA UserEntity to a domain User
     * 
     * @param entity The UserEntity to convert
     * @return The corresponding domain User
     */
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return User.reconstitute(
                entity.getId(),
                Email.of(entity.getEmail()),
                PasswordHash.fromHash(entity.getPasswordHash()),
                entity.getCreatedAt().toLocalDateTime(),
                entity.getUpdatedAt().toLocalDateTime());
    }
}