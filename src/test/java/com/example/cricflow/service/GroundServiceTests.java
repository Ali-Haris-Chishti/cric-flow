package com.example.cricflow.service;

import com.example.cricflow.BaseData;
import com.example.cricflow.exception.EntityDoesNotExistsException;
import com.example.cricflow.exception.NameAlreadyExistsException;
import com.example.cricflow.model.Ground;
import com.example.cricflow.repository.GroundRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class GroundServiceTests extends BaseData {

    @Mock
    GroundRepo groundRepo;

    @InjectMocks
    GroundService groundService;

    @BeforeEach
    void setUp() {
        deleteRelatedTablesData();
        prepare();
    }

    @DisplayName("Service Test for adding a ground with unique name")
    @Test
    public void givenGroundObjectWithUniqueName_whenSaved_thenGroundObjectIsReturned() {
        //given
        given(groundRepo.findByGroundNameIgnoreCase(anyString()))
                .willReturn(Optional.empty());
        given(groundRepo.save(any(Ground.class))).willReturn(ground1);

        //when
        ResponseEntity<Ground> savedGround = groundService.createGround("SCME Ground");

        //then
        assertThat(savedGround.getBody().getGroundName()).isEqualTo("SCME Ground");
        assertThat(savedGround.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @DisplayName("Service Test for adding a ground with non-unique name")
    @Test
    public void givenGroundObjectWithNonUniqueName_whenSaved_thenGroundNameAlreadyExistsExceptionIsThrown() {
        //given
        given(groundRepo.findByGroundNameIgnoreCase(anyString()))
                .willReturn(Optional.of(new Ground()));

        //when
        Executable executable = () -> groundService.createGround("SCME Ground");

        //then
        assertThrows(NameAlreadyExistsException.class, executable);
    }

    @DisplayName("Service Test for reading a ground with id that exists")
    @Test
    public void givenGroundIdThatExists_whenRead_thenRespectiveGroundObjectIsReturned() {
        //given
        given(groundRepo.findById(anyLong()))
                .willReturn(Optional.of(new Ground()));
        given(groundRepo.findById(ground1.getGroundId()))
                .willReturn(Optional.of(ground1));

        //when
        ResponseEntity<Ground> savedGround = groundService.readGround(ground1.getGroundId());

        //then
        assertThat(savedGround.getBody()).isEqualTo(ground1);
        assertThat(savedGround.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @DisplayName("Service Test for reading a ground with id that does not exists")
    @Test
    public void givenGroundIdWhichDoesNotExists_whenRead_thenEntityDoesNotExistsExceptionIsThrown() {
        //given
        given(groundRepo.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        Executable executable = () -> groundService.readGround(ground1.getGroundId());

        //then
        assertThrows(EntityDoesNotExistsException.class, executable);
    }

    @DisplayName("Service Test for updating a ground already saved")
    @Test
    public void givenGroundAlreadySaved_whenUpdated_thenUpdatedGroundObjectIsReturned() {
        //given
        ground1.setGroundName("NICE GROUND");
        given(groundRepo.findById(anyLong()))
                .willReturn(Optional.of(new Ground()));
        given(groundRepo.save(ground1))
                .willReturn(ground1);

        //when
        ResponseEntity<Ground> savedGround = groundService.updateGround(ground1);

        //then
        assertThat(savedGround.getBody()).isEqualTo(ground1);
        assertThat(savedGround.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @DisplayName("Service Test for updating a ground not saved before")
    @Test
    public void givenGroundObjectNotSavedBefore_whenUpdated_thenEntityDoesNotExistsExceptionIsThrown() {
        //given
        given(groundRepo.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        Executable executable = () -> groundService.updateGround(ground1);

        //then
        assertThrows(EntityDoesNotExistsException.class, executable);
    }

    @DisplayName("Service Test for deleting a ground which exists in database")
    @Test
    public void givenGroundThatIsPresentInDatabase_whenDeleted_thenItIsRemovedWithOkStatus() {
        //given
        given(groundRepo.findById(anyLong()))
                .willReturn(Optional.of(new Ground()));
        willDoNothing().given(groundRepo).delete(any(Ground.class));

        //when
        ResponseEntity<?> deletionStatus = groundService.deleteGround(ground1.getGroundId());

        //then
        assertThat(deletionStatus.getBody()).isEqualTo(("GROUND WITH ID: " + ground1.getGroundId() + ", DELETED SUCCESSFULLY!"));
        assertThat(deletionStatus.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @DisplayName("Service Test for deleting a ground which does not exists")
    @Test
    public void givenGroundObjectThatDoesNotExists_whenDeleted_thenEntityDoesNotExistsExceptionIsThrown() {
        //given
        given(groundRepo.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        Executable executable = () -> groundService.deleteGround(ground1.getGroundId());

        //then
        assertThrows(EntityDoesNotExistsException.class, executable);
    }

    @DisplayName("Service Test for searching a ground")
    @Test
    public void givenCharacterSequence_whenSearched_thenListOfGroundsWithThatSequenceInTheirNamesIsReturned() {
        //given
        given(groundRepo.findAllByFullNameContaining("ice g"))
                .willReturn(Collections.singletonList(ground1));

        //when
        ResponseEntity<List<Ground>> grounds = groundService.searchGroundsByNameSequence("ice g");

        //then
        assertThat(grounds.getBody().size()).isEqualTo(1);
        assertThat(grounds.getBody().get(0).groundEquals(ground1)).isTrue();
        assertThat(grounds.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Override
    public void deleteRelatedTablesData() {
    }

    @Override
    public void prepare() {
        ground1.setGroundId(1L);
        ground2.setGroundId(2L);
    }
}
