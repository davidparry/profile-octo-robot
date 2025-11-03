package com.bug.robot.robot.repository;

import com.bug.robot.robot.domain.Robot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RobotRepository extends JpaRepository<Robot, Long> {
    Optional<Robot> findBySerialNumber(String serialNumber);
    boolean existsBySerialNumber(String serialNumber);
}
