package com.bug.robot.widget.service;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.repository.ProfileRepository;
import com.bug.robot.widget.domain.Widget;
import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;
import com.bug.robot.widget.exception.ProfileNotFoundException;
import com.bug.robot.widget.exception.WidgetValidationException;
import com.bug.robot.widget.repository.WidgetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WidgetServiceImplTest {

    @Mock
    private WidgetRepository widgetRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private WidgetServiceImpl widgetService;

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
        testWidget.setConfiguration("{\"key\":\"value\"}");
        testWidget.setCreatedAt(LocalDateTime.now());
        testWidget.setUpdatedAt(LocalDateTime.now());

        Map<String, Object> config = new HashMap<>();
        config.put("key", "value");
        testRequest = new WidgetRequestDTO("Test Widget", "Test Description", config);
    }

    @Test
    void createWidget_Success() throws Exception {
        // Arrange
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(widgetRepository.existsByProfileIdAndName(1L, "Test Widget")).thenReturn(false);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"key\":\"value\"}");
        when(widgetRepository.save(any(Widget.class))).thenReturn(testWidget);
        when(objectMapper.readValue(anyString(), eq(Map.class))).thenReturn(testRequest.getConfiguration());

        // Act
        WidgetResponseDTO result = widgetService.createWidget(1L, testRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getProfileId());
        assertEquals("Test Widget", result.getName());
        assertEquals("Test Description", result.getDescription());
        verify(widgetRepository, times(1)).save(any(Widget.class));
    }

    @Test
    void createWidget_ProfileNotFound() {
        // Arrange
        when(profileRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProfileNotFoundException.class, () -> {
            widgetService.createWidget(999L, testRequest);
        });
        verify(widgetRepository, never()).save(any(Widget.class));
    }

    @Test
    void createWidget_DuplicateName() {
        // Arrange
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(widgetRepository.existsByProfileIdAndName(1L, "Test Widget")).thenReturn(true);

        // Act & Assert
        assertThrows(WidgetValidationException.class, () -> {
            widgetService.createWidget(1L, testRequest);
        });
        verify(widgetRepository, never()).save(any(Widget.class));
    }

    @Test
    void getWidgetsByProfileId_Success() throws Exception {
        // Arrange
        List<Widget> widgets = Arrays.asList(testWidget);
        when(profileRepository.existsById(1L)).thenReturn(true);
        when(widgetRepository.findByProfileId(1L)).thenReturn(widgets);
        when(objectMapper.readValue(anyString(), eq(Map.class))).thenReturn(testRequest.getConfiguration());

        // Act
        List<WidgetResponseDTO> result = widgetService.getWidgetsByProfileId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Widget", result.get(0).getName());
    }

    @Test
    void getWidgetsByProfileId_ProfileNotFound() {
        // Arrange
        when(profileRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ProfileNotFoundException.class, () -> {
            widgetService.getWidgetsByProfileId(999L);
        });
    }

    @Test
    void getWidgetById_Success() throws Exception {
        // Arrange
        when(widgetRepository.findByIdAndProfileId(1L, 1L)).thenReturn(Optional.of(testWidget));
        when(objectMapper.readValue(anyString(), eq(Map.class))).thenReturn(testRequest.getConfiguration());

        // Act
        WidgetResponseDTO result = widgetService.getWidgetById(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Widget", result.getName());
    }

    @Test
    void getWidgetById_NotFound() {
        // Arrange
        when(widgetRepository.findByIdAndProfileId(999L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(WidgetValidationException.class, () -> {
            widgetService.getWidgetById(1L, 999L);
        });
    }
}
