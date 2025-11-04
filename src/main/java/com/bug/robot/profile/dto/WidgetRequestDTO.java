package com.bug.robot.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class WidgetRequestDTO {
    
    @NotBlank(message = "Widget name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private String configuration;
    
    // Constructors
    public WidgetRequestDTO() {
    }
    
    public WidgetRequestDTO(String name, String description, String configuration) {
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
    
    public String getConfiguration() {
        return configuration;
    }
    
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }
}
