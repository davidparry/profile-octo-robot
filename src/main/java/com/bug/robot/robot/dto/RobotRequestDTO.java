package com.bug.robot.robot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RobotRequestDTO {
    
    @NotBlank(message = "Robot name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;
    
    @NotBlank(message = "Model is required")
    @Size(max = 50, message = "Model must not exceed 50 characters")
    private String model;
    
    @NotBlank(message = "Serial number is required")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Serial number must contain only uppercase letters, numbers, and hyphens")
    @Size(max = 50, message = "Serial number must not exceed 50 characters")
    private String serialNumber;
    
    @NotNull(message = "Status is required")
    private String status;
    
    @Size(max = 100, message = "Manufacturer must not exceed 100 characters")
    private String manufacturer;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Manufacturing date must be in YYYY-MM-DD format")
    private String manufacturingDate;
    
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Last maintenance date must be in YYYY-MM-DD format")
    private String lastMaintenanceDate;
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getManufacturingDate() {
        return manufacturingDate;
    }
    
    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }
    
    public String getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }
    
    public void setLastMaintenanceDate(String lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }
}
