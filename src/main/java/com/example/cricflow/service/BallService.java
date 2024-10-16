package com.example.cricflow.service;

import com.example.cricflow.repository.BallRepo;
import org.springframework.stereotype.Service;

@Service
public class BallService {

    private final BallRepo ballRepo;


    public BallService(BallRepo ballRepo) {
        this.ballRepo = ballRepo;
    }
}
