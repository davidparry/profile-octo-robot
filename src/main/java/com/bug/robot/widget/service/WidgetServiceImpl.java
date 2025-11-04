package com.bug.robot.widget.service;

import com.bug.robot.common.exception.UserNotFoundException;
import com.bug.robot.common.exception.WidgetValidationException;
import com.bug.robot.user.domain.User;
import com.bug.robot.user.service.UserService;
import com.bug.robot.widget.domain.Widget;
import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;
import com.bug.robot.widget.repository.WidgetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WidgetServiceImpl implements WidgetService {
    
    private static final Logger logger = LoggerFactory.getLogger(WidgetServiceImpl.class);
    
    private final WidgetRepository widgetRepository;
    private final UserService userService;

    public WidgetServiceImpl(WidgetRepository widgetRepository, UserService userService) {
        this.widgetRepository = widgetRepository;
        this.userService = userService;
    }

    @Override
    public WidgetResponseDTO createWidget(Long userId, WidgetRequestDTO request) {
        logger.info("Creating widget for user {}: {}", userId, request);
        
        // Validate user exists
        User user = userService.getUserByIdOrThrow(userId);
        
        // Validate widget data
        validateWidgetRequest(request);
        
        // Create widget entity
        Widget widget = new Widget();
        widget.setUser(user);
        widget.setName(request.getName());
        widget.setDescription(request.getDescription());
        widget.setConfiguration(request.getConfiguration());
        
        // Save widget
        Widget savedWidget = widgetRepository.save(widget);
        
        logger.info("Widget created successfully with id: {}", savedWidget.getId());
        
        return mapToResponseDTO(savedWidget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WidgetResponseDTO> getWidgetsByUserId(Long userId) {
        logger.info("Fetching widgets for user {}", userId);
        
        // Validate user exists
        if (!userService.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        
        List<Widget> widgets = widgetRepository.findByUserId(userId);
        
        return widgets.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WidgetResponseDTO getWidgetById(Long widgetId, Long userId) {
        logger.info("Fetching widget {} for user {}", widgetId, userId);
        
        Widget widget = widgetRepository.findByIdAndUserId(widgetId, userId)
                .orElseThrow(() -> new WidgetValidationException(
                        "Widget not found with id: " + widgetId + " for user: " + userId));
        
        return mapToResponseDTO(widget);
    }

    private void validateWidgetRequest(WidgetRequestDTO request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new WidgetValidationException("Widget name cannot be empty");
        }
        
        if (request.getName().length() > 255) {
            throw new WidgetValidationException("Widget name cannot exceed 255 characters");
        }
    }

    private WidgetResponseDTO mapToResponseDTO(Widget widget) {
        WidgetResponseDTO dto = new WidgetResponseDTO();
        dto.setId(widget.getId());
        dto.setUserId(widget.getUser().getId());
        dto.setName(widget.getName());
        dto.setDescription(widget.getDescription());
        dto.setConfiguration(widget.getConfiguration());
        dto.setCreatedAt(widget.getCreatedAt());
        dto.setUpdatedAt(widget.getUpdatedAt());
        return dto;
    }
}
