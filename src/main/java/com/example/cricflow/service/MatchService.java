package com.example.cricflow.service;

import com.example.cricflow.controller.MatchController;
import com.example.cricflow.exception.common.EntityDoesNotExistsException;
import com.example.cricflow.exception.match.SameTeamInMatchException;
import com.example.cricflow.exception.match.TeamPlayerCountException;
import com.example.cricflow.exception.validator.MatchFieldsException;
import com.example.cricflow.exception.validator.TeamFieldsException;
import com.example.cricflow.exception.validator.TossFieldsException;
import com.example.cricflow.model.*;
import com.example.cricflow.model.literal.TeamSide;
import com.example.cricflow.repository.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class MatchService {

    private final GroundRepo groundRepo;
    private final TeamRepo teamRepo;
    private final PlayerRepo playerRepo;
    private final TeamPlayerRelationRepo relationRepo;
    private final TossRepo tossRepo;
    private final MatchRepo matchRepo;
    private final InningRepo inningRepo;
    private final Validator validator;

    private final String referencedClass = "MATCH";

    @Autowired
    public MatchService(GroundRepo groundRepo, TeamRepo teamRepo, PlayerRepo playerRepo, TossRepo tossRepo, MatchRepo matchRepo, InningRepo inningRepo, TeamPlayerRelationRepo relationRepo) {
        this.groundRepo = groundRepo;
        this.teamRepo = teamRepo;
        this.playerRepo = playerRepo;
        this.tossRepo = tossRepo;
        this.matchRepo = matchRepo;
        this.inningRepo = inningRepo;
        this.relationRepo = relationRepo;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Transactional
    public ResponseEntity<Match> createMatch(MatchController.MatchWrapper wrapper)
            throws EntityDoesNotExistsException, TeamFieldsException, MatchFieldsException, TeamPlayerCountException, SameTeamInMatchException {
        validateToss(wrapper.toss());
        Ground ground = checkIfGroundExists(wrapper.groundId());
        Team teamA = checkIfTeamExists(wrapper.teamAId());
        Team teamB = checkIfTeamExists(wrapper.teamBId());
        checkBattingAndBowlingSideOfTossAreOpposite(wrapper.toss());
        checkTeamsValidations(teamA, teamB);

        Match match = Match.builder()
                .matchDate(LocalDate.now())
                .ground(ground)
                .teamA(teamA)
                .teamB(teamB)
                .toss(wrapper.toss())
                .noOfOvers(wrapper.noOfOvers())
                .build();

        validateMatch(match);
        Toss toss = wrapper.toss();

        Inning inning1 = Inning.builder()
                .battingSide(toss.getBattingSide() == TeamSide.SIDE_A? teamA: teamB)
                .bowlingSide(toss.getBattingSide() == TeamSide.SIDE_B? teamA: teamB)
                .numberOfOvers(wrapper.noOfOvers())
                .inningStatus(Inning.InningStatus.YET_TO_START)
                .build();

        Inning inning2 = Inning.builder()
                .battingSide(toss.getBattingSide() == TeamSide.SIDE_B? teamA: teamB)
                .bowlingSide(toss.getBattingSide() == TeamSide.SIDE_A? teamA: teamB)
                .numberOfOvers(wrapper.noOfOvers())
                .inningStatus(Inning.InningStatus.YET_TO_START)
                .build();


        match.setFirstInnings(inning1);
        match.setSecondInnings(inning2);
        match.setTeamA(teamA);
        match.setTeamB(teamB);

        match = matchRepo.save(match);

        return new ResponseEntity<>(match, HttpStatus.CREATED);
    }


    @Transactional
    public ResponseEntity<List<Match>> createMultipleMatches(List<MatchController.MatchWrapper> wrappers){
        List<Match> createdMatches = new ArrayList<>();
        for (MatchController.MatchWrapper wrapper : wrappers) {
            createdMatches.add(createMatch(wrapper).getBody());
        }
        return new ResponseEntity<>(createdMatches, HttpStatus.CREATED);
    }

    public ResponseEntity<Match> readMatch(long id) {
        Match match = checkIfMatchExists(id);
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    public ResponseEntity<List<Match>> readAllMatches() {
        List<Match> matches = matchRepo.findAll();
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Match> updateMatch(MatchController.MatchUpdateWrapper wrapper)
    throws EntityDoesNotExistsException, MatchFieldsException
    {
        // validations
        Match match = checkIfMatchExists(wrapper.matchId());
        Ground ground = checkIfGroundExists(wrapper.groundId());
        if (wrapper.noOfOvers() < 2 || wrapper.noOfOvers() > 20)
            throw new MatchFieldsException(List.of("noOfOvers must be 2-20"));


        // update
        match.setGround(ground);
        match.setNoOfOvers(wrapper.noOfOvers());
        match.getFirstInnings().setNumberOfOvers(wrapper.noOfOvers());
        match.getSecondInnings().setNumberOfOvers(wrapper.noOfOvers());

        // transaction
        match = matchRepo.save(match);
        return new ResponseEntity<>(match, HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<String> deleteMatch(long id) throws EntityDoesNotExistsException{
        Match match = checkIfMatchExists(id);
        matchRepo.delete(match);
        return new ResponseEntity<>("MATCH WITH ID: " + id + " DELETED SUCCESSFULLY!", HttpStatus.OK);
    }

    public ResponseEntity<List<Team>> getBothTeamsWithPlayersWhoPlayedTheMatch(long matchId) throws EntityDoesNotExistsException{
        Match match = checkIfMatchExists(matchId);

        Team teamA = match.getTeamA();
        Team teamB = match.getTeamB();

        List<Player> teamAPlayers = relationRepo.findAllPlayersWhoPlayedTheMatch(teamA, match.getMatchDate());
        List<Player> teamBPlayers = relationRepo.findAllPlayersWhoPlayedTheMatch(teamB, match.getMatchDate());

        teamA.setPlayers(new ArrayList<>(teamAPlayers));
        teamB.setPlayers(new ArrayList<>(teamBPlayers));

        return new ResponseEntity<>(List.of(teamA, teamB), HttpStatus.OK);
    }



    private void validateMatch(Match match) throws MatchFieldsException {
        Set<ConstraintViolation<Match>> violations = validator.validate(match);
        if (!violations.isEmpty()) {
            List<String> violationsString = new ArrayList<>();
            for (ConstraintViolation<Match> violation : violations) {
                violationsString.add(violation.getMessage());
            }
            throw new MatchFieldsException(violationsString);
        }
    }

    private void validateToss(Toss toss) throws TossFieldsException {
        Set<ConstraintViolation<Toss>> violations = validator.validate(toss);
        if (!violations.isEmpty()) {
            List<String> violationsString = new ArrayList<>();
            for (ConstraintViolation<Toss> violation : violations) {
                violationsString.add(violation.getMessage());
            }
            throw new TossFieldsException(violationsString);
        }
    }

    private Match checkIfMatchExists(long matchId) throws EntityDoesNotExistsException {
        Optional<Match> optionalMatch = matchRepo.findById(matchId);
        if (optionalMatch.isEmpty())
            throw new EntityDoesNotExistsException(TeamService.referencedClass, matchId);
        return optionalMatch.get();
    }

    private Team checkIfTeamExists(long teamId) throws EntityDoesNotExistsException {
        Optional<Team> optionalTeam = teamRepo.findById(teamId);
        if (optionalTeam.isEmpty())
            throw new EntityDoesNotExistsException(TeamService.referencedClass, teamId);
        System.out.println(optionalTeam.get());
        return optionalTeam.get();
    }

    private Ground checkIfGroundExists(long groundId) throws EntityDoesNotExistsException {
        Optional<Ground> optionalGround = groundRepo.findById(groundId);
        if (optionalGround.isEmpty())
            throw new EntityDoesNotExistsException(GroundService.referencedClass, groundId);
        return optionalGround.get();
    }

    private void checkTeamsValidations(Team teamA, Team teamB) throws SameTeamInMatchException, TeamPlayerCountException{
        if (teamA.equals(teamB))
            throw new SameTeamInMatchException(teamA.getTeamId());
        if (Math.abs(teamA.getPlayers().size() - teamB.getPlayers().size()) > 1)
            throw new TeamPlayerCountException(teamA.getTeamId(), teamA.getPlayers().size(), teamB.getTeamId(), teamB.getPlayers().size());
    }

    private void checkBattingAndBowlingSideOfTossAreOpposite(Toss toss) throws TossFieldsException{
        if (toss.getBattingSide() == toss.getBowlingSide())
            throw new TossFieldsException(List.of("Batting and Bowling side of toss are same: " + toss.getBattingSide()));
    }

}
