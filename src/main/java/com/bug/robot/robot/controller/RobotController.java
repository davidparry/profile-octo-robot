package com.bug.robot.robot.controller;

import com.bug.robot.robot.domain.Robot;
import com.bug.robot.robot.dto.RobotRequestDTO;
import com.bug.robot.robot.dto.RobotResponseDTO;
import com.bug.robot.robot.exception.DuplicateSerialNumberException;
import com.bug.robot.robot.service.RobotService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/robots")
public class RobotController {
    
    private static final Logger logger = LoggerFactory.getLogger(RobotController.class);
    
    private final RobotService robotService;
    
    public RobotController(RobotService robotService) {
        this.robotService = robotService;
    }
    
    @GetMapping
    public List<RobotResponseDTO> getAll() {
        return robotService.getAll().stream()
                .map(RobotResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RobotResponseDTO> getById(@PathVariable Long id) {
        return robotService.getById(id)
                .map(robot -> ResponseEntity.ok(new RobotResponseDTO(robot)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<RobotResponseDTO> createRobot(@Valid @RequestBody RobotRequestDTO dto) {
        logger.info("Creating robot with serial number: {}", dto.getSerialNumber());
        try {
            Robot robot = robotService.create(dto);
            RobotResponseDTO response = new RobotResponseDTO(robot);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DuplicateSerialNumberException e) {
            logger.error("Duplicate serial number: {}", dto.getSerialNumber());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid robot data: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RobotResponseDTO> update(@PathVariable Long id, @Valid @RequestBody RobotRequestDTO dto) {
        logger.info("Updating robot with id: {}", id);
        try {
            return robotService.update(id, dto)
                    .map(robot -> ResponseEntity.ok(new RobotResponseDTO(robot)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (DuplicateSerialNumberException e) {
            logger.error("Duplicate serial number: {}", dto.getSerialNumber());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid robot data: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Deleting robot with id: {}", id);
        robotService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        logger.error("Validation errors: {}", errors);
        return ResponseEntity.badRequest().body(errors);
    }
}
