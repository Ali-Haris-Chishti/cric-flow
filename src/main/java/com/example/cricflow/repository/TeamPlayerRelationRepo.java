package com.example.cricflow.repository;

import com.example.cricflow.model.Player;
import com.example.cricflow.model.Team;
import com.example.cricflow.model.TeamPlayerRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TeamPlayerRelationRepo extends JpaRepository<TeamPlayerRelation, Long> {
    List<TeamPlayerRelation> findAllByTeam(Team team);
    List<TeamPlayerRelation> findAllByPlayer(Player player);
    TeamPlayerRelation findTopByTeamAndPlayerOrderByStartDateDesc(Team team, Player player);
    void deleteAllByPlayer(Player player);
    void deleteAllByTeam(Team team);

    @Query("SELECT tpr.player FROM TeamPlayerRelation tpr " +
            "WHERE tpr.team = :team " +
            "AND :matchDate <= current date " +
            "AND tpr.startDate <= :matchDate " +
            "AND (tpr.endDate IS NULL OR tpr.endDate >= :matchDate)")
    List<Player> findAllPlayersWhoPlayedTheMatch(Team team, LocalDate matchDate);
}
