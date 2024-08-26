package com.example.cricflow.controller;

import com.example.cricflow.exception.EntityDoesNotExistsException;
import com.example.cricflow.exception.NameAlreadyExistsException;
import com.example.cricflow.exception.validator.GroundFieldsException;
import com.example.cricflow.model.Ground;
import com.example.cricflow.service.GroundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ground")
public class GroundController {

    private final GroundService groundService;

    @Autowired
    public GroundController(GroundService groundService) {
        this.groundService = groundService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGround(@RequestParam String groundName){
        try {
            return groundService.createGround(groundName);
        }
        catch (NameAlreadyExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        catch (GroundFieldsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/create-all")
    public ResponseEntity<?> createMultipleGrounds(@RequestBody List<String> groundNames){
        try {
            return groundService.createMultipleGrounds(groundNames);
        }
        catch (NameAlreadyExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        catch (GroundFieldsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getGroundById(@PathVariable Long id){
        try {
            return groundService.readGround(id);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllGrounds(){
        return groundService.readAllGrounds();
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateGround(@RequestBody Ground ground){
        try {
            return groundService.updateGround(ground);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (NameAlreadyExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        catch (GroundFieldsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGroundWithGivenId(@PathVariable Long id){
        try {
            return groundService.deleteGround(id);
        }
        catch (EntityDoesNotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<?> deleteAllGrounds(){
        return groundService.deleteAllGrounds();
    }
}
