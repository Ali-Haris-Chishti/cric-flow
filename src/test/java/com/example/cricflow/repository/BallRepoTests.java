package com.example.cricflow.repository;

import com.example.cricflow.BaseData;
import com.example.cricflow.model.Ball;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BallRepoTests extends BaseData {
    
    @Autowired private PlayerRepo playerRepo;
    @Autowired private BallRepo ballRepo;
    @Autowired private EventRepo eventRepo;

    @BeforeEach
    public void setUp() {
        deleteRelatedTablesData();
        prepare();
    }

    @DisplayName("Repository Test for adding a ball")
    @Test
    public void givenBallObject_whenSaved_thenBallIsReturned() {
        //given

        //when
        Ball savedBall = ballRepo.save(ball1);

        //then
        assertThat(savedBall).isNotNull();
        assertThat(savedBall.ballEquals(ball1)).isTrue();
    }

    @DisplayName("Repository Test for updating a ball")
    @Test
    public void givenBallObject_whenUpdated_thenUpdatedBallIsReturned() {
        //given
        ballRepo.save(ball1);

        //when
        ball1.setStriker(player4);
        Ball updatedBall = ballRepo.save(ball1);

        //then
        assertThat(updatedBall.getStriker().playerEquals(player4)).isTrue();
        assertThat(updatedBall.ballEquals(ball1)).isTrue();
    }

    @DisplayName("Repository Test for retrieving single ball")
    @Test
    public void givenBallId_whenRetrieved_thenOptionalBallIsReturned() {
        //given
        ball1 = ballRepo.save(ball1);

        //when
        Optional<Ball> present = ballRepo.findById(ball1.getBallId());
        Optional<Ball> empty = ballRepo.findById(999L);

        //then
        assertThat(present).isPresent();
        assertThat(empty).isNotPresent();
    }

    @DisplayName("Repository Test for deleting a ball")
    @Test
    public void givenBallId_whenDeleted_thenBallIsRemoved(){
        //given
        ball1 = ballRepo.save(ball1);

        //when
        ballRepo.deleteById(ball1.getBallId());
        Optional<Ball> optionalBall = ballRepo.findById(ball1.getBallId());

        //then
        assertThat(optionalBall).isNotPresent();
    }

    @DisplayName("Repository Test for adding multiple balls")
    @Test
    public void givenListOfBalls_whenSaved_thenListOfEmployeesIsReturned() {
        //given
        List<Ball> balls = List.of(ball1, ball2);

        //when
        List<Ball> savedBalls = ballRepo.saveAll(balls);

        //then
        assertThat(savedBalls.get(0).ballEquals(balls.get(0))).isTrue();
        assertThat(savedBalls.get(1).ballEquals(balls.get(1))).isTrue();
        assertThat(savedBalls.size()).isEqualTo(balls.size());
    }

    @DisplayName("Repository Test for retrieving list of all balls")
    @Test
    public void givenNothing_whenAllBallsRetrieved_thenListOfAllBallsIsReturned(){
        //given
        List<Ball> balls = List.of(ball1, ball2);
        ballRepo.saveAll(balls);

        //when
        List<Ball> retrievedBalls = ballRepo.findAll();

        //then
        assertThat(retrievedBalls.get(0).ballEquals(balls.get(0))).isTrue();
        assertThat(retrievedBalls.get(1).ballEquals(balls.get(1))).isTrue();
        assertThat(retrievedBalls.size()).isEqualTo(balls.size());
    }

    @Override
    public void deleteRelatedTablesData(){
        eventRepo.deleteAll();
        ballRepo.deleteAll();
        playerRepo.deleteAll();
        ballRepo.deleteAll();
    }

    @Override
    public void prepare() {
        // saving ball events, as they do not depend on others;
        event1 = eventRepo.save(event1);
        event2 = eventRepo.save(event2);
        event3 = eventRepo.save(event3);
        event4 = eventRepo.save(event4);

        // saving players
        player1 = playerRepo.save(player1);
        player2 = playerRepo.save(player2);
        player3 = playerRepo.save(player3);
        player4 = playerRepo.save(player4);

        // updating balls with ball events, and players
        ball1.setBallEvent(event1);
        ball1.setStriker(player1);
        ball1.setNonStriker(player2);
        ball1.setBowler(player3);
        ball2.setBallEvent(event2);
        ball2.setStriker(player2);
        ball2.setNonStriker(player1);
        ball2.setBowler(player3);
        ball3.setBallEvent(event3);
        ball3.setStriker(player1);
        ball3.setNonStriker(player3);
        ball3.setBowler(player4);
        ball4.setBallEvent(event4);
        ball4.setStriker(player1);
        ball4.setNonStriker(player4);
        ball4.setBowler(player2);
    }
}
