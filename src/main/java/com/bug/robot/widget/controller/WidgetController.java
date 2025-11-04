package com.bug.robot.widget.controller;

import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;
import com.bug.robot.widget.service.WidgetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for widget operations.
 * Follows Single Responsibility Principle - handles only HTTP request/response mapping.
 * Uses Constructor Injection following Dependency Inversion Principle.
 */
@RestController
@RequestMapping("/api")
public class WidgetController {

    private static final Logger logger = LoggerFactory.getLogger(WidgetController.class);

    private final WidgetService widgetService;

    /**
     * Constructor injection for service dependency.
     */
    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    /**
     * Create a new widget for a user profile.
     * POST /api/users/{userId}/widgets
     * 
     * @param userId the user/profile ID
     * @param dto the widget request data
     * @return 201 Created with widget response
     */
    @PostMapping("/users/{userId}/widgets")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<WidgetResponseDTO> createWidget(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody WidgetRequestDTO dto) {
        logger.info("Received request to create widget for user {}: {}", userId, dto.getName());
        WidgetResponseDTO response = widgetService.createWidget(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all widgets for a user profile.
     * GET /api/users/{userId}/widgets
     * 
     * @param userId the user/profile ID
     * @return 200 OK with list of widgets
     */
    @GetMapping("/users/{userId}/widgets")
    public ResponseEntity<List<WidgetResponseDTO>> getWidgets(@PathVariable("userId") Long userId) {
        logger.info("Received request to get widgets for user {}", userId);
        List<WidgetResponseDTO> widgets = widgetService.getWidgetsByProfileId(userId);
        return ResponseEntity.ok(widgets);
    }

    /**
     * Get a specific widget by ID.
     * GET /api/users/{userId}/widgets/{widgetId}
     * 
     * @param userId the user/profile ID
     * @param widgetId the widget ID
     * @return 200 OK with widget response
     */
    @GetMapping("/users/{userId}/widgets/{widgetId}")
    public ResponseEntity<WidgetResponseDTO> getWidget(
            @PathVariable("userId") Long userId,
            @PathVariable("widgetId") Long widgetId) {
        logger.info("Received request to get widget {} for user {}", widgetId, userId);
        WidgetResponseDTO widget = widgetService.getWidget(widgetId, userId);
        return ResponseEntity.ok(widget);
    }
}
