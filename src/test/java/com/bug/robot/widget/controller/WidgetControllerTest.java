package com.bug.robot.widget.controller;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.repository.ProfileRepository;
import com.bug.robot.widget.domain.Widget;
import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.repository.WidgetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for WidgetController.
 * Tests the full stack from HTTP request to database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WidgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private WidgetRepository widgetRepository;

    private Profile testProfile;

    @BeforeEach
    void setUp() {
        // Clean up
        widgetRepository.deleteAll();
        profileRepository.deleteAll();

        // Create test profile
        testProfile = new Profile();
        testProfile.setFirstName("John");
        testProfile.setLastName("Doe");
        testProfile.setEmail("john@example.com");
        testProfile = profileRepository.save(testProfile);
    }

    @Test
    void shouldCreateWidgetSuccessfully() throws Exception {
        // Given
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("Dashboard Widget");
        request.setDescription("Main dashboard widget");
        request.setConfiguration("{\"theme\":\"dark\"}");

        // When & Then
        mockMvc.perform(post("/api/users/{userId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(testProfile.getId()))
                .andExpect(jsonPath("$.name").value("Dashboard Widget"))
                .andExpect(jsonPath("$.description").value("Main dashboard widget"))
                .andExpect(jsonPath("$.configuration").value("{\"theme\":\"dark\"}"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
        // Given
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("");
        request.setDescription("Test");

        // When & Then
        mockMvc.perform(post("/api/users/{userId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturnNotFoundWhenProfileDoesNotExist() throws Exception {
        // Given
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("Test Widget");
        request.setDescription("Test");

        // When & Then
        mockMvc.perform(post("/api/users/{userId}/widgets", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Profile not found with id: 999"));
    }

    @Test
    void shouldReturnBadRequestWhenDuplicateWidgetName() throws Exception {
        // Given - Create first widget
        Widget existingWidget = new Widget();
        existingWidget.setName("Duplicate Widget");
        existingWidget.setDescription("First widget");
        existingWidget.setProfile(testProfile);
        widgetRepository.save(existingWidget);

        // Try to create duplicate
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("Duplicate Widget");
        request.setDescription("Second widget");

        // When & Then
        mockMvc.perform(post("/api/users/{userId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Widget with name 'Duplicate Widget' already exists for this profile"));
    }

    @Test
    void shouldGetAllWidgetsForProfile() throws Exception {
        // Given - Create multiple widgets
        Widget widget1 = new Widget();
        widget1.setName("Widget 1");
        widget1.setDescription("First widget");
        widget1.setProfile(testProfile);
        widgetRepository.save(widget1);

        Widget widget2 = new Widget();
        widget2.setName("Widget 2");
        widget2.setDescription("Second widget");
        widget2.setProfile(testProfile);
        widgetRepository.save(widget2);

        // When & Then
        mockMvc.perform(get("/api/users/{userId}/widgets", testProfile.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Widget 1"))
                .andExpect(jsonPath("$[1].name").value("Widget 2"));
    }

    @Test
    void shouldGetSpecificWidget() throws Exception {
        // Given
        Widget widget = new Widget();
        widget.setName("Specific Widget");
        widget.setDescription("Test widget");
        widget.setProfile(testProfile);
        widget = widgetRepository.save(widget);

        // When & Then
        mockMvc.perform(get("/api/users/{userId}/widgets/{widgetId}", 
                testProfile.getId(), widget.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(widget.getId()))
                .andExpect(jsonPath("$.name").value("Specific Widget"))
                .andExpect(jsonPath("$.userId").value(testProfile.getId()));
    }

    @Test
    void shouldReturnBadRequestWhenWidgetDoesNotBelongToProfile() throws Exception {
        // Given - Create another profile
        Profile otherProfile = new Profile();
        otherProfile.setFirstName("Jane");
        otherProfile.setLastName("Smith");
        otherProfile.setEmail("jane@example.com");
        otherProfile = profileRepository.save(otherProfile);

        // Create widget for other profile
        Widget widget = new Widget();
        widget.setName("Other Widget");
        widget.setProfile(otherProfile);
        widget = widgetRepository.save(widget);

        // When & Then - Try to access with wrong profile ID
        mockMvc.perform(get("/api/users/{userId}/widgets/{widgetId}", 
                testProfile.getId(), widget.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Widget not found or does not belong to this profile"));
    }
}
