package com.example.cricflow.service;

import com.example.cricflow.exception.CascadeFailureException;
import com.example.cricflow.model.Player;
import com.example.cricflow.model.Team;
import com.example.cricflow.repository.PlayerRepo;
import com.example.cricflow.repository.TeamRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    @Autowired private TeamRepo teamRepo;
    @Autowired private PlayerRepo playerRepo;

    public ResponseEntity<Team> createTeam(Team team, boolean cascade) {
        if (team.getPlayers() != null){
            for (Player player : team.getPlayers()) {
                if (player.getPlayerId() == null || playerRepo.findById(player.getPlayerId()).isEmpty())
                    createPlayerForTeam(player, cascade);
            }
        }
        Team savedTeam = teamRepo.save(team);
        return new ResponseEntity<>(savedTeam, HttpStatus.CREATED);
    }

    private void createPlayerForTeam(Player player, boolean cascade) {
        if (cascade)
            playerRepo.save(player);
        else
            throw new CascadeFailureException("TEAM", "PLAYER", "ADD");
    }



}
