package com.bug.robot.widget.service;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.repository.ProfileRepository;
import com.bug.robot.widget.domain.Widget;
import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;
import com.bug.robot.widget.exception.ProfileNotFoundException;
import com.bug.robot.widget.repository.WidgetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WidgetService {

    private final WidgetRepository widgetRepository;
    private final ProfileRepository profileRepository;

    public WidgetService(WidgetRepository widgetRepository, ProfileRepository profileRepository) {
        this.widgetRepository = widgetRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public WidgetResponseDTO createWidget(Long profileId, WidgetRequestDTO dto) {
        // Validate profile exists (following DIP - depend on abstraction)
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found with id: " + profileId));

        // Create widget entity
        Widget widget = new Widget();
        widget.setProfile(profile);
        widget.setName(dto.getName());
        widget.setDescription(dto.getDescription());
        widget.setConfiguration(dto.getConfiguration());

        // Save and return response
        Widget saved = widgetRepository.save(widget);
        return mapToResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<WidgetResponseDTO> getWidgetsByProfileId(Long profileId) {
        return widgetRepository.findByProfileId(profileId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<WidgetResponseDTO> getWidgetById(Long id) {
        return widgetRepository.findById(id)
                .map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public Optional<WidgetResponseDTO> getWidgetByIdAndProfileId(Long id, Long profileId) {
        return widgetRepository.findByIdAndProfileId(id, profileId)
                .map(this::mapToResponseDTO);
    }

    @Transactional
    public void deleteWidget(Long id) {
        widgetRepository.deleteById(id);
    }

    // Manual DTO mapping (following existing pattern - no MapStruct/ModelMapper)
    private WidgetResponseDTO mapToResponseDTO(Widget widget) {
        WidgetResponseDTO dto = new WidgetResponseDTO();
        dto.setId(widget.getId());
        dto.setProfileId(widget.getProfile().getId());
        dto.setName(widget.getName());
        dto.setDescription(widget.getDescription());
        dto.setConfiguration(widget.getConfiguration());
        dto.setCreatedAt(widget.getCreatedAt());
        dto.setUpdatedAt(widget.getUpdatedAt());
        return dto;
    }
}
