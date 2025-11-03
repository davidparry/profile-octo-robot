package com.bug.robot.widget.controller;

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

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WidgetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WidgetRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testCreateWidget_ValidData_Returns201() throws Exception {
        WidgetRequestDTO dto = new WidgetRequestDTO();
        dto.setName("Integration Test Widget");
        dto.setDescription("Test Description");
        dto.setType("STANDARD");
        dto.setStatus("ACTIVE");
        dto.setPrice(new BigDecimal("99.99"));

        mockMvc.perform(post("/api/widgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Integration Test Widget"))
                .andExpect(jsonPath("$.type").value("STANDARD"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void testCreateWidget_InvalidData_Returns400() throws Exception {
        WidgetRequestDTO dto = new WidgetRequestDTO();
        // Missing required name field
        dto.setType("INVALID_TYPE");
        dto.setPrice(new BigDecimal("-10.00"));

        mockMvc.perform(post("/api/widgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateWidget_DuplicateName_Returns400() throws Exception {
        // Create first widget
        Widget widget = new Widget();
        widget.setName("Duplicate Widget");
        widget.setType("STANDARD");
        widget.setPrice(new BigDecimal("50.00"));
        repository.save(widget);

        // Try to create duplicate
        WidgetRequestDTO dto = new WidgetRequestDTO();
        dto.setName("Duplicate Widget");
        dto.setType("PREMIUM");
        dto.setPrice(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/widgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllWidgets() throws Exception {
        // Create test widgets
        Widget widget1 = new Widget();
        widget1.setName("Widget 1");
        widget1.setType("STANDARD");
        widget1.setPrice(new BigDecimal("10.00"));
        repository.save(widget1);

        Widget widget2 = new Widget();
        widget2.setName("Widget 2");
        widget2.setType("PREMIUM");
        widget2.setPrice(new BigDecimal("20.00"));
        repository.save(widget2);

        mockMvc.perform(get("/api/widgets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Widget 1"))
                .andExpect(jsonPath("$[1].name").value("Widget 2"));
    }

    @Test
    void testGetWidgetById_Found() throws Exception {
        Widget widget = new Widget();
        widget.setName("Test Widget");
        widget.setType("STANDARD");
        widget.setPrice(new BigDecimal("30.00"));
        Widget saved = repository.save(widget);

        mockMvc.perform(get("/api/widgets/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Test Widget"));
    }

    @Test
    void testGetWidgetById_NotFound() throws Exception {
        mockMvc.perform(get("/api/widgets/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateWidget_Success() throws Exception {
        Widget widget = new Widget();
        widget.setName("Original Widget");
        widget.setType("STANDARD");
        widget.setPrice(new BigDecimal("40.00"));
        Widget saved = repository.save(widget);

        WidgetRequestDTO dto = new WidgetRequestDTO();
        dto.setName("Updated Widget");
        dto.setType("PREMIUM");
        dto.setPrice(new BigDecimal("50.00"));

        mockMvc.perform(put("/api/widgets/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Widget"))
                .andExpect(jsonPath("$.type").value("PREMIUM"))
                .andExpect(jsonPath("$.price").value(50.00));
    }

    @Test
    void testUpdateWidget_NotFound() throws Exception {
        WidgetRequestDTO dto = new WidgetRequestDTO();
        dto.setName("Non-existent Widget");
        dto.setType("STANDARD");
        dto.setPrice(new BigDecimal("60.00"));

        mockMvc.perform(put("/api/widgets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteWidget() throws Exception {
        Widget widget = new Widget();
        widget.setName("Widget to Delete");
        widget.setType("STANDARD");
        widget.setPrice(new BigDecimal("70.00"));
        Widget saved = repository.save(widget);

        mockMvc.perform(delete("/api/widgets/" + saved.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/widgets/" + saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetWidgetsByType() throws Exception {
        Widget widget1 = new Widget();
        widget1.setName("Standard Widget 1");
        widget1.setType("STANDARD");
        widget1.setPrice(new BigDecimal("10.00"));
        repository.save(widget1);

        Widget widget2 = new Widget();
        widget2.setName("Standard Widget 2");
        widget2.setType("STANDARD");
        widget2.setPrice(new BigDecimal("20.00"));
        repository.save(widget2);

        Widget widget3 = new Widget();
        widget3.setName("Premium Widget");
        widget3.setType("PREMIUM");
        widget3.setPrice(new BigDecimal("100.00"));
        repository.save(widget3);

        mockMvc.perform(get("/api/widgets/type/STANDARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].type", everyItem(is("STANDARD"))));
    }

    @Test
    void testGetWidgetsByStatus() throws Exception {
        Widget widget1 = new Widget();
        widget1.setName("Active Widget");
        widget1.setType("STANDARD");
        widget1.setStatus("ACTIVE");
        widget1.setPrice(new BigDecimal("10.00"));
        repository.save(widget1);

        Widget widget2 = new Widget();
        widget2.setName("Inactive Widget");
        widget2.setType("STANDARD");
        widget2.setStatus("INACTIVE");
        widget2.setPrice(new BigDecimal("20.00"));
        repository.save(widget2);

        mockMvc.perform(get("/api/widgets/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }
}
