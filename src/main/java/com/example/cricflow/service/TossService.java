package com.example.cricflow.service;

import com.example.cricflow.exception.validator.TossFieldsException;
import com.example.cricflow.model.Toss;
import com.example.cricflow.repository.TossRepo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class TossService {

    @Autowired private TossRepo tossRepo;

    @Autowired private Validator validator;

    public ResponseEntity<Toss> createToss(Toss toss) {
        validateToss(toss);
        return new ResponseEntity<>(tossRepo.save(toss), HttpStatus.CREATED);
    }

    private void validateToss(Toss toss){
        Set<ConstraintViolation<Toss>> violations = validator.validate(toss);
        if (!violations.isEmpty()) {
            List<String> violationsString = new ArrayList<>();
            for (ConstraintViolation<Toss> violation : violations) {
                violationsString.add(violation.getMessage());
            }
            throw new TossFieldsException(violationsString);
        }
    }

}
