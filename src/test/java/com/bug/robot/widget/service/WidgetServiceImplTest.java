package com.bug.robot.widget.service;

import com.bug.robot.common.exception.UserNotFoundException;
import com.bug.robot.common.exception.WidgetValidationException;
import com.bug.robot.user.domain.User;
import com.bug.robot.user.service.UserService;
import com.bug.robot.widget.domain.Widget;
import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.dto.WidgetResponseDTO;
import com.bug.robot.widget.repository.WidgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WidgetServiceImplTest {

    @Mock
    private WidgetRepository widgetRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private WidgetServiceImpl widgetService;

    private User testUser;
    private WidgetRequestDTO validRequest;
    private Widget testWidget;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        validRequest = new WidgetRequestDTO();
        validRequest.setName("Test Widget");
        validRequest.setDescription("Test Description");
        Map<String, Object> config = new HashMap<>();
        config.put("color", "blue");
        validRequest.setConfiguration(config);

        testWidget = new Widget();
        testWidget.setId(1L);
        testWidget.setUser(testUser);
        testWidget.setName("Test Widget");
        testWidget.setDescription("Test Description");
        testWidget.setConfiguration(config);
        testWidget.setCreatedAt(LocalDateTime.now());
        testWidget.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createWidget_Success() {
        // Arrange
        when(userService.getUserByIdOrThrow(1L)).thenReturn(testUser);
        when(widgetRepository.save(any(Widget.class))).thenReturn(testWidget);

        // Act
        WidgetResponseDTO response = widgetService.createWidget(1L, validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals("Test Widget", response.getName());
        assertEquals("Test Description", response.getDescription());
        verify(userService).getUserByIdOrThrow(1L);
        verify(widgetRepository).save(any(Widget.class));
    }

    @Test
    void createWidget_UserNotFound() {
        // Arrange
        when(userService.getUserByIdOrThrow(999L))
                .thenThrow(new UserNotFoundException("User not found with id: 999"));

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            widgetService.createWidget(999L, validRequest);
        });
        verify(widgetRepository, never()).save(any(Widget.class));
    }

    @Test
    void createWidget_EmptyName() {
        // Arrange
        validRequest.setName("");
        when(userService.getUserByIdOrThrow(1L)).thenReturn(testUser);

        // Act & Assert
        assertThrows(WidgetValidationException.class, () -> {
            widgetService.createWidget(1L, validRequest);
        });
        verify(widgetRepository, never()).save(any(Widget.class));
    }

    @Test
    void createWidget_NameTooLong() {
        // Arrange
        String longName = "a".repeat(256);
        validRequest.setName(longName);
        when(userService.getUserByIdOrThrow(1L)).thenReturn(testUser);

        // Act & Assert
        assertThrows(WidgetValidationException.class, () -> {
            widgetService.createWidget(1L, validRequest);
        });
        verify(widgetRepository, never()).save(any(Widget.class));
    }

    @Test
    void getWidgetsByUserId_Success() {
        // Arrange
        when(userService.existsById(1L)).thenReturn(true);
        when(widgetRepository.findByUserId(1L)).thenReturn(List.of(testWidget));

        // Act
        List<WidgetResponseDTO> widgets = widgetService.getWidgetsByUserId(1L);

        // Assert
        assertNotNull(widgets);
        assertEquals(1, widgets.size());
        assertEquals("Test Widget", widgets.get(0).getName());
        verify(widgetRepository).findByUserId(1L);
    }

    @Test
    void getWidgetsByUserId_UserNotFound() {
        // Arrange
        when(userService.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            widgetService.getWidgetsByUserId(999L);
        });
        verify(widgetRepository, never()).findByUserId(anyLong());
    }
}
