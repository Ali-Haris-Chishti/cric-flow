package com.example.cricflow.service;

import com.example.cricflow.exception.common.EntityDoesNotExistsException;
import com.example.cricflow.exception.validator.PlayerFieldsException;
import com.example.cricflow.model.Player;
import com.example.cricflow.model.Team;
import com.example.cricflow.model.TeamPlayerRelation;
import com.example.cricflow.repository.PlayerRepo;
import com.example.cricflow.repository.TeamPlayerRelationRepo;
import com.example.cricflow.repository.TeamRepo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PlayerService {

    private final PlayerRepo playerRepo;
    private final TeamPlayerRelationRepo relationRepo;
    private final TeamRepo teamRepo;
    private final Validator validator;

    public static final String referencedClass = "PLAYER";

    @Autowired
    public PlayerService(PlayerRepo playerRepo, TeamPlayerRelationRepo relationRepo, TeamRepo teamRepo) {
        this.playerRepo = playerRepo;
        this.relationRepo = relationRepo;
        this.teamRepo = teamRepo;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public ResponseEntity<Player> createPlayer(Player player) throws PlayerFieldsException {
        validatePlayer(player);
        Player savedPlayer = playerRepo.save(player);
        return new ResponseEntity<Player>(savedPlayer, HttpStatus.CREATED);
    }

    public ResponseEntity<List<Player>> createMultiplePlayers(List<Player> players) throws PlayerFieldsException {
        for (Player player : players) {
            validatePlayer(player);
        }
        List<Player> savedPlayers = playerRepo.saveAll(players);
        return new ResponseEntity<List<Player>>(savedPlayers, HttpStatus.CREATED);
    }

    public ResponseEntity<Player> readPlayer(Long playerId) throws EntityDoesNotExistsException {
        Player player = findPlayerById(playerId);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    public ResponseEntity<List<Player>> readAllPlayers() {
        List<Player> allPlayers = playerRepo.findAll();
        return new ResponseEntity<>(allPlayers, HttpStatus.OK);
    }

    public ResponseEntity<Player> updatePlayer(Player player) throws EntityDoesNotExistsException, PlayerFieldsException{
        findPlayerById(player.getPlayerId());
        validatePlayer(player);
        Player savedPlayer = playerRepo.save(player);
        return new ResponseEntity<Player>(savedPlayer, HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<String> deletePlayer(Long playerId) throws EntityDoesNotExistsException{
        Player player = findPlayerById(playerId);
        relationRepo.deleteAllByPlayer(player);
        if (player.getTeam() != null) {
            player.getTeam().getPlayers().remove(player);
            teamRepo.save(player.getTeam());
        }
        playerRepo.deleteById(playerId);
        return new ResponseEntity<>("PLAYER WITH ID: " + playerId + ", DELETED SUCCESSFULLY!", HttpStatus.OK);
    }

    public ResponseEntity<String> deleteAllPlayers() {
        long count = playerRepo.count();
        playerRepo.deleteAll();
        List<Team> teams = teamRepo.findAll();
        for (Team team : teams) {
            team.setPlayers(null);
            teamRepo.save(team);
        }
        relationRepo.deleteAll();
        return new ResponseEntity<>("ALL " + count + " PLAYERS DELETED SUCCESSFULLY!", HttpStatus.OK);
    }

    public ResponseEntity<List<Player>> searchPlayersByName(String seq) {
        List<Player> players = playerRepo.findAllByFullNameContaining(seq);
        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    public ResponseEntity<List<TeamPlayerRelation>> getPlayerHistory(Long playerId) throws EntityDoesNotExistsException{
        Player player = findPlayerById(playerId);
        List<TeamPlayerRelation> relations = relationRepo.findAllByPlayer(player);
        return new ResponseEntity<>(relations, HttpStatus.OK);
    }

    public ResponseEntity<List<Player>> getAllPlayersNotInAnyTeam(){
        return new ResponseEntity<>(playerRepo.findAllByTeamIsNull(), HttpStatus.OK);
    }


    private void validatePlayer(Player player) throws PlayerFieldsException{
        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        if (!violations.isEmpty()){
            List<String> violationsString = new ArrayList<>();
            for (ConstraintViolation<Player> violation : violations) {
                violationsString.add(violation.getMessage());
            }
            throw new PlayerFieldsException(violationsString);
        }
    }

    private Player findPlayerById(Long playerId) throws EntityDoesNotExistsException{
        Optional<Player> optionalPlayer = playerRepo.findById(playerId);
        if (optionalPlayer.isPresent())
            return optionalPlayer.get();
        throw new EntityDoesNotExistsException(referencedClass, playerId);
    }


    public ResponseEntity<List<Player>> findFilteredPlayers(Player.PlayerType playerType, Player.BattingStyle battingStyle, Player.BowlingStyle bowlingStyle) {

        if (playerType != null) {
            if (battingStyle != null) {
                if (bowlingStyle != null)
                    return new ResponseEntity<>(playerRepo.findAllByPlayerTypeAndBattingStyleAndBowlingStyle(playerType, battingStyle, bowlingStyle), HttpStatus.OK);
                else
                    return new ResponseEntity<>(playerRepo.findAllByPlayerTypeAndBattingStyle(playerType, battingStyle), HttpStatus.OK);
            } else {
                if (bowlingStyle != null)
                    return new ResponseEntity<>(playerRepo.findAllByPlayerTypeAndBowlingStyle(playerType, bowlingStyle), HttpStatus.OK);
                else
                    return new ResponseEntity<>(playerRepo.findAllByPlayerType(playerType), HttpStatus.OK);
            }
        }
        else {
            if (battingStyle != null) {
                if (bowlingStyle != null)
                    return new ResponseEntity<>(playerRepo.findAllByBattingStyleAndBowlingStyle(battingStyle, bowlingStyle), HttpStatus.OK);
                else
                    return new ResponseEntity<>(playerRepo.findAllByBattingStyle(battingStyle), HttpStatus.OK);
            } else {
                if (bowlingStyle != null)
                    return new ResponseEntity<>(playerRepo.findAllByBowlingStyle(bowlingStyle), HttpStatus.OK);
                else
                    return new ResponseEntity<>(playerRepo.findAll(), HttpStatus.OK);
            }
        }
    }

    private Team checkIfTeamExists(Long teamId) throws EntityDoesNotExistsException{
        Optional<Team> team = teamRepo.findById(teamId);
        if (team.isEmpty())
            throw new EntityDoesNotExistsException(referencedClass, teamId);
        else
            return team.get();
    }

}
