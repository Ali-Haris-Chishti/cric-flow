package com.example.cricflow.service;

import com.example.cricflow.BaseData;
import com.example.cricflow.exception.EntityDoesNotExistsException;
import com.example.cricflow.exception.validator.PlayerFieldsException;
import com.example.cricflow.model.Player;
import com.example.cricflow.repository.PlayerRepo;
import com.example.cricflow.repository.TeamRepo;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
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

    @DisplayName("Service Test for adding a player")
    @Test
    public void givenPlayer_whenSaved_thenPlayerObjectIsReturned() {
        //given
        given(playerRepo.save(player1)).willReturn(player1);

        //when
        ResponseEntity<Player> savedPlayer = playerService.createPlayer(player1);

        //then
        assertThat(savedPlayer.getBody()).isEqualTo(player1);
        assertThat(savedPlayer.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @DisplayName("Service Test for adding a player not meeting jakarta validations constraints")
    @Test
    public void givenInValidPlayer_whenSaved_thenPlayerFieldsExceptionIsThrown() {
        //given
        Player player = new Player();

        //when
        Executable executable = () -> playerService.createPlayer(player);

        //then
        assertThrows(PlayerFieldsException.class, executable);
    }

    @DisplayName("Service Test for adding multiple players")
    @Test
    public void givenMultiplePlayers_whenSaved_thenListOfPlayerObjectsIsReturned() {
        //given
        List<Player> players = Arrays.asList(player1, player2);
        given(playerRepo.saveAll(players)).willReturn(Arrays.asList(player1, player2));

        //when
        ResponseEntity<List<Player>> savedPlayers = playerService.createMultiplePlayers(players);

        //then
        assertThat(savedPlayers.getBody().get(0)).isEqualTo(players.get(0));
        assertThat(savedPlayers.getBody().get(1)).isEqualTo(players.get(1));
        assertThat(savedPlayers.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @DisplayName("Service Test for reading player with id that exists")
    @Test
    public void givenPlayerIdThatExists_whenRead_thenPlayerWithThatIdIsReturned(){
        //given
        given(playerRepo.findById(anyLong())).willReturn(Optional.of(player1));

        //when
        ResponseEntity<Player> readPlayer = playerService.readPlayer(player1.getPlayerId());

        //then
        assertThat(readPlayer.getBody()).isEqualTo(player1);
        assertThat(readPlayer.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @DisplayName("Service Test for reading player with id that does not exist")
    @Test
    public void givenPlayerIdThatDoesNotExists_whenRead_thenEntityDoesNotExistsExceptionIsThrown(){
        //given
        given(playerRepo.findById(anyLong())).willReturn(Optional.empty());

        //when
        Executable executable = () -> playerService.readPlayer(player1.getPlayerId());

        //then
        assertThrows(EntityDoesNotExistsException.class, executable);
    }

    @DisplayName("Service Test for reading all players")
    @Test
    public void givenNothing_whenReadAll_thenListOfAllPlayersIsReturned(){
        //given
        given(playerRepo.findAll()).willReturn(Arrays.asList(player1, player2));

        //when
        ResponseEntity<List<Player>> retrievedPlayers = playerService.readAllPlayers();

        //then
        assertThat(retrievedPlayers.getBody().get(0)).isEqualTo(player1);
        assertThat(retrievedPlayers.getBody().get(1)).isEqualTo(player2);
        assertThat(retrievedPlayers.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @DisplayName("Service Test for updating an old player")
    @Test
    public void givenAlreadySavedPlayer_whenUpdated_thenUpdatedPlayerObjectIsReturned() {
        //given
        given(playerRepo.findById(anyLong())).willReturn(Optional.of(new Player()));
        given(playerRepo.save(player1)).willReturn(player1);

        //when
        player1.setPlayerType(Player.PlayerType.ALL_ROUNDER);
        ResponseEntity<Player> savedPlayer = playerService.updatePlayer(player1);

        //then
        assertThat(savedPlayer.getBody().getPlayerType()).isEqualTo(player1.getPlayerType());
        assertThat(savedPlayer.getBody()).isEqualTo(player1);
        assertThat(savedPlayer.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @DisplayName("Service Test for updating a new player")
    @Test
    public void givenNewPlayer_whenUpdated_thenEntityDoesNotExistsExceptionIsThrown() {
        //given
        given(playerRepo.findById(anyLong())).willReturn(Optional.empty());

        //when
        Executable executable = () -> playerService.updatePlayer(player1);

        //then
        assertThrows(EntityDoesNotExistsException.class, executable);
    }


    @DisplayName("Service Test for deleting player with id that exists")
    @Test
    public void givenPlayerIdThatExists_whenDeleted_thenPlayerWithThatIdIsRemoved(){
        //given
        given(playerRepo.findById(anyLong())).willReturn(Optional.of(player1));
        willDoNothing().given(playerRepo).deleteById(player1.getPlayerId());

        //when
        ResponseEntity<String> deletedPlayerStatus = playerService.deletePlayer(player1.getPlayerId());

        //then
        assertThat(deletedPlayerStatus.getBody()).isEqualTo("PLAYER WITH ID: " + player1.getPlayerId() + ", DELETED SUCCESSFULLY!");
        assertThat(deletedPlayerStatus.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @DisplayName("Service Test for deleting player with id that does not exist")
    @Test
    public void givenPlayerIdThatDoesNotExists_whenDeleted_thenEntityDoesNotExistsExceptionIsThrown(){
        //given
        given(playerRepo.findById(anyLong())).willReturn(Optional.empty());

        //when
        Executable executable = () -> playerService.deletePlayer(player1.getPlayerId());

        //then
        assertThrows(EntityDoesNotExistsException.class, executable);
    }

    @DisplayName("Service Test for deleting All Players")
    @Test
    public void givenNothing_whenDeleteAll_thenDeleteAllForPlayerRepoIsCalledOnce(){
        //given
        willDoNothing().given(playerRepo).deleteAll();

        //when
        ResponseEntity<String> deletedPlayers = playerService.deleteAllPlayers();

        //then
        assertThat(deletedPlayers.getBody()).isEqualTo("ALL PLAYERS DELETED SUCCESSFULLY!");
        assertThat(deletedPlayers.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(playerRepo, times(1)).deleteAll();
    }

    @Override
    public void prepare() {
        player1.setPlayerId(1L);
        player2.setPlayerId(2L);

        teamA.setTeamId(1L);
        teamB.setTeamId(2L);
        teamA.setPlayers(new ArrayList<>());
        teamB.setPlayers(new ArrayList<>());
    }
}
