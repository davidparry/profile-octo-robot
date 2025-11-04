package com.bug.robot.widget.service;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.repository.ProfileRepository;
import com.bug.robot.widget.domain.Widget;
import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;
import com.bug.robot.widget.exception.ProfileNotFoundException;
import com.bug.robot.widget.exception.WidgetValidationException;
import com.bug.robot.widget.repository.WidgetRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class WidgetServiceImpl implements WidgetService {

    private final WidgetRepository widgetRepository;
    private final ProfileRepository profileRepository;
    private final ObjectMapper objectMapper;

    public WidgetServiceImpl(WidgetRepository widgetRepository, 
                            ProfileRepository profileRepository,
                            ObjectMapper objectMapper) {
        this.widgetRepository = widgetRepository;
        this.profileRepository = profileRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public WidgetResponseDTO createWidget(Long profileId, WidgetRequestDTO request) {
        // Validate profile exists
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(profileId));

        // Check for duplicate widget names
        if (widgetRepository.existsByProfileIdAndName(profileId, request.getName())) {
            throw new WidgetValidationException("Widget with name '" + request.getName() + "' already exists for this profile");
        }

        // Convert configuration map to JSON string
        String configurationJson = null;
        if (request.getConfiguration() != null && !request.getConfiguration().isEmpty()) {
            try {
                configurationJson = objectMapper.writeValueAsString(request.getConfiguration());
            } catch (JsonProcessingException e) {
                throw new WidgetValidationException("Invalid configuration format", e);
            }
        }

        // Create and save widget
        Widget widget = new Widget(profile, request.getName(), request.getDescription(), configurationJson);
        Widget savedWidget = widgetRepository.save(widget);

        return mapToResponseDTO(savedWidget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WidgetResponseDTO> getWidgetsByProfileId(Long profileId) {
        // Validate profile exists
        if (!profileRepository.existsById(profileId)) {
            throw new ProfileNotFoundException(profileId);
        }

        List<Widget> widgets = widgetRepository.findByProfileId(profileId);
        return widgets.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WidgetResponseDTO getWidgetById(Long profileId, Long widgetId) {
        Widget widget = widgetRepository.findByIdAndProfileId(widgetId, profileId)
                .orElseThrow(() -> new WidgetValidationException("Widget not found or does not belong to this profile"));

        return mapToResponseDTO(widget);
    }

    private WidgetResponseDTO mapToResponseDTO(Widget widget) {
        Map<String, Object> configuration = null;
        if (widget.getConfiguration() != null && !widget.getConfiguration().isEmpty()) {
            try {
                configuration = objectMapper.readValue(widget.getConfiguration(), Map.class);
            } catch (JsonProcessingException e) {
                // Log error but don't fail - return null configuration
                configuration = null;
            }
        }

        return new WidgetResponseDTO(
                widget.getId(),
                widget.getProfile().getId(),
                widget.getName(),
                widget.getDescription(),
                configuration,
                widget.getCreatedAt(),
                widget.getUpdatedAt()
        );
    }
}
