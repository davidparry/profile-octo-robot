package com.bug.robot.widget.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashMap;
import java.util.Map;

public class WidgetRequestDTO {

    @NotBlank(message = "Widget name is required")
    @Size(min = 1, max = 255, message = "Widget name must be between 1 and 255 characters")
    private String name;

    private String description;

    private Map<String, Object> configuration = new HashMap<>();

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

    @Override
    public String toString() {
        return "WidgetRequestDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", configuration=" + configuration +
                '}';
    }
}
