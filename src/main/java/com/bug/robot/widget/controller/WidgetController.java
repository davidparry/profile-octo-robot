package com.bug.robot.widget.controller;

import com.bug.robot.widget.domain.Widget;
import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;
import com.bug.robot.widget.service.WidgetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/widgets")
public class WidgetController {
    private static final Logger logger = LoggerFactory.getLogger(WidgetController.class);

    private final WidgetService service;

    public WidgetController(WidgetService service) {
        this.service = service;
    }

    @GetMapping
    public List<WidgetResponseDTO> getAll() {
        return service.getAll().stream()
                .map(WidgetResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WidgetResponseDTO> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(WidgetResponseDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WidgetResponseDTO create(@Valid @RequestBody WidgetRequestDTO dto) {
        logger.info("Creating widget: {}", dto.getName());
        Widget widget = service.create(dto);
        return WidgetResponseDTO.fromEntity(widget);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WidgetResponseDTO> update(@PathVariable Long id, @Valid @RequestBody WidgetRequestDTO dto) {
        return service.update(id, dto)
                .map(WidgetResponseDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    public List<WidgetResponseDTO> getByType(@PathVariable String type) {
        return service.getByType(type).stream()
                .map(WidgetResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/status/{status}")
    public List<WidgetResponseDTO> getByStatus(@PathVariable String status) {
        return service.getByStatus(status).stream()
                .map(WidgetResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
