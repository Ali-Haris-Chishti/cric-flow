package com.example.cricflow.controller;

import com.example.cricflow.exception.common.EntityDoesNotExistsException;
import com.example.cricflow.exception.match.SameTeamInMatchException;
import com.example.cricflow.exception.match.TeamPlayerCountException;
import com.example.cricflow.exception.validator.MatchFieldsException;
import com.example.cricflow.exception.validator.TossFieldsException;
import com.example.cricflow.model.Toss;
import com.example.cricflow.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/match")
public class MatchController {

    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    public record MatchWrapper(
            long teamAId,
            long teamBId,
            long groundId,
            int noOfOvers,
            Toss toss
    ) {}

    public record MatchUpdateWrapper(
            long matchId,
            long groundId,
            int noOfOvers
    ) {}

    @PostMapping("/create")
    public ResponseEntity<?> createMatch(@RequestBody MatchWrapper wrapper) {
        try {
            return matchService.createMatch(wrapper);
        }
        catch (TeamPlayerCountException | SameTeamInMatchException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (EntityDoesNotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (TossFieldsException | MatchFieldsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/create-all")
    public ResponseEntity<?> createMultipleMatches(@RequestBody List<MatchWrapper> wrappers) {
        try {
            return matchService.createMultipleMatches(wrappers);
        }
        catch (TeamPlayerCountException | SameTeamInMatchException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (EntityDoesNotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (TossFieldsException | MatchFieldsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMatchById(@PathVariable long id) {
        try {
            return matchService.readMatch(id);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllMatches() {
        return matchService.readAllMatches();
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateMatch(@RequestBody MatchUpdateWrapper wrapper) {
        try {
            return matchService.updateMatch(wrapper);
        } catch (EntityDoesNotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (MatchFieldsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<?> deleteMatch(@PathVariable long id) {
        try {
            return matchService.deleteMatch(id);
        } catch (EntityDoesNotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("get/{id}/get-teams")
    ResponseEntity<?> getTeamPlayersWhoPlayedTheMatch(@PathVariable long id) {
        try {
            return matchService.getBothTeamsWithPlayersWhoPlayedTheMatch(id);
        } catch (EntityDoesNotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
