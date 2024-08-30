package com.example.cricflow.repository;

import com.example.cricflow.BaseData;
import com.example.cricflow.model.Ground;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.ARRAY;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@DataJpaTest
public class GroundRepoTests extends BaseData {

    @Autowired private GroundRepo groundRepo;

    @BeforeEach
    public void setUp() {
        prepare();
    }

    @AfterEach
    public void tearDown() {
        deleteRelatedTablesData();
    }

    @DisplayName("Repository Test for adding a ground")
    @Test
    public void givenGroundObject_whenSaved_thenGroundIsReturned() {
        //given

        //when
        Ground savedGround = groundRepo.save(ground1);

        //then
        assertThat(savedGround.groundEquals(ground1)).isTrue();
    }

    @DisplayName("Repository Test for updating a ground")
    @Test
    public void givenGroundObject_whenUpdated_thenUpdatedGroundIsReturned() {
        //given
        ground1 = groundRepo.save(ground1);

        //when
        ground1.setGroundName("NICE ground");
        Ground updatedGround = groundRepo.save(ground1);

        //then
        assertThat(updatedGround.getGroundName()).isEqualTo(ground1.getGroundName());
        assertThat(updatedGround.groundEquals(ground1)).isTrue();
    }

    @DisplayName("Repository Test for retrieving single ground")
    @Test
    public void givenGroundId_whenRetrieved_thenOptionalGroundIsReturned() {
        //given
        ground1 = groundRepo.save(ground1);

        //when
        Optional<Ground> present = groundRepo.findById(ground1.getGroundId());
        Optional<Ground> empty = groundRepo.findById(999L);

        //then
        assertThat(present).isPresent();
        assertThat(empty).isNotPresent();
    }

    @DisplayName("Repository Test for deleting a ground")
    @Test
    public void givenGroundId_whenDeleted_thenGroundIsRemoved(){
        //given
        groundRepo.save(ground1);

        //when
        groundRepo.deleteById(ground1.getGroundId());
        Optional<Ground> optionalGround = groundRepo.findById(ground1.getGroundId());

        //then
        assertThat(optionalGround).isNotPresent();
    }

    @DisplayName("Repository Test for adding multiple grounds")
    @Test
    public void givenListOfGrounds_whenSaved_thenListOfEmployeesIsReturned() {
        //given
        List<Ground> grounds = List.of(ground1, ground2);

        //when
        List<Ground> savedGrounds = groundRepo.saveAll(grounds);

        //then
        assertThat(savedGrounds.get(0).groundEquals(grounds.get(0))).isTrue();
        assertThat(savedGrounds.get(1).groundEquals(grounds.get(1))).isTrue();
        assertThat(grounds.size()).isEqualTo(savedGrounds.size());
    }

    @DisplayName("Repository Test for retrieving list of all grounds")
    @Test
    public void givenNothing_whenAllGroundsRetrieved_thenListOfAllGroundsIsReturned(){
        //given
        List<Ground> grounds = List.of(ground1, ground2);
        groundRepo.saveAll(grounds);

        //when
        List<Ground> retrievedGrounds = groundRepo.findAll();

        //then
        assertThat(retrievedGrounds.get(0).groundEquals(grounds.get(0))).isTrue();
        assertThat(retrievedGrounds.get(1).groundEquals(grounds.get(1))).isTrue();
        assertThat(retrievedGrounds.size()).isEqualTo(grounds.size());
    }

    @DisplayName("Repository Test for checking if ground name already exists, if name exists")
    @Test
    public void givenUniqueGroundName_whenFindByGroundNameIsCalled_thenEmptyOptionalIsReturned(){
        //given
        ground1.setGroundName("NICE ground");
        groundRepo.save(ground1);

        //when
        Optional<Ground> groundWithSameName = groundRepo.findByGroundNameIgnoreCase("SCME ground");

        //then
        assertThat(groundWithSameName).isNotPresent();
    }

    @DisplayName("Repository Test for checking if ground name already exists, if name does not exists")
    @Test
    public void givenNonUniqueGroundName_whenFindByGroundNameIsCalled_thenOptionalOfGroundWithThatNameIsReturned(){
        //given
        ground1.setGroundName("NICE ground");
        groundRepo.save(ground1);

        //when
        Optional<Ground> groundWithSameName = groundRepo.findByGroundNameIgnoreCase("nice Ground");

        //then
        assertThat(groundWithSameName).isPresent();
    }

    @DisplayName("Repository Test for searching ground name with character sequence")
    @Test
    public void givenCharacterSequence_whenFindAllByFullNameContainingIsCalled_thenListOfGroundsHavingSequenceInTheirNameIsReturned(){
        //given
        ground1.setGroundName("NICE ground");
        ground2.setGroundName("SCME ground");
        groundRepo.saveAll(Arrays.asList(ground1, ground2));

        //when
        List<Ground> groundsWithSequence1 = groundRepo.findAllByFullNameContaining("e g");
        List<Ground> groundsWithSequence2 = groundRepo.findAllByFullNameContaining("ice");
        List<Ground> groundsWithSequence3 = groundRepo.findAllByFullNameContaining("hb");

        //then
        assertThat(groundsWithSequence1.size()).isEqualTo(2);
        assertThat(groundsWithSequence2.size()).isEqualTo(1);
        assertThat(groundsWithSequence3.size()).isEqualTo(0);
    }

    @Override
    public void deleteRelatedTablesData() {
        groundRepo.deleteAll();
    }

    @Override
    public void prepare() {
    }
}
