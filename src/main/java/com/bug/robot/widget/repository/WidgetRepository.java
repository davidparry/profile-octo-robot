package com.bug.robot.widget.repository;

import com.bug.robot.widget.domain.Widget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WidgetRepository extends JpaRepository<Widget, Long> {
    
    List<Widget> findByProfileId(Long profileId);
    
    Optional<Widget> findByIdAndProfileId(Long id, Long profileId);
    
    boolean existsByProfileIdAndName(Long profileId, String name);
}
