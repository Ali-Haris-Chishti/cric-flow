package com.example.cricflow.integration;

import com.example.cricflow.model.Player;
import com.example.cricflow.repository.PlayerRepo;
import com.example.cricflow.repository.TeamPlayerRelationRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PlayerIntegrationTests {

    @Autowired private PlayerRepo playerRepo;
    @Autowired private TeamPlayerRelationRepo relationRepo;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        playerRepo.deleteAll();
    }

    @DisplayName("Integration Test for creating single player")
    @Test
    public void givenPlayerObject_whenCreateIsHit_thenCreatedPlayerAndCreatedStatusIsReturned() throws Exception {
        //given
        Player player = new Player(null, "Ali", "Haris", Player.PlayerType.BATSMAN, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER, null);

        //when
        ResultActions response = mockMvc.perform(post("/api/v1/player/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player)));

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(player.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(player.getLastName())))
                .andExpect(jsonPath("$.playerType", is(player.getPlayerType().toString())))
                .andExpect(jsonPath("$.bowlingStyle", is(player.getBowlingStyle().toString())))
                .andExpect(jsonPath("$.battingStyle", is(player.getBattingStyle().toString())));
        assertThat(playerRepo.findAll().size()).isEqualTo(1);
    }

    @DisplayName("Integration Test for creating single player with violations")
    @Test
    public void givenPlayerObjectWithConstraintViolations_whenCreateIsHit_thenPlayerFieldViolationExceptionIsThrownAndNotAcceptableStatusIsReturned() throws Exception {
        //given
        Player player = new Player(null, "Ali", "H", null, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER, null);


        //when
        ResultActions response = mockMvc.perform(post("/api/v1/player/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player)));

        //then
        response.andDo(print())
                .andExpect(status().isNotAcceptable());
        assertThat(playerRepo.findAll().size()).isEqualTo(0); // making sure that invalid player is not saved in the database
    }

    @DisplayName("Integration Test for creating multiple players")
    @Test
    public void givenListOfPlayerObjects_whenCreateAllIsHit_thenCreatedPlayerListAndCreatedStatusIsReturned() throws Exception {
        //given
        Player player1 = new Player(null, "Ali", "Haris", Player.PlayerType.BATSMAN, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER, null);
        Player player2 = new Player(null, "Tauha", "Kashif", Player.PlayerType.BOWLER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, null);
        Player player3 = new Player(null, "Hafiz", "Ammar", Player.PlayerType.ALL_ROUNDER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, null);

        //when
        ResultActions response = mockMvc.perform(post("/api/v1/player/create-all")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(player1, player2, player3))));

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.size()", is(3)));
        assertThat(playerRepo.findAll().size()).isEqualTo(3);
    }


    @DisplayName("Integration Test for retrieving a player with ID that exists")
    @Test
    public void givenPlayerIdThatExists_whenGetIsHit_thenPlayerWithThatIdAndOkStatusIsReturned() throws Exception {
        //given
        Player player = new Player(1L, "Ali", "Haris", Player.PlayerType.BATSMAN, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER, null);
        player = playerRepo.save(player);

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/player/get/" + player.getPlayerId()));

        //then
        response.andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.firstName", is(player.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(player.getLastName())))
                .andExpect(jsonPath("$.playerType", is(player.getPlayerType().toString())))
                .andExpect(jsonPath("$.bowlingStyle", is(player.getBowlingStyle().toString())))
                .andExpect(jsonPath("$.battingStyle", is(player.getBattingStyle().toString())));
    }

    @DisplayName("Integration Test for retrieving a player with ID that does not exists")
    @Test
    public void givenPlayerIdThatDoesNotExist_whenGetIsHit_thenEntityDoesNotExistsExceptionIsThrown() throws Exception {
        //given
        long idThatDoesNotExists = 99L;

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/player/get/" + idThatDoesNotExists));

        //then
        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("PLAYER with ID: " + idThatDoesNotExists + " does not exist")));
    }

    @DisplayName("Integration Test for retrieving all players")
    @Test
    public void givenNoting_whenRetrievedAllPlayers_thenListOfAllPlayersIsReturned() throws Exception {
        //given
        Player player1 = new Player(null, "Ali", "Haris", Player.PlayerType.BATSMAN, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER, null);
        Player player2 = new Player(null, "Tauha", "Kashif", Player.PlayerType.BOWLER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, null);
        Player player3 = new Player(null, "Hafiz", "Ammar", Player.PlayerType.ALL_ROUNDER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, null);
        playerRepo.saveAll(Arrays.asList(player1, player2, player3));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/player/get-all"));

        //then
        response.andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.size()", is(3)));
    }

    @DisplayName("Integration Test for retrieving all players with given player type")
    @Test
    public void givenPlayerType_whenRetrievedAllPlayers_thenListOfAllPlayersWithThatPlayerTypeIsReturned() throws Exception {
        //given
        Player player1 = new Player(null, "Ali", "Haris", Player.PlayerType.BATSMAN, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER, null);
        Player player2 = new Player(null, "Tauha", "Kashif", Player.PlayerType.BOWLER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, null);
        Player player3 = new Player(null, "Hafiz", "Ammar", Player.PlayerType.ALL_ROUNDER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, null);
        playerRepo.saveAll(Arrays.asList(player1, player2, player3));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/player/get-all")
                .param("playerType", "BATSMAN")
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].firstName", is(player1.getFirstName())))
                .andExpect(jsonPath("$[0].lastName", is(player1.getLastName())))
                .andExpect(jsonPath("$[0].playerType", is(player1.getPlayerType().toString())))
                .andExpect(jsonPath("$[0].bowlingStyle", is(player1.getBowlingStyle().toString())))
                .andExpect(jsonPath("$[0].battingStyle", is(player1.getBattingStyle().toString())));
    }

    @DisplayName("Integration Test for retrieving all players with given batting style")
    @Test
    public void givenBattingStyle_whenRetrievedAllPlayers_thenListOfAllPlayersWithThatBattingStyleIsReturned() throws Exception {
        //given
        Player player1 = new Player(null, "Ali", "Haris", Player.PlayerType.BATSMAN, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER, null);
        Player player2 = new Player(null, "Tauha", "Kashif", Player.PlayerType.BOWLER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, null);
        Player player3 = new Player(null, "Hafiz", "Ammar", Player.PlayerType.ALL_ROUNDER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, null);
        playerRepo.saveAll(Arrays.asList(player1, player2, player3));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/player/get-all")
                .param("battingStyle", "RIGHT_HANDED")
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(3)))
                .andExpect(jsonPath("$[0].battingStyle", is(Player.BattingStyle.RIGHT_HANDED.toString())))
                .andExpect(jsonPath("$[1].battingStyle", is(Player.BattingStyle.RIGHT_HANDED.toString())))
                .andExpect(jsonPath("$[2].battingStyle", is(Player.BattingStyle.RIGHT_HANDED.toString())));
    }

    @DisplayName("Integration Test for retrieving all players with given player type")
    @Test
    public void givenBowlingStyle_whenRetrievedAllPlayers_thenListOfAllPlayersWithThatBowlingStyleIsReturned() throws Exception {
        //given
        Player player1 = new Player(null, "Ali", "Haris", Player.PlayerType.BATSMAN, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER, null);
        Player player2 = new Player(null, "Tauha", "Kashif", Player.PlayerType.BOWLER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, null);
        Player player3 = new Player(null, "Hafiz", "Ammar", Player.PlayerType.ALL_ROUNDER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, null);
        playerRepo.saveAll(Arrays.asList(player1, player2, player3));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/player/get-all")
                .param("bowlingStyle", "RIGHT_ARM_FAST_BOWLER")
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].bowlingStyle", is(Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER.toString())))
                .andExpect(jsonPath("$[1].bowlingStyle", is(Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER.toString())));
    }

    @DisplayName("Integration Test for update single player")
    @Test
    public void givenPlayerObject_whenUpdateIsHit_thenUpdatedPlayerAndCreatedStatusIsReturned() throws Exception {
        //given
        Player player = new Player(null, "Ali", "Haris", Player.PlayerType.BATSMAN, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER, null);
        player = playerRepo.save(player);
        player.setPlayerType(Player.PlayerType.ALL_ROUNDER);

        //when
        ResultActions response = mockMvc.perform(put("/api/v1/player/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player)));

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.playerType", is(Player.PlayerType.ALL_ROUNDER.toString())));
        assertThat(playerRepo.findById(player.getPlayerId()).get().getPlayerType()).isEqualTo(Player.PlayerType.ALL_ROUNDER);
    }

    @DisplayName("Integration Test for deleting a player with ID that exists")
    @Test
    public void givenPlayerIdThatExists_whenDeleteIsHit_thenPlayerWithThatIdIsDeletedAndOkStatusIsReturned() throws Exception {
        //given
        Player player = new Player(null, "Ali", "Haris", Player.PlayerType.BATSMAN, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER, null);
        player = playerRepo.save(player);

        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/player/delete/" + player.getPlayerId()));

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("PLAYER WITH ID: " + player.getPlayerId() + ", DELETED SUCCESSFULLY!")));
        assertThat(playerRepo.findById(player.getPlayerId())).isNotPresent();
        assertThat(playerRepo.findAll().size()).isEqualTo(0);
        assertThat(relationRepo.findAllByPlayer(player).size()).isEqualTo(0);
    }

    @DisplayName("Integration Test for deleting a player with ID that does not exists")
    @Test
    public void givenPlayerIdThatDoesNotExist_whenDeleteIsHit_thenEntityDoesNotExistsExceptionIsThrownAndNotFoundStatusIsReturned() throws Exception {
        //given
        long idThatDoesNotExists = 999L;

        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/player/delete/" + idThatDoesNotExists));

        //then
        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("PLAYER with ID: " + idThatDoesNotExists + " does not exist")));
    }

    @DisplayName("Integration Test for deleting all players")
    @Test
    public void givenNothing_whenDeleteAllIsHit_thenAllPlayersAreDeletedAndOkIsReturned() throws Exception {
        //given
        Player player1 = new Player(null, "Ali", "Haris", Player.PlayerType.BATSMAN, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER, null);
        Player player2 = new Player(null, "Tauha", "Kashif", Player.PlayerType.BOWLER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, null);
        Player player3 = new Player(null, "Hafiz", "Ammar", Player.PlayerType.ALL_ROUNDER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, null);
        playerRepo.saveAll(Arrays.asList(player1, player2, player3));

        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/player/delete-all"));

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("ALL 3 PLAYERS DELETED SUCCESSFULLY!")));
        assertThat(playerRepo.findAll().size()).isEqualTo(0);
        assertThat(relationRepo.findAll().size()).isEqualTo(0);
    }

    @DisplayName("Integration Test for searching a player")
    @Test
    public void givenCharacterSequence_whenSearchIsHit_thenListOfAllGroundsWithGivenSequenceInTheirNameAndOkStatusIsReturned() throws Exception{
        //given
        Player player1 = new Player(null, "Ali", "Haris", Player.PlayerType.BATSMAN, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER, null);
        Player player2 = new Player(null, "Tauha", "Kashif", Player.PlayerType.BOWLER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, null);
        Player player3 = new Player(null, "Hafiz", "Ammar", Player.PlayerType.ALL_ROUNDER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, null);
        playerRepo.saveAll(Arrays.asList(player1, player2, player3));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/player/search")
                .param("seq", "ar")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
    }

}
