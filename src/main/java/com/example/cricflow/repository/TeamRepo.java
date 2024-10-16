package com.example.cricflow.repository;

import com.example.cricflow.model.Player;
import com.example.cricflow.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepo extends JpaRepository<Team, Long> {

    Optional<Team> findByTeamNameIgnoreCase(String teamName);

    @Query("SELECT t FROM Team t WHERE LOWER(t.teamName) LIKE LOWER(CONCAT('%', :nameSequence, '%'))")
    List<Team> findAllByFullNameContaining(@Param("nameSequence") String nameSequence);
}
