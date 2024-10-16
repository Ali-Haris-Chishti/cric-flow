package com.example.cricflow.controller;

import com.example.cricflow.exception.common.EntityDoesNotExistsException;
import com.example.cricflow.exception.common.NameAlreadyExistsException;
import com.example.cricflow.exception.common.ReferentialConstraintException;
import com.example.cricflow.exception.team.DuplicatePlayerInTeamException;
import com.example.cricflow.exception.team.PlayerRemovalFromTeamException;
import com.example.cricflow.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/team")
public class TeamController {

    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/create")
    ResponseEntity<?> createTeam(@RequestParam String teamName){
        try {
            return teamService.createTeam(teamName);
        }
        catch (NameAlreadyExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/create-all")
    ResponseEntity<?> createMultipleTeam(@RequestBody List<String> teamNames){
        try {
            return teamService.createMultipleTeams(teamNames);
        }
        catch (NameAlreadyExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/get/id/{id}")
    ResponseEntity<?> getTeamById(@PathVariable long id){
        try {
            return teamService.readTeam(id);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get/name/{name}")
    ResponseEntity<?> getTeamByName(@PathVariable String name){
        try {
            return teamService.readTeam(name);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-all")
    ResponseEntity<?> getAllTeams(){
        return teamService.readAllTeams();
    }

    @PutMapping("/update/{id}")
    ResponseEntity<?> updateTeam(@PathVariable long id, @RequestParam String newName){
        try {
            return teamService.updateTeamName(newName, id);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (NameAlreadyExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("delete/{id}")
    ResponseEntity<?> deleteTeam(@PathVariable long id){
        try {
            return teamService.deleteTeam(id);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ReferentialConstraintException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("delete-all")
    ResponseEntity<?> deleteAllTeams(){
        return teamService.deleteAllTeams();
    }

    @PutMapping("/add-players")
    ResponseEntity<?> addPlayersToTeam(@RequestParam Long teamId, @RequestBody List<Long> playerIds){
        try {
            return teamService.addMultiplePlayersToTeam(teamId, playerIds);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (DuplicatePlayerInTeamException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/remove-players")
    ResponseEntity<?> removePlayersFromTeam(@RequestParam Long teamId, @RequestBody List<Long> playerIds){
        try {
            return teamService.removeMultiplePlayersFromTeam(teamId, playerIds);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (PlayerRemovalFromTeamException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    ResponseEntity<?> searchTeam(@RequestParam String seq){
        return teamService.searchTeamsByName(seq);
    }
}
