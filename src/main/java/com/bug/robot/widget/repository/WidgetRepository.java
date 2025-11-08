package com.bug.robot.widget.repository;

import com.bug.robot.widget.domain.Widget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Widget entity.
 * Follows Spring Data JPA best practices with custom query methods.
 */
@Repository
public interface WidgetRepository extends JpaRepository<Widget, Long> {

    /**
     * Find all widgets for a specific profile.
     * @param profileId the profile ID
     * @return list of widgets
     */
    List<Widget> findByProfileId(Long profileId);

    /**
     * Find a widget by ID and profile ID (for ownership verification).
     * @param id the widget ID
     * @param profileId the profile ID
     * @return optional widget
     */
    Optional<Widget> findByIdAndProfileId(Long id, Long profileId);

    /**
     * Check if a widget with the given name exists for a profile.
     * @param name the widget name
     * @param profileId the profile ID
     * @return true if exists, false otherwise
     */
    boolean existsByNameAndProfileId(String name, Long profileId);
}
