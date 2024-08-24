package com.example.cricflow.repository;

import com.example.cricflow.model.BallEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepo extends JpaRepository<BallEvent, Long> {
}
