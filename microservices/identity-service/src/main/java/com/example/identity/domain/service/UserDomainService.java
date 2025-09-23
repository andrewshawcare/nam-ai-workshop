package com.example.identity.domain.service;

import com.example.identity.domain.model.Email;
import com.example.identity.domain.model.Password;
import com.example.identity.domain.model.User;
import org.springframework.stereotype.Service;

/**
 * Domain service containing business logic related to users.
 */
@Service
public class UserDomainService {
    
    /**
     * Registers a new user with the given email and password.
     *
     * @param email the user's email
     * @param password the user's password
     * @return a new User instance
     */
    public User registerUser(Email email, Password password) {
        return User.create(email, password);
    }
    
    /**
     * Authenticates a user with the given password.
     *
     * @param user the user to authenticate
     * @param password the password to verify
     * @return true if authentication is successful, false otherwise
     */
    public boolean authenticateUser(User user, Password password) {
        return user.verifyPassword(password);
    }
}