package com.bug.robot.widget.dto;

import com.bug.robot.widget.domain.Widget;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WidgetResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String type;
    private String status;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static WidgetResponseDTO fromEntity(Widget widget) {
        WidgetResponseDTO dto = new WidgetResponseDTO();
        dto.setId(widget.getId());
        dto.setName(widget.getName());
        dto.setDescription(widget.getDescription());
        dto.setType(widget.getType());
        dto.setStatus(widget.getStatus());
        dto.setPrice(widget.getPrice());
        dto.setCreatedAt(widget.getCreatedAt());
        dto.setUpdatedAt(widget.getUpdatedAt());
        return dto;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
