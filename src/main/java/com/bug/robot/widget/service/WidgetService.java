package com.bug.robot.widget.service;

import com.bug.robot.widget.domain.Widget;
import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.repository.WidgetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WidgetService {

    private final WidgetRepository repository;

    public WidgetService(WidgetRepository repository) {
        this.repository = repository;
    }

    public List<Widget> getAll() {
        return repository.findAll();
    }

    public Optional<Widget> getById(Long id) {
        return repository.findById(id);
    }

    public Widget create(WidgetRequestDTO dto) {
        // Check for duplicate names
        if (repository.findByName(dto.getName()).isPresent()) {
            throw new IllegalArgumentException(
                String.format("Widget with name '%s' already exists", dto.getName()));
        }

        Widget widget = new Widget();
        widget.setName(dto.getName());
        widget.setDescription(dto.getDescription());
        widget.setType(dto.getType());
        widget.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        widget.setPrice(dto.getPrice());

        return repository.save(widget);
    }

    public Optional<Widget> update(Long id, WidgetRequestDTO dto) {
        return repository.findById(id).map(existing -> {
            // Check if name is being changed to a duplicate
            if (!existing.getName().equals(dto.getName()) && 
                repository.findByName(dto.getName()).isPresent()) {
                throw new IllegalArgumentException(
                    String.format("Widget with name '%s' already exists", dto.getName()));
            }

            existing.setName(dto.getName());
            existing.setDescription(dto.getDescription());
            existing.setType(dto.getType());
            existing.setStatus(dto.getStatus() != null ? dto.getStatus() : existing.getStatus());
            existing.setPrice(dto.getPrice());

            return repository.save(existing);
        });
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Widget> getByType(String type) {
        return repository.findByType(type);
    }

    public List<Widget> getByStatus(String status) {
        return repository.findByStatus(status);
    }
}
