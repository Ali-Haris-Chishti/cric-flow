package com.example.cricflow.service;

import com.example.cricflow.exception.EntityDoesNotExistsException;
import com.example.cricflow.exception.NameAlreadyExistsException;
import com.example.cricflow.exception.validator.GroundFieldsException;
import com.example.cricflow.model.Ground;
import com.example.cricflow.repository.GroundRepo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class GroundService {

    private final GroundRepo groundRepo;
    private final Validator validator;
    
    private final String referencedClass = "GROUND";
    
    public GroundService(GroundRepo groundRepo) {
        this.groundRepo = groundRepo;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public ResponseEntity<Ground> createGround(String groundName) throws NameAlreadyExistsException, GroundFieldsException {
        checkIfGroundNameAlreadyExists(groundName);
        Ground savedGround = validateGround(new Ground(null, groundName.toUpperCase()), true);
        return new ResponseEntity<Ground>(savedGround, HttpStatus.CREATED);
    }

    public ResponseEntity<List<Ground>> createMultipleGrounds(List<String> groundNames) throws NameAlreadyExistsException, GroundFieldsException {
        checkNameEquals(groundNames);
        List<Ground> savedGrounds = new ArrayList<>();
        for (String name : groundNames) {
            checkIfGroundNameAlreadyExists(name);
            validateGround(new Ground(null, name.toUpperCase()), false);
        }
        for (String name : groundNames)
            savedGrounds.add(groundRepo.save(new Ground(null, name.toUpperCase())));
        return new ResponseEntity<>(savedGrounds, HttpStatus.CREATED);
    }

    public ResponseEntity<Ground> readGround(Long groundId) throws EntityDoesNotExistsException {
        Ground ground = findGroundById(groundId);
        return new ResponseEntity<Ground>(ground, HttpStatus.OK);
    }

    public ResponseEntity<List<Ground>> readAllGrounds(){
        return new ResponseEntity<>(groundRepo.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Ground> updateGround(Ground ground) throws EntityDoesNotExistsException, GroundFieldsException {
        checkIfGroundNameAlreadyExists(ground.getGroundName());
        findGroundById(ground.getGroundId());
        Ground savedGround = validateGround(ground, true);
        return new ResponseEntity<Ground>(savedGround, HttpStatus.CREATED);
    }

    public ResponseEntity<String> deleteGround(Long groundId) throws EntityDoesNotExistsException{
        Ground ground = findGroundById(groundId);
        groundRepo.delete(ground);
        return new ResponseEntity<>("GROUND WITH ID: " + groundId + ", DELETED SUCCESSFULLY!", HttpStatus.OK);
    }

    public ResponseEntity<String> deleteAllGrounds(){
        long count = groundRepo.count();
        groundRepo.deleteAll();
        return new ResponseEntity<>("ALL " + count + " GROUNDS DELETED SUCCESSFULLY!", HttpStatus.OK);
    }

    private void checkIfGroundNameAlreadyExists(String groundName) throws NameAlreadyExistsException {
        if (groundRepo.findByGroundName(groundName.toUpperCase()).isPresent()) {
            throw new NameAlreadyExistsException(referencedClass, groundName.toUpperCase());
        }
    }

    private Ground findGroundById(Long groundId) throws EntityDoesNotExistsException{
        Optional<Ground> optionalGround = groundRepo.findById(groundId);
        if (optionalGround.isEmpty())
            throw new EntityDoesNotExistsException(referencedClass, groundId);
        return optionalGround.get();
    }

    private Ground validateGround(Ground ground, boolean save) throws GroundFieldsException{
        Set<ConstraintViolation<Ground>> violations = validator.validate(ground);
        if (!violations.isEmpty()) {
            List<String> violationsString = new ArrayList<>();
            for (ConstraintViolation<Ground> violation : violations)
                violationsString.add(violation.getMessage());
            throw new GroundFieldsException(violationsString);
        }
        ground.setGroundName(ground.getGroundName().toUpperCase());
        if (save)
            ground = groundRepo.save(ground);
        return ground;
    }

    private void checkNameEquals(List<String> groundNames){
        for (int i = 0; i < groundNames.size(); i++) {
            for (int j = 0; j < groundNames.size(); j++) {
                if (i == j)
                    continue;
                if (groundNames.get(i).equalsIgnoreCase(groundNames.get(j))) {
                    throw new NameAlreadyExistsException(referencedClass, groundNames.get(j));
                }
            }
        }
    }
}
