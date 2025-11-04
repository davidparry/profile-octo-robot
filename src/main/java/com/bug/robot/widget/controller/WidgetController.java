package com.bug.robot.widget.controller;

import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;
import com.bug.robot.widget.service.WidgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/{profileId}/widgets")
@Tag(name = "Widgets", description = "Widget management endpoints")
public class WidgetController {

    private final WidgetService widgetService;

    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    @PostMapping
    @Operation(summary = "Add widget to profile", description = "Creates a new widget and associates it with the specified profile")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Widget created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or widget validation failed"),
        @ApiResponse(responseCode = "404", description = "Profile not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<WidgetResponseDTO> createWidget(
            @PathVariable Long profileId,
            @Valid @RequestBody WidgetRequestDTO request) {
        WidgetResponseDTO response = widgetService.createWidget(profileId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all widgets for a profile", description = "Retrieves all widgets associated with the specified profile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Widgets retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<List<WidgetResponseDTO>> getWidgetsByProfile(@PathVariable Long profileId) {
        List<WidgetResponseDTO> widgets = widgetService.getWidgetsByProfileId(profileId);
        return ResponseEntity.ok(widgets);
    }

    @GetMapping("/{widgetId}")
    @Operation(summary = "Get a specific widget", description = "Retrieves a specific widget by ID for the specified profile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Widget retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Widget not found or does not belong to this profile")
    })
    public ResponseEntity<WidgetResponseDTO> getWidget(
            @PathVariable Long profileId,
            @PathVariable Long widgetId) {
        WidgetResponseDTO widget = widgetService.getWidgetById(profileId, widgetId);
        return ResponseEntity.ok(widget);
    }
}
