package com.example.cricflow.controller;

import com.example.cricflow.exception.common.EntityDoesNotExistsException;
import com.example.cricflow.service.OverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/over")
public class OverController {

    private final OverService overService;

    @Autowired
    public OverController(OverService overService) {
        this.overService = overService;
    }


    @GetMapping("/get{overId}")
    ResponseEntity<?> getOverById(@PathVariable long overId) {
        try {
            return overService.readOver(overId);
        } catch (EntityDoesNotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
        }
    }

    @GetMapping("/get-by-match/{matchId}")
    ResponseEntity<?> getAllOversOfMatch(@PathVariable long matchId) {
        try {
            return overService.readAllOversOfMatch(matchId);
        } catch (EntityDoesNotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
        }
    }

    @GetMapping("/get-by-inning/{inningId}")
    ResponseEntity<?> getAllOversByInning(@PathVariable long inningId) {
        try {
            return overService.readAllOversOfMatch(inningId);
        } catch (EntityDoesNotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
        }
    }

}
