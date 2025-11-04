package com.bug.robot.widget.service;

import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;

import java.util.List;

public interface WidgetService {
    
    WidgetResponseDTO createWidget(Long userId, WidgetRequestDTO request);
    
    List<WidgetResponseDTO> getWidgetsByUserId(Long userId);
    
    WidgetResponseDTO getWidgetById(Long widgetId, Long userId);
}
