package com.example.cricflow.repository;

import com.example.cricflow.BaseData;
import com.example.cricflow.model.Over;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class OverRepoTests extends BaseData {

    @Autowired private PlayerRepo playerRepo;
    @Autowired private OverRepo overRepo;
    @Autowired private BallRepo ballRepo;
    @Autowired private EventRepo eventRepo;

    @BeforeEach
    public void setUp() {
        deleteRelatedTablesData();
        prepare();
    }

    @DisplayName("Repository Test for adding a over")
    @Test
    public void givenOverObject_whenSaved_thenOverIsReturned() {
        //given

        //when
        Over savedOver = overRepo.save(over1);

        //then
        assertThat(savedOver).isNotNull();
        assertThat(savedOver.overEquals(over1)).isTrue();
    }

    @DisplayName("Repository Test for updating a over")
    @Test
    public void givenOverObject_whenUpdated_thenUpdatedOverIsReturned() {
        //given
        overRepo.save(over1);

        //when
        over1.setBalls(new ArrayList<>());
        Over updatedOver = overRepo.save(over1);

        //then
        assertThat(updatedOver.getBalls()).isEmpty();
        assertThat(updatedOver.overEquals(over1)).isTrue();
    }

    @DisplayName("Repository Test for retrieving single over")
    @Test
    public void givenOverId_whenRetrieved_thenOptionalOverIsReturned() {
        //given
        over1 = overRepo.save(over1);

        //when
        Optional<Over> present = overRepo.findById(over1.getOverId());
        Optional<Over> empty = overRepo.findById(2L);

        //then
        assertThat(present).isPresent();
        assertThat(empty).isNotPresent();
    }

    @DisplayName("Repository Test for deleting a over")
    @Test
    public void givenOverId_whenDeleted_thenOverIsRemoved(){
        //given
        over1 = overRepo.save(over1);

        //when
        overRepo.deleteById(over1.getOverId());
        Optional<Over> optionalOver = overRepo.findById(over1.getOverId());

        //then
        assertThat(optionalOver).isNotPresent();
    }

    @DisplayName("Repository Test for adding multiple overs")
    @Test
    public void givenListOfOvers_whenSaved_thenListOfEmployeesIsReturned() {
        //given
        List<Over> overs = List.of(over1, over2);

        //when
        List<Over> savedOvers = overRepo.saveAll(overs);

        //then
        assertThat(savedOvers.get(0).overEquals(overs.get(0))).isTrue();
        assertThat(savedOvers.get(1).overEquals(overs.get(1))).isTrue();
        assertThat(savedOvers.size()).isEqualTo(overs.size());
    }

    @DisplayName("Repository Test for retrieving list of all overs")
    @Test
    public void givenNothing_whenAllOversRetrieved_thenListOfAllOversIsReturned(){
        //given
        List<Over> overs = List.of(over1, over2);
        overRepo.saveAll(overs);

        //when
        List<Over> retrievedOvers = overRepo.findAll();

        //then
        assertThat(retrievedOvers.get(0).overEquals(overs.get(0))).isTrue();
        assertThat(retrievedOvers.get(1).overEquals(overs.get(1))).isTrue();
        assertThat(retrievedOvers.size()).isEqualTo(overs.size());
    }

    @Override
    public void deleteRelatedTablesData(){
        eventRepo.deleteAll();
        ballRepo.deleteAll();
        playerRepo.deleteAll();
        overRepo.deleteAll();
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
        // saving the updated balls
        ball1 = ballRepo.save(ball1);
        ball2 = ballRepo.save(ball2);
        ball3 = ballRepo.save(ball3);
        ball4 = ballRepo.save(ball4);

        // updating overs with balls
        over1.setBalls(new ArrayList<>(Arrays.asList(ball1, ball2)));
        over2.setBalls(new ArrayList<>(Arrays.asList(ball3, ball4)));
    }
}
