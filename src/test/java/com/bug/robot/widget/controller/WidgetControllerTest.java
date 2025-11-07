package com.bug.robot.widget.controller;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.repository.ProfileRepository;
import com.bug.robot.widget.domain.Widget;
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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WidgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private WidgetRepository widgetRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
        testProfile.setPhoneNumber("1234567890");
        testProfile.setAddress("123 Main St");
        testProfile.setBio("Test bio");
        testProfile.setBirthDate(LocalDate.of(1990, 1, 1));
        testProfile = profileRepository.save(testProfile);
    }

    @Test
    void shouldCreateWidgetSuccessfully() throws Exception {
        // Given
        Map<String, String> widgetRequest = new HashMap<>();
        widgetRequest.put("name", "Dashboard Widget");
        widgetRequest.put("description", "Main dashboard widget");
        widgetRequest.put("configuration", "{\"theme\":\"dark\"}");

        // When & Then
        mockMvc.perform(post("/api/profiles/{profileId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(widgetRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.profileId").value(testProfile.getId()))
                .andExpect(jsonPath("$.name").value("Dashboard Widget"))
                .andExpect(jsonPath("$.description").value("Main dashboard widget"))
                .andExpect(jsonPath("$.configuration").value("{\"theme\":\"dark\"}"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void shouldReturnBadRequestForInvalidWidget() throws Exception {
        // Given - empty name
        Map<String, String> widgetRequest = new HashMap<>();
        widgetRequest.put("name", "");
        widgetRequest.put("description", "Test");

        // When & Then
        mockMvc.perform(post("/api/profiles/{profileId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(widgetRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundForNonExistentProfile() throws Exception {
        // Given
        Map<String, String> widgetRequest = new HashMap<>();
        widgetRequest.put("name", "Test Widget");
        widgetRequest.put("description", "Test");

        // When & Then
        mockMvc.perform(post("/api/profiles/{profileId}/widgets", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(widgetRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Profile Not Found"))
                .andExpect(jsonPath("$.message").value("Profile not found with id: 999"));
    }

    @Test
    void shouldGetWidgetsByProfileId() throws Exception {
        // Given - create test widgets
        Widget widget1 = new Widget();
        widget1.setProfile(testProfile);
        widget1.setName("Widget 1");
        widget1.setDescription("Description 1");
        widgetRepository.save(widget1);

        Widget widget2 = new Widget();
        widget2.setProfile(testProfile);
        widget2.setName("Widget 2");
        widget2.setDescription("Description 2");
        widgetRepository.save(widget2);

        // When & Then
        mockMvc.perform(get("/api/profiles/{profileId}/widgets", testProfile.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Widget 1"))
                .andExpect(jsonPath("$[1].name").value("Widget 2"));
    }

    @Test
    void shouldGetSpecificWidget() throws Exception {
        // Given
        Widget widget = new Widget();
        widget.setProfile(testProfile);
        widget.setName("Specific Widget");
        widget.setDescription("Specific Description");
        widget = widgetRepository.save(widget);

        // When & Then
        mockMvc.perform(get("/api/profiles/{profileId}/widgets/{widgetId}", 
                testProfile.getId(), widget.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(widget.getId()))
                .andExpect(jsonPath("$.name").value("Specific Widget"));
    }

    @Test
    void shouldReturnNotFoundForNonExistentWidget() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/profiles/{profileId}/widgets/{widgetId}", 
                testProfile.getId(), 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteWidget() throws Exception {
        // Given
        Widget widget = new Widget();
        widget.setProfile(testProfile);
        widget.setName("Widget to Delete");
        widget = widgetRepository.save(widget);

        // When & Then
        mockMvc.perform(delete("/api/profiles/{profileId}/widgets/{widgetId}", 
                testProfile.getId(), widget.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/profiles/{profileId}/widgets/{widgetId}", 
                testProfile.getId(), widget.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotDeleteWidgetFromDifferentProfile() throws Exception {
        // Given - create another profile
        Profile otherProfile = new Profile();
        otherProfile.setFirstName("Jane");
        otherProfile.setLastName("Smith");
        otherProfile.setEmail("jane@example.com");
        otherProfile.setBirthDate(LocalDate.of(1992, 5, 15));
        otherProfile = profileRepository.save(otherProfile);

        // Create widget for first profile
        Widget widget = new Widget();
        widget.setProfile(testProfile);
        widget.setName("Widget");
        widget = widgetRepository.save(widget);

        // When & Then - try to delete using wrong profile ID
        mockMvc.perform(delete("/api/profiles/{profileId}/widgets/{widgetId}", 
                otherProfile.getId(), widget.getId()))
                .andExpect(status().isNotFound());
    }
}
