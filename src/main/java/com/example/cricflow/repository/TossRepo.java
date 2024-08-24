package com.example.cricflow.repository;

import com.example.cricflow.model.Toss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TossRepo extends JpaRepository<Toss, Long> {
}
