package com.example.cricflow.repository;

import com.example.cricflow.model.Over;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OverRepo extends JpaRepository<Over, Long> {
}
