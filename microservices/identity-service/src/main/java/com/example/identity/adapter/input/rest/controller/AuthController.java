package com.example.identity.adapter.input.rest.controller;

import com.example.identity.adapter.input.rest.dto.ErrorResponse;
import com.example.identity.adapter.input.rest.dto.LoginRequest;
import com.example.identity.adapter.input.rest.dto.TokenResponse;
import com.example.identity.adapter.input.rest.dto.UserRegistrationRequest;
import com.example.identity.adapter.input.rest.dto.UserResponse;
import com.example.identity.domain.model.User;
import com.example.identity.port.input.LoginPort;
import com.example.identity.port.input.RegisterUserPort;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for authentication operations.
 */
@RestController
public class AuthController {
    
    private final RegisterUserPort registerUserPort;
    private final LoginPort loginPort;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    public AuthController(RegisterUserPort registerUserPort, LoginPort loginPort) {
        this.registerUserPort = registerUserPort;
        this.loginPort = loginPort;
    }
    
    /**
     * Registers a new user.
     *
     * @param request the registration request
     * @return the created user
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody UserRegistrationRequest request) {
        User user = registerUserPort.registerUser(request.getEmail(), request.getPassword());
        return UserResponse.fromUser(user);
    }
    
    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request the login request
     * @return the JWT token
     */
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        String token = loginPort.login(request.getEmail(), request.getPassword());
        return new TokenResponse(token, jwtExpiration / 1000); // Convert to seconds
    }
    
    /**
     * Handles validation exceptions.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors);
    }
    
    /**
     * Handles user already exists exceptions.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(RegisterUserPort.UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExistsException(RegisterUserPort.UserAlreadyExistsException ex) {
        return new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
    }
    
    /**
     * Handles authentication failed exceptions.
     *
     * @param ex the exception
     * @return the error response
     */
    @ExceptionHandler(LoginPort.AuthenticationFailedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationFailedException(LoginPort.AuthenticationFailedException ex) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    }
}