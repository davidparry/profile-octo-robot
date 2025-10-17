package com.bug.robot.profile.service;

import com.bug.robot.profile.domain.Profile;
import com.bug.robot.profile.dto.ProfileRequestDTO;
import com.bug.robot.profile.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository repository;
    
    // Custom formatter for 'yy-MM-dd' format (e.g., '25-10-17')
    private static final DateTimeFormatter CUSTOM_DATE_FORMATTER = DateTimeFormatter.ofPattern("yy-MM-dd");

    public ProfileService(ProfileRepository repository) {
        this.repository = repository;
    }

    public List<Profile> getAll() {
        return repository.findAll();
    }

    public Optional<Profile> getById(Long id) {
        return repository.findById(id);
    }

    public Profile create(ProfileRequestDTO dto) {
        Profile profile = new Profile();
        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setEmail(dto.getEmail());
        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setAddress(dto.getAddress());
        profile.setBio(dto.getBio());
        
        // Parse date with custom formatter for 'yy-MM-dd' format
        try {
            LocalDate birthDate = LocalDate.parse(dto.getBirthDate(), CUSTOM_DATE_FORMATTER);
            profile.setBirthDate(birthDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                String.format("Invalid birth date format: '%s'. Expected format: YY-MM-DD (e.g., 25-10-17)", 
                dto.getBirthDate()), e);
        }
        
        return repository.save(profile);
    }

    public Optional<Profile> update(Long id, ProfileRequestDTO dto) {
        return repository.findById(id).map(existing -> {
            existing.setFirstName(dto.getFirstName());
            existing.setLastName(dto.getLastName());
            existing.setEmail(dto.getEmail());
            existing.setPhoneNumber(dto.getPhoneNumber());
            existing.setAddress(dto.getAddress());
            existing.setBio(dto.getBio());
            
            // Parse date with custom formatter for 'yy-MM-dd' format
            try {
                LocalDate birthDate = LocalDate.parse(dto.getBirthDate(), CUSTOM_DATE_FORMATTER);
                existing.setBirthDate(birthDate);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(
                    String.format("Invalid birth date format: '%s'. Expected format: YY-MM-DD (e.g., 25-10-17)", 
                    dto.getBirthDate()), e);
            }
            
            return repository.save(existing);
        });
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}