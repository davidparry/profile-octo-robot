package com.bug.robot.profile.dto;

import java.time.LocalDateTime;

public class WidgetResponseDTO {
    
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String configuration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public WidgetResponseDTO() {
    }
    
    public WidgetResponseDTO(Long id, Long userId, String name, String description, 
                            String configuration, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.configuration = configuration;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
