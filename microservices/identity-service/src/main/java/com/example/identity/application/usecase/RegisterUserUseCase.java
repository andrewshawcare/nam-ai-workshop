package com.example.identity.application.usecase;

import com.example.identity.domain.model.Email;
import com.example.identity.domain.model.Password;
import com.example.identity.domain.model.User;
import com.example.identity.domain.service.UserDomainService;
import com.example.identity.port.input.RegisterUserPort;
import com.example.identity.port.output.UserRepositoryPort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for registering a new user.
 */
@Service
public class RegisterUserUseCase implements RegisterUserPort {
    
    private final UserDomainService userDomainService;
    private final UserRepositoryPort userRepository;
    
    public RegisterUserUseCase(UserDomainService userDomainService, UserRepositoryPort userRepository) {
        this.userDomainService = userDomainService;
        this.userRepository = userRepository;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public User registerUser(String emailStr, String passwordStr) throws UserAlreadyExistsException {
        // Create email value object
        Email email = Email.of(emailStr);
        
        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with email " + emailStr + " already exists");
        }
        
        // Create password value object
        Password password = Password.of(passwordStr);
        
        // Register user using domain service
        User user = userDomainService.registerUser(email, password);
        
        // Save user to repository
        return userRepository.save(user);
    }
}