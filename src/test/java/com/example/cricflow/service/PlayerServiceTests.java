package com.example.cricflow.service;

import com.example.cricflow.BaseData;
import com.example.cricflow.exception.CascadeFailureException;
import com.example.cricflow.model.Player;
import com.example.cricflow.model.Team;
import com.example.cricflow.repository.PlayerRepo;
import com.example.cricflow.repository.TeamRepo;
import jakarta.persistence.EntityNotFoundException;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTests extends BaseData {

    @Mock
    PlayerRepo playerRepo;

    @Mock
    TeamRepo teamRepo;

    @InjectMocks
    PlayerService playerService;

    @BeforeEach
    void setUp() {
        deleteRelatedTablesData();
        prepare();
    }

//    @DisplayName("Service Test for adding a player with No Team")
//    @Test
//    public void givenPlayerWithNoTeam_whenSaved_thenPlayerObjectIsReturned() {
//        //given
//        given(playerRepo.save(player1)).willReturn(player1);
//        given(playerRepo.save(player1)).willReturn(player1);
//
//        //when
//        ResponseEntity<Player> savedPlayer = playerService.createPlayer(player1);
//
//        //then
//        assertThat(savedPlayer.getBody()).isEqualTo(player1);
//        assertThat(savedPlayer.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//    }
//
//    @DisplayName("Service Test for adding a player with a Team, not saved already provided cascade true")
//    @Test
//    public void givenPlayerObjectWithTeamNotSavedAlreadyAndCascadePermission_whenSaved_thenTeamsAreSavedPlayerObjectIsReturned() {
//        //given
//        player1.setTeams(new ArrayList<>(Arrays.asList(teamA, teamB))); // to check creation of teams with player
//        given(teamRepo.save(teamA)).willReturn(teamA);
//        given(teamRepo.save(teamB)).willReturn(teamB);
//        given(playerRepo.save(player1)).willReturn(player1);
//
//        //when
//        ResponseEntity<Player> savedPlayer = playerService.createPlayer(player1, true);
//
//        //then
//        assertThat(savedPlayer.getBody()).isEqualTo(player1);
//        assertThat(savedPlayer.getBody().getTeams().get(0)).isEqualTo(player1.getTeams().get(0));
//        assertThat(savedPlayer.getBody().getTeams().get(1)).isEqualTo(player1.getTeams().get(1));
//        assertThat(savedPlayer.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//    }
//
//    @DisplayName("Service Test for adding a player with a Team, not saved already, and provided cascade false")
//    @Test
//    public void givenPlayerObjectWithTeamNotSavedAlreadyAndWithoutCascadePermission_whenSaved_thenCascadeFailureExceptionIsThrown() {
//        //given
//        player1.setTeams(new ArrayList<>(Arrays.asList(teamA, teamB))); // to check creation of teams with player
//        given(teamRepo.findById(anyLong()))
//                .willReturn(Optional.empty());
//
//        //when
//        Executable executable = () -> playerService.createPlayer(player1, false);
//
//        //then
//        assertThrows(CascadeFailureException.class, executable);
//    }
//
//    @DisplayName("Service Test for adding a player with a Team, saved already provided cascade false")
//    @Test
//    public void givenPlayerObjectWithTeamSavedAlreadyAndCascadePermission_whenSaved_thenPlayerObjectIsReturned() {
//        //given
//        player1.setTeams(new ArrayList<>(Arrays.asList(teamA, teamB))); // to check creation of teams with player
//        given(teamRepo.findById(anyLong())).willReturn(Optional.of(new Team()));
//        given(teamRepo.save(teamA)).willReturn(teamA);
//        given(teamRepo.save(teamB)).willReturn(teamB);
//        given(playerRepo.save(player1)).willReturn(player1);
//
//        //when
//        ResponseEntity<Player> savedPlayer = playerService.createPlayer(player1, false);
//
//        //then
//        assertThat(savedPlayer.getBody()).isEqualTo(player1);
//        assertThat(savedPlayer.getBody().getTeams().get(0)).isEqualTo(player1.getTeams().get(0));
//        assertThat(savedPlayer.getBody().getTeams().get(1)).isEqualTo(player1.getTeams().get(1));
//        assertThat(savedPlayer.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//    }
//
//    @DisplayName("Service Test for reading player with id that exists")
//    @Test
//    public void givenPlayerIdThatExists_whenRead_thenPlayerWithThatIdIsReturned(){
//        //given
//        given(playerRepo.findById(anyLong())).willReturn(Optional.of(player1));
//
//        //when
//        ResponseEntity<Player> readPlayer = playerService.readPlayer(player1.getPlayerId());
//
//        //then
//        assertThat(readPlayer.getBody()).isEqualTo(player1);
//        assertThat(readPlayer.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//    }
//
//    @DisplayName("Service Test for reading player with id that does not exist")
//    @Test
//    public void givenPlayerIdThatDoesNotExists_whenRead_thenEntityDoesNotExistsExceptionIsThrown(){
//        //given
//        given(playerRepo.findById(anyLong())).willReturn(Optional.empty());
//
//        //when
//        Executable executable = () -> playerService.readPlayer(player1.getPlayerId());
//
//        //then
//        assertThrows(EntityNotFoundException.class, executable);
//    }
//
//    @DisplayName("Service Test for updating an old player with No Team")
//    @Test
//    public void givenAlreadySavedPlayerWithNoTeam_whenUpdated_thenUpdatedPlayerObjectIsReturned() {
//        //given
//        given(playerRepo.findById(anyLong())).willReturn(Optional.of(new Player()));
//        given(playerRepo.save(player1)).willReturn(player1);
//
//        //when
//        player1.setPlayerType(Player.PlayerType.ALL_ROUNDER);
//        ResponseEntity<Player> savedPlayer = playerService.updatePlayer(player1);
//
//        //then
//        assertThat(savedPlayer.getBody().getPlayerType()).isEqualTo(player1.getPlayerType());
//        assertThat(savedPlayer.getBody()).isEqualTo(player1);
//        assertThat(savedPlayer.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//    }
//
//    @DisplayName("Service Test for updating a new player with No Team")
//    @Test
//    public void givenNewPlayer_whenUpdated_thenEntityDoesNotExistsExceptionIsThrown() {
//        //given
//        given(playerRepo.findById(anyLong())).willReturn(Optional.empty());
//
//        //when
//        Executable executable = () -> playerService.updatePlayer(player1);
//
//        //then
//        assertThrows(EntityNotFoundException.class, executable);
//    }
//
//    @DisplayName("Service Test for updating an old player with a Team, not saved already provided cascade true")
//    @Test
//    public void givenOldPlayerObjectWithTeamNotSavedAlreadyAndCascadePermission_whenUpdated_thenTeamsAreSavedAndPlayerObjectIsReturned() {
//        //given
//        player1.setTeams(new ArrayList<>(Arrays.asList(teamA, teamB))); // to check creation of teams with player
//        given(playerRepo.findById(anyLong())).willReturn(Optional.of(new Player()));
//        given(teamRepo.save(teamA)).willReturn(teamA);
//        given(teamRepo.save(teamB)).willReturn(teamB);
//        given(playerRepo.save(player1)).willReturn(player1);
//
//        //when
//        ResponseEntity<Player> updatedPlayer = playerService.updatePlayer(player1, true);
//
//        //then
//        assertThat(updatedPlayer.getBody()).isEqualTo(player1);
//        assertThat(updatedPlayer.getBody().getTeams().get(0)).isEqualTo(player1.getTeams().get(0));
//        assertThat(updatedPlayer.getBody().getTeams().get(1)).isEqualTo(player1.getTeams().get(1));
//        assertThat(updatedPlayer.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//    }
//
//    @DisplayName("Service Test for updating an old player with a Team, not saved already, and provided cascade false")
//    @Test
//    public void givenOldPlayerObjectWithTeamNotSavedAlreadyAndWithoutCascadePermission_whenUpdated_thenCascadeFailureExceptionIsThrown() {
//        //given
//        player1.setTeams(new ArrayList<>(Arrays.asList(teamA, teamB))); // to check creation of teams with player
//        given(playerRepo.findById(anyLong())).willReturn(Optional.of(new Player()));
//        given(teamRepo.findById(anyLong())).willReturn(Optional.empty());
//
//        //when
//        Executable executable = () -> playerService.updatePlayer(player1, false);
//
//        //then
//        assertThrows(CascadeFailureException.class, executable);
//    }

//    @DisplayName("Service Test for updating an old player with a Team, saved already provided cascade false")
//    @Test
//    public void givenOldPlayerObjectWithTeamSavedAlreadyAndCascadePermission_whenUpdated_thenUpdatedPlayerObjectIsReturned() {
//        //given
//        player1.setTeams(new ArrayList<>(Arrays.asList(teamA, teamB))); // to check creation of teams with player
//        given(playerRepo.findById(anyLong())).willReturn(Optional.of(new Player()));
//        given(teamRepo.findById(anyLong())).willReturn(Optional.of(new Team()));
//        given(teamRepo.save(teamA)).willReturn(teamA);
//        given(teamRepo.save(teamB)).willReturn(teamB);
//        given(playerRepo.save(player1)).willReturn(player1);
//
//        //when
//        ResponseEntity<Player> savedPlayer = playerService.updatePlayer(player1, false);
//
//        //then
//        assertThat(savedPlayer.getBody()).isEqualTo(player1);
//        assertThat(savedPlayer.getBody().getTeams().get(0)).isEqualTo(player1.getTeams().get(0));
//        assertThat(savedPlayer.getBody().getTeams().get(1)).isEqualTo(player1.getTeams().get(1));
//        assertThat(savedPlayer.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//    }

    @DisplayName("Service Test for deleting player with id that exists")
    @Test
    public void givenPlayerIdThatExists_whenDeleted_thenPlayerWithThatIdIsRemoved(){
        //given
        given(playerRepo.findById(anyLong())).willReturn(Optional.of(player1));
        willDoNothing().given(playerRepo).deleteById(player1.getPlayerId());

        //when
        ResponseEntity<Player> deletedPlayer = playerService.deletePlayer(player1.getPlayerId());

        //then
        assertThat(deletedPlayer.getBody()).isEqualTo((Player) null);
        assertThat(deletedPlayer.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @DisplayName("Service Test for deleting player with id that does not exist")
    @Test
    public void givenPlayerIdThatDoesNotExists_whenDeleted_thenEntityDoesNotExistsExceptionIsThrown(){
        //given
        given(playerRepo.findById(anyLong())).willReturn(Optional.empty());

        //when
        Executable executable = () -> playerService.deletePlayer(player1.getPlayerId());

        //then
        assertThrows(EntityNotFoundException.class, executable);
    }

    @Override
    public void deleteRelatedTablesData() {

    }

    @Override
    public void prepare() {
        player1.setPlayerId(1L);
        player2.setPlayerId(2L);

        teamA.setTeamId(1L);
        teamB.setTeamId(2L);
        teamA.setPlayers(new ArrayList<>());
        teamB.setPlayers(new ArrayList<>());

//        player1.setTeams(new ArrayList<>());
//        player2.setTeams(new ArrayList<>());
    }
}
