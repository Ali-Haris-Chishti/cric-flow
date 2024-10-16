package com.example.cricflow.controller;

import com.example.cricflow.exception.common.EntityDoesNotExistsException;
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
            @RequestParam(required = false) Player.PlayerType playerType,
            @RequestParam(required = false) Player.BattingStyle battingStyle,
            @RequestParam(required = false) Player.BowlingStyle bowlingStyle
    )
    {
        return playerService.findFilteredPlayers(playerType, battingStyle, bowlingStyle);
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

    @GetMapping("/search")
    ResponseEntity<?> searchPlayerByName(@RequestParam String seq){
        return playerService.searchPlayersByName(seq);
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
