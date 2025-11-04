package com.bug.robot.robot.service;

import com.bug.robot.robot.domain.Robot;
import com.bug.robot.robot.domain.RobotStatus;
import com.bug.robot.robot.dto.RobotRequestDTO;
import com.bug.robot.robot.exception.DuplicateSerialNumberException;
import com.bug.robot.robot.repository.RobotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RobotService {
    
    private final RobotRepository repository;
    
    public RobotService(RobotRepository repository) {
        this.repository = repository;
    }
    
    public List<Robot> getAll() {
        return repository.findAll();
    }
    
    public Optional<Robot> getById(Long id) {
        return repository.findById(id);
    }
    
    public Robot create(RobotRequestDTO dto) {
        // Check for duplicate serial number
        if (repository.existsBySerialNumber(dto.getSerialNumber())) {
            throw new DuplicateSerialNumberException(
                String.format("Robot with serial number '%s' already exists", dto.getSerialNumber())
            );
        }
        
        Robot robot = new Robot();
        robot.setName(dto.getName());
        robot.setModel(dto.getModel());
        robot.setSerialNumber(dto.getSerialNumber());
        
        // Parse and validate status
        try {
            RobotStatus status = RobotStatus.valueOf(dto.getStatus().toUpperCase());
            robot.setStatus(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                String.format("Invalid status: '%s'. Valid values are: ACTIVE, INACTIVE, MAINTENANCE, RETIRED", 
                dto.getStatus())
            );
        }
        
        robot.setManufacturer(dto.getManufacturer());
        robot.setDescription(dto.getDescription());
        
        // Parse manufacturing date if provided
        if (dto.getManufacturingDate() != null && !dto.getManufacturingDate().isEmpty()) {
            try {
                LocalDate manufacturingDate = LocalDate.parse(dto.getManufacturingDate(), DateTimeFormatter.ISO_LOCAL_DATE);
                robot.setManufacturingDate(manufacturingDate);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(
                    String.format("Invalid manufacturing date format: '%s'. Expected format: YYYY-MM-DD (e.g., 2024-01-15)", 
                    dto.getManufacturingDate()), e
                );
            }
        }
        
        // Parse last maintenance date if provided
        if (dto.getLastMaintenanceDate() != null && !dto.getLastMaintenanceDate().isEmpty()) {
            try {
                LocalDate lastMaintenanceDate = LocalDate.parse(dto.getLastMaintenanceDate(), DateTimeFormatter.ISO_LOCAL_DATE);
                robot.setLastMaintenanceDate(lastMaintenanceDate);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(
                    String.format("Invalid last maintenance date format: '%s'. Expected format: YYYY-MM-DD (e.g., 2024-01-15)", 
                    dto.getLastMaintenanceDate()), e
                );
            }
        }
        
        return repository.save(robot);
    }
    
    public Optional<Robot> update(Long id, RobotRequestDTO dto) {
        return repository.findById(id).map(existing -> {
            // Check for duplicate serial number (excluding current robot)
            if (!existing.getSerialNumber().equals(dto.getSerialNumber()) && 
                repository.existsBySerialNumber(dto.getSerialNumber())) {
                throw new DuplicateSerialNumberException(
                    String.format("Robot with serial number '%s' already exists", dto.getSerialNumber())
                );
            }
            
            existing.setName(dto.getName());
            existing.setModel(dto.getModel());
            existing.setSerialNumber(dto.getSerialNumber());
            
            // Parse and validate status
            try {
                RobotStatus status = RobotStatus.valueOf(dto.getStatus().toUpperCase());
                existing.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                    String.format("Invalid status: '%s'. Valid values are: ACTIVE, INACTIVE, MAINTENANCE, RETIRED", 
                    dto.getStatus())
                );
            }
            
            existing.setManufacturer(dto.getManufacturer());
            existing.setDescription(dto.getDescription());
            
            // Parse manufacturing date if provided
            if (dto.getManufacturingDate() != null && !dto.getManufacturingDate().isEmpty()) {
                try {
                    LocalDate manufacturingDate = LocalDate.parse(dto.getManufacturingDate(), DateTimeFormatter.ISO_LOCAL_DATE);
                    existing.setManufacturingDate(manufacturingDate);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException(
                        String.format("Invalid manufacturing date format: '%s'. Expected format: YYYY-MM-DD (e.g., 2024-01-15)", 
                        dto.getManufacturingDate()), e
                    );
                }
            }
            
            // Parse last maintenance date if provided
            if (dto.getLastMaintenanceDate() != null && !dto.getLastMaintenanceDate().isEmpty()) {
                try {
                    LocalDate lastMaintenanceDate = LocalDate.parse(dto.getLastMaintenanceDate(), DateTimeFormatter.ISO_LOCAL_DATE);
                    existing.setLastMaintenanceDate(lastMaintenanceDate);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException(
                        String.format("Invalid last maintenance date format: '%s'. Expected format: YYYY-MM-DD (e.g., 2024-01-15)", 
                        dto.getLastMaintenanceDate()), e
                    );
                }
            }
            
            return repository.save(existing);
        });
    }
    
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
