package com.bug.robot.profile.widget.controller;

import com.bug.robot.profile.widget.dto.WidgetRequestDTO;
import com.bug.robot.profile.widget.dto.WidgetResponseDTO;
import com.bug.robot.profile.widget.exception.ProfileNotFoundException;
import com.bug.robot.profile.widget.service.WidgetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Widget management endpoints.
 * Follows RESTful API design principles with proper HTTP status codes.
 * Uses constructor injection following DIP.
 */
@RestController
@RequestMapping("/api/profiles/{profileId}/widgets")
public class WidgetController {

    private static final Logger logger = LoggerFactory.getLogger(WidgetController.class);

    private final WidgetService widgetService;

    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    /**
     * Create a new widget for a profile.
     * POST /api/profiles/{profileId}/widgets
     * 
     * @return 201 Created with widget data
     * @throws ProfileNotFoundException if profile not found (handled by exception handler)
     */
    @PostMapping
    public ResponseEntity<WidgetResponseDTO> createWidget(
            @PathVariable Long profileId,
            @Valid @RequestBody WidgetRequestDTO request) {
        
        logger.info("Creating widget for profile ID: {}", profileId);
        WidgetResponseDTO response = widgetService.createWidget(profileId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all widgets for a profile.
     * GET /api/profiles/{profileId}/widgets
     * 
     * @return 200 OK with list of widgets
     */
    @GetMapping
    public ResponseEntity<List<WidgetResponseDTO>> getWidgets(@PathVariable Long profileId) {
        logger.debug("Fetching widgets for profile ID: {}", profileId);
        List<WidgetResponseDTO> widgets = widgetService.getWidgetsByProfile(profileId);
        return ResponseEntity.ok(widgets);
    }

    /**
     * Get a specific widget by ID.
     * GET /api/profiles/{profileId}/widgets/{widgetId}
     * 
     * @return 200 OK with widget data, or 404 Not Found
     */
    @GetMapping("/{widgetId}")
    public ResponseEntity<WidgetResponseDTO> getWidget(
            @PathVariable Long profileId,
            @PathVariable Long widgetId) {
        
        logger.debug("Fetching widget ID: {} for profile ID: {}", widgetId, profileId);
        return widgetService.getWidget(profileId, widgetId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update an existing widget.
     * PUT /api/profiles/{profileId}/widgets/{widgetId}
     * 
     * @return 200 OK with updated widget data, or 404 Not Found
     */
    @PutMapping("/{widgetId}")
    public ResponseEntity<WidgetResponseDTO> updateWidget(
            @PathVariable Long profileId,
            @PathVariable Long widgetId,
            @Valid @RequestBody WidgetRequestDTO request) {
        
        logger.info("Updating widget ID: {} for profile ID: {}", widgetId, profileId);
        return widgetService.updateWidget(profileId, widgetId, request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a widget.
     * DELETE /api/profiles/{profileId}/widgets/{widgetId}
     * 
     * @return 204 No Content on success, or 404 Not Found
     */
    @DeleteMapping("/{widgetId}")
    public ResponseEntity<Void> deleteWidget(
            @PathVariable Long profileId,
            @PathVariable Long widgetId) {
        
        logger.info("Deleting widget ID: {} for profile ID: {}", widgetId, profileId);
        boolean deleted = widgetService.deleteWidget(profileId, widgetId);
        
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Global exception handler for ProfileNotFoundException.
     * Returns 404 with meaningful error message.
     */
    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProfileNotFound(ProfileNotFoundException ex) {
        logger.error("Profile not found: {}", ex.getMessage());
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Profile Not Found");
        error.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
