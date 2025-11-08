package com.bug.robot.widget.service;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.repository.ProfileRepository;
import com.bug.robot.widget.domain.Widget;
import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;
import com.bug.robot.widget.exception.ProfileNotFoundException;
import com.bug.robot.widget.exception.WidgetValidationException;
import com.bug.robot.widget.repository.WidgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WidgetService.
 * Follows TDD best practices with comprehensive test coverage.
 * Uses Mockito for mocking dependencies.
 */
@ExtendWith(MockitoExtension.class)
class WidgetServiceTest {

    @Mock
    private WidgetRepository widgetRepository;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private WidgetService widgetService;

    private Profile testProfile;
    private WidgetRequestDTO testRequest;
    private Widget testWidget;

    @BeforeEach
    void setUp() {
        // Setup test profile
        testProfile = new Profile();
        testProfile.setId(1L);
        testProfile.setFirstName("John");
        testProfile.setLastName("Doe");
        testProfile.setEmail("john@example.com");

        // Setup test request
        testRequest = new WidgetRequestDTO();
        testRequest.setName("Test Widget");
        testRequest.setDescription("Test Description");
        Map<String, Object> config = new HashMap<>();
        config.put("color", "blue");
        config.put("size", "large");
        testRequest.setConfiguration(config);

        // Setup test widget
        testWidget = new Widget();
        testWidget.setId(1L);
        testWidget.setName("Test Widget");
        testWidget.setDescription("Test Description");
        testWidget.setConfiguration(config);
        testWidget.setProfile(testProfile);
        testWidget.setCreatedAt(LocalDateTime.now());
        testWidget.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void shouldCreateWidgetSuccessfully() {
        // Given
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(widgetRepository.existsByNameAndProfileId("Test Widget", 1L)).thenReturn(false);
        when(widgetRepository.save(any(Widget.class))).thenReturn(testWidget);

        // When
        WidgetResponseDTO response = widgetService.createWidget(1L, testRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals("Test Widget", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertNotNull(response.getConfiguration());
        assertEquals("blue", response.getConfiguration().get("color"));
        
        verify(profileRepository, times(1)).findById(1L);
        verify(widgetRepository, times(1)).existsByNameAndProfileId("Test Widget", 1L);
        verify(widgetRepository, times(1)).save(any(Widget.class));
    }

    @Test
    void shouldThrowProfileNotFoundExceptionWhenProfileDoesNotExist() {
        // Given
        when(profileRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ProfileNotFoundException exception = assertThrows(
            ProfileNotFoundException.class,
            () -> widgetService.createWidget(999L, testRequest)
        );

        assertEquals("Profile not found with id: 999", exception.getMessage());
        verify(profileRepository, times(1)).findById(999L);
        verify(widgetRepository, never()).save(any(Widget.class));
    }

    @Test
    void shouldThrowWidgetValidationExceptionWhenDuplicateWidgetName() {
        // Given
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(widgetRepository.existsByNameAndProfileId("Test Widget", 1L)).thenReturn(true);

        // When & Then
        WidgetValidationException exception = assertThrows(
            WidgetValidationException.class,
            () -> widgetService.createWidget(1L, testRequest)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(profileRepository, times(1)).findById(1L);
        verify(widgetRepository, times(1)).existsByNameAndProfileId("Test Widget", 1L);
        verify(widgetRepository, never()).save(any(Widget.class));
    }

    @Test
    void shouldHandleNullConfiguration() {
        // Given
        testRequest.setConfiguration(null);
        Widget widgetWithNullConfig = new Widget();
        widgetWithNullConfig.setId(1L);
        widgetWithNullConfig.setName("Test Widget");
        widgetWithNullConfig.setDescription("Test Description");
        widgetWithNullConfig.setConfiguration(null);
        widgetWithNullConfig.setProfile(testProfile);
        widgetWithNullConfig.setCreatedAt(LocalDateTime.now());
        widgetWithNullConfig.setUpdatedAt(LocalDateTime.now());

        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(widgetRepository.existsByNameAndProfileId("Test Widget", 1L)).thenReturn(false);
        when(widgetRepository.save(any(Widget.class))).thenReturn(widgetWithNullConfig);

        // When
        WidgetResponseDTO response = widgetService.createWidget(1L, testRequest);

        // Then
        assertNotNull(response);
        assertNull(response.getConfiguration());
        verify(widgetRepository, times(1)).save(any(Widget.class));
    }

    @Test
    void shouldHandleEmptyDescription() {
        // Given
        testRequest.setDescription("");
        testWidget.setDescription("");

        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(widgetRepository.existsByNameAndProfileId("Test Widget", 1L)).thenReturn(false);
        when(widgetRepository.save(any(Widget.class))).thenReturn(testWidget);

        // When
        WidgetResponseDTO response = widgetService.createWidget(1L, testRequest);

        // Then
        assertNotNull(response);
        assertEquals("", response.getDescription());
        verify(widgetRepository, times(1)).save(any(Widget.class));
    }
}
