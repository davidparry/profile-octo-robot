package com.bug.robot.profile.service;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.domain.Widget;
import com.bug.robot.profile.dto.WidgetRequestDTO;
import com.bug.robot.profile.dto.WidgetResponseDTO;
import com.bug.robot.profile.exception.ProfileNotFoundException;
import com.bug.robot.profile.repository.ProfileRepository;
import com.bug.robot.profile.repository.WidgetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    
    public WidgetResponseDTO createWidget(Long profileId, WidgetRequestDTO request) {
        logger.info("Creating widget for profile ID: {}", profileId);
        
        // Validate that profile exists
        Profile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new ProfileNotFoundException(profileId));
        
        // Create widget entity
        Widget widget = new Widget();
        widget.setProfile(profile);
        widget.setName(request.getName());
        widget.setDescription(request.getDescription());
        widget.setConfiguration(request.getConfiguration());
        
        // Save widget
        Widget savedWidget = widgetRepository.save(widget);
        logger.info("Widget created successfully with ID: {}", savedWidget.getId());
        
        // Convert to DTO and return
        return convertToDTO(savedWidget);
    }
    
    @Transactional(readOnly = true)
    public List<WidgetResponseDTO> getWidgetsByProfile(Long profileId) {
        logger.info("Fetching widgets for profile ID: {}", profileId);
        
        // Validate that profile exists
        if (!profileRepository.existsById(profileId)) {
            throw new ProfileNotFoundException(profileId);
        }
        
        List<Widget> widgets = widgetRepository.findByProfileId(profileId);
        return widgets.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public WidgetResponseDTO getWidget(Long widgetId, Long profileId) {
        logger.info("Fetching widget ID: {} for profile ID: {}", widgetId, profileId);
        
        Widget widget = widgetRepository.findByIdAndProfileId(widgetId, profileId)
            .orElseThrow(() -> new ProfileNotFoundException("Widget not found with id: " + widgetId + " for profile: " + profileId));
        
        return convertToDTO(widget);
    }
    
    private WidgetResponseDTO convertToDTO(Widget widget) {
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
