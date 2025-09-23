package com.example.identity.adapter.input.rest.controller;

import com.example.identity.adapter.input.rest.dto.ErrorResponse;
import com.example.identity.adapter.input.rest.dto.UserResponse;
import com.example.identity.domain.model.User;
import com.example.identity.port.input.GetUserInfoPort;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user operations.
 */
@RestController
public class UserController {
    
    private final GetUserInfoPort getUserInfoPort;
    
    public UserController(GetUserInfoPort getUserInfoPort) {
        this.getUserInfoPort = getUserInfoPort;
    }
    
    /**
     * Retrieves the current authenticated user's information.
     *
     * @return the user information
     */
    @GetMapping("/me")
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        
        User user = getUserInfoPort.getUserInfo(userId);
        return UserResponse.fromUser(user);
    }
    
    /**
     * Handles user not found exceptions.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(GetUserInfoPort.UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(GetUserInfoPort.UserNotFoundException ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }
}