package com.example.cricflow.controller;

import com.example.cricflow.BaseData;
import com.example.cricflow.exception.EntityDoesNotExistsException;
import com.example.cricflow.exception.validator.PlayerFieldsException;
import com.example.cricflow.model.Player;
import com.example.cricflow.service.PlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerController.class)
public class PlayerControllerTests extends BaseData {

    @MockBean private PlayerService playerService;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @DisplayName("Controller Test for creating single player")
    @Test
    public void givenPlayerObject_whenCreateIsHit_thenCreatedPlayerAndCreatedStatusIsReturned() throws Exception {
        //given
        given(playerService.createPlayer(any(Player.class)))
                .willAnswer((invocationOnMock -> {
                    return new ResponseEntity<Player>((Player) invocationOnMock.getArgument(0), HttpStatus.CREATED);
                }));


        //when
        ResultActions response = mockMvc.perform(post("/api/v1/player/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player1)));

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(player1.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(player1.getLastName())))
                .andExpect(jsonPath("$.playerType", is(player1.getPlayerType().toString())))
                .andExpect(jsonPath("$.bowlingStyle", is(player1.getBowlingStyle().toString())))
                .andExpect(jsonPath("$.battingStyle", is(player1.getBattingStyle().toString())));
    }

    @DisplayName("Controller Test for creating single player with violations")
    @Test
    public void givenPlayerObjectWithConstraintViolations_whenCreateIsHit_thenPlayerFieldViolationExceptionIsThrownAndNotAcceptableStatusIsReturned() throws Exception {
        //given
        given(playerService.createPlayer(any(Player.class)))
                .willThrow(PlayerFieldsException.class);


        //when
        ResultActions response = mockMvc.perform(post("/api/v1/player/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player1)));

        //then
        response.andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @DisplayName("Controller Test for creating multiple players")
    @Test
    public void givenListOfPlayerObjects_whenCreateAllIsHit_thenCreatedPlayerListAndCreatedStatusIsReturned() throws Exception {
        //given
        given(playerService.createMultiplePlayers(anyList()))
                .willAnswer((invocationOnMock -> {
                    return new ResponseEntity<List<Player>>((List<Player>) invocationOnMock.getArgument(0), HttpStatus.CREATED);
                }));


        //when
        ResultActions response = mockMvc.perform(post("/api/v1/player/create-all")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(player1, player2, player3, player4))));

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.size()", is(4)));
    }


    @DisplayName("Controller Test for retrieving a player with ID that exists")
    @Test
    public void givenPlayerIdThatExists_whenGetIsHit_thenPlayerWithThatIdAndOkStatusIsReturned() throws Exception {
        //given
        given(playerService.readPlayer(1L))
                .willReturn(new ResponseEntity<>(player1, HttpStatus.OK));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/player/get/1"));

        //then
        response.andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.firstName", is(player1.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(player1.getLastName())))
                .andExpect(jsonPath("$.playerType", is(player1.getPlayerType().toString())))
                .andExpect(jsonPath("$.bowlingStyle", is(player1.getBowlingStyle().toString())))
                .andExpect(jsonPath("$.battingStyle", is(player1.getBattingStyle().toString())));
    }

    @DisplayName("Controller Test for retrieving a player with ID that does not exists")
    @Test
    public void givenPlayerIdThatDoesNotExist_whenGetIsHit_thenEntityDoesNotExistsExceptionIsThrown() throws Exception {
        //given
        given(playerService.readPlayer(anyLong()))
                .willThrow(new EntityDoesNotExistsException("PLAYER", 1L));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/player/get/1"));

        //then
        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("PLAYER with ID: 1 does not exist")));
    }

    @DisplayName("Controller Test for retrieving all players")
    @Test
    public void givenNoting_whenRetrievedAllPlayers_thenListOfAllPlayersIsReturned() throws Exception {
        //given
        given(playerService.findFilteredPlayers(any(Player.PlayerType.class), any(Player.BattingStyle.class), any(Player.BowlingStyle.class)))
                .willReturn(new ResponseEntity<>(Arrays.asList(player1, player2, player3, player4), HttpStatus.OK));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/player/get-all")
                .param("playerType", "BATSMAN")
                .param("battingStyle", "RIGHT_HANDED")
                .param("bowlingStyle", "RIGHT_ARM_LEG_SPINNER")
        );

        //then
        response.andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.size()", is(4)));
    }

    @DisplayName("Controller Test for update single player")
    @Test
    public void givenPlayerObject_whenUpdateIsHit_thenUpdatedPlayerAndCreatedStatusIsReturned() throws Exception {
        //given
        player1.setFirstName("Ahmad");
        given(playerService.updatePlayer(any(Player.class)))
                .willAnswer((invocationOnMock -> {
                    return new ResponseEntity<Player>((Player) invocationOnMock.getArgument(0), HttpStatus.CREATED);
                }));


        //when
        ResultActions response = mockMvc.perform(put("/api/v1/player/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player1)));

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("Ahmad")));
        player1.setFirstName("Ali"); // making name as before, as player1 is static variable, so it wont affect other tests
    }

    @DisplayName("Controller Test for deleting a player with ID that exists")
    @Test
    public void givenPlayerIdThatExists_whenDeleteIsHit_thenPlayerWithThatIdIsDeletedAndOkStatusIsReturned() throws Exception {
        //given
        given(playerService.deletePlayer(1L))
                .willReturn(new ResponseEntity<>("", HttpStatus.OK));

        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/player/delete/1"));

        //then
        response.andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("Controller Test for deleting a player with ID that does not exists")
    @Test
    public void givenPlayerIdThatDoesNotExist_whenDeleteIsHit_thenEntityDoesNotExistsExceptionIsThrownAndNotFoundStatusIsReturned() throws Exception {
        //given
        given(playerService.deletePlayer(anyLong()))
                .willThrow(new EntityDoesNotExistsException("PLAYER", 1L));

        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/player/delete/1"));

        //then
        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("PLAYER with ID: 1 does not exist")));
    }

    @DisplayName("Controller Test for deleting all players")
    @Test
    public void givenNothing_whenDeleteAllIsHit_thenAllPlayersAreDeletedAndOkIsReturned() throws Exception {
        //given
        given(playerService.deleteAllPlayers())
                .willReturn(new ResponseEntity<>("", HttpStatus.OK));

        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/player/delete-all"));

        //then
        response.andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("Controller Test for searching a player")
    @Test
    public void givenCharacterSequence_whenSearchIsHit_thenListOfAllGroundsWithGivenSequenceInTheirNameAndOkStatusIsReturned() throws Exception{
        //given
        given(playerService.searchPlayersByName("a h"))
                .willReturn(new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK));
        //when
        ResultActions response = mockMvc.perform(get("/api/v1/player/search")
                .param("seq", "a h")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk());
    }

}
