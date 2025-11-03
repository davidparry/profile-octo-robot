package com.bug.robot.robot.dto;

import com.bug.robot.robot.domain.Robot;

public class RobotResponseDTO {
    
    private Long id;
    private String name;
    private String model;
    private String serialNumber;
    private String status;
    private String manufacturer;
    private String description;
    private String manufacturingDate;
    private String lastMaintenanceDate;
    private String createdAt;
    private String updatedAt;
    
    public RobotResponseDTO() {
    }
    
    public RobotResponseDTO(Robot robot) {
        this.id = robot.getId();
        this.name = robot.getName();
        this.model = robot.getModel();
        this.serialNumber = robot.getSerialNumber();
        this.status = robot.getStatus() != null ? robot.getStatus().name() : null;
        this.manufacturer = robot.getManufacturer();
        this.description = robot.getDescription();
        this.manufacturingDate = robot.getManufacturingDate() != null ? robot.getManufacturingDate().toString() : null;
        this.lastMaintenanceDate = robot.getLastMaintenanceDate() != null ? robot.getLastMaintenanceDate().toString() : null;
        this.createdAt = robot.getCreatedAt() != null ? robot.getCreatedAt().toString() : null;
        this.updatedAt = robot.getUpdatedAt() != null ? robot.getUpdatedAt().toString() : null;
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
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
