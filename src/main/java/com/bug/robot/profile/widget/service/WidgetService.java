package com.bug.robot.profile.widget.service;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.repository.ProfileRepository;
import com.bug.robot.profile.widget.domain.Widget;
import com.bug.robot.profile.widget.dto.WidgetRequestDTO;
import com.bug.robot.profile.widget.dto.WidgetResponseDTO;
import com.bug.robot.profile.widget.exception.ProfileNotFoundException;
import com.bug.robot.profile.widget.repository.WidgetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for Widget business logic.
 * Implements SRP by focusing only on widget-related operations.
 * Uses constructor injection following DIP.
 */
@Service
@Transactional
public class WidgetService {

    private static final Logger logger = LoggerFactory.getLogger(WidgetService.class);

    private final WidgetRepository widgetRepository;
    private final ProfileRepository profileRepository;

    public WidgetService(WidgetRepository widgetRepository, ProfileRepository profileRepository) {
        this.widgetRepository = widgetRepository;
        this.profileRepository = profileRepository;
    }

    /**
     * Create a new widget for a profile.
     * Validates profile existence before creating widget.
     */
    public WidgetResponseDTO createWidget(Long profileId, WidgetRequestDTO request) {
        logger.info("Creating widget for profile ID: {}", profileId);

        // Validate profile exists
        Profile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new ProfileNotFoundException(profileId));

        // Create widget entity
        Widget widget = new Widget();
        widget.setName(request.getName());
        widget.setDescription(request.getDescription());
        widget.setConfiguration(request.getConfiguration());
        widget.setProfile(profile);

        // Save and return
        Widget savedWidget = widgetRepository.save(widget);
        logger.info("Widget created successfully with ID: {}", savedWidget.getId());

        return WidgetResponseDTO.from(savedWidget);
    }

    /**
     * Get all widgets for a specific profile.
     */
    @Transactional(readOnly = true)
    public List<WidgetResponseDTO> getWidgetsByProfile(Long profileId) {
        logger.debug("Fetching widgets for profile ID: {}", profileId);

        // Validate profile exists
        if (!profileRepository.existsById(profileId)) {
            throw new ProfileNotFoundException(profileId);
        }

        return widgetRepository.findByProfileId(profileId).stream()
            .map(WidgetResponseDTO::from)
            .collect(Collectors.toList());
    }

    /**
     * Get a specific widget by ID and profile ID.
     * Ensures widget ownership validation.
     */
    @Transactional(readOnly = true)
    public Optional<WidgetResponseDTO> getWidget(Long profileId, Long widgetId) {
        logger.debug("Fetching widget ID: {} for profile ID: {}", widgetId, profileId);

        return widgetRepository.findByIdAndProfileId(widgetId, profileId)
            .map(WidgetResponseDTO::from);
    }

    /**
     * Update an existing widget.
     * Validates widget ownership before updating.
     */
    public Optional<WidgetResponseDTO> updateWidget(Long profileId, Long widgetId, WidgetRequestDTO request) {
        logger.info("Updating widget ID: {} for profile ID: {}", widgetId, profileId);

        Optional<Widget> widgetOpt = widgetRepository.findByIdAndProfileId(widgetId, profileId);

        if (widgetOpt.isEmpty()) {
            logger.warn("Widget not found or access denied - Widget ID: {}, Profile ID: {}", widgetId, profileId);
            return Optional.empty();
        }

        Widget widget = widgetOpt.get();
        widget.setName(request.getName());
        widget.setDescription(request.getDescription());
        widget.setConfiguration(request.getConfiguration());

        Widget updatedWidget = widgetRepository.save(widget);
        logger.info("Widget updated successfully - ID: {}", updatedWidget.getId());

        return Optional.of(WidgetResponseDTO.from(updatedWidget));
    }

    /**
     * Delete a widget.
     * Validates widget ownership before deletion.
     */
    public boolean deleteWidget(Long profileId, Long widgetId) {
        logger.info("Deleting widget ID: {} for profile ID: {}", widgetId, profileId);

        Optional<Widget> widgetOpt = widgetRepository.findByIdAndProfileId(widgetId, profileId);

        if (widgetOpt.isEmpty()) {
            logger.warn("Widget not found or access denied - Widget ID: {}, Profile ID: {}", widgetId, profileId);
            return false;
        }

        widgetRepository.delete(widgetOpt.get());
        logger.info("Widget deleted successfully - ID: {}", widgetId);

        return true;
    }
}
