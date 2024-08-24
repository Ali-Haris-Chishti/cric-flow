package com.example.cricflow.repository;

import com.example.cricflow.BaseData;
import com.example.cricflow.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;


@DataJpaTest
public class PlayerRepoTests extends BaseData {

    @Autowired
    private PlayerRepo playerRepo;

    @BeforeEach
    public void setUp() {
        deleteRelatedTablesData();
        prepare();
    }

    @DisplayName("Repository Test for adding a player")
    @Test
    public void givenPlayerObject_whenSaved_thenPlayerIsReturned() {
        //given

        //when
        Player savedPlayer = playerRepo.save(player1);

        //then
        assertThat(savedPlayer.playerEquals(player1)).isTrue();
    }

    @DisplayName("Repository Test for updating a player")
    @Test
    public void givenPlayerObject_whenUpdated_thenUpdatedPlayerIsReturned() {
        //given
        playerRepo.save(player1);

        //when
        player1.setFirstName("Haris");
        player1.setLastName("Chishti");
        Player updatedPlayer = playerRepo.save(player1);

        //then
        assertThat(updatedPlayer.getFirstName()).isEqualTo(player1.getFirstName());
        assertThat(updatedPlayer.getLastName()).isEqualTo(player1.getLastName());
        assertThat(updatedPlayer.playerEquals(player1)).isTrue();
    }

    @DisplayName("Repository Test for retrieving single player")
    @Test
    public void givenPlayerId_whenRetrieved_thenOptionalPlayerIsReturned() {
        //given
        player1 = playerRepo.save(player1);

        //when
        Optional<Player> present = playerRepo.findById(player1.getPlayerId());
        Optional<Player> empty = playerRepo.findById(2L);

        //then
        assertThat(present.isPresent()).isTrue();
        assertThat(empty).isNotPresent();
    }

    @DisplayName("Repository Test for deleting a player")
    @Test
    public void givenPlayerId_whenDeleted_thenPlayerIsRemoved(){
        //given
        playerRepo.save(player1);

        //when
        playerRepo.deleteById(player1.getPlayerId());
        Optional<Player> optionalPlayer = playerRepo.findById(player1.getPlayerId());

        //then
        assertThat(optionalPlayer).isNotPresent();
    }

    @DisplayName("Repository Test for adding multiple players")
    @Test
    public void givenListOfPlayers_whenSaved_thenListOfEmployeesIsReturned() {
        //given
        List<Player> players = List.of(player1, player2);

        //when
        List<Player> savedPlayers = playerRepo.saveAll(players);

        //then
        assertThat(savedPlayers.get(0).playerEquals(players.get(0))).isTrue();
        assertThat(savedPlayers.get(1).playerEquals(players.get(1))).isTrue();
        assertThat(savedPlayers.size()).isEqualTo(players.size());
    }

    @DisplayName("Repository Test for retrieving list of all players")
    @Test
    public void givenNothing_whenAllPlayersRetrieved_thenListOfAllPlayersIsReturned(){
        //given
        List<Player> players = List.of(player1, player2);
        playerRepo.saveAll(players);

        //when
        List<Player> retrievedPlayers = playerRepo.findAll();

        //then
        assertThat(retrievedPlayers.get(0).playerEquals(players.get(0))).isTrue();
        assertThat(retrievedPlayers.get(1).playerEquals(players.get(1))).isTrue();
        assertThat(retrievedPlayers.size()).isEqualTo(players.size());
    }

    @Override
    public void deleteRelatedTablesData() {
        playerRepo.deleteAll();
    }

    @Override
    public void prepare() {
    }
}
