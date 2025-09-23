package com.example.identity.application.usecase;

import com.example.identity.domain.model.User;
import com.example.identity.port.input.GetUserInfoPort;
import com.example.identity.port.output.UserRepositoryPort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case for retrieving user information.
 */
@Service
public class GetUserInfoUseCase implements GetUserInfoPort {
    
    private final UserRepositoryPort userRepository;
    
    public GetUserInfoUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public User getUserInfo(String userId) throws UserNotFoundException {
        try {
            UUID id = UUID.fromString(userId);
            return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        } catch (IllegalArgumentException e) {
            throw new UserNotFoundException("Invalid user ID format: " + userId);
        }
    }
}