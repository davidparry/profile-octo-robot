package com.bug.robot.widget.integration;

import com.bug.robot.user.domain.User;
import com.bug.robot.user.repository.UserRepository;
import com.bug.robot.widget.dto.WidgetRequestDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WidgetIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Use existing test user from data.sql (id=1)
        testUser = userRepository.findById(1L).orElseGet(() -> {
            User user = new User();
            user.setUsername("testuser1");
            user.setEmail("testuser1@example.com");
            user.setPasswordHash("$2a$10$dummyhash1");
            return userRepository.save(user);
        });
    }

    @Test
    void createWidget_FullIntegration_Success() throws Exception {
        // Arrange
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("Integration Test Widget");
        request.setDescription("Created in integration test");
        Map<String, Object> config = new HashMap<>();
        config.put("theme", "dark");
        config.put("size", "large");
        request.setConfiguration(config);

        // Act & Assert
        mockMvc.perform(post("/api/users/" + testUser.getId() + "/widgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(testUser.getId()))
                .andExpect(jsonPath("$.name").value("Integration Test Widget"))
                .andExpect(jsonPath("$.description").value("Created in integration test"))
                .andExpect(jsonPath("$.configuration.theme").value("dark"))
                .andExpect(jsonPath("$.configuration.size").value("large"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void createWidget_InvalidUser_ReturnsNotFound() throws Exception {
        // Arrange
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("Test Widget");

        // Act & Assert
        mockMvc.perform(post("/api/users/99999/widgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void createWidget_InvalidData_ReturnsBadRequest() throws Exception {
        // Arrange
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName(""); // Invalid: empty name

        // Act & Assert
        mockMvc.perform(post("/api/users/" + testUser.getId() + "/widgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWidgetsByUserId_AfterCreation_ReturnsWidgets() throws Exception {
        // Arrange - Create a widget first
        WidgetRequestDTO request = new WidgetRequestDTO();
        request.setName("Test Widget for Retrieval");
        request.setDescription("Test");

        mockMvc.perform(post("/api/users/" + testUser.getId() + "/widgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Act & Assert
        mockMvc.perform(get("/api/users/" + testUser.getId() + "/widgets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Widget for Retrieval"));
    }
}
