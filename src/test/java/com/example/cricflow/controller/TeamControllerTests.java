package com.example.cricflow.controller;

import com.example.cricflow.BaseData;
import com.example.cricflow.exception.EntityDoesNotExistsException;
import com.example.cricflow.exception.NameAlreadyExistsException;
import com.example.cricflow.model.Team;
import com.example.cricflow.service.TeamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamController.class)
public class TeamControllerTests extends BaseData {

    @MockBean private TeamService teamService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;


    @BeforeEach
    void setup() {
        prepare();
    }


    @DisplayName("Controller Test for creating single team with unique name")
    @Test
    public void givenUniqueTeamName_whenCreateIsHit_thenCreatedTeamAndCreatedStatusIsReturned() throws Exception {
        //given
        given(teamService.createTeam(anyString()))
                .willAnswer((invocationOnMock -> {
                    return new ResponseEntity<Team>((Team) new Team(1L, ((String) invocationOnMock.getArgument(0)).toUpperCase(), null), HttpStatus.CREATED);
                }));

        //when
        ResultActions response = mockMvc.perform(post("/api/v1/team/create")
                        .param("teamName", "Lahore Qalandars")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teamName", is("LAHORE QALANDARS")));
    }

    @DisplayName("Controller Test for creating single team with a non unique name")
    @Test
    public void givenNonUniqueTeamName_whenCreateIsHit_thenNameAlreadyExistsExceptionIsThrownAndConflictStatusIsReturned() throws Exception {
        //given
        given(teamService.createTeam(anyString()))
                .willThrow(NameAlreadyExistsException.class);


        //when
        ResultActions response = mockMvc.perform(post("/api/v1/team/create")
                .param("teamName", "Lahore Qalandars")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isConflict());
    }

    @DisplayName("Controller Test for retrieving a team with id that exists")
    @Test
    public void givenTeamIdThatExists_whenGetByIdIsHit_thenTeamWithGivenIdAndCreatedStatusIsReturned() throws Exception {
        //given
        given(teamService.readTeam(teamA.getTeamId()))
                .willReturn(new ResponseEntity<Team>(teamA, HttpStatus.OK));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/team/get/id/" + teamA.getTeamId())
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName", is(teamA.getTeamName())));
    }

    @DisplayName("Controller Test for retrieving a team with name that exists")
    @Test
    public void givenTeamNameThatExists_whenGetByNameIsHit_thenTeamWithGivenNameAndCreatedStatusIsReturned() throws Exception {
        //given
        given(teamService.readTeam(teamA.getTeamName()))
                .willReturn(new ResponseEntity<Team>(teamA, HttpStatus.OK));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/team/get/name/" + teamA.getTeamName())
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName", is(teamA.getTeamName())));
    }

    @DisplayName("Controller Test for retrieving a team with id that does not exists")
    @Test
    public void givenTeamIdThatDoesNotExists_whenGetByIdIsHit_thenEntityDoesNotExistsExceptionIsThrownAndNotFoundStatusIsReturned() throws Exception {
        //given
        long idThatDoesNotExists = 99L;
        given(teamService.readTeam(idThatDoesNotExists))
                .willThrow(EntityDoesNotExistsException.class);

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/team/get/id/" + idThatDoesNotExists)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Controller Test for retrieving a team with name that does not exists")
    @Test
    public void givenTeamNameThatDoesExists_whenGetByNameIsHit_thenEntityDoesNotExistsExceptionIsThrownAndNotFoundStatusIsReturned() throws Exception {
        //given
        String nameThatDoesNotExists = "Team";
        given(teamService.readTeam(nameThatDoesNotExists))
                .willThrow(EntityDoesNotExistsException.class);

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/team/get/name/" + nameThatDoesNotExists)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Controller Test for retrieving all teams")
    @Test
    public void givenNothing_whenGetAllIsHit_thenListOfAllTeamsAndOkStatusIsReturned() throws Exception {
        //given
        given(teamService.readAllTeams())
                .willReturn(new ResponseEntity<>(Arrays.asList(teamA, teamB), HttpStatus.OK));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/team/get-all")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @DisplayName("Controller Test for updating a team's name")
    @Test
    public void givenTeamIdAndNewName_whenUpdateIsHit_thenUpdatedTeamAndCreatedStatusIsReturned() throws Exception {
        //given
        given(teamService.updateTeamName("TEAMNAME", 1L))
                .willReturn(new ResponseEntity<>(new Team(null, "TEAMNAME", null), HttpStatus.CREATED));

        //when
        ResultActions response = mockMvc.perform(put("/api/v1/team/update/1")
                        .param("newName", "TEAMNAME")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teamName", is("TEAMNAME")));
    }

    @DisplayName("Controller Test for deleting a single team, with ID that exists")
    @Test
    public void givenTeamIdThatExists_whenDeleteIsHit_thenDeletionSuccessMessageAndOkStatusIsReturned() throws Exception {
        //given
        given(teamService.deleteTeam(1L))
                .willReturn(new ResponseEntity<>("", HttpStatus.OK));

        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/team/delete/1")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("Controller Test for deleting a single team, with ID that does not exists")
    @Test
    public void givenTeamIdThatDoesNotExists_whenDeleteIsHit_thenEntityDoesNotExistsExceptionIsThrownAndNotFoundStatusIsReturned() throws Exception {
        // given
        long idThatDoesNotExists = 99L;
        given(teamService.deleteTeam(idThatDoesNotExists))
                .willThrow(EntityDoesNotExistsException.class);

        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/team/delete/" + idThatDoesNotExists)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Controller Test for deleting all teams")
    @Test
    public void givenNothing_whenDeleteAllIsHit_thenDeletionSuccessMessageAndOkStatusIsReturned() throws Exception {
        //given
        given(teamService.deleteAllTeams())
                .willReturn(new ResponseEntity<>("", HttpStatus.OK));

        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/team/delete-all")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk());
    }

    @Override
    public void prepare() {
        teamA.setTeamId(1L);
        teamA.setPlayers(null);
        teamB.setTeamId(2L);
        teamB.setPlayers(null);
    }
}
