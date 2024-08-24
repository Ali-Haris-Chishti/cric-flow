package com.example.cricflow.service;

import com.example.cricflow.exception.CascadeFailureException;
import com.example.cricflow.exception.TeamsAddedToPlayerException;
import com.example.cricflow.model.Player;
import com.example.cricflow.model.Team;
import com.example.cricflow.repository.PlayerRepo;
import com.example.cricflow.repository.TeamRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    @Autowired private PlayerRepo playerRepo;
    @Autowired private TeamRepo teamRepo;

    public ResponseEntity<Player> createPlayer(Player player) {
        Player savedPlayer = playerRepo.save(player);
        return new ResponseEntity<Player>(savedPlayer, HttpStatus.CREATED);
    }

    public ResponseEntity<Player> readPlayer(Long playerId) {
        Optional<Player> player = playerRepo.findById(playerId);
        if (player.isPresent())
            return new ResponseEntity<>(player.get(), HttpStatus.OK);
        else
            throw new EntityNotFoundException();
    }

    public ResponseEntity<Player> updatePlayer(Player player) {
        if (playerRepo.findById(player.getPlayerId()).isEmpty())
            throw new EntityNotFoundException();
        Player savedPlayer = playerRepo.save(player);
        return new ResponseEntity<Player>(savedPlayer, HttpStatus.CREATED);
    }

    public ResponseEntity<Player> deletePlayer(Long playerId) {
        Optional<Player> player = playerRepo.findById(playerId);
        if (player.isPresent()) {
            playerRepo.deleteById(playerId);
            return new ResponseEntity<>((Player) null, HttpStatus.OK);
        }
        else
            throw new EntityNotFoundException();
    }
}
