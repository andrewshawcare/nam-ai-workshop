package com.example.task.adapter.input.rest.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for error responses.
 */
public class ErrorResponse {
    
    private int status;
    private String message;
    private List<String> details = new ArrayList<>();
    
    public ErrorResponse() {
    }
    
    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
    
    public ErrorResponse(int status, String message, List<String> details) {
        this.status = status;
        this.message = message;
        this.details = details;
    }
    
    // Getters and setters
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public List<String> getDetails() {
        return details;
    }
    
    public void setDetails(List<String> details) {
        this.details = details;
    }
    
    /**
     * Adds a detail to the error response.
     *
     * @param detail the detail to add
     */
    public void addDetail(String detail) {
        this.details.add(detail);
    }
}