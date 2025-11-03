package com.bug.robot.widget.repository;

import com.bug.robot.widget.domain.Widget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WidgetRepository extends JpaRepository<Widget, Long> {
    Optional<Widget> findByName(String name);
    List<Widget> findByType(String type);
    List<Widget> findByStatus(String status);
}
