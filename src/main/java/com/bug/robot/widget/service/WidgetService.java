package com.bug.robot.widget.service;

import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;

import java.util.List;

public interface WidgetService {
    
    WidgetResponseDTO createWidget(Long profileId, WidgetRequestDTO request);
    
    List<WidgetResponseDTO> getWidgetsByProfileId(Long profileId);
    
    WidgetResponseDTO getWidgetById(Long profileId, Long widgetId);
}
