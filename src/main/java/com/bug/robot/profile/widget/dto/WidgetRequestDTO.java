package com.bug.robot.profile.widget.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * Data Transfer Object for Widget creation and update requests.
 * Includes Jakarta Bean Validation annotations for input validation.
 */
public class WidgetRequestDTO {

    @NotBlank(message = "Widget name is required")
    @Size(min = 1, max = 100, message = "Widget name must be between 1 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Map<String, Object> configuration;

    // Constructors
    public WidgetRequestDTO() {
    }

    public WidgetRequestDTO(String name, String description, Map<String, Object> configuration) {
        this.name = name;
        this.description = description;
        this.configuration = configuration;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
    }
}
