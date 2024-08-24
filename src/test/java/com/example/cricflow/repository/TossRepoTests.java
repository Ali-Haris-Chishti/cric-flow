package com.example.cricflow.repository;

import com.example.cricflow.BaseData;
import com.example.cricflow.model.Toss;
import com.example.cricflow.model.literal.TeamSide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class TossRepoTests extends BaseData {

    @Autowired private MatchRepo matchRepo;
    @Autowired private TeamRepo teamRepo;
    @Autowired private PlayerRepo playerRepo;
    @Autowired private TossRepo tossRepo;
    @Autowired private InningRepo inningRepo;
    @Autowired private OverRepo overRepo;
    @Autowired private BallRepo ballRepo;
    @Autowired private GroundRepo groundRepo;

    @BeforeEach
    public void setUp() {
        deleteRelatedTablesData();
        prepare();
    }

    @DisplayName("Repository Test for adding a toss")
    @Test
    public void givenTossObject_whenSaved_thenTossIsReturned() {
        //given

        //when
        Toss savedToss = tossRepo.save(toss1);

        //then
        assertThat(savedToss).isNotNull();
        assertThat(savedToss.tossEquals(toss1)).isTrue();
    }

    @DisplayName("Repository Test for updating a toss")
    @Test
    public void givenTossObject_whenUpdated_thenUpdatedTossIsReturned() {
        //given
        tossRepo.save(toss1);

        //when
        toss1.setWinningSide(TeamSide.SIDE_B);
        Toss updatedToss = tossRepo.save(toss1);

        //then
        assertThat(updatedToss.tossEquals(toss1)).isTrue();
    }

    @DisplayName("Repository Test for retrieving single toss")
    @Test
    public void givenTossId_whenRetrieved_thenOptionalTossIsReturned() {
        //given
        toss1 = tossRepo.save(toss1);

        //when
        Optional<Toss> present = tossRepo.findById(toss1.getTossId());
        Optional<Toss> empty = tossRepo.findById(2L);

        //then
        assertThat(present).isPresent();
        assertThat(empty).isNotPresent();
    }

    @DisplayName("Repository Test for deleting a toss")
    @Test
    public void givenTossId_whenDeleted_thenTossIsRemoved(){
        //given
        toss1 = tossRepo.save(toss1);

        //when
        tossRepo.deleteById(toss1.getTossId());
        Optional<Toss> optionalToss = tossRepo.findById(toss1.getTossId());

        //then
        assertThat(optionalToss).isNotPresent();
    }

    @DisplayName("Repository Test for adding multiple tosses")
    @Test
    public void givenListOfTosses_whenSaved_thenListOfEmployeesIsReturned() {
        //given
        List<Toss> tosses = List.of(toss1, toss2);

        //when
        List<Toss> savedTosses = tossRepo.saveAll(tosses);

        //then
        assertThat(savedTosses.get(0).tossEquals(tosses.get(0))).isTrue();
        assertThat(savedTosses.get(1).tossEquals(tosses.get(1))).isTrue();
        assertThat(savedTosses.size()).isEqualTo(tosses.size());
    }

    @DisplayName("Repository Test for retrieving list of all tosses")
    @Test
    public void givenNothing_whenAllTossesRetrieved_thenListOfAllTossesIsReturned(){
        //given
        List<Toss> tosses = List.of(toss1, toss2);
        tossRepo.saveAll(tosses);

        //when
        List<Toss> retrievedTosses = tossRepo.findAll();

        //then
        assertThat(retrievedTosses.get(0).tossEquals(tosses.get(0))).isTrue();
        assertThat(retrievedTosses.get(1).tossEquals(tosses.get(1))).isTrue();
        assertThat(retrievedTosses.size()).isEqualTo(tosses.size());
    }

    @Override
    public void deleteRelatedTablesData() {

    }

    @Override
    public void prepare() {
        // saving ground, and players, as they do not depend on others;
        ground1 = groundRepo.save(ground1);
        ground2 = groundRepo.save(ground2);

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

        // updating balls with players
        ball1.setStriker(player1); ball1.setNonStriker(player2); ball1.setBowler(player3);
        ball2.setStriker(player1); ball2.setNonStriker(player2); ball2.setBowler(player3);
        ball3.setStriker(player1); ball3.setNonStriker(player2); ball3.setBowler(player3);
        ball4.setStriker(player1); ball4.setNonStriker(player2); ball4.setBowler(player3);
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

        //now, updating matches
        match1.setTeamA(teamA);
        match1.setTeamB(teamB);
        match1.setGround(ground1);
        match1.setFirstInnings(inning1);
        match1.setSecondInnings(inning2);
        match2.setTeamA(teamB);
        match2.setTeamB(teamA);
        match2.setGround(ground2);
        match2.setFirstInnings(inning3);
        match2.setSecondInnings(inning4);
        // saving matches
        match1 = matchRepo.save(match1);
        match2 = matchRepo.save(match2);

        // finally updating the tosses
        toss1.setMatch(match1);
        toss2.setMatch(match2);
    }
}
