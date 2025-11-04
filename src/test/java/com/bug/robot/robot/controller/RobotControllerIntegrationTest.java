package com.bug.robot.robot.controller;

import com.bug.robot.robot.domain.Robot;
import com.bug.robot.robot.repository.RobotRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RobotControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private RobotRepository repository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }
    
    @Test
    void testCreateRobot_ValidPayload_Returns201() throws Exception {
        String requestBody = """
            {
                "name": "Assembly Bot Alpha",
                "model": "ASM-2000",
                "serialNumber": "ASM-2000-001",
                "status": "ACTIVE",
                "manufacturer": "RoboTech Industries",
                "description": "High-precision assembly robot",
                "manufacturingDate": "2024-01-15"
            }
            """;
        
        mockMvc.perform(post("/api/robots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Assembly Bot Alpha")))
                .andExpect(jsonPath("$.model", is("ASM-2000")))
                .andExpect(jsonPath("$.serialNumber", is("ASM-2000-001")))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.manufacturer", is("RoboTech Industries")))
                .andExpect(jsonPath("$.manufacturingDate", is("2024-01-15")))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));
    }
    
    @Test
    void testCreateRobot_MissingRequiredFields_Returns400() throws Exception {
        String requestBody = """
            {
                "name": "Assembly Bot Alpha"
            }
            """;
        
        mockMvc.perform(post("/api/robots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.model", containsString("required")))
                .andExpect(jsonPath("$.serialNumber", containsString("required")))
                .andExpect(jsonPath("$.status", containsString("required")));
    }
    
    @Test
    void testCreateRobot_InvalidSerialNumberFormat_Returns400() throws Exception {
        String requestBody = """
            {
                "name": "Assembly Bot Alpha",
                "model": "ASM-2000",
                "serialNumber": "asm-2000-001",
                "status": "ACTIVE",
                "manufacturer": "RoboTech Industries"
            }
            """;
        
        mockMvc.perform(post("/api/robots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serialNumber", containsString("uppercase")));
    }
    
    @Test
    void testCreateRobot_DuplicateSerialNumber_Returns400() throws Exception {
        String requestBody = """
            {
                "name": "Assembly Bot Alpha",
                "model": "ASM-2000",
                "serialNumber": "ASM-2000-001",
                "status": "ACTIVE",
                "manufacturer": "RoboTech Industries"
            }
            """;
        
        // Create first robot
        mockMvc.perform(post("/api/robots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());
        
        // Try to create duplicate
        mockMvc.perform(post("/api/robots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testCreateRobot_InvalidDateFormat_Returns400() throws Exception {
        String requestBody = """
            {
                "name": "Assembly Bot Alpha",
                "model": "ASM-2000",
                "serialNumber": "ASM-2000-001",
                "status": "ACTIVE",
                "manufacturer": "RoboTech Industries",
                "manufacturingDate": "2024/01/15"
            }
            """;
        
        mockMvc.perform(post("/api/robots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.manufacturingDate", containsString("YYYY-MM-DD")));
    }
    
    @Test
    void testGetAllRobots_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/robots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
    
    @Test
    void testGetAllRobots_ReturnsRobotsList() throws Exception {
        // Create a robot first
        String requestBody = """
            {
                "name": "Assembly Bot Alpha",
                "model": "ASM-2000",
                "serialNumber": "ASM-2000-001",
                "status": "ACTIVE",
                "manufacturer": "RoboTech Industries"
            }
            """;
        
        mockMvc.perform(post("/api/robots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());
        
        mockMvc.perform(get("/api/robots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Assembly Bot Alpha")));
    }
    
    @Test
    void testGetRobotById_Found_Returns200() throws Exception {
        // Create a robot first
        String requestBody = """
            {
                "name": "Assembly Bot Alpha",
                "model": "ASM-2000",
                "serialNumber": "ASM-2000-001",
                "status": "ACTIVE",
                "manufacturer": "RoboTech Industries"
            }
            """;
        
        String response = mockMvc.perform(post("/api/robots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        Long id = objectMapper.readTree(response).get("id").asLong();
        
        mockMvc.perform(get("/api/robots/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.name", is("Assembly Bot Alpha")));
    }
    
    @Test
    void testGetRobotById_NotFound_Returns404() throws Exception {
        mockMvc.perform(get("/api/robots/999"))
                .andExpect(status().isNotFound());
    }
}
