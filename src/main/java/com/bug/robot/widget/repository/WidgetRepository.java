package com.bug.robot.widget.repository;

import com.bug.robot.widget.domain.Widget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WidgetRepository extends JpaRepository<Widget, Long> {
    
    List<Widget> findByUserId(Long userId);
    
    Optional<Widget> findByIdAndUserId(Long id, Long userId);
}
