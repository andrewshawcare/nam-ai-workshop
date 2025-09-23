package com.example.task.infrastructure.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user is not authenticated or not authorized to access a resource
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

    /**
     * Create a new UnauthorizedException with the specified message
     * 
     * @param message The error message
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Create a new UnauthorizedException with the specified message and cause
     * 
     * @param message The error message
     * @param cause The cause of the exception
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}