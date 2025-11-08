package com.bug.robot.widget.controller;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.repository.ProfileRepository;
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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for WidgetController.
 * Follows best practices with @SpringBootTest and MockMvc.
 * Tests the complete flow from HTTP request to database.
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
        testProfile.setPhoneNumber("1234567890");
        testProfile.setAddress("123 Main St");
        testProfile.setBio("Test bio");
        testProfile.setBirthDate(LocalDate.of(1990, 1, 1));
        testProfile = profileRepository.save(testProfile);
    }

    @Test
    void shouldCreateWidgetSuccessfully() throws Exception {
        // Given
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("Dashboard Widget");
        request.setDescription("Main dashboard widget");
        Map<String, Object> config = new HashMap<>();
        config.put("color", "blue");
        config.put("size", "large");
        request.setConfiguration(config);

        // When & Then
        mockMvc.perform(post("/api/users/{userId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.userId", is(testProfile.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Dashboard Widget")))
                .andExpect(jsonPath("$.description", is("Main dashboard widget")))
                .andExpect(jsonPath("$.configuration.color", is("blue")))
                .andExpect(jsonPath("$.configuration.size", is("large")))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));
    }

    @Test
    void shouldReturn404WhenProfileNotFound() throws Exception {
        // Given
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("Test Widget");
        request.setDescription("Test Description");

        // When & Then
        mockMvc.perform(post("/api/users/{userId}/widgets", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Profile Not Found")))
                .andExpect(jsonPath("$.message", containsString("999")));
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        // Given
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("");
        request.setDescription("Test Description");

        // When & Then
        mockMvc.perform(post("/api/users/{userId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.fieldErrors.name", notNullValue()));
    }

    @Test
    void shouldReturn400WhenNameIsTooLong() throws Exception {
        // Given
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("a".repeat(256)); // Exceeds 255 character limit
        request.setDescription("Test Description");

        // When & Then
        mockMvc.perform(post("/api/users/{userId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.fieldErrors.name", notNullValue()));
    }

    @Test
    void shouldReturn400WhenDescriptionIsTooLong() throws Exception {
        // Given
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("Test Widget");
        request.setDescription("a".repeat(1001)); // Exceeds 1000 character limit

        // When & Then
        mockMvc.perform(post("/api/users/{userId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.fieldErrors.description", notNullValue()));
    }

    @Test
    void shouldReturn400WhenDuplicateWidgetName() throws Exception {
        // Given - Create first widget
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("Unique Widget");
        request.setDescription("First widget");

        mockMvc.perform(post("/api/users/{userId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // When & Then - Try to create duplicate
        mockMvc.perform(post("/api/users/{userId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    void shouldCreateWidgetWithNullConfiguration() throws Exception {
        // Given
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("Simple Widget");
        request.setDescription("Widget without configuration");
        request.setConfiguration(null);

        // When & Then
        mockMvc.perform(post("/api/users/{userId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Simple Widget")));
    }

    @Test
    void shouldCreateWidgetWithEmptyConfiguration() throws Exception {
        // Given
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("Empty Config Widget");
        request.setDescription("Widget with empty configuration");
        request.setConfiguration(new HashMap<>());

        // When & Then
        mockMvc.perform(post("/api/users/{userId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Empty Config Widget")));
    }
}
