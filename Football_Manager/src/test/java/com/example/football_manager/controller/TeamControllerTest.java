package com.example.football_manager.controller;

import com.example.football_manager.dto.TeamRequestDTO;
import com.example.football_manager.model.Country;
import com.example.football_manager.model.Team;
import com.example.football_manager.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    @Mock
    private TeamService teamService;

    private TeamController teamController;

    @BeforeEach
    void setUp() {
        teamController = new TeamController(teamService);
    }

    @Test
    void createTeam_shouldReturnCreatedTeam() {
        Country country = new Country(1L, "Spain");
        TeamRequestDTO dto = new TeamRequestDTO("Real Sociedad", "logo.png", 1L);
        Team team = new Team(1L, "Real Sociedad", "logo.png", country);

        when(teamService.createTeam(dto)).thenReturn(team);

        ResponseEntity<Team> response = teamController.createTeam(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(team, response.getBody());
        verify(teamService).createTeam(dto);
    }

    @Test
    void updateTeam_shouldReturnUpdatedTeam() {
        Country country = new Country(1L, "Spain");
        TeamRequestDTO dto = new TeamRequestDTO("Athletic Club", "logo.png", 1L);
        Team team = new Team(2L, "Athletic Club", "logo.png", country);

        when(teamService.updateTeam(2L, dto)).thenReturn(team);

        ResponseEntity<Team> response = teamController.updateTeam(2L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(team, response.getBody());
        verify(teamService).updateTeam(2L, dto);
    }

    @Test
    void deleteTeam_shouldReturnNoContent() {
        ResponseEntity<Void> response = teamController.deleteTeam(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(teamService).deleteTeam(1L);
    }

    @Test
    void getTeamById_whenTeamExists_shouldReturnOk() {
        Country country = new Country(1L, "Spain");
        Team team = new Team(1L, "Real Sociedad", "logo.png", country);

        when(teamService.getTeamById(1L)).thenReturn(Optional.of(team));

        ResponseEntity<Team> response = teamController.getTeamById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(team, response.getBody());
        verify(teamService).getTeamById(1L);
    }

    @Test
    void getTeamById_whenTeamDoesNotExist_shouldReturnNotFound() {
        when(teamService.getTeamById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Team> response = teamController.getTeamById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(teamService).getTeamById(99L);
    }

    @Test
    void getTeamsContainingName_shouldReturnMatchingTeams() {
        Country country = new Country(1L, "Spain");
        Team team = new Team(1L, "Real Sociedad", "logo.png", country);

        when(teamService.getTeamsContainingName("Real")).thenReturn(List.of(team));

        ResponseEntity<List<Team>> response = teamController.getTeamsContainingName("Real");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(team), response.getBody());
        verify(teamService).getTeamsContainingName("Real");
    }

    @Test
    void getAllTeams_shouldReturnAllTeams() {
        Country country = new Country(1L, "Spain");
        Team team = new Team(1L, "Real Sociedad", "logo.png", country);

        when(teamService.getAllTeams()).thenReturn(List.of(team));

        ResponseEntity<List<Team>> response = teamController.getAllTeams();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(team), response.getBody());
        verify(teamService).getAllTeams();
    }
}