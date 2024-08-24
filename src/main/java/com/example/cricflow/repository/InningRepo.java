package com.example.cricflow.repository;

import com.example.cricflow.model.Inning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InningRepo extends JpaRepository<Inning, Long> {
}
