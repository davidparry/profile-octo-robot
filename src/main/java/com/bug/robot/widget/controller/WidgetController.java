package com.bug.robot.widget.controller;

import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;
import com.bug.robot.widget.exception.ProfileNotFoundException;
import com.bug.robot.widget.exception.WidgetValidationException;
import com.bug.robot.widget.service.WidgetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for Widget operations.
 * Follows RESTful API best practices with proper HTTP status codes and error handling.
 * Uses constructor-based dependency injection.
 */
@RestController
@RequestMapping("/api")
public class WidgetController {

    private final WidgetService widgetService;

    /**
     * Constructor-based dependency injection (recommended best practice).
     */
    @Autowired
    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    /**
     * Create a new widget for a user profile.
     * 
     * Endpoint: POST /api/users/{userId}/widgets
     * 
     * @param userId the user/profile ID
     * @param requestDTO the widget request data
     * @return ResponseEntity with created widget (201) or error
     */
    @PostMapping("/users/{userId}/widgets")
    public ResponseEntity<WidgetResponseDTO> createWidget(
            @PathVariable Long userId,
            @Valid @RequestBody WidgetRequestDTO requestDTO) {
        
        WidgetResponseDTO response = widgetService.createWidget(userId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Global exception handler for ProfileNotFoundException.
     * Returns 404 Not Found with error message.
     */
    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProfileNotFound(ProfileNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Profile Not Found");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Global exception handler for WidgetValidationException.
     * Returns 400 Bad Request with error message.
     */
    @ExceptionHandler(WidgetValidationException.class)
    public ResponseEntity<Map<String, String>> handleWidgetValidation(WidgetValidationException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Validation Failed");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Global exception handler for validation errors.
     * Returns 400 Bad Request with field-specific error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        Map<String, Object> error = new HashMap<>();
        error.put("error", "Validation Failed");
        error.put("message", "Invalid input data");
        error.put("fieldErrors", fieldErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Global exception handler for unexpected errors.
     * Returns 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralError(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
