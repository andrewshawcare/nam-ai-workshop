package com.example.identity.application.usecase;

import com.example.identity.domain.model.Email;
import com.example.identity.domain.model.Password;
import com.example.identity.domain.model.User;
import com.example.identity.domain.service.UserDomainService;
import com.example.identity.port.input.LoginPort;
import com.example.identity.port.output.TokenGenerationPort;
import com.example.identity.port.output.UserRepositoryPort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for user authentication.
 */
@Service
public class LoginUseCase implements LoginPort {
    
    private final UserDomainService userDomainService;
    private final UserRepositoryPort userRepository;
    private final TokenGenerationPort tokenGenerator;
    
    public LoginUseCase(
        UserDomainService userDomainService,
        UserRepositoryPort userRepository,
        TokenGenerationPort tokenGenerator
    ) {
        this.userDomainService = userDomainService;
        this.userRepository = userRepository;
        this.tokenGenerator = tokenGenerator;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public String login(String emailStr, String passwordStr) throws AuthenticationFailedException {
        // Create email value object
        Email email = Email.of(emailStr);
        
        // Find user by email
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new AuthenticationFailedException("Invalid credentials"));
        
        // Create password value object
        Password password = Password.of(passwordStr);
        
        // Authenticate user
        if (!userDomainService.authenticateUser(user, password)) {
            throw new AuthenticationFailedException("Invalid credentials");
        }
        
        // Generate JWT token
        return tokenGenerator.generateToken(user.getId().toString());
    }
}