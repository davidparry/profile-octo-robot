package com.bug.robot.profile.controller;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.dto.WidgetRequestDTO;
import com.bug.robot.profile.repository.ProfileRepository;
import com.bug.robot.profile.repository.WidgetRepository;
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
        testProfile.setBirthDate(LocalDate.of(1990, 1, 1));
        testProfile = profileRepository.save(testProfile);
    }
    
    @Test
    void createWidget_WithValidData_ShouldReturn201() throws Exception {
        // Arrange
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("Dashboard Widget");
        request.setDescription("Main dashboard widget");
        request.setConfiguration("{\"color\":\"blue\",\"size\":\"large\"}");
        
        // Act & Assert
        mockMvc.perform(post("/api/users/{userId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.userId", is(testProfile.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Dashboard Widget")))
                .andExpect(jsonPath("$.description", is("Main dashboard widget")))
                .andExpect(jsonPath("$.configuration", is("{\"color\":\"blue\",\"size\":\"large\"}")))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));
    }
    
    @Test
    void createWidget_WithNonExistentUser_ShouldReturn404() throws Exception {
        // Arrange
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("Test Widget");
        request.setDescription("Test Description");
        
        // Act & Assert
        mockMvc.perform(post("/api/users/{userId}/widgets", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Profile not found")));
    }
    
    @Test
    void createWidget_WithMissingName_ShouldReturn400() throws Exception {
        // Arrange
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setDescription("Test Description");
        
        // Act & Assert
        mockMvc.perform(post("/api/users/{userId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Validation failed")));
    }
    
    @Test
    void createWidget_WithNameTooLong_ShouldReturn400() throws Exception {
        // Arrange
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("A".repeat(256)); // Exceeds 255 character limit
        request.setDescription("Test Description");
        
        // Act & Assert
        mockMvc.perform(post("/api/users/{userId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }
    
    @Test
    void getWidgets_WithValidUser_ShouldReturnWidgetList() throws Exception {
        // Arrange - Create two widgets
        WidgetRequestDTO request1 = new WidgetRequestDTO();
        request1.setName("Widget 1");
        request1.setDescription("First widget");
        
        WidgetRequestDTO request2 = new WidgetRequestDTO();
        request2.setName("Widget 2");
        request2.setDescription("Second widget");
        
        mockMvc.perform(post("/api/users/{userId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)));
        
        mockMvc.perform(post("/api/users/{userId}/widgets", testProfile.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)));
        
        // Act & Assert
        mockMvc.perform(get("/api/users/{userId}/widgets", testProfile.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Widget 1")))
                .andExpect(jsonPath("$[1].name", is("Widget 2")));
    }
    
    @Test
    void getWidgets_WithNonExistentUser_ShouldReturn404() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users/{userId}/widgets", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }
}
