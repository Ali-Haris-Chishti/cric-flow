package com.example.cricflow.repository;

import com.example.cricflow.model.Match;
import com.example.cricflow.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepo extends JpaRepository<Match, Long> {

    @Query("SELECT m FROM Match m WHERE m.teamA = :team OR m.teamB = :team")
    List<Match> findAllMatchesPlayedByTeam(Team team);

    @Query("SELECT m FROM Match m WHERE (m.teamA = :team AND m.winner = com.example.cricflow.model.literal.TeamSide.SIDE_A) OR (m.teamB = :team AND m.winner = com.example.cricflow.model.literal.TeamSide.SIDE_B)")
    List<Match> findAllMatchesWonByTeam(Team team);

    @Query("SELECT m FROM Match m WHERE (m.teamB = :team AND m.winner = com.example.cricflow.model.literal.TeamSide.SIDE_A) OR (m.teamA = :team AND m.winner = com.example.cricflow.model.literal.TeamSide.SIDE_B)")
    List<Match> findAllMatchesLostByTeam(Team team);

}
