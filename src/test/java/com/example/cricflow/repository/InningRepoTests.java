package com.example.cricflow.repository;

import com.example.cricflow.BaseData;
import com.example.cricflow.model.Inning;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class InningRepoTests extends BaseData {
    
    @Autowired InningRepo inningRepo;
    @Autowired TeamRepo teamRepo;
    @Autowired PlayerRepo playerRepo;
    @Autowired OverRepo overRepo;
    @Autowired BallRepo ballRepo;
    @Autowired EventRepo eventRepo;

    @BeforeEach
    void setUp() {
        prepare();
    }

    @AfterEach
    void tearDown() {
        deleteRelatedTablesData();
    }

    @DisplayName("Repository Test for adding a inning")
    @Test
    public void givenInningObject_whenSaved_thenInningIsReturned() {
        //given

        //when
        Inning savedInning = inningRepo.save(inning1);

        //then
        assertThat(savedInning.inningEquals(inning1)).isTrue();
    }

    @DisplayName("Repository Test for updating a inning")
    @Test
    public void givenInningObject_whenUpdated_thenUpdatedInningIsReturned() {
        //given
        inning1 = inningRepo.save(inning1);

        //when
        inning1.setNumberOfOvers(20);
        Inning updatedInning = inningRepo.save(inning1);

        //then
        assertThat(updatedInning.inningEquals(inning1)).isTrue();
    }

    @DisplayName("Repository Test for retrieving single inning")
    @Test
    public void givenInningId_whenRetrieved_thenOptionalInningIsReturned() {
        //given
        inning1 = inningRepo.save(inning1);

        //when
        Optional<Inning> present = inningRepo.findById(inning1.getInningId());
        Optional<Inning> empty = inningRepo.findById(999L);

        //then
        assertThat(present).isPresent();
        assertThat(empty).isEmpty();
    }

    @DisplayName("Repository Test for deleting a inning")
    @Test
    public void givenInningId_whenDeleted_thenInningIsRemoved(){
        //given
        inningRepo.save(inning1);

        //when
        inningRepo.deleteById(inning1.getInningId());
        Optional<Inning> optionalInning = inningRepo.findById(inning1.getInningId());

        //then
        assertThat(optionalInning).isEmpty();
    }

    @DisplayName("Repository Test for adding multiple innings")
    @Test
    public void givenListOfInnings_whenSaved_thenListOfEmployeesIsReturned() {
        //given
        List<Inning> innings = List.of(inning1, inning2);

        //when
        List<Inning> savedInnings = inningRepo.saveAll(innings);

        //then
        assertThat(savedInnings.get(0).inningEquals(innings.get(0))).isTrue();
        assertThat(savedInnings.get(1).inningEquals(innings.get(1))).isTrue();
        assertThat(savedInnings.size()).isEqualTo(innings.size());
    }

    @DisplayName("Repository Test for retrieving list of all innings")
    @Test
    public void givenNothing_whenAllInningsRetrieved_thenListOfAllInningsIsReturned(){
        //given
        List<Inning> innings = List.of(inning1, inning2);
        inningRepo.saveAll(innings);

        //when
        List<Inning> retrievedInnings = inningRepo.findAll();

        //then
        assertThat(retrievedInnings.get(0).inningEquals(innings.get(0))).isTrue();
        assertThat(retrievedInnings.get(1).inningEquals(innings.get(1))).isTrue();
        assertThat(retrievedInnings.size()).isEqualTo(innings.size());
    }

    @Override
    public void deleteRelatedTablesData(){
        ballRepo.deleteAll();
        eventRepo.deleteAll();
        overRepo.deleteAll();
        playerRepo.deleteAll();
        teamRepo.deleteAll();
        inningRepo.deleteAll();
    }

    @Override
    public void prepare() {
        // saving players first as, they do not depend on others
        player1 = playerRepo.save(player1);
        player2 = playerRepo.save(player2);
        player3 = playerRepo.save(player3);
        player4 = playerRepo.save(player4);
        // updating teams with players
        teamA.setPlayers(List.of(player1, player2));
        teamB.setPlayers(List.of(player3, player4));
        // Teams, depend on players, so now saving teams
        teamA = teamRepo.save(teamA);
        teamB = teamRepo.save(teamB);

        // saving events, as they are needed in the ball
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
        over1.setBalls(List.of(ball1, ball2));
        over2.setBalls(List.of(ball3, ball4));
        // over depends on balls, so now saving balls
        over1 = overRepo.save(over1);
        over2 = overRepo.save(over2);

        // Finally, updating inning with over and teams
        inning1.setOvers(List.of(over1));
        inning1.setBattingSide(teamA);
        inning1.setBowlingSide(teamB);
        inning2.setOvers(List.of(over2));
        inning2.setBattingSide(teamB);
        inning2.setBowlingSide(teamA);
    }
}
