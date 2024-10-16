package com.example.cricflow.controller;

import com.example.cricflow.exception.inning.BatsmanBowlerInitializationException;
import com.example.cricflow.exception.common.EntityDoesNotExistsException;
import com.example.cricflow.exception.inning.InningInitializationException;
import com.example.cricflow.exception.team.PlayerNotInTeamException;
import com.example.cricflow.model.BallEvent;
import com.example.cricflow.service.InningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("api/v1/inning")
public class InningController {

    private final InningService inningService;

    @Autowired
    public InningController(InningService inningService) {
        this.inningService = inningService;
    }

    @GetMapping("/get/{id}")
    ResponseEntity<?> getInningById(@PathVariable long id) {
        try {
            return inningService.readInning(id);
        } catch (EntityDoesNotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
        }
    }

    @GetMapping("/get-all")
    ResponseEntity<?> getAllInnings(@RequestParam(required = false) Long matchId) {
        try {
            if (matchId != null)
                return inningService.getInningsOfAMatch(matchId);
            return inningService.readAllInnings();
        } catch (EntityDoesNotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
        }
    }

    public record BatsmanBowlerInitializr (
            long strikerId,
            long nonStrikerId,
            long bowlerId
    ) { }


    @PutMapping("/start")
    ResponseEntity<?> startInnings(@RequestParam long matchId, @RequestBody BatsmanBowlerInitializr initializr){
        try {
            return inningService.initializeInning(matchId, initializr);
        }
        catch (EntityDoesNotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
        }
        catch (BatsmanBowlerInitializationException | PlayerNotInTeamException e){
            return new ResponseEntity<>(e.getMessage(), CONFLICT);
        }
        catch (InningInitializationException e){
            return new ResponseEntity<>(e.getMessage(), NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/start-over/{matchId}")
    ResponseEntity<?> startOverWithBowler(@PathVariable long matchId, @RequestParam long bowlerId){
        try {
            return inningService.addBowlerForOver(matchId, bowlerId);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
        }
    }

    @PostMapping("add-ball")
    ResponseEntity<?> addBall(@RequestParam long matchId, @RequestBody BallEvent event){
            return inningService.addBall(matchId, event);
    }

    @PostMapping("add-balls")
    ResponseEntity<?> addMultipleBallsOfOver(@RequestParam long matchId, @RequestBody BallEvent [] events){
        return inningService.addMultipleBalls(matchId, events);
    }

    @GetMapping("/{id}/get-stats")
    ResponseEntity<?> getInningsStats(@PathVariable long id) {
        try {
            return inningService.getInningsStats(id);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
        }
    }
}
