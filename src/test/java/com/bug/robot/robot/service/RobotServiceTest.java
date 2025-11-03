package com.bug.robot.robot.service;

import com.bug.robot.robot.domain.Robot;
import com.bug.robot.robot.domain.RobotStatus;
import com.bug.robot.robot.dto.RobotRequestDTO;
import com.bug.robot.robot.exception.DuplicateSerialNumberException;
import com.bug.robot.robot.repository.RobotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RobotServiceTest {
    
    @Mock
    private RobotRepository repository;
    
    @InjectMocks
    private RobotService service;
    
    private RobotRequestDTO validDto;
    private Robot savedRobot;
    
    @BeforeEach
    void setUp() {
        validDto = new RobotRequestDTO();
        validDto.setName("Assembly Bot Alpha");
        validDto.setModel("ASM-2000");
        validDto.setSerialNumber("ASM-2000-001");
        validDto.setStatus("ACTIVE");
        validDto.setManufacturer("RoboTech Industries");
        validDto.setDescription("High-precision assembly robot");
        validDto.setManufacturingDate("2024-01-15");
        
        savedRobot = new Robot();
        savedRobot.setId(1L);
        savedRobot.setName("Assembly Bot Alpha");
        savedRobot.setModel("ASM-2000");
        savedRobot.setSerialNumber("ASM-2000-001");
        savedRobot.setStatus(RobotStatus.ACTIVE);
        savedRobot.setManufacturer("RoboTech Industries");
        savedRobot.setDescription("High-precision assembly robot");
        savedRobot.setManufacturingDate(LocalDate.of(2024, 1, 15));
    }
    
    @Test
    void testCreateRobot_Success() {
        when(repository.existsBySerialNumber(anyString())).thenReturn(false);
        when(repository.save(any(Robot.class))).thenReturn(savedRobot);
        
        Robot result = service.create(validDto);
        
        assertNotNull(result);
        assertEquals("Assembly Bot Alpha", result.getName());
        assertEquals("ASM-2000", result.getModel());
        assertEquals("ASM-2000-001", result.getSerialNumber());
        assertEquals(RobotStatus.ACTIVE, result.getStatus());
        
        verify(repository).existsBySerialNumber("ASM-2000-001");
        verify(repository).save(any(Robot.class));
    }
    
    @Test
    void testCreateRobot_DuplicateSerialNumber() {
        when(repository.existsBySerialNumber(anyString())).thenReturn(true);
        
        assertThrows(DuplicateSerialNumberException.class, () -> service.create(validDto));
        
        verify(repository).existsBySerialNumber("ASM-2000-001");
        verify(repository, never()).save(any(Robot.class));
    }
    
    @Test
    void testCreateRobot_InvalidStatus() {
        validDto.setStatus("INVALID_STATUS");
        when(repository.existsBySerialNumber(anyString())).thenReturn(false);
        
        assertThrows(IllegalArgumentException.class, () -> service.create(validDto));
        
        verify(repository, never()).save(any(Robot.class));
    }
    
    @Test
    void testCreateRobot_InvalidDateFormat() {
        validDto.setManufacturingDate("2024/01/15");
        when(repository.existsBySerialNumber(anyString())).thenReturn(false);
        
        assertThrows(IllegalArgumentException.class, () -> service.create(validDto));
        
        verify(repository, never()).save(any(Robot.class));
    }
    
    @Test
    void testGetAll() {
        List<Robot> robots = Arrays.asList(savedRobot);
        when(repository.findAll()).thenReturn(robots);
        
        List<Robot> result = service.getAll();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findAll();
    }
    
    @Test
    void testGetById_Found() {
        when(repository.findById(1L)).thenReturn(Optional.of(savedRobot));
        
        Optional<Robot> result = service.getById(1L);
        
        assertTrue(result.isPresent());
        assertEquals("ASM-2000-001", result.get().getSerialNumber());
        verify(repository).findById(1L);
    }
    
    @Test
    void testGetById_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        
        Optional<Robot> result = service.getById(999L);
        
        assertFalse(result.isPresent());
        verify(repository).findById(999L);
    }
    
    @Test
    void testUpdate_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(savedRobot));
        when(repository.existsBySerialNumber(anyString())).thenReturn(false);
        when(repository.save(any(Robot.class))).thenReturn(savedRobot);
        
        validDto.setName("Updated Robot");
        Optional<Robot> result = service.update(1L, validDto);
        
        assertTrue(result.isPresent());
        verify(repository).findById(1L);
        verify(repository).save(any(Robot.class));
    }
    
    @Test
    void testUpdate_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        
        Optional<Robot> result = service.update(999L, validDto);
        
        assertFalse(result.isPresent());
        verify(repository).findById(999L);
        verify(repository, never()).save(any(Robot.class));
    }
    
    @Test
    void testDelete() {
        doNothing().when(repository).deleteById(1L);
        
        service.delete(1L);
        
        verify(repository).deleteById(1L);
    }
}
