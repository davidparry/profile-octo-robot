package com.bug.robot.widget.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public class WidgetRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @Pattern(regexp = "^(STANDARD|PREMIUM|CUSTOM)$", 
             message = "Type must be STANDARD, PREMIUM, or CUSTOM")
    private String type;

    @Pattern(regexp = "^(ACTIVE|INACTIVE|DEPRECATED)$",
             message = "Status must be ACTIVE, INACTIVE, or DEPRECATED")
    private String status;

    @DecimalMin(value = "0.0", inclusive = false, 
                message = "Price must be greater than 0")
    private BigDecimal price;

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

    @Override
    public String toString() {
        return "WidgetRequestDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", price=" + price +
                '}';
    }
}
