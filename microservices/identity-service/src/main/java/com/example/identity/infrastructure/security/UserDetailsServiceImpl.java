package com.example.identity.infrastructure.security;

import com.example.identity.domain.model.Email;
import com.example.identity.domain.model.User;
import com.example.identity.domain.ports.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

/**
 * Implementation of Spring Security's UserDetailsService
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        
        try {
            // Try to parse username as UUID (for JWT authentication)
            UUID userId = UUID.fromString(username);
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + username));
        } catch (IllegalArgumentException e) {
            // If username is not a valid UUID, try to find by email
            user = userRepository.findByEmail(Email.of(username))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        }
        
        return new org.springframework.security.core.userdetails.User(
                user.getId().toString(),
                user.getPasswordHash().getHash(),
                Collections.emptyList()
        );
    }
}