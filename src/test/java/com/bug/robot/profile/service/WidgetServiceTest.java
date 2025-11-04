package com.bug.robot.profile.service;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.domain.Widget;
import com.bug.robot.profile.dto.WidgetRequestDTO;
import com.bug.robot.profile.dto.WidgetResponseDTO;
import com.bug.robot.profile.exception.ProfileNotFoundException;
import com.bug.robot.profile.repository.ProfileRepository;
import com.bug.robot.profile.repository.WidgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WidgetServiceTest {
    
    @Mock
    private WidgetRepository widgetRepository;
    
    @Mock
    private ProfileRepository profileRepository;
    
    @InjectMocks
    private WidgetService widgetService;
    
    private Profile testProfile;
    private Widget testWidget;
    private WidgetRequestDTO testRequest;
    
    @BeforeEach
    void setUp() {
        testProfile = new Profile();
        testProfile.setId(1L);
        testProfile.setFirstName("John");
        testProfile.setLastName("Doe");
        
        testWidget = new Widget();
        testWidget.setId(1L);
        testWidget.setProfile(testProfile);
        testWidget.setName("Test Widget");
        testWidget.setDescription("Test Description");
        testWidget.setConfiguration("{\"color\":\"blue\"}");
        testWidget.setCreatedAt(LocalDateTime.now());
        testWidget.setUpdatedAt(LocalDateTime.now());
        
        testRequest = new WidgetRequestDTO();
        testRequest.setName("Test Widget");
        testRequest.setDescription("Test Description");
        testRequest.setConfiguration("{\"color\":\"blue\"}");
    }
    
    @Test
    void createWidget_WithValidProfile_ShouldReturnWidgetResponseDTO() {
        // Arrange
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(widgetRepository.save(any(Widget.class))).thenReturn(testWidget);
        
        // Act
        WidgetResponseDTO result = widgetService.createWidget(1L, testRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals("Test Widget", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals("{\"color\":\"blue\"}", result.getConfiguration());
        
        verify(profileRepository, times(1)).findById(1L);
        verify(widgetRepository, times(1)).save(any(Widget.class));
    }
    
    @Test
    void createWidget_WithNonExistentProfile_ShouldThrowProfileNotFoundException() {
        // Arrange
        when(profileRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ProfileNotFoundException.class, () -> {
            widgetService.createWidget(999L, testRequest);
        });
        
        verify(profileRepository, times(1)).findById(999L);
        verify(widgetRepository, never()).save(any(Widget.class));
    }
    
    @Test
    void getWidgetsByProfile_WithValidProfile_ShouldReturnListOfWidgets() {
        // Arrange
        Widget widget2 = new Widget();
        widget2.setId(2L);
        widget2.setProfile(testProfile);
        widget2.setName("Widget 2");
        widget2.setCreatedAt(LocalDateTime.now());
        widget2.setUpdatedAt(LocalDateTime.now());
        
        when(profileRepository.existsById(1L)).thenReturn(true);
        when(widgetRepository.findByProfileId(1L)).thenReturn(Arrays.asList(testWidget, widget2));
        
        // Act
        List<WidgetResponseDTO> results = widgetService.getWidgetsByProfile(1L);
        
        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("Test Widget", results.get(0).getName());
        assertEquals("Widget 2", results.get(1).getName());
        
        verify(profileRepository, times(1)).existsById(1L);
        verify(widgetRepository, times(1)).findByProfileId(1L);
    }
    
    @Test
    void getWidgetsByProfile_WithNonExistentProfile_ShouldThrowProfileNotFoundException() {
        // Arrange
        when(profileRepository.existsById(999L)).thenReturn(false);
        
        // Act & Assert
        assertThrows(ProfileNotFoundException.class, () -> {
            widgetService.getWidgetsByProfile(999L);
        });
        
        verify(profileRepository, times(1)).existsById(999L);
        verify(widgetRepository, never()).findByProfileId(anyLong());
    }
    
    @Test
    void getWidget_WithValidWidgetAndProfile_ShouldReturnWidget() {
        // Arrange
        when(widgetRepository.findByIdAndProfileId(1L, 1L)).thenReturn(Optional.of(testWidget));
        
        // Act
        WidgetResponseDTO result = widgetService.getWidget(1L, 1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Widget", result.getName());
        
        verify(widgetRepository, times(1)).findByIdAndProfileId(1L, 1L);
    }
    
    @Test
    void getWidget_WithNonExistentWidget_ShouldThrowProfileNotFoundException() {
        // Arrange
        when(widgetRepository.findByIdAndProfileId(999L, 1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ProfileNotFoundException.class, () -> {
            widgetService.getWidget(999L, 1L);
        });
        
        verify(widgetRepository, times(1)).findByIdAndProfileId(999L, 1L);
    }
}
