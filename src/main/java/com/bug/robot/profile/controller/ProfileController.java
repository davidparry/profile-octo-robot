package com.bug.robot.profile.controller;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.dto.ProfileRequestDTO;
import com.bug.robot.profile.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/profiles")
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @GetMapping
    public List<Profile> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profile> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Profile create(@RequestBody ProfileRequestDTO dto) {
        logger.info("creating {}", dto);
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Profile> update(@PathVariable Long id, @RequestBody ProfileRequestDTO dto) {
        return service.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
