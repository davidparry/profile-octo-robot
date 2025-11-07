package com.bug.robot.widget.controller;

import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;
import com.bug.robot.widget.exception.ProfileNotFoundException;
import com.bug.robot.widget.service.WidgetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profiles/{profileId}/widgets")
public class WidgetController {
    
    private static final Logger logger = LoggerFactory.getLogger(WidgetController.class);

    private final WidgetService widgetService;

    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    @PostMapping
    public ResponseEntity<WidgetResponseDTO> createWidget(
            @PathVariable Long profileId,
            @Valid @RequestBody WidgetRequestDTO dto) {
        logger.info("Creating widget for profile {}: {}", profileId, dto);
        WidgetResponseDTO response = widgetService.createWidget(profileId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WidgetResponseDTO>> getWidgetsByProfile(@PathVariable Long profileId) {
        logger.info("Fetching widgets for profile {}", profileId);
        List<WidgetResponseDTO> widgets = widgetService.getWidgetsByProfileId(profileId);
        return ResponseEntity.ok(widgets);
    }

    @GetMapping("/{widgetId}")
    public ResponseEntity<WidgetResponseDTO> getWidget(
            @PathVariable Long profileId,
            @PathVariable Long widgetId) {
        logger.info("Fetching widget {} for profile {}", widgetId, profileId);
        return widgetService.getWidgetByIdAndProfileId(widgetId, profileId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{widgetId}")
    public ResponseEntity<Void> deleteWidget(
            @PathVariable Long profileId,
            @PathVariable Long widgetId) {
        logger.info("Deleting widget {} for profile {}", widgetId, profileId);
        // Verify widget belongs to profile before deleting
        return widgetService.getWidgetByIdAndProfileId(widgetId, profileId)
                .map(widget -> {
                    widgetService.deleteWidget(widgetId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProfileNotFound(ProfileNotFoundException ex) {
        logger.error("Profile not found: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Profile Not Found");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
