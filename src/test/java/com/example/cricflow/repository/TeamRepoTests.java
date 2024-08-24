package com.example.cricflow.repository;

import com.example.cricflow.BaseData;
import com.example.cricflow.model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@DataJpaTest
public class TeamRepoTests extends BaseData {

    @Autowired private TeamRepo teamRepo;
    @Autowired private PlayerRepo playerRepo;

    @BeforeEach
    public void setUp() {
        deleteRelatedTablesData();
        prepare();

    }

    @DisplayName("Repository Test for adding a team")
    @Test
    public void givenTeamObject_whenSaved_thenTeamIsReturned() {
        //given

        //when
        Team savedTeam = teamRepo.save(teamA);

        //then
        assertThat(savedTeam.teamEquals(teamA)).isTrue();
    }

    @DisplayName("Repository Test for updating a team")
    @Test
    public void givenTeamObject_whenUpdated_thenUpdatedTeamIsReturned() {
        //given
        teamRepo.save(teamA);

        //when
        teamA.setTeamName("Pishawer Zalmi");
        Team updatedTeam = teamRepo.save(teamA);

        //then
        assertThat(updatedTeam.getTeamName()).isEqualTo(teamA.getTeamName());
        assertThat(updatedTeam.teamEquals(teamA)).isTrue();
    }

    @DisplayName("Repository Test for retrieving single team")
    @Test
    public void givenTeamId_whenRetrieved_thenOptionalTeamIsReturned() {
        //given
        teamA = teamRepo.save(teamA);

        //when
        Optional<Team> present = teamRepo.findById(teamA.getTeamId());
        Optional<Team> empty = teamRepo.findById(99L);

        //then
        assertThat(present).isPresent();
        assertThat(empty).isNotPresent();
    }

    @DisplayName("Repository Test for deleting a team")
    @Test
    public void givenTeamId_whenDeleted_thenTeamIsRemoved(){
        //given
        teamRepo.save(teamA);

        //when
        teamRepo.deleteById(teamA.getTeamId());
        Optional<Team> optionalTeam = teamRepo.findById(teamA.getTeamId());

        //then
        assertThat(optionalTeam).isNotPresent();
    }

    @DisplayName("Repository Test for adding multiple teams")
    @Test
    public void givenListOfTeams_whenSaved_thenListOfEmployeesIsReturned() {
        //given
        List<Team> teams = List.of(teamA, teamB);

        //when
        List<Team> savedTeams = teamRepo.saveAll(teams);

        //then
        assertThat(savedTeams.get(0).teamEquals(teams.get(0))).isTrue();
        assertThat(savedTeams.get(1).teamEquals(teams.get(1))).isTrue();
        assertThat(savedTeams.size()).isEqualTo(teams.size());
    }

    @DisplayName("Repository Test for retrieving list of all teams")
    @Test
    public void givenNothing_whenAllTeamsRetrieved_thenListOfAllTeamsIsReturned(){
        //given
        List<Team> teams = List.of(teamA, teamB);
        teamRepo.saveAll(teams);

        //when
        List<Team> retrievedTeams = teamRepo.findAll();

        //then
        assertThat(retrievedTeams.get(0).teamEquals(teams.get(0))).isTrue();
        assertThat(retrievedTeams.get(1).teamEquals(teams.get(1))).isTrue();
        assertThat(retrievedTeams.size()).isEqualTo(teams.size());
    }

    @Override
    public void deleteRelatedTablesData() {
        teamRepo.deleteAll();
        playerRepo.deleteAll();
    }

    @Override
    public void prepare() {
        // saving players as meta data for teams, also storing it back so player 1 & 2, are updated with new ID
        player1 = playerRepo.save(player1);
        player2 = playerRepo.save(player2);
        player3 = playerRepo.save(player3);
        player4 = playerRepo.save(player4);

        // updating matches to add players for testing
        teamA.setPlayers(new ArrayList<>(Arrays.asList(player1, player2)));
        teamB.setPlayers(new ArrayList<>(Arrays.asList(player3, player4)));
    }
}
