package com.example.cricflow.service;

import com.example.cricflow.BaseData;
import com.example.cricflow.exception.DuplicatePlayerInTeamException;
import com.example.cricflow.exception.EntityDoesNotExistsException;
import com.example.cricflow.exception.NameAlreadyExistsException;
import com.example.cricflow.exception.PlayerRemovalFromTeamException;
import com.example.cricflow.model.Player;
import com.example.cricflow.model.Team;
import com.example.cricflow.model.TeamPlayerRelation;
import com.example.cricflow.repository.PlayerRepo;
import com.example.cricflow.repository.TeamPlayerRelationRepo;
import com.example.cricflow.repository.TeamRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTests extends BaseData {

    @InjectMocks
    private TeamService teamService;

    @Mock
    private TeamRepo teamRepo;
    @Mock
    private PlayerRepo playerRepo;
    @Mock
    private TeamPlayerRelationRepo relationRepo;

    @BeforeEach
    void setUp() {
        prepare();
    }


    @DisplayName("Service Test for creating a Team with unique name")
    @Test
    public void givenUniqueTeamName_whenCreateTeamCalled_thenCreatedTeamWithGivenNameIsReturned(){
        //given
        String teamName = "Lahore Qalandars";
        Team expectedTeam = new Team(1L, teamName.toUpperCase(), null);
        given(teamRepo.findByTeamNameIgnoreCase(anyString())).willReturn(Optional.empty());
        given(teamRepo.save(any(Team.class))).willReturn(expectedTeam);

        //when
        ResponseEntity<Team> createdTeam = teamService.createTeam(teamName);

        //then
        assertThat(createdTeam.getBody()).isEqualTo(expectedTeam);
        assertThat(createdTeam.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        System.out.println(createdTeam.getBody());
    }

    @DisplayName("Service Test for creating a Team with a non unique name")
    @Test
    public void givenNonUniqueTeamName_whenCreateTeamCalled_thenNameAlreadyExistsExceptionIsThrown(){
        //given
        String teamName = "Lahore Qalandars";
        given(teamRepo.findByTeamNameIgnoreCase(anyString())).willReturn(Optional.of(new Team()));

        //when
        Executable executable = () ->teamService.createTeam(teamName);

        //then
        assertThrows(NameAlreadyExistsException.class, executable);
    }

    @DisplayName("Service Test for creating a Team with unique name")
    @Test
    public void givenMultipleUniqueTeamNames_whenCreateAllTeamsCalled_thenListOfCreatedTeamsWithGivenNameIsReturned(){
        //given
        List<String> teamNames = Arrays.asList("Lahore Qalandars", "Karachi Kings", "Islamabad United");
        List<Team> expectedTeams = new ArrayList<>();
        for (String teamName : teamNames) {
            long id = 0L;
            expectedTeams.add(new Team(++id, teamName.toUpperCase(), null));
        }
        given(teamRepo.findByTeamNameIgnoreCase(anyString())).willReturn(Optional.empty());
        Team team1 = new Team(null, teamNames.get(0).toUpperCase(), null);
        Team team2 = new Team(null, teamNames.get(1).toUpperCase(), null);
        Team team3 = new Team(null, teamNames.get(2).toUpperCase(), null);
        given(teamRepo.saveAll(Arrays.asList(team1, team2, team3))).willReturn(expectedTeams);

        //when
        ResponseEntity<List<Team>> createdTeams = teamService.createMultipleTeams(teamNames);

        //then
        assertThat(createdTeams.getBody().get(0)).isEqualTo(expectedTeams.get(0));
        assertThat(createdTeams.getBody().get(1)).isEqualTo(expectedTeams.get(1));
        assertThat(createdTeams.getBody().get(2)).isEqualTo(expectedTeams.get(2));
        System.out.println(createdTeams);
    }

    @DisplayName("Service Test for adding players to a team, present in no other team previously")
    @Test
    public void givenListOfPlayersNotPresentInAnyOtherTeam_whenAddedToATeam_thenPlayersAreUpdatedAndUpdatedListIsReturned(){
        //given
        teamA.setPlayers(Arrays.asList(player1, player2));
        List<Long> playersToBeAdded = Arrays.asList(player3.getPlayerId(), player4.getPlayerId());
        given(teamRepo.findById(teamA.getTeamId())).willReturn(Optional.of(teamA));
        given(playerRepo.findById(player3.getPlayerId())).willReturn(Optional.of(player3));
        given(playerRepo.findById(player4.getPlayerId())).willReturn(Optional.of(player4));
        given(playerRepo.save(player3)).willReturn(player3);
        given(playerRepo.save(player4)).willReturn(player4);
        given(teamRepo.save(any(Team.class))).willAnswer(new Answer<Team>() {
            @Override
            public Team answer(InvocationOnMock invocation) throws Throwable {
                // Return the first argument passed to the method
                return (Team) invocation.getArguments()[0];
            }
        });

        //when
        ResponseEntity<Team> teamWithAddedPlayers = teamService.addMultiplePlayersToTeam(1L, playersToBeAdded);

        //then
        assertThat(teamWithAddedPlayers.getBody().getPlayers().size()).isEqualTo(4);
        assertThat(teamWithAddedPlayers.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println(teamWithAddedPlayers.getBody());
    }

    @DisplayName("Service Test for adding players to a team, present in some other team previously")
    @Test
    public void givenListOfPlayersSomePresentInAnotherTeam_whenAddedToATeam_thenPlayersAreUpdatedAndUpdatedListIsReturned(){
        //given
        teamA.setPlayers(Collections.singletonList(player1));
        player3.setTeam(teamA);
        teamB.setPlayers(Collections.singletonList(player3));
        List<Long> playersToBeAdded = Arrays.asList(player3.getPlayerId(), player4.getPlayerId());
        given(teamRepo.findById(teamA.getTeamId())).willReturn(Optional.of(teamA));
        given(playerRepo.findById(player3.getPlayerId())).willReturn(Optional.of(player3));
        given(playerRepo.findById(player4.getPlayerId())).willReturn(Optional.of(player4));
        given(playerRepo.save(player3)).willReturn(player3);
        given(playerRepo.save(player4)).willReturn(player4);
        given(teamRepo.save(any(Team.class))).willAnswer(new Answer<Team>() {
            @Override
            public Team answer(InvocationOnMock invocation) throws Throwable {
                // Return the first argument passed to the method
                return (Team) invocation.getArguments()[0];
            }
        });

        //when
        ResponseEntity<Team> teamWithAddedPlayers = teamService.addMultiplePlayersToTeam(1L, playersToBeAdded);

        //then
        assertThat(teamWithAddedPlayers.getBody().getPlayers().size()).isEqualTo(3);
        assertThat(teamWithAddedPlayers.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println(teamWithAddedPlayers.getBody());
    }

    @DisplayName("Service Test for adding players to a team, when team does not exists")
    @Test
    public void givenListOfPlayers_whenAddedToATeamWhichDoesNotExists_thenEntityDoesNotExistsExceptionIsThrown(){
        //given
        List<Long> playersToBeAdded = Arrays.asList(player3.getPlayerId(), player4.getPlayerId());
        given(teamRepo.findById(teamA.getTeamId())).willReturn(Optional.empty());

        //when
        Executable executable = () -> teamService.addMultiplePlayersToTeam(1L, playersToBeAdded);

        //then
        assertThrows(EntityDoesNotExistsException.class, executable);
    }

    @DisplayName("Service Test for adding players to a team,at least on of which already exists in that team")
    @Test
    public void givenListOfPlayers_whenAddedToATeamContainingAtLeastOneOfThePlayersAlready_thenDuplicatePlayerInTeamExceptionIsThrown(){
        //given
        teamA.setPlayers(Arrays.asList(player1, player2));
        List<Long> playersToBeAdded = Arrays.asList(player2.getPlayerId(), player4.getPlayerId());
        given(teamRepo.findById(teamA.getTeamId())).willReturn(Optional.of(teamA));
        given(playerRepo.findById(player2.getPlayerId())).willReturn(Optional.of(player2));

        //when
        Executable executable = () -> teamService.addMultiplePlayersToTeam(1L, playersToBeAdded);

        //then
        assertThrows(DuplicatePlayerInTeamException.class, executable);
    }

    @DisplayName("Service Test for adding players to a team,at least on of which already exists in that team")
    @Test
    public void givenListOfPlayersAtLeastOneOfWhichNotExists_whenAddedToATeam_thenEntityDoesNotExistsExceptionIsThrown(){
        //given
        List<Long> playersToBeAdded = Arrays.asList(player1.getPlayerId(), player2.getPlayerId());
        given(teamRepo.findById(teamA.getTeamId())).willReturn(Optional.of(teamA));
        // considering 1st player exists in DB, 2nd does not
        given(playerRepo.findById(player1.getPlayerId())).willReturn(Optional.of(player1));
        given(playerRepo.findById(player2.getPlayerId())).willReturn(Optional.empty());

        //when
        Executable executable = () -> teamService.addMultiplePlayersToTeam(1L, playersToBeAdded);

        //then
        assertThrows(EntityDoesNotExistsException.class, executable);
    }

    @DisplayName("Service Test for removing players from a team, all of which exists in the team")
    @Test
    public void givenListOfPlayersAllExistingInTheTeam_whenRemovedToATeam_thenUpdatedTeamAfterRemovingPlayersIsReturned(){
        //given
        teamA.setPlayers(Arrays.asList(player1, player2, player3));
        List<Long> playersToBeRemoved = Arrays.asList(player1.getPlayerId(), player3.getPlayerId());
        given(teamRepo.findById(teamA.getTeamId())).willReturn(Optional.of(teamA));
        // considering 1st player exists in DB, 2nd does not
        given(playerRepo.findById(player1.getPlayerId())).willReturn(Optional.of(player1));
        given(playerRepo.findById(player3.getPlayerId())).willReturn(Optional.of(player3));
        given(relationRepo.findTopByTeamAndPlayerOrderByStartDateDesc(any(Team.class), any(Player.class))).willReturn(new TeamPlayerRelation());

        //when
        ResponseEntity<Team> teamAfterRemovingPlayers =  teamService.removeMultiplePlayersFromTeam(1L, playersToBeRemoved);

        //then
        assertThat(teamAfterRemovingPlayers.getBody().getPlayers().size()).isEqualTo(1);
        assertThat(teamAfterRemovingPlayers.getBody().getPlayers().get(0)).isEqualTo(player2);
        assertThat(teamAfterRemovingPlayers.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @DisplayName("Service Test for removing players from a team,at least on of which does not exists in that team")
    @Test
    public void givenListOfPlayersAtLeastOneOfWhichNotExists_whenRemovedToATeam_thenEntityDoesNotExistsExceptionIsThrown(){
        //given
        teamA.setPlayers(Arrays.asList(player1, player2, player3));
        List<Long> playersToBeRemoved = Arrays.asList(player1.getPlayerId(), player2.getPlayerId());
        given(teamRepo.findById(teamA.getTeamId())).willReturn(Optional.of(teamA));
        // considering 1st player exists in DB, 2nd does not
        given(playerRepo.findById(player1.getPlayerId())).willReturn(Optional.of(player1));
        given(playerRepo.findById(player2.getPlayerId())).willReturn(Optional.empty());

        //when
        Executable executable = () -> teamService.removeMultiplePlayersFromTeam(1L, playersToBeRemoved);

        //then
        assertThrows(EntityDoesNotExistsException.class, executable);
    }

    @DisplayName("Service Test for removing players from a team, at least one of players does not exist")
    @Test
    public void givenListOfPlayersAtLeastOneOfWhichIsNotInTheTeam_whenRemovedToATeam_thenPlayerRemovalFromTeamExceptionIsThrown(){
        //given
        teamA.setPlayers(Arrays.asList(player1, player2));
        List<Long> playersToBeRemoved = Arrays.asList(player2.getPlayerId(), player3.getPlayerId());
        given(teamRepo.findById(teamA.getTeamId())).willReturn(Optional.of(teamA));
        // considering 1st player exists in DB, 2nd does not
        given(playerRepo.findById(player2.getPlayerId())).willReturn(Optional.of(player2));
        given(playerRepo.findById(player3.getPlayerId())).willReturn(Optional.of(player3));

        //when
        Executable executable = () -> teamService.removeMultiplePlayersFromTeam(1L, playersToBeRemoved);

        //then
        assertThrows(PlayerRemovalFromTeamException.class, executable);
    }

    @DisplayName("Service Test for searching teams with character sequence")
    @Test
    public void givenCharacterSequence_whenSearchIsCalled_thenListOfPlayersWithThatSequenceInTheirNameIsReturned(){
        //given
        given(teamRepo.findAllByFullNameContaining("lah"))
                .willReturn(Collections.singletonList(teamA));

        //when
        ResponseEntity<List<Team>> searchedTeams = teamService.searchTeamsByName("lah");

        //then
        assertThat(searchedTeams.getBody().size()).isEqualTo(1);
        assertThat(searchedTeams.getBody().get(0)).isEqualTo(teamA);
        assertThat(searchedTeams.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Override
    public void prepare() {
        player1.setPlayerId(1L);
        player1.setTeam(null);
        player2.setPlayerId(2L);
        player2.setTeam(null);
        player3.setPlayerId(3L);
        player3.setTeam(null);
        player4.setPlayerId(4L);
        player4.setTeam(null);

        teamA.setTeamId(1L);
        teamB.setTeamId(2L);
        teamA.setPlayers(new ArrayList<>());
        teamB.setPlayers(new ArrayList<>());
    }
}
