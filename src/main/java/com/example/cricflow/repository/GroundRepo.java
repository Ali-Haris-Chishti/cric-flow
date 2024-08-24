package com.example.cricflow.repository;

import com.example.cricflow.model.Ground;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroundRepo extends JpaRepository<Ground, Long> {
    Optional<Ground> findByGroundName(String groundName);
}
