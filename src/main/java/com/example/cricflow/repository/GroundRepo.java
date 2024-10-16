package com.example.cricflow.repository;

import com.example.cricflow.model.Ground;
import com.example.cricflow.model.Player;
import com.example.cricflow.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroundRepo extends JpaRepository<Ground, Long> {
    Optional<Ground> findByGroundNameIgnoreCase(String groundName);

    @Query("SELECT g FROM Ground g WHERE LOWER(g.groundName) LIKE LOWER(CONCAT('%', :nameSequence, '%'))")
    List<Ground> findAllByFullNameContaining(@Param("nameSequence") String nameSequence);
}
