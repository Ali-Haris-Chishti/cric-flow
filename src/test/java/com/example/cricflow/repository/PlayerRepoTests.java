package com.example.cricflow.repository;

import com.example.cricflow.BaseData;
import com.example.cricflow.model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@DataJpaTest
public class PlayerRepoTests extends BaseData {

    @Autowired private PlayerRepo playerRepo;
    @Autowired private TeamRepo teamRepo;

    @BeforeEach
    public void setUp() {
        prepare();
    }

    @AfterEach
    public void tearDown() {
        deleteRelatedTablesData();
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
        player1 = playerRepo.save(player1);

        //when
        player1.setFirstName("Haris");
        player1.setLastName("Chishti");
        Player updatedPlayer = playerRepo.save(player1);

        //then
        assertThat(updatedPlayer.getFirstName()).isEqualTo(player1.getFirstName());
        assertThat(updatedPlayer.getLastName()).isEqualTo(player1.getLastName());
        assertThat(updatedPlayer.playerEquals(player1)).isTrue();

        player1.setFirstName("Ali"); // restoring it to the original value, as it is static, so it may not affect others
        player1.setLastName("Haris"); // restoring it to the original value, as it is static, so it may not affect others
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

    @DisplayName("Repository Test for getting all players currently not in any team")
    @Test
    public void givenNothing_whenFindAllByTeamIsNullIsCalled_thenAllPlayersCurrentlyInNoTeamAreReturned(){
        //given
        teamA.setPlayers(null); // making sure if any player added by other tests still present, is deleted
        teamA = teamRepo.save(teamA);
        player1.setTeam(teamA);
        playerRepo.saveAll(Arrays.asList(player1, player2, player3, player4));

        //when
        List<Player> players = playerRepo.findAllByTeamIsNull();
        //then
        assertThat(players.size()).isEqualTo(3);
        assertThat(players.get(0).playerEquals(player2)).isTrue();
        assertThat(players.get(1).playerEquals(player3)).isTrue();
        assertThat(players.get(2).playerEquals(player4)).isTrue();
    }

    @DisplayName("Repository Test for getting all players currently not in any team")
    @Test
    public void givenNothing_whenFindAllByTeamIsNotNullIsCalled_thenAllPlayersCurrentlyInAnyTeamAreReturned(){
        //given
        teamA.setPlayers(null); // making sure if any player added by other tests still present, is deleted
        teamA = teamRepo.save(teamA);
        player1.setTeam(teamA);
        playerRepo.saveAll(Arrays.asList(player1, player2, player3, player4));

        //when
        List<Player> players = playerRepo.findAllByTeamIsNotNull();
        //then
        assertThat(players.size()).isEqualTo(1);
        assertThat(players.get(0).playerEquals(player1)).isTrue();
    }

    @DisplayName("Repository Test for getting all players currently in any of the teams")
    @Test
    public void givenCharacterSequence_whenFindAllByFullNameContainingIsCalled_thenListOfAllPlayersHavingThatInFullNamesIReturned(){
        //given
        // fullName = firstName + space + lastName
        // player1 -> Ali Haris, player -> Tauha Kashif, player3 -> Ali Hamza, player4 -> Abdul Sami
        playerRepo.saveAll(Arrays.asList(player1, player2, player3, player4));

        //when
        List<Player> playersWithSequence1 = playerRepo.findAllByFullNameContaining("ali ha");
        List<Player> playersWithSequence2 = playerRepo.findAllByFullNameContaining("tau");

        //then
        assertThat(playersWithSequence1.size()).isEqualTo(2);
        assertThat(playersWithSequence1.get(0).playerEquals(player1)).isTrue();
        assertThat(playersWithSequence1.get(1).playerEquals(player3)).isTrue();

        assertThat(playersWithSequence2.size()).isEqualTo(1);
        assertThat(playersWithSequence2.get(0).playerEquals(player2)).isTrue();
    }

    @DisplayName("Repository Test for getting all players with given player type")
    @Test
    public void givenPlayerType_whenFindAllByPlayerTypeIsCalled_thenListOfAllPlayersOfThatTypeIsReturned(){
        //given
        playerRepo.saveAll(Arrays.asList(player1, player2, player3, player4));

        //when
        List<Player> players = playerRepo.findAllByPlayerType(Player.PlayerType.BATSMAN);
        //then
        assertThat(players.size()).isEqualTo(2);
        assertThat(players.get(0).playerEquals(player1)).isTrue();
        assertThat(players.get(1).playerEquals(player4)).isTrue();
    }

    @DisplayName("Repository Test for getting all players with given batting style")
    @Test
    public void givenBattingStyle_whenFindAllByBattingStyleIsCalled_thenListOfAllPlayersOfThatBattingStyleIsReturned(){
        //given
        playerRepo.saveAll(Arrays.asList(player1, player2, player3, player4));

        //when
        List<Player> players = playerRepo.findAllByBattingStyle(Player.BattingStyle.LEFT_HANDED);
        //then
        assertThat(players.size()).isEqualTo(1);
        assertThat(players.get(0).playerEquals(player3)).isTrue();
    }

    @DisplayName("Repository Test for getting all players with given bowling style")
    @Test
    public void givenBattingStyle_whenFindAllByBowlingStyleIsCalled_thenListOfAllPlayersOfThatBowlingStyleIsReturned(){
        //given
        playerRepo.saveAll(Arrays.asList(player1, player2, player3, player4));

        //when
        List<Player> players = playerRepo.findAllByBowlingStyle(Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER);
        //then
        assertThat(players.size()).isEqualTo(1);
        assertThat(players.get(0).playerEquals(player1)).isTrue();
    }

    @DisplayName("Repository Test for getting all players with given player type and batting style")
    @Test
    public void givenPlayerTypeAndBattingStyle_whenFindAllByPlayerTypeAndBattingStyleIsCalled_thenListOfAllMatchingPlayersIsReturned() {
        //given
        playerRepo.saveAll(Arrays.asList(player1, player2, player3, player4));
        System.out.println("***************************\n************************\n**************************");
        System.out.println(playerRepo.findAll());
        System.out.println("***************************\n************************\n**************************");

        //when
        List<Player> players = playerRepo.findAllByPlayerTypeAndBattingStyle(Player.PlayerType.BATSMAN, Player.BattingStyle.RIGHT_HANDED);

        //then
        assertThat(players.size()).isEqualTo(2);
        assertThat(players.get(0).playerEquals(player1)).isTrue();
        assertThat(players.get(1).playerEquals(player4)).isTrue();
    }

    @DisplayName("Repository Test for getting all players with given player type and bowling style")
    @Test
    public void givenPlayerTypeAndBowlingStyle_whenFindAllByPlayerTypeAndBowlingStyleIsCalled_thenListOfAllMatchingPlayersIsReturned() {
        //given
        playerRepo.saveAll(Arrays.asList(player1, player2, player3, player4));

        //when
        List<Player> players = playerRepo.findAllByPlayerTypeAndBowlingStyle(Player.PlayerType.BATSMAN, Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER);

        //then
        assertThat(players.size()).isEqualTo(1);
        assertThat(players.get(0).playerEquals(player1)).isTrue();
    }

    @DisplayName("Repository Test for getting all players with given batting style and bowling style")
    @Test
    public void givenBattingStyleAndBowlingStyle_whenFindAllByBattingStyleAndBowlingStyleIsCalled_thenListOfAllMatchingPlayersIsReturned() {
        //given
        playerRepo.saveAll(Arrays.asList(player1, player2, player3, player4));

        //when
        List<Player> players = playerRepo.findAllByBattingStyleAndBowlingStyle(Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER);

        //then
        assertThat(players.size()).isEqualTo(1);
        assertThat(players.get(0).playerEquals(player2)).isTrue();
    }

    @DisplayName("Repository Test for getting all players with given player type, batting style, and bowling style")
    @Test
    public void givenPlayerTypeBattingStyleAndBowlingStyle_whenFindAllByPlayerTypeAndBattingStyleAndBowlingStyleIsCalled_thenListOfAllMatchingPlayersIsReturned() {
        //given
        playerRepo.saveAll(Arrays.asList(player1, player2, player3, player4));

        //when
        List<Player> players = playerRepo.findAllByPlayerTypeAndBattingStyleAndBowlingStyle(
                Player.PlayerType.BATSMAN,
                Player.BattingStyle.RIGHT_HANDED,
                Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER
        );

        //then
        assertThat(players.size()).isEqualTo(1);
        assertThat(players.get(0).playerEquals(player1)).isTrue();
    }



    @Override
    public void deleteRelatedTablesData() {
        playerRepo.deleteAll();
        teamRepo.deleteAll();
    }

    @Override
    public void prepare() {
        player1.setTeam(null);
        player2.setTeam(null);
        player3.setTeam(null);
        player4.setTeam(null);
    }
}
