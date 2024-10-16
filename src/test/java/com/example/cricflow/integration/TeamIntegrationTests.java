package com.example.cricflow.integration;

import com.example.cricflow.model.Player;
import com.example.cricflow.model.Team;
import com.example.cricflow.repository.PlayerRepo;
import com.example.cricflow.repository.TeamPlayerRelationRepo;
import com.example.cricflow.repository.TeamRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TeamIntegrationTests {

    @Autowired private TeamRepo teamRepo;
    @Autowired private PlayerRepo playerRepo;
    @Autowired private TeamPlayerRelationRepo relationRepo;

    @Autowired private ObjectMapper objectMapper;
    @Autowired private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        relationRepo.deleteAll();
        playerRepo.deleteAll();
        teamRepo.deleteAll();
    }

    @DisplayName("Integration Test for creating single team with unique name")
    @Test
    public void givenUniqueTeamName_whenCreateIsHit_thenCreatedTeamAndCreatedStatusIsReturned() throws Exception {
        //given
        String teamName = "Lahore Qalandars";

        //when
        ResultActions response = mockMvc.perform(post("/api/v1/team/create")
                .param("teamName", teamName)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teamName", is(teamName.toUpperCase())));
        assertThat(teamRepo.findByTeamNameIgnoreCase(teamName)).isPresent();
    }

    @DisplayName("Integration Test for creating single team with a non unique name")
    @Test
    public void givenNonUniqueTeamName_whenCreateIsHit_thenNameAlreadyExistsExceptionIsThrownAndConflictStatusIsReturned() throws Exception {
        //given
        String nonUniqueTeamName = "Karachi Kings";
        teamRepo.save(new Team(null, nonUniqueTeamName, null));

        //when
        ResultActions response = mockMvc.perform(post("/api/v1/team/create")
                .param("teamName", nonUniqueTeamName)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isConflict());
        assertThat(teamRepo.findAll().size()).isEqualTo(1);
    }

    @DisplayName("Integration Test for retrieving a team with id that exists")
    @Test
    public void givenTeamIdThatExists_whenGetByIdIsHit_thenTeamWithGivenIdAndCreatedStatusIsReturned() throws Exception {
        //given
        Team team = teamRepo.save(new Team(null, "Islamabad United", null));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/team/get/id/" + team.getTeamId())
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName", is(team.getTeamName())));
    }

    @DisplayName("Integration Test for retrieving a team with name that exists")
    @Test
    public void givenTeamNameThatExists_whenGetByNameIsHit_thenTeamWithGivenNameAndCreatedStatusIsReturned() throws Exception {
        //given
        Team team = teamRepo.save(new Team(null, "Islamabad United", null));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/team/get/name/" + team.getTeamName())
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName", is(team.getTeamName())));
    }

    @DisplayName("Integration Test for retrieving a team with id that does not exists")
    @Test
    public void givenTeamIdThatDoesNotExists_whenGetByIdIsHit_thenEntityDoesNotExistsExceptionIsThrownAndNotFoundStatusIsReturned() throws Exception {
        //given
        long idThatDoesNotExists = 99L;

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/team/get/id/" + idThatDoesNotExists)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Integration Test for retrieving a team with name that does not exists")
    @Test
    public void givenTeamNameThatDoesExists_whenGetByNameIsHit_thenEntityDoesNotExistsExceptionIsThrownAndNotFoundStatusIsReturned() throws Exception {
        //given
        String nameThatDoesNotExists = "Quetta Gladiators";

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/team/get/name/" + nameThatDoesNotExists)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Integration Test for retrieving all teams")
    @Test
    public void givenNothing_whenGetAllIsHit_thenListOfAllTeamsAndOkStatusIsReturned() throws Exception {
        //given
        Team team1 = teamRepo.save(new Team(null, "Peshawer Zalmi", null));
        Team team2 = teamRepo.save(new Team(null, "Multan Sultans", null));
        Team team3 = teamRepo.save(new Team(null, "Islamabad United", null));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/team/get-all")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(3)))
                .andExpect(jsonPath("$[2].teamName", is(team1.getTeamName())))
                .andExpect(jsonPath("$[1].teamName", is(team2.getTeamName())))
                .andExpect(jsonPath("$[0].teamName", is(team3.getTeamName())));
        assertThat(teamRepo.findAll().size()).isEqualTo(3);
    }

    @DisplayName("Integration Test for updating a team's name")
    @Test
    public void givenTeamIdAndNewName_whenUpdateIsHit_thenUpdatedTeamAndCreatedStatusIsReturned() throws Exception {
        //given
        Team team = teamRepo.save(new Team(null, "Islamabad United", null));

        //when
        ResultActions response = mockMvc.perform(put("/api/v1/team/update/" + team.getTeamId())
                .param("newName", "Multan Sultans")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teamName", is("Multan Sultans")));
        assertThat(teamRepo.findByTeamNameIgnoreCase("Islamabad United")).isNotPresent();
        assertThat(teamRepo.findByTeamNameIgnoreCase("Multan Sultans")).isPresent();
    }

    @DisplayName("Integration Test for deleting a single team, with ID that exists")
    @Test
    public void givenTeamIdThatExists_whenDeleteIsHit_thenDeletionSuccessMessageAndOkStatusIsReturned() throws Exception {
        //given
        Team team = new Team(null, "Islamabad United", null);
        Player player = new Player(null, "Ali", "Haris", Player.PlayerType.BATSMAN, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER, null);
        player = playerRepo.save(player);
        team.setPlayers(List.of(player));
        team = teamRepo.save(team);
        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/team/delete/" + team.getTeamId())
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk());
        assertThat(teamRepo.findById(team.getTeamId())).isEmpty();
    }

    @DisplayName("Integration Test for deleting a single team, with ID that does not exists")
    @Test
    public void givenTeamIdThatDoesNotExists_whenDeleteIsHit_thenEntityDoesNotExistsExceptionIsThrownAndNotFoundStatusIsReturned() throws Exception {
        // given
        long idThatDoesNotExists = 99L;

        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/team/delete/" + idThatDoesNotExists)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("Integration Test for deleting all teams")
    @Test
    public void givenNothing_whenDeleteAllIsHit_thenDeletionSuccessMessageAndOkStatusIsReturned() throws Exception {
        //given
        teamRepo.saveAll(Arrays.asList(new Team(null, "Team1", null), new Team(null, "Team2", null), new Team(null, "Team3", null)));

        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/team/delete-all")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk());
        assertThat(teamRepo.findAll().size()).isEqualTo(0);
    }

    @DisplayName("Integration Test for adding players to team")
    @Test
    public void givenTeamIdAndListOfPlayerIds_whenAddPlayersToTeamIsHit_thenPlayersAreAddedToTeamAndUpdatedTeamAndOkStatusIsReturned() throws Exception {
        //given
        Team team = teamRepo.save(new Team(null, "Karachi Kings", null));
        Player player1 = playerRepo.save(new Player(null, "Ali", "Haris", Player.PlayerType.BATSMAN, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER, null));
        Player player2 = playerRepo.save(new Player(null, "Tauha", "Kashif", Player.PlayerType.BOWLER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, null));
        Player player3 = playerRepo.save(new Player(null, "Hafiz", "Ammar", Player.PlayerType.ALL_ROUNDER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, null));

        //when
        ResultActions response = mockMvc.perform(put("/api/v1/team/add-players")
                .param("teamId", team.getTeamId().toString())
                .content(objectMapper.writeValueAsString(Arrays.asList(player1.getPlayerId(), player2.getPlayerId(), player3.getPlayerId())))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk());
        assertThat(teamRepo.findById(team.getTeamId()).get().getPlayers().size()).isEqualTo(3);
    }

    @DisplayName("Integration Test for removing players from a team")
    @Test
    public void givenTeamIdAndListOfPlayerIds_whenRemovePlayersFromTeamIsHit_thenPlayersAreRemovedFromTeamAndUpdatedTeamAndOkStatusIsReturned() throws Exception {
        //given
        Team team = teamRepo.save(new Team(null, "Karachi Kings", null));
        Player player1 = playerRepo.save(new Player(null, "Ali", "Haris", Player.PlayerType.BATSMAN, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_LEG_SPINNER, team));
        Player player2 = playerRepo.save(new Player(null, "Tauha", "Kashif", Player.PlayerType.BOWLER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, team));
        Player player3 = playerRepo.save(new Player(null, "Hafiz", "Ammar", Player.PlayerType.ALL_ROUNDER, Player.BattingStyle.RIGHT_HANDED, Player.BowlingStyle.RIGHT_ARM_FAST_BOWLER, team));
        team.setPlayers(Arrays.asList(player1, player2, player3));
        team = teamRepo.save(team);

        //when
        ResultActions response = mockMvc.perform(put("/api/v1/team/remove-players")
                .param("teamId", team.getTeamId().toString())
                .content(objectMapper.writeValueAsString(Arrays.asList(player1.getPlayerId(), player2.getPlayerId(), player3.getPlayerId())))
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("Integration Test for searching teams by name")
    @Test
    public void givenCharacterSequence_whenSearchIsHit_thenListOfPlayerWithThatSequenceInTheirNameAndOkStatusIsReturned() throws Exception {
        //given
        teamRepo.saveAll(Arrays.asList(new Team(null, "Lahore", null), new Team(null, "Karachi", null), new Team(null, "Quetta", null)));

        //when
        ResultActions response = mockMvc.perform(get("/api/v1/team/search")
                .param("seq", "lah")
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].teamName", is("Lahore")));
    }

}
