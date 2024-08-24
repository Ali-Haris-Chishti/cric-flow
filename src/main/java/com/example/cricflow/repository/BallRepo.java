package com.example.cricflow.repository;

import com.example.cricflow.model.Ball;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BallRepo extends JpaRepository<Ball, Long> {
}
