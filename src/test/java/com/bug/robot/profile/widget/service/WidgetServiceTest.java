package com.bug.robot.profile.widget.service;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.repository.ProfileRepository;
import com.bug.robot.profile.widget.domain.Widget;
import com.bug.robot.profile.widget.dto.WidgetRequestDTO;
import com.bug.robot.profile.widget.dto.WidgetResponseDTO;
import com.bug.robot.profile.widget.exception.ProfileNotFoundException;
import com.bug.robot.profile.widget.repository.WidgetRepository;
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
import static org.mockito.Mockito.*;

/**
 * Unit tests for WidgetService.
 * Uses Mockito for mocking dependencies following TDD principles.
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
    private Widget testWidget;
    private WidgetRequestDTO testRequest;

    @BeforeEach
    void setUp() {
        // Setup test profile
        testProfile = new Profile();
        testProfile.setId(1L);
        testProfile.setFirstName("John");
        testProfile.setLastName("Doe");

        // Setup test widget
        testWidget = new Widget();
        testWidget.setId(1L);
        testWidget.setName("Test Widget");
        testWidget.setDescription("Test Description");
        testWidget.setConfiguration(Map.of("key", "value"));
        testWidget.setProfile(testProfile);
        testWidget.setCreatedAt(LocalDateTime.now());
        testWidget.setUpdatedAt(LocalDateTime.now());

        // Setup test request
        testRequest = new WidgetRequestDTO();
        testRequest.setName("Test Widget");
        testRequest.setDescription("Test Description");
        testRequest.setConfiguration(Map.of("key", "value"));
    }

    @Test
    void shouldCreateWidgetSuccessfully() {
        // Given
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(widgetRepository.save(any(Widget.class))).thenReturn(testWidget);

        // When
        WidgetResponseDTO response = widgetService.createWidget(1L, testRequest);

        // Then
        assertNotNull(response);
        assertEquals("Test Widget", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(1L, response.getProfileId());
        verify(profileRepository, times(1)).findById(1L);
        verify(widgetRepository, times(1)).save(any(Widget.class));
    }

    @Test
    void shouldThrowExceptionWhenProfileNotFoundOnCreate() {
        // Given
        when(profileRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProfileNotFoundException.class, () -> {
            widgetService.createWidget(999L, testRequest);
        });
        verify(widgetRepository, never()).save(any(Widget.class));
    }

    @Test
    void shouldGetWidgetsByProfileSuccessfully() {
        // Given
        when(profileRepository.existsById(1L)).thenReturn(true);
        when(widgetRepository.findByProfileId(1L)).thenReturn(Arrays.asList(testWidget));

        // When
        List<WidgetResponseDTO> widgets = widgetService.getWidgetsByProfile(1L);

        // Then
        assertNotNull(widgets);
        assertEquals(1, widgets.size());
        assertEquals("Test Widget", widgets.get(0).getName());
        verify(widgetRepository, times(1)).findByProfileId(1L);
    }

    @Test
    void shouldThrowExceptionWhenProfileNotFoundOnGetWidgets() {
        // Given
        when(profileRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(ProfileNotFoundException.class, () -> {
            widgetService.getWidgetsByProfile(999L);
        });
    }

    @Test
    void shouldGetWidgetByIdSuccessfully() {
        // Given
        when(widgetRepository.findByIdAndProfileId(1L, 1L)).thenReturn(Optional.of(testWidget));

        // When
        Optional<WidgetResponseDTO> response = widgetService.getWidget(1L, 1L);

        // Then
        assertTrue(response.isPresent());
        assertEquals("Test Widget", response.get().getName());
        verify(widgetRepository, times(1)).findByIdAndProfileId(1L, 1L);
    }

    @Test
    void shouldReturnEmptyWhenWidgetNotFound() {
        // Given
        when(widgetRepository.findByIdAndProfileId(999L, 1L)).thenReturn(Optional.empty());

        // When
        Optional<WidgetResponseDTO> response = widgetService.getWidget(1L, 999L);

        // Then
        assertFalse(response.isPresent());
    }

    @Test
    void shouldUpdateWidgetSuccessfully() {
        // Given
        WidgetRequestDTO updateRequest = new WidgetRequestDTO();
        updateRequest.setName("Updated Widget");
        updateRequest.setDescription("Updated Description");
        updateRequest.setConfiguration(Map.of("newKey", "newValue"));

        when(widgetRepository.findByIdAndProfileId(1L, 1L)).thenReturn(Optional.of(testWidget));
        when(widgetRepository.save(any(Widget.class))).thenReturn(testWidget);

        // When
        Optional<WidgetResponseDTO> response = widgetService.updateWidget(1L, 1L, updateRequest);

        // Then
        assertTrue(response.isPresent());
        verify(widgetRepository, times(1)).save(any(Widget.class));
    }

    @Test
    void shouldReturnEmptyWhenUpdatingNonExistentWidget() {
        // Given
        when(widgetRepository.findByIdAndProfileId(999L, 1L)).thenReturn(Optional.empty());

        // When
        Optional<WidgetResponseDTO> response = widgetService.updateWidget(1L, 999L, testRequest);

        // Then
        assertFalse(response.isPresent());
        verify(widgetRepository, never()).save(any(Widget.class));
    }

    @Test
    void shouldDeleteWidgetSuccessfully() {
        // Given
        when(widgetRepository.findByIdAndProfileId(1L, 1L)).thenReturn(Optional.of(testWidget));

        // When
        boolean deleted = widgetService.deleteWidget(1L, 1L);

        // Then
        assertTrue(deleted);
        verify(widgetRepository, times(1)).delete(testWidget);
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentWidget() {
        // Given
        when(widgetRepository.findByIdAndProfileId(999L, 1L)).thenReturn(Optional.empty());

        // When
        boolean deleted = widgetService.deleteWidget(1L, 999L);

        // Then
        assertFalse(deleted);
        verify(widgetRepository, never()).delete(any(Widget.class));
    }
}
