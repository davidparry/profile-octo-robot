package com.bug.robot.common.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response structure for API errors.
 */
public class ErrorResponse {

    private LocalDateTime timestamp;
    private String error;
    private String message;
    private Map<String, String> validationErrors;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String error, String message) {
        this();
        this.error = error;
        this.message = message;
    }

    public ErrorResponse(String error, String message, Map<String, String> validationErrors) {
        this(error, message);
        this.validationErrors = validationErrors;
    }

    // Getters and setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
