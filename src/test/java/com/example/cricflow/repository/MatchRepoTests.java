package com.example.cricflow.repository;

import com.example.cricflow.BaseData;
import com.example.cricflow.model.Match;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class MatchRepoTests extends BaseData {

    @Autowired private MatchRepo matchRepo;
    @Autowired private TeamRepo teamRepo;
    @Autowired private PlayerRepo playerRepo;
    @Autowired private TossRepo tossRepo;
    @Autowired private InningRepo inningRepo;
    @Autowired private OverRepo overRepo;
    @Autowired private BallRepo ballRepo;
    @Autowired private EventRepo eventRepo;
    @Autowired private GroundRepo groundRepo;

    @BeforeEach
    public void setUp() {
        prepare();
    }

    @AfterEach
    public void tearDown() {
        deleteRelatedTablesData();
    }

    @DisplayName("Repository Test for adding a match")
    @Test
    public void givenMatchObject_whenSaved_thenMatchIsReturned() {
        //given

        //when
        Match savedMatch = matchRepo.save(match1);

        //then
        assertThat(savedMatch).isNotNull();
        assertThat(savedMatch.matchEquals(match1)).isTrue();
    }

    @DisplayName("Repository Test for updating a match")
    @Test
    public void givenMatchObject_whenUpdated_thenUpdatedMatchIsReturned() {
        //given
        match1 = matchRepo.save(match1);

        //when
        match1.setMatchDate(LocalDate.of(2024, 8, 12));
        Match updatedMatch = matchRepo.save(match1);

        //then
        assertThat(updatedMatch.getMatchDate()).isEqualTo(match1.getMatchDate());
        assertThat(updatedMatch.matchEquals(match1)).isTrue();
    }

    @DisplayName("Repository Test for retrieving single match")
    @Test
    public void givenMatchId_whenRetrieved_thenOptionalMatchIsReturned() {
        //given
        match1 = matchRepo.save(match1);


        //when
        Optional<Match> present = matchRepo.findById(match1.getMatchId());
        Optional<Match> empty = matchRepo.findById(2L);

        //then
        assertThat(present).isPresent();
        assertThat(empty).isNotPresent();
    }

    @DisplayName("Repository Test for deleting a match")
    @Test
    public void givenMatchId_whenDeleted_thenMatchIsRemoved(){
        //given
        match1 = matchRepo.save(match1);

        //when
        matchRepo.deleteById(match1.getMatchId());
        Optional<Match> optionalMatch = matchRepo.findById(match1.getMatchId());

        //then
        assertThat(optionalMatch).isNotPresent();
    }

    @DisplayName("Repository Test for adding multiple matches")
    @Test
    public void givenListOfMatches_whenSaved_thenListOfEmployeesIsReturned() {
        //given
        List<Match> matches = List.of(match1, match2);

        //when
        List<Match> savedMatches = matchRepo.saveAll(matches);

        //then
        assertThat(savedMatches.get(0).matchEquals(matches.get(0))).isTrue();
        assertThat(savedMatches.get(1).matchEquals(matches.get(1))).isTrue();
        assertThat(savedMatches.size()).isEqualTo(matches.size());
    }

    @DisplayName("Repository Test for retrieving list of all matches")
    @Test
    public void givenNothing_whenAllMatchesRetrieved_thenListOfAllMatchesIsReturned(){
        //given
        List<Match> matches = List.of(match1, match2);
        matchRepo.saveAll(matches);

        //when
        List<Match> retrievedMatches = matchRepo.findAll();

        //then
        assertThat(retrievedMatches.get(0).matchEquals(matches.get(0))).isTrue();
        assertThat(retrievedMatches.get(1).matchEquals(matches.get(1))).isTrue();
        assertThat(retrievedMatches.size()).isEqualTo(matches.size());
    }

    @Override
    public void deleteRelatedTablesData() {
        groundRepo.deleteAll();
        eventRepo.deleteAll();
        ballRepo.deleteAll();
        overRepo.deleteAll();
        inningRepo.deleteAll();
        tossRepo.deleteAll();
        playerRepo.deleteAll();
        teamRepo.deleteAll();
        matchRepo.deleteAll();
    }

    @Override
    public void prepare() {
        // saving ground, toss and players, as they do not depend on others;
        ground1 = groundRepo.save(ground1);
        ground2 = groundRepo.save(ground2);

        toss1 = tossRepo.save(toss1);
        toss2 = tossRepo.save(toss2);

        player1 = playerRepo.save(player1);
        player2 = playerRepo.save(player2);
        player3 = playerRepo.save(player3);
        player4 = playerRepo.save(player4);


        // updating teams with players
        teamA.setPlayers(new ArrayList<>(Arrays.asList(player1, player2)));
        teamB.setPlayers(new ArrayList<>(Arrays.asList(player3, player4)));
        // Teams, depend on players, so now saving teams
        teamA = teamRepo.save(teamA);
        teamB = teamRepo.save(teamB);

        // saving events, they are required in balls
        event1 = eventRepo.save(event1);
        event2 = eventRepo.save(event2);
        event3 = eventRepo.save(event3);
        event4 = eventRepo.save(event4);

        // updating balls with players
        ball1.setStriker(player1); ball1.setNonStriker(player2); ball1.setBowler(player3); ball1.setBallEvent(event1);
        ball2.setStriker(player1); ball2.setNonStriker(player2); ball2.setBowler(player3); ball2.setBallEvent(event2);
        ball3.setStriker(player1); ball3.setNonStriker(player2); ball3.setBowler(player3); ball3.setBallEvent(event3);
        ball4.setStriker(player1); ball4.setNonStriker(player2); ball4.setBowler(player3); ball4.setBallEvent(event4);
        // saving balls now, as they do not depend on others
        ball1 = ballRepo.save(ball1);
        ball2 = ballRepo.save(ball2);
        ball3 = ballRepo.save(ball3);
        ball4 = ballRepo.save(ball4);

        // updating overs with balls
        over1.setBalls(new ArrayList<>(Arrays.asList(ball1, ball2)));
        over2.setBalls(new ArrayList<>(Arrays.asList(ball3, ball4)));
        // over depends on balls, so now saving balls
        over1 = overRepo.save(over1);
        over2 = overRepo.save(over2);

        // now, updating inning with over and teams
        inning1.setOvers(new ArrayList<>(Arrays.asList(over1)));
        inning1.setBattingSide(teamA);
        inning1.setBowlingSide(teamB);
        inning2.setOvers(new ArrayList<>(Arrays.asList(over2)));
        inning2.setBattingSide(teamB);
        inning2.setBowlingSide(teamA);
        inning3.setOvers(new ArrayList<>());
        inning3.setBattingSide(teamB);
        inning3.setBowlingSide(teamA);
        inning4.setOvers(new ArrayList<>());
        inning4.setBattingSide(teamA);
        inning4.setBowlingSide(teamB);
        // saving the innings
        inning1 = inningRepo.save(inning1);
        inning2 = inningRepo.save(inning2);
        inning3 = inningRepo.save(inning3);
        inning4 = inningRepo.save(inning4);

        // finally updating matches
        match1.setToss(toss1);
        match1.setTeamA(teamA);
        match1.setTeamB(teamB);
        match1.setGround(ground1);
        match1.setFirstInnings(inning1);
        match1.setSecondInnings(inning2);
        match2.setToss(toss2);
        match2.setTeamA(teamB);
        match2.setTeamB(teamA);
        match2.setGround(ground2);
        match2.setFirstInnings(inning3);
        match2.setSecondInnings(inning4);
    }
}
