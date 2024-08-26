package com.example.cricflow.controller;

import com.example.cricflow.exception.EntityDoesNotExistsException;
import com.example.cricflow.exception.validator.PlayerFieldsException;
import com.example.cricflow.model.Player;
import com.example.cricflow.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/player")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/create")
    ResponseEntity<?> createPlayer(@RequestBody Player player) {
        try {
            return playerService.createPlayer(player);
        }
        catch (PlayerFieldsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/create-all")
    ResponseEntity<?> createMultiplePlayer(@RequestBody List<Player> players) {
        try {
            System.out.println(players);
            return playerService.createMultiplePlayers(players);
        }
        catch (PlayerFieldsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/get/{id}")
    ResponseEntity<?> getPlayerWithId(@PathVariable Long id) {
        try {
            return playerService.readPlayer(id);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-all")
    ResponseEntity<?> getAllPlayers(
            @RequestParam(required = false) Long teamId
    ) {
        try {
            if (teamId != null)
                return playerService.findAllByTeam(teamId);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return playerService.readAllPlayers();
    }

    @PutMapping("/update")
    ResponseEntity<?> updatePlayer(@RequestBody Player player) {
        try {
            return playerService.updatePlayer(player);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (PlayerFieldsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<?> deletePlayer(@PathVariable Long id) {
        try {
            return playerService.deletePlayer(id);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete-all")
    ResponseEntity<?> deleteAllPlayers(){
        return playerService.deleteAllPlayers();
    }

    @GetMapping("/get-record/{id}")
    ResponseEntity<?> getPlayerRecordWithId(@PathVariable Long id) {
        try {
            return playerService.getPlayerHistory(id);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
