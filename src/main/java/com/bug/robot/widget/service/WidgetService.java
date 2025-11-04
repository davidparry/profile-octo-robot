package com.bug.robot.widget.service;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.repository.ProfileRepository;
import com.bug.robot.widget.domain.Widget;
import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;
import com.bug.robot.widget.exception.ProfileNotFoundException;
import com.bug.robot.widget.exception.WidgetValidationException;
import com.bug.robot.widget.repository.WidgetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for widget business logic.
 * Follows Single Responsibility Principle - handles only widget-related business operations.
 * Uses Constructor Injection following Dependency Inversion Principle.
 */
@Service
@Transactional
public class WidgetService {

    private static final Logger logger = LoggerFactory.getLogger(WidgetService.class);

    private final WidgetRepository widgetRepository;
    private final ProfileRepository profileRepository;

    /**
     * Constructor injection for dependencies.
     * Follows Dependency Inversion Principle - depends on abstractions (repositories).
     */
    public WidgetService(WidgetRepository widgetRepository, ProfileRepository profileRepository) {
        this.widgetRepository = widgetRepository;
        this.profileRepository = profileRepository;
    }

    /**
     * Create a new widget for a profile.
     * @param profileId the profile ID
     * @param dto the widget request data
     * @return the created widget response
     * @throws ProfileNotFoundException if profile not found
     * @throws WidgetValidationException if widget validation fails
     */
    public WidgetResponseDTO createWidget(Long profileId, WidgetRequestDTO dto) {
        logger.info("Creating widget '{}' for profile ID: {}", dto.getName(), profileId);

        // Validate profile exists
        Profile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new ProfileNotFoundException("Profile not found with id: " + profileId));

        // Check for duplicate widget name for this profile
        if (widgetRepository.existsByNameAndProfileId(dto.getName(), profileId)) {
            throw new WidgetValidationException("Widget with name '" + dto.getName() + "' already exists for this profile");
        }

        // Create and save widget
        Widget widget = new Widget();
        widget.setName(dto.getName());
        widget.setDescription(dto.getDescription());
        widget.setConfiguration(dto.getConfiguration());
        widget.setProfile(profile);

        Widget savedWidget = widgetRepository.save(widget);
        logger.info("Widget created successfully with ID: {}", savedWidget.getId());

        return mapToResponseDTO(savedWidget);
    }

    /**
     * Get all widgets for a profile.
     * @param profileId the profile ID
     * @return list of widget responses
     */
    @Transactional(readOnly = true)
    public List<WidgetResponseDTO> getWidgetsByProfileId(Long profileId) {
        logger.info("Fetching widgets for profile ID: {}", profileId);
        List<Widget> widgets = widgetRepository.findByProfileId(profileId);
        return widgets.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get a specific widget by ID and profile ID.
     * @param widgetId the widget ID
     * @param profileId the profile ID
     * @return the widget response
     * @throws WidgetValidationException if widget not found or doesn't belong to profile
     */
    @Transactional(readOnly = true)
    public WidgetResponseDTO getWidget(Long widgetId, Long profileId) {
        logger.info("Fetching widget ID: {} for profile ID: {}", widgetId, profileId);
        Widget widget = widgetRepository.findByIdAndProfileId(widgetId, profileId)
            .orElseThrow(() -> new WidgetValidationException("Widget not found or does not belong to this profile"));
        return mapToResponseDTO(widget);
    }

    /**
     * Map Widget entity to WidgetResponseDTO.
     * @param widget the widget entity
     * @return the widget response DTO
     */
    private WidgetResponseDTO mapToResponseDTO(Widget widget) {
        WidgetResponseDTO dto = new WidgetResponseDTO();
        dto.setId(widget.getId());
        dto.setUserId(widget.getProfile().getId());
        dto.setName(widget.getName());
        dto.setDescription(widget.getDescription());
        dto.setConfiguration(widget.getConfiguration());
        dto.setCreatedAt(widget.getCreatedAt());
        dto.setUpdatedAt(widget.getUpdatedAt());
        return dto;
    }
}
