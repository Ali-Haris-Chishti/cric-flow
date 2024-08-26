package com.example.cricflow.controller;

import com.example.cricflow.exception.DuplicatePlayerInTeamException;
import com.example.cricflow.exception.EntityDoesNotExistsException;
import com.example.cricflow.exception.NameAlreadyExistsException;
import com.example.cricflow.exception.PlayerRemovalFromTeamException;
import com.example.cricflow.model.Team;
import com.example.cricflow.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/team")
public class TeamController {

    @Autowired private TeamService teamService;

    @PostMapping("/create")
    ResponseEntity<Team> createTeam(@RequestParam String teamName){
        try {
            return teamService.createTeam(teamName);
        }
        catch (NameAlreadyExistsException e){
            return new ResponseEntity<>(new Team(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/create-all")
    ResponseEntity<List<Team>> createMultipleTeam(@RequestBody List<String> teamNames){
        try {
            return teamService.createMultipleTeams(teamNames);
        }
        catch (NameAlreadyExistsException e){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/add-players")
    ResponseEntity<Team> addPlayersToTeam(@RequestParam Long teamId, @RequestBody List<Long> playerIds){
        try {
            return teamService.addMultiplePlayersToTeam(teamId, playerIds);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(new Team(), HttpStatus.NOT_FOUND);
        }
        catch (DuplicatePlayerInTeamException e){
            return new ResponseEntity<>(new Team(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/remove-players")
    ResponseEntity<Team> removePlayersFromTeam(@RequestParam Long teamId, @RequestBody List<Long> playerIds){
        try {
            return teamService.removeMultiplePlayersFromTeam(teamId, playerIds);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(new Team(), HttpStatus.NOT_FOUND);
        }
        catch (PlayerRemovalFromTeamException e){
            return new ResponseEntity<>(new Team(), HttpStatus.BAD_REQUEST);
        }
    }

}
