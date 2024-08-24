package com.example.cricflow.service;

import com.example.cricflow.model.Match;
import com.example.cricflow.repository.GroundRepo;
import com.example.cricflow.repository.MatchRepo;
import com.example.cricflow.repository.PlayerRepo;
import com.example.cricflow.repository.TeamRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MatchService {

    @Autowired private GroundRepo groundRepo;
    @Autowired private TeamRepo teamRepo;
    @Autowired private PlayerRepo playerRepo;
    @Autowired private MatchRepo matchRepo;


}
