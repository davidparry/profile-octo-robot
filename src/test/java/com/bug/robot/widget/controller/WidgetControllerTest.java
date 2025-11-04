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

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        widgetRepository.deleteAll();
        profileRepository.deleteAll();

        testProfile = new Profile();
        testProfile.setFirstName("John");
        testProfile.setLastName("Doe");
        testProfile.setEmail("john.doe@example.com");
        testProfile = profileRepository.save(testProfile);
    }

    @Test
    void createWidget_Success() throws Exception {
        // Arrange
        Map<String, Object> config = new HashMap<>();
        config.put("color", "blue");
        config.put("size", "large");

        WidgetRequestDTO request = new WidgetRequestDTO("Dashboard Widget", "Main dashboard widget", config);

        // Act & Assert
        mockMvc.perform(post("/api/profiles/{profileId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.profileId").value(testProfile.getId()))
                .andExpect(jsonPath("$.name").value("Dashboard Widget"))
                .andExpect(jsonPath("$.description").value("Main dashboard widget"))
                .andExpect(jsonPath("$.configuration.color").value("blue"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void createWidget_ProfileNotFound() throws Exception {
        // Arrange
        WidgetRequestDTO request = new WidgetRequestDTO("Test Widget", "Description", null);

        // Act & Assert
        mockMvc.perform(post("/api/profiles/{profileId}/widgets", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Profile not found with id: 999"));
    }

    @Test
    void createWidget_InvalidRequest_MissingName() throws Exception {
        // Arrange
        WidgetRequestDTO request = new WidgetRequestDTO(null, "Description", null);

        // Act & Assert
        mockMvc.perform(post("/api/profiles/{profileId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    void createWidget_DuplicateName() throws Exception {
        // Arrange
        WidgetRequestDTO request1 = new WidgetRequestDTO("Unique Widget", "First widget", null);
        WidgetRequestDTO request2 = new WidgetRequestDTO("Unique Widget", "Duplicate widget", null);

        // Create first widget
        mockMvc.perform(post("/api/profiles/{profileId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Try to create duplicate
        mockMvc.perform(post("/api/profiles/{profileId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Widget with name 'Unique Widget' already exists for this profile"));
    }

    @Test
    void getWidgetsByProfile_Success() throws Exception {
        // Arrange - Create two widgets
        WidgetRequestDTO request1 = new WidgetRequestDTO("Widget 1", "First widget", null);
        WidgetRequestDTO request2 = new WidgetRequestDTO("Widget 2", "Second widget", null);

        mockMvc.perform(post("/api/profiles/{profileId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/profiles/{profileId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        // Act & Assert
        mockMvc.perform(get("/api/profiles/{profileId}/widgets", testProfile.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists());
    }

    @Test
    void getWidget_Success() throws Exception {
        // Arrange - Create a widget
        WidgetRequestDTO request = new WidgetRequestDTO("Test Widget", "Description", null);

        String response = mockMvc.perform(post("/api/profiles/{profileId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, Object> createdWidget = objectMapper.readValue(response, Map.class);
        Long widgetId = ((Number) createdWidget.get("id")).longValue();

        // Act & Assert
        mockMvc.perform(get("/api/profiles/{profileId}/widgets/{widgetId}", testProfile.getId(), widgetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(widgetId))
                .andExpect(jsonPath("$.name").value("Test Widget"));
    }
}
