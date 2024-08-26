package com.example.cricflow.controller;

import com.example.cricflow.exception.validator.TossFieldsException;
import com.example.cricflow.model.Toss;
import com.example.cricflow.service.TossService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/toss")
public class TossController {

    @Autowired private TossService tossService;

    @PostMapping("/create")
    ResponseEntity<?> createToss(@RequestBody Toss toss){
        try {
            return tossService.createToss(toss);
        }
        catch (TossFieldsException e){
            return new ResponseEntity<>(e.violations, HttpStatus.BAD_REQUEST);
        }
    }

}
