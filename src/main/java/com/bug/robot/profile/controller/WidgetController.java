package com.bug.robot.profile.controller;

import com.bug.robot.profile.dto.WidgetRequestDTO;
import com.bug.robot.profile.dto.WidgetResponseDTO;
import com.bug.robot.profile.service.WidgetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WidgetController {
    
    private static final Logger logger = LoggerFactory.getLogger(WidgetController.class);
    
    private final WidgetService widgetService;
    
    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }
    
    @PostMapping("/users/{userId}/widgets")
    public ResponseEntity<WidgetResponseDTO> createWidget(
            @PathVariable Long userId,
            @Valid @RequestBody WidgetRequestDTO request) {
        logger.info("Received request to create widget for user {}: {}", userId, request.getName());
        WidgetResponseDTO response = widgetService.createWidget(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/users/{userId}/widgets")
    public ResponseEntity<List<WidgetResponseDTO>> getWidgets(@PathVariable Long userId) {
        logger.info("Received request to get widgets for user {}", userId);
        List<WidgetResponseDTO> widgets = widgetService.getWidgetsByProfile(userId);
        return ResponseEntity.ok(widgets);
    }
    
    @GetMapping("/users/{userId}/widgets/{widgetId}")
    public ResponseEntity<WidgetResponseDTO> getWidget(
            @PathVariable Long userId,
            @PathVariable Long widgetId) {
        logger.info("Received request to get widget {} for user {}", widgetId, userId);
        WidgetResponseDTO widget = widgetService.getWidget(widgetId, userId);
        return ResponseEntity.ok(widget);
    }
}
