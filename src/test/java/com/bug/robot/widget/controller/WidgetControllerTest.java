package com.bug.robot.widget.controller;

import com.bug.robot.common.exception.UserNotFoundException;
import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;
import com.bug.robot.widget.service.WidgetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WidgetController.class)
class WidgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WidgetService widgetService;

    private WidgetRequestDTO validRequest;
    private WidgetResponseDTO validResponse;

    @BeforeEach
    void setUp() {
        validRequest = new WidgetRequestDTO();
        validRequest.setName("Test Widget");
        validRequest.setDescription("Test Description");
        Map<String, Object> config = new HashMap<>();
        config.put("color", "blue");
        validRequest.setConfiguration(config);

        validResponse = new WidgetResponseDTO();
        validResponse.setId(1L);
        validResponse.setUserId(1L);
        validResponse.setName("Test Widget");
        validResponse.setDescription("Test Description");
        validResponse.setConfiguration(config);
        validResponse.setCreatedAt(LocalDateTime.now());
        validResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createWidget_Success() throws Exception {
        // Arrange
        when(widgetService.createWidget(eq(1L), any(WidgetRequestDTO.class)))
                .thenReturn(validResponse);

        // Act & Assert
        mockMvc.perform(post("/api/users/1/widgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("Test Widget"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void createWidget_InvalidRequest_EmptyName() throws Exception {
        // Arrange
        validRequest.setName("");

        // Act & Assert
        mockMvc.perform(post("/api/users/1/widgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWidget_UserNotFound() throws Exception {
        // Arrange
        when(widgetService.createWidget(eq(999L), any(WidgetRequestDTO.class)))
                .thenThrow(new UserNotFoundException("User not found with id: 999"));

        // Act & Assert
        mockMvc.perform(post("/api/users/999/widgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void getWidgetsByUserId_Success() throws Exception {
        // Arrange
        when(widgetService.getWidgetsByUserId(1L))
                .thenReturn(List.of(validResponse));

        // Act & Assert
        mockMvc.perform(get("/api/users/1/widgets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Widget"));
    }

    @Test
    void getWidgetById_Success() throws Exception {
        // Arrange
        when(widgetService.getWidgetById(1L, 1L))
                .thenReturn(validResponse);

        // Act & Assert
        mockMvc.perform(get("/api/users/1/widgets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Widget"));
    }
}
