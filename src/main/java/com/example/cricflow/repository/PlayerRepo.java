package com.example.cricflow.repository;

import com.example.cricflow.model.Player;
import com.example.cricflow.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepo extends JpaRepository<Player, Long> {
    List<Player> findAllByTeam(Team team);
}
