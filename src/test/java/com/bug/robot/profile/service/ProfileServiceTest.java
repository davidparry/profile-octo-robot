package com.bug.robot.profile.service;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.dto.ProfileRequestDTO;
import com.bug.robot.profile.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProfileServiceTest {

    @Mock
    private ProfileRepository repository;

    @InjectMocks
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProfileWithYyMmDdDateFormat() {
        // Given
        ProfileRequestDTO dto = new ProfileRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setBirthDate("25-10-17"); // yy-MM-dd format

        Profile savedProfile = new Profile();
        savedProfile.setFirstName("John");
        savedProfile.setLastName("Doe");
        savedProfile.setEmail("john.doe@example.com");
        savedProfile.setBirthDate(LocalDate.of(2025, 10, 17));

        when(repository.save(any(Profile.class))).thenReturn(savedProfile);

        // When
        Profile result = profileService.create(dto);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals(LocalDate.of(2025, 10, 17), result.getBirthDate());
    }

    @Test
    void testCreateProfileWithInvalidDateFormat() {
        // Given
        ProfileRequestDTO dto = new ProfileRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setBirthDate("2025-10-17"); // Invalid format for our custom parser

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> profileService.create(dto)
        );

        assertTrue(exception.getMessage().contains("Invalid birth date format"));
        assertTrue(exception.getMessage().contains("Expected format: YY-MM-DD"));
    }

    @Test
    void testCreateProfileWithAnotherValidYyMmDdDate() {
        // Given
        ProfileRequestDTO dto = new ProfileRequestDTO();
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setEmail("jane.smith@example.com");
        dto.setBirthDate("99-12-31"); // yy-MM-dd format for year 1999

        Profile savedProfile = new Profile();
        savedProfile.setFirstName("Jane");
        savedProfile.setLastName("Smith");
        savedProfile.setEmail("jane.smith@example.com");
        savedProfile.setBirthDate(LocalDate.of(2099, 12, 31)); // Note: 2-digit year parsing defaults to 20xx

        when(repository.save(any(Profile.class))).thenReturn(savedProfile);

        // When
        Profile result = profileService.create(dto);

        // Then
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("jane.smith@example.com", result.getEmail());
        assertEquals(LocalDate.of(2099, 12, 31), result.getBirthDate());
    }
}
