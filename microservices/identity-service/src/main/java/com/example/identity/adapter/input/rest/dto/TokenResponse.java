package com.example.identity.adapter.input.rest.dto;

/**
 * DTO for token responses.
 */
public class TokenResponse {
    
    private String token;
    private long expiresIn;
    
    public TokenResponse() {
    }
    
    public TokenResponse(String token, long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }
    
    // Getters and setters
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}