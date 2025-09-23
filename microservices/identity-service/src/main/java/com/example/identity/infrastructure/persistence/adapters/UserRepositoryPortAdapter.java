package com.example.identity.infrastructure.persistence.adapters;

import com.example.identity.domain.model.Email;
import com.example.identity.domain.model.User;
import com.example.identity.domain.ports.UserRepository;
import com.example.identity.port.output.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter that implements UserRepositoryPort and delegates to UserRepository
 */
@Component
public class UserRepositoryPortAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;

    public UserRepositoryPortAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userRepository.existsByEmail(email);
    }
}