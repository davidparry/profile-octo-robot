package com.bug.robot.widget.service;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.repository.ProfileRepository;
import com.bug.robot.widget.domain.Widget;
import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;
import com.bug.robot.widget.exception.ProfileNotFoundException;
import com.bug.robot.widget.exception.WidgetValidationException;
import com.bug.robot.widget.repository.WidgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for Widget operations.
 * Follows SOLID principles with single responsibility for widget business logic.
 * Uses constructor-based dependency injection as per best practices.
 */
@Service
@Transactional
public class WidgetService {

    private final WidgetRepository widgetRepository;
    private final ProfileRepository profileRepository;

    /**
     * Constructor-based dependency injection (recommended best practice).
     */
    @Autowired
    public WidgetService(WidgetRepository widgetRepository, ProfileRepository profileRepository) {
        this.widgetRepository = widgetRepository;
        this.profileRepository = profileRepository;
    }

    /**
     * Create a new widget for a user profile.
     * 
     * @param profileId the profile ID
     * @param requestDTO the widget request data
     * @return the created widget response
     * @throws ProfileNotFoundException if profile doesn't exist
     * @throws WidgetValidationException if widget validation fails
     */
    public WidgetResponseDTO createWidget(Long profileId, WidgetRequestDTO requestDTO) {
        // Validate profile exists (Dependency Inversion Principle - depend on abstraction)
        Profile profile = validateProfileExists(profileId);

        // Check for duplicate widget name (business rule)
        if (widgetRepository.existsByNameAndProfileId(requestDTO.getName(), profileId)) {
            throw new WidgetValidationException(
                "Widget with name '" + requestDTO.getName() + "' already exists for this profile"
            );
        }

        // Map DTO to entity
        Widget widget = mapToEntity(requestDTO, profile);

        // Save widget
        Widget savedWidget = widgetRepository.save(widget);

        // Map entity to response DTO
        return mapToResponseDTO(savedWidget);
    }

    /**
     * Validate that a profile exists.
     * 
     * @param profileId the profile ID
     * @return the profile if found
     * @throws ProfileNotFoundException if not found
     */
    private Profile validateProfileExists(Long profileId) {
        return profileRepository.findById(profileId)
            .orElseThrow(() -> new ProfileNotFoundException(profileId));
    }

    /**
     * Map WidgetRequestDTO to Widget entity.
     * Manual mapping following existing codebase pattern (no MapStruct/ModelMapper).
     * 
     * @param dto the request DTO
     * @param profile the associated profile
     * @return the widget entity
     */
    private Widget mapToEntity(WidgetRequestDTO dto, Profile profile) {
        Widget widget = new Widget();
        widget.setName(dto.getName());
        widget.setDescription(dto.getDescription());
        widget.setConfiguration(dto.getConfiguration());
        widget.setProfile(profile);
        return widget;
    }

    /**
     * Map Widget entity to WidgetResponseDTO.
     * Manual mapping following existing codebase pattern.
     * 
     * @param widget the widget entity
     * @return the response DTO
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
