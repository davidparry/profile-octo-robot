package com.bug.robot.profile.widget.controller;

import com.bug.robot.profile.widget.dto.WidgetRequestDTO;
import com.bug.robot.profile.widget.dto.WidgetResponseDTO;
import com.bug.robot.profile.widget.exception.ProfileNotFoundException;
import com.bug.robot.profile.widget.service.WidgetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for WidgetController.
 * Uses MockMvc for testing REST endpoints.
 */
@WebMvcTest(WidgetController.class)
class WidgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WidgetService widgetService;

    private WidgetResponseDTO testResponse;
    private WidgetRequestDTO testRequest;

    @BeforeEach
    void setUp() {
        testResponse = new WidgetResponseDTO();
        testResponse.setId(1L);
        testResponse.setProfileId(1L);
        testResponse.setName("Test Widget");
        testResponse.setDescription("Test Description");
        testResponse.setConfiguration(Map.of("key", "value"));
        testResponse.setCreatedAt(LocalDateTime.now());
        testResponse.setUpdatedAt(LocalDateTime.now());

        testRequest = new WidgetRequestDTO();
        testRequest.setName("Test Widget");
        testRequest.setDescription("Test Description");
        testRequest.setConfiguration(Map.of("key", "value"));
    }

    @Test
    void shouldCreateWidgetSuccessfully() throws Exception {
        // Given
        when(widgetService.createWidget(eq(1L), any(WidgetRequestDTO.class)))
            .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(post("/api/profiles/1/widgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Widget"))
                .andExpect(jsonPath("$.profileId").value(1));
    }

    @Test
    void shouldReturn404WhenProfileNotFoundOnCreate() throws Exception {
        // Given
        when(widgetService.createWidget(eq(999L), any(WidgetRequestDTO.class)))
            .thenThrow(new ProfileNotFoundException(999L));

        // When & Then
        mockMvc.perform(post("/api/profiles/999/widgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Profile Not Found"));
    }

    @Test
    void shouldReturn400WhenInvalidWidgetData() throws Exception {
        // Given
        WidgetRequestDTO invalidRequest = new WidgetRequestDTO();
        invalidRequest.setName(""); // Invalid: empty name
        invalidRequest.setDescription("Test");

        // When & Then
        mockMvc.perform(post("/api/profiles/1/widgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetWidgetsSuccessfully() throws Exception {
        // Given
        when(widgetService.getWidgetsByProfile(1L))
            .thenReturn(Arrays.asList(testResponse));

        // When & Then
        mockMvc.perform(get("/api/profiles/1/widgets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Widget"));
    }

    @Test
    void shouldGetWidgetByIdSuccessfully() throws Exception {
        // Given
        when(widgetService.getWidget(1L, 1L))
            .thenReturn(Optional.of(testResponse));

        // When & Then
        mockMvc.perform(get("/api/profiles/1/widgets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Widget"));
    }

    @Test
    void shouldReturn404WhenWidgetNotFound() throws Exception {
        // Given
        when(widgetService.getWidget(1L, 999L))
            .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/profiles/1/widgets/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateWidgetSuccessfully() throws Exception {
        // Given
        when(widgetService.updateWidget(eq(1L), eq(1L), any(WidgetRequestDTO.class)))
            .thenReturn(Optional.of(testResponse));

        // When & Then
        mockMvc.perform(put("/api/profiles/1/widgets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldDeleteWidgetSuccessfully() throws Exception {
        // Given
        when(widgetService.deleteWidget(1L, 1L))
            .thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/profiles/1/widgets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentWidget() throws Exception {
        // Given
        when(widgetService.deleteWidget(1L, 999L))
            .thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/profiles/1/widgets/999"))
                .andExpect(status().isNotFound());
    }
}
