package com.bug.robot.widget.service;

import com.bug.robot.widget.domain.Widget;
import com.bug.robot.widget.dto.WidgetRequestDTO;
import com.bug.robot.widget.repository.WidgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WidgetServiceTest {

    @Mock
    private WidgetRepository repository;

    @InjectMocks
    private WidgetService service;

    private WidgetRequestDTO validDto;
    private Widget widget;

    @BeforeEach
    void setUp() {
        validDto = new WidgetRequestDTO();
        validDto.setName("Test Widget");
        validDto.setDescription("Test Description");
        validDto.setType("STANDARD");
        validDto.setStatus("ACTIVE");
        validDto.setPrice(new BigDecimal("99.99"));

        widget = new Widget();
        widget.setId(1L);
        widget.setName("Test Widget");
        widget.setDescription("Test Description");
        widget.setType("STANDARD");
        widget.setStatus("ACTIVE");
        widget.setPrice(new BigDecimal("99.99"));
    }

    @Test
    void testCreateWidget_Success() {
        when(repository.findByName(validDto.getName())).thenReturn(Optional.empty());
        when(repository.save(any(Widget.class))).thenReturn(widget);

        Widget result = service.create(validDto);

        assertNotNull(result);
        assertEquals("Test Widget", result.getName());
        assertEquals("STANDARD", result.getType());
        verify(repository, times(1)).findByName(validDto.getName());
        verify(repository, times(1)).save(any(Widget.class));
    }

    @Test
    void testCreateWidget_DuplicateName_ThrowsException() {
        when(repository.findByName(validDto.getName())).thenReturn(Optional.of(widget));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.create(validDto)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(repository, times(1)).findByName(validDto.getName());
        verify(repository, never()).save(any(Widget.class));
    }

    @Test
    void testCreateWidget_DefaultStatus() {
        validDto.setStatus(null);
        when(repository.findByName(validDto.getName())).thenReturn(Optional.empty());
        when(repository.save(any(Widget.class))).thenAnswer(invocation -> {
            Widget saved = invocation.getArgument(0);
            assertEquals("ACTIVE", saved.getStatus());
            return saved;
        });

        service.create(validDto);

        verify(repository, times(1)).save(any(Widget.class));
    }

    @Test
    void testGetAll() {
        List<Widget> widgets = Arrays.asList(widget, widget);
        when(repository.findAll()).thenReturn(widgets);

        List<Widget> result = service.getAll();

        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetById_Found() {
        when(repository.findById(1L)).thenReturn(Optional.of(widget));

        Optional<Widget> result = service.getById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Widget", result.get().getName());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testGetById_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Optional<Widget> result = service.getById(999L);

        assertFalse(result.isPresent());
        verify(repository, times(1)).findById(999L);
    }

    @Test
    void testUpdate_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(widget));
        when(repository.findByName(validDto.getName())).thenReturn(Optional.of(widget));
        when(repository.save(any(Widget.class))).thenReturn(widget);

        Optional<Widget> result = service.update(1L, validDto);

        assertTrue(result.isPresent());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Widget.class));
    }

    @Test
    void testUpdate_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Optional<Widget> result = service.update(999L, validDto);

        assertFalse(result.isPresent());
        verify(repository, times(1)).findById(999L);
        verify(repository, never()).save(any(Widget.class));
    }

    @Test
    void testDelete() {
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testGetByType() {
        List<Widget> widgets = Arrays.asList(widget);
        when(repository.findByType("STANDARD")).thenReturn(widgets);

        List<Widget> result = service.getByType("STANDARD");

        assertEquals(1, result.size());
        verify(repository, times(1)).findByType("STANDARD");
    }

    @Test
    void testGetByStatus() {
        List<Widget> widgets = Arrays.asList(widget);
        when(repository.findByStatus("ACTIVE")).thenReturn(widgets);

        List<Widget> result = service.getByStatus("ACTIVE");

        assertEquals(1, result.size());
        verify(repository, times(1)).findByStatus("ACTIVE");
    }
}
