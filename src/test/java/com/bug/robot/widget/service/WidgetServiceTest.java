package com.bug.robot.widget.service;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.repository.ProfileRepository;
import com.bug.robot.widget.domain.Widget;
import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;
import com.bug.robot.widget.exception.ProfileNotFoundException;
import com.bug.robot.widget.repository.WidgetRepository;
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
    private WidgetRequestDTO testRequestDTO;

    @BeforeEach
    void setUp() {
        // Setup test profile
        testProfile = new Profile();
        testProfile.setId(1L);
        testProfile.setFirstName("John");
        testProfile.setLastName("Doe");
        testProfile.setEmail("john@example.com");

        // Setup test widget
        testWidget = new Widget();
        testWidget.setId(1L);
        testWidget.setProfile(testProfile);
        testWidget.setName("Test Widget");
        testWidget.setDescription("Test Description");
        testWidget.setConfiguration("{\"key\":\"value\"}");
        testWidget.setCreatedAt(LocalDateTime.now());
        testWidget.setUpdatedAt(LocalDateTime.now());

        // Setup test request DTO
        testRequestDTO = new WidgetRequestDTO();
        testRequestDTO.setName("Test Widget");
        testRequestDTO.setDescription("Test Description");
        testRequestDTO.setConfiguration("{\"key\":\"value\"}");
    }

    @Test
    void shouldCreateWidgetSuccessfully() {
        // Given
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(widgetRepository.save(any(Widget.class))).thenReturn(testWidget);

        // When
        WidgetResponseDTO result = widgetService.createWidget(1L, testRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getProfileId());
        assertEquals("Test Widget", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals("{\"key\":\"value\"}", result.getConfiguration());
        
        verify(profileRepository, times(1)).findById(1L);
        verify(widgetRepository, times(1)).save(any(Widget.class));
    }

    @Test
    void shouldThrowExceptionWhenProfileNotFound() {
        // Given
        when(profileRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ProfileNotFoundException exception = assertThrows(
            ProfileNotFoundException.class,
            () -> widgetService.createWidget(999L, testRequestDTO)
        );
        
        assertEquals("Profile not found with id: 999", exception.getMessage());
        verify(profileRepository, times(1)).findById(999L);
        verify(widgetRepository, never()).save(any(Widget.class));
    }

    @Test
    void shouldGetWidgetsByProfileId() {
        // Given
        Widget widget2 = new Widget();
        widget2.setId(2L);
        widget2.setProfile(testProfile);
        widget2.setName("Widget 2");
        widget2.setCreatedAt(LocalDateTime.now());
        widget2.setUpdatedAt(LocalDateTime.now());

        when(widgetRepository.findByProfileId(1L)).thenReturn(Arrays.asList(testWidget, widget2));

        // When
        List<WidgetResponseDTO> results = widgetService.getWidgetsByProfileId(1L);

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("Test Widget", results.get(0).getName());
        assertEquals("Widget 2", results.get(1).getName());
        
        verify(widgetRepository, times(1)).findByProfileId(1L);
    }

    @Test
    void shouldGetWidgetById() {
        // Given
        when(widgetRepository.findById(1L)).thenReturn(Optional.of(testWidget));

        // When
        Optional<WidgetResponseDTO> result = widgetService.getWidgetById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Test Widget", result.get().getName());
        
        verify(widgetRepository, times(1)).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenWidgetNotFound() {
        // Given
        when(widgetRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<WidgetResponseDTO> result = widgetService.getWidgetById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(widgetRepository, times(1)).findById(999L);
    }

    @Test
    void shouldGetWidgetByIdAndProfileId() {
        // Given
        when(widgetRepository.findByIdAndProfileId(1L, 1L)).thenReturn(Optional.of(testWidget));

        // When
        Optional<WidgetResponseDTO> result = widgetService.getWidgetByIdAndProfileId(1L, 1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(1L, result.get().getProfileId());
        
        verify(widgetRepository, times(1)).findByIdAndProfileId(1L, 1L);
    }

    @Test
    void shouldDeleteWidget() {
        // Given
        doNothing().when(widgetRepository).deleteById(1L);

        // When
        widgetService.deleteWidget(1L);

        // Then
        verify(widgetRepository, times(1)).deleteById(1L);
    }
}
