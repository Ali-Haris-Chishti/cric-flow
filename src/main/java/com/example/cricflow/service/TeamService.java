package com.example.cricflow.service;

import com.example.cricflow.exception.DuplicatePlayerInTeamException;
import com.example.cricflow.exception.EntityDoesNotExistsException;
import com.example.cricflow.exception.NameAlreadyExistsException;
import com.example.cricflow.exception.PlayerRemovalFromTeamException;
import com.example.cricflow.exception.validator.TeamFieldsException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TeamService {

    private final TeamRepo teamRepo;
    private final PlayerRepo playerRepo;
    private final TeamPlayerRelationRepo relationRepo;
    private final Validator validator;

    private final String referencedClass = "TEAM";

    public TeamService(TeamRepo teamRepo, PlayerRepo playerRepo, TeamPlayerRelationRepo relationRepo) {
        this.teamRepo = teamRepo;
        this.playerRepo = playerRepo;
        this.relationRepo = relationRepo;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public ResponseEntity<Team> createTeam(String teamName) {
        Optional<Team> sameNameTeam = teamRepo.findByTeamName(teamName.toUpperCase());
        if (sameNameTeam.isPresent()) {
            throw new NameAlreadyExistsException(referencedClass, teamName.toUpperCase());
        }
        Team savedTeam = teamRepo.save(Team.builder().teamName(teamName.toUpperCase()).build());
        return new ResponseEntity<>(savedTeam, HttpStatus.CREATED);
    }

    public ResponseEntity<List<Team>> createMultipleTeams(List<String> teamNames) {
        List<Team> savedTeams = new ArrayList<>();
        // if any team has duplicate name, exception will be thrown, without saving any team
        for (String teamName : teamNames) {
            savedTeams.add(createTeam(teamName).getBody());
        }
        // if all are unique, all will be saved
        return new ResponseEntity<>(savedTeams, HttpStatus.CREATED);
    }


    private Player addSinglePlayerToTeam(Team team, Long playerId) {
        // existence of players has been checked before, so no need for checking .ifPresent()
        Player player = playerRepo.findById(playerId).get();

        player.setTeam(team);
        player = playerRepo.save(player);

        // updating team history for the player
        TeamPlayerRelation relation =
                TeamPlayerRelation.builder()
                .team(team)
                .player(player)
                .startDate(LocalDate.now())
                .build();
        relationRepo.save(relation);

        return player;
    }

    public ResponseEntity<Team> addMultiplePlayersToTeam(Long teamId, List<Long> playerIds) {
        // if team does not exist, exception will be thrown
        Team team = checkIfTeamExists(teamId);

        // list for storing all the previous player id's of the team, for checks
        List<Long> previousPlayerIds = new ArrayList<>();
        for (Player player: team.getPlayers())
            previousPlayerIds.add(player.getPlayerId());

        // checking if all the players to be added, exists, if not exception is thrown
        checkPlayerListExists(playerIds, previousPlayerIds, team, true);
        // after confirming, all the players exist, adding them to the team
        List<Player> addedPlayers = new ArrayList<>();
        for (Long playerId : playerIds) {
            addedPlayers.add(addSinglePlayerToTeam(team, playerId));
        }
        // saving in the new list, because the one retrieved is immutable
        List<Player> allPlayers = new ArrayList<>();
        // adding all the previous players in the new empty list
        allPlayers.addAll(team.getPlayers());
        allPlayers.addAll(addedPlayers);
        //updating team with all players, already present and new ones
        team.setPlayers(allPlayers);

        return new ResponseEntity<>(teamRepo.save(team), HttpStatus.OK);
    }

    private void removeSinglePlayerFromTeam(Team team, Long playerId) {
        // existence of players has been checked before, so no need for checking .ifPresent()
        Player player = playerRepo.findById(playerId).get();

        // using in the new list, because the one retrieved is immutable
        List<Player> remainingPlayers = new ArrayList<>();
        remainingPlayers = new ArrayList<>(team.getPlayers());
        remainingPlayers.remove(player);
        // updating the team after removing that player
        team.setPlayers(remainingPlayers);
        teamRepo.save(team);
        // the player has currently no team assigned
        player.setTeam(null);
        playerRepo.save(player);

        updateTeamPlayerRelationAfterPlayerRemoval(team, player);
    }

    public ResponseEntity<Team> removeMultiplePlayersFromTeam(Long teamId, List<Long> playerIds) {
        // if team does not exist, exception will be thrown
        Team team = checkIfTeamExists(teamId);

        // list for storing ID's of all previously assigned players of the team, for checks
        List<Long> previousPlayerIds = new ArrayList<>();
        for (Player player: team.getPlayers())
            previousPlayerIds.add(player.getPlayerId());

        // checking if all the players already exist
        checkPlayerListExists(playerIds, previousPlayerIds, team, false);

        // once confirmed the existence, removing all from the team
        for (Long playerId : playerIds) {
            removeSinglePlayerFromTeam(team, playerId);
        }

        // returning the saved team
        return new ResponseEntity<>(team, HttpStatus.OK);
    }



    private void checkPlayerListExists(List<Long> newPlayerIds, List<Long> prevPlayerIds, Team team, boolean add) {
        for (Long playerId : newPlayerIds) {
            Optional<Player> player = playerRepo.findById(playerId);
            // if player with given id does not exist, throw exception
            if (player.isEmpty())
                throw new EntityDoesNotExistsException(referencedClass, playerId);
            // if function is for adding, then we check that if player already exists in the team, then throw exception
            // if function is for removing, then we check that if player does not exist in the team, then throw exception
            if (add)
                checkIfPlayerExistsInTheSameTeamWhereItIsToBeAdded(prevPlayerIds, playerId, team);
            else
                checkIfPlayerRequestedToBeRemovedFromTeamIsActuallyPresent(prevPlayerIds, playerId, team);

        }
        // if it is for adding, then before adding to new team, we need to remove from previous team, if it has any
        if (add)
            removePlayerFromPreviousTeamIfBeingAddedToNewTeam(newPlayerIds, team);
    }

    private void checkIfPlayerExistsInTheSameTeamWhereItIsToBeAdded(List<Long> prevPlayerIds, Long playerId, Team team){
        // if the player we need to add in a team, is already present in the same team, then there is no sense in adding it
        if (prevPlayerIds.contains(playerId))
            throw new DuplicatePlayerInTeamException(playerId, team.getTeamId());
    }

    private void checkIfPlayerRequestedToBeRemovedFromTeamIsActuallyPresent(List<Long> prevPlayerIds, Long playerId, Team team){
        // if the player, we are going to remove from a team, not present in it, then there is no sense in it
        if (!prevPlayerIds.contains(playerId))
            throw new PlayerRemovalFromTeamException(team.getTeamId(), playerId);
    }

    private void removePlayerFromPreviousTeamIfBeingAddedToNewTeam(List<Long> newPlayerIds, Team team){
        for (Long playerId : newPlayerIds) {
            // players existence has been checked already, so no need to check again
            Player player = playerRepo.findById(playerId).get();

            // if players, previous team was not null, then removing player first from the previous team
            if (player.getTeam() != null && player.getTeam() != team){
                Team oldTeam = player.getTeam();
                oldTeam.getPlayers().remove(player);
                player.setTeam(null);
                playerRepo.save(player);
                teamRepo.save(oldTeam);
                updateTeamPlayerRelationAfterPlayerRemoval(oldTeam, player);
            }
        }
    }

    private void validateTeam(Team team){
        Set<ConstraintViolation<Team>> violations = validator.validate(team);
        List<String> violationsString = new ArrayList<>();
        if (!violations.isEmpty()){
            for (ConstraintViolation<Team> violation : violations){
                violationsString.add(violation.getMessage());
            }
        }
        throw new TeamFieldsException(violationsString);
    }

    private Team checkIfTeamExists(Long teamId) {
        Optional<Team> team = teamRepo.findById(teamId);
        if (team.isEmpty())
            throw new EntityDoesNotExistsException(referencedClass, teamId);
        else
            return team.get();
    }

    private void updateTeamPlayerRelationAfterPlayerRemoval(Team oldTeam, Player player ){
        // once a player is removed from a team, updating the relation table by adding the end date
        TeamPlayerRelation relation = relationRepo.findTopByTeamAndPlayerOrderByStartDateDesc(oldTeam, player);
        relation.setEndDate(LocalDate.now());
        relationRepo.save(relation);
    }

}
