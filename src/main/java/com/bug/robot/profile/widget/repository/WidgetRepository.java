package com.bug.robot.profile.widget.repository;

import com.bug.robot.profile.widget.domain.Widget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Widget entity.
 * Provides custom query methods with optimized fetching to prevent N+1 queries.
 */
@Repository
public interface WidgetRepository extends JpaRepository<Widget, Long> {

    /**
     * Find all widgets for a specific profile.
     * Uses JOIN FETCH to prevent N+1 query problem.
     */
    @Query("SELECT w FROM Widget w LEFT JOIN FETCH w.profile WHERE w.profile.id = :profileId")
    List<Widget> findByProfileId(@Param("profileId") Long profileId);

    /**
     * Find a specific widget by ID and profile ID.
     * Ensures widget ownership validation.
     */
    @Query("SELECT w FROM Widget w LEFT JOIN FETCH w.profile WHERE w.id = :widgetId AND w.profile.id = :profileId")
    Optional<Widget> findByIdAndProfileId(@Param("widgetId") Long widgetId, @Param("profileId") Long profileId);

    /**
     * Check if a widget exists for a specific profile.
     */
    boolean existsByIdAndProfileId(Long id, Long profileId);
}
