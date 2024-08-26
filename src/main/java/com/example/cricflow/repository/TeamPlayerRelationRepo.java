package com.example.cricflow.repository;

import com.example.cricflow.model.Player;
import com.example.cricflow.model.Team;
import com.example.cricflow.model.TeamPlayerRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamPlayerRelationRepo extends JpaRepository<TeamPlayerRelation, Long> {
    List<TeamPlayerRelation> findAllByTeam(Team team);
    List<TeamPlayerRelation> findAllByPlayer(Player player);
    TeamPlayerRelation findTopByTeamAndPlayerOrderByStartDateDesc(Team team, Player player);
}
