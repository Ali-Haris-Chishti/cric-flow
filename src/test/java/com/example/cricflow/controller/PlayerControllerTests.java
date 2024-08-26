package com.example.cricflow.controller;

import com.example.cricflow.BaseData;
import com.example.cricflow.exception.EntityDoesNotExistsException;
import com.example.cricflow.service.PlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerController.class)
public class PlayerControllerTests extends BaseData {
    @MockBean
    private PlayerService playerService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Controller Test for retrieving a player with ID that exists")
    @Test
    public void givenPlayerIdThatExists_whenRetrieved_thenPlayerWithThatIdIsReturned() throws Exception {
        //given
        given(playerService.readPlayer(1L)).willReturn(new ResponseEntity<>(player1, HttpStatus.OK));

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
    public void givenPlayerIdThatDoesNotExist_whenRetrieved_thenPlayerWithNullFieldsAndNotFoundStatusIsReturned() throws Exception {
        //given
        given(playerService.readPlayer(anyLong()))
                .willThrow(EntityDoesNotExistsException.class);

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/player/get/1"));

        //then
        response.andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.firstName", nullValue()))
                .andExpect(jsonPath("$.lastName", nullValue()))
                .andExpect(jsonPath("$.playerType", nullValue()))
                .andExpect(jsonPath("$.bowlingStyle", nullValue()))
                .andExpect(jsonPath("$.battingStyle", nullValue()));
    }

    @DisplayName("Controller Test for retrieving a player with ID that does not exists")
    @Test
    public void givenNoting_whenRetrievedAllPlayers_thenListOfAllPlayersIsReturned() throws Exception {
        //given
        given(playerService.readAllPlayers())
                .willReturn(new ResponseEntity<>(Arrays.asList(player1, player2, player3, player4), HttpStatus.OK));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/player/get-all"));

        //then
        response.andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()));
    }

}
