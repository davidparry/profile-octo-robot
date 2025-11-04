package com.bug.robot.widget.controller;

import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;
import com.bug.robot.widget.service.WidgetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class WidgetController {
    
    private static final Logger logger = LoggerFactory.getLogger(WidgetController.class);
    
    private final WidgetService widgetService;

    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    @PostMapping("/{userId}/widgets")
    public ResponseEntity<WidgetResponseDTO> createWidget(
            @PathVariable Long userId,
            @Valid @RequestBody WidgetRequestDTO request) {
        
        logger.info("POST /api/users/{}/widgets - Creating widget: {}", userId, request);
        
        WidgetResponseDTO response = widgetService.createWidget(userId, request);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        
        logger.info("Widget created successfully with id: {}", response.getId());
        
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{userId}/widgets")
    public ResponseEntity<List<WidgetResponseDTO>> getWidgetsByUserId(@PathVariable Long userId) {
        logger.info("GET /api/users/{}/widgets - Fetching widgets", userId);
        
        List<WidgetResponseDTO> widgets = widgetService.getWidgetsByUserId(userId);
        
        return ResponseEntity.ok(widgets);
    }

    @GetMapping("/{userId}/widgets/{widgetId}")
    public ResponseEntity<WidgetResponseDTO> getWidgetById(
            @PathVariable Long userId,
            @PathVariable Long widgetId) {
        
        logger.info("GET /api/users/{}/widgets/{} - Fetching widget", userId, widgetId);
        
        WidgetResponseDTO widget = widgetService.getWidgetById(widgetId, userId);
        
        return ResponseEntity.ok(widget);
    }
}
