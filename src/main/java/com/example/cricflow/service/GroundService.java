package com.example.cricflow.service;

import com.example.cricflow.exception.EntityDoesNotExists;
import com.example.cricflow.exception.GroundNameAlreadyExistsException;
import com.example.cricflow.model.Ground;
import com.example.cricflow.repository.GroundRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GroundService {

    @Autowired private GroundRepo groundRepo;

    public ResponseEntity<Ground> createGround(Ground ground) {
        if (groundRepo.findByGroundName(ground.getGroundName()).isPresent()) {
            throw new GroundNameAlreadyExistsException(ground.getGroundName());
        }
        Ground savedGround = groundRepo.save(ground);
        return new ResponseEntity<Ground>(savedGround, HttpStatus.CREATED);
    }

    public ResponseEntity<Ground> readGround(Long groundId) {
        Optional<Ground> optionalGround = groundRepo.findById(groundId);
        if (optionalGround.isEmpty()) {
            throw new EntityDoesNotExists(groundId);
        }
        return new ResponseEntity<Ground>(optionalGround.get(), HttpStatus.OK);
    }

    public ResponseEntity<Ground> updateGround(Ground ground) {
        if (groundRepo.findById(ground.getGroundId()).isEmpty()) {
            throw new EntityDoesNotExists(ground.getGroundId());
        }
        Ground savedGround = groundRepo.save(ground);
        return new ResponseEntity<Ground>(savedGround, HttpStatus.CREATED);
    }

    public ResponseEntity<Ground> deleteGround(Ground ground) {
        if (groundRepo.findById(ground.getGroundId()).isEmpty()) {
            throw new EntityDoesNotExists(ground.getGroundId());
        }
        groundRepo.delete(ground);
        return new ResponseEntity<Ground>((Ground) null, HttpStatus.OK);
    }
}
