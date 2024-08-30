package com.example.cricflow.repository;

import com.example.cricflow.BaseData;
import com.example.cricflow.model.Toss;
import com.example.cricflow.model.literal.TeamSide;
import org.junit.jupiter.api.AfterEach;
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

    @Autowired private TossRepo tossRepo;

    @BeforeEach
    public void setUp() {
        prepare();
    }

    @AfterEach
    public void tearDown() {
        deleteRelatedTablesData();
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
        toss1 = tossRepo.save(toss1);

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
        toss1 = tossRepo.save(toss1);
        toss2 = tossRepo.save(toss2);

        //when
        List<Toss> retrievedTosses = tossRepo.findAll();

        //then
        assertThat(retrievedTosses.get(0).tossEquals(toss1)).isTrue();
        assertThat(retrievedTosses.get(1).tossEquals(toss2)).isTrue();
        assertThat(retrievedTosses.size()).isEqualTo(2);
    }

    @Override
    public void deleteRelatedTablesData() {
        tossRepo.deleteAll();
    }
}
