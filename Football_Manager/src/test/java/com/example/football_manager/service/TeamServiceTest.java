package com.example.football_manager.service;

import com.example.football_manager.dto.TeamRequestDTO;
import com.example.football_manager.model.Country;
import com.example.football_manager.model.Team;
import com.example.football_manager.repository.CountryRepository;
import com.example.football_manager.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private TeamService teamService;

    private Country spain;
    private TeamRequestDTO validDto;

    @BeforeEach
    void setUp() {
        spain = new Country();
        spain.setId(1L);
        spain.setName("Spain");

        validDto = new TeamRequestDTO();
        validDto.setName(" Real Madrid ");
        validDto.setLogoUrl(" /img/rm.png ");
        validDto.setCountryId(1L);
    }

    @Test
    void createTeam_shouldCreateTeamSuccessfully() {
        when(countryRepository.findById(1L)).thenReturn(Optional.of(spain));

        Team savedTeam = new Team();
        savedTeam.setId(10L);
        savedTeam.setName("Real Madrid");
        savedTeam.setLogoUrl("/img/rm.png");
        savedTeam.setCountry(spain);

        when(teamRepository.save(any(Team.class))).thenReturn(savedTeam);

        Team result = teamService.createTeam(validDto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Real Madrid", result.getName());
        assertEquals("/img/rm.png", result.getLogoUrl());
        assertEquals("Spain", result.getCountry().getName());

        ArgumentCaptor<Team> captor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(captor.capture());

        Team teamToSave = captor.getValue();
        assertEquals("Real Madrid", teamToSave.getName());
        assertEquals("/img/rm.png", teamToSave.getLogoUrl());
        assertEquals(spain, teamToSave.getCountry());
    }

    @Test
    void createTeam_shouldThrowExceptionWhenCountryNotFound() {
        when(countryRepository.findById(999L)).thenReturn(Optional.empty());

        TeamRequestDTO dto = new TeamRequestDTO("Team A", "/logo.png", 999L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.createTeam(dto));

        assertEquals("Country not found with id: 999", ex.getMessage());
        verify(teamRepository, never()).save(any());
    }

    @Test
    void getAllTeams_shouldReturnEmptyList() {
        when(teamRepository.findAll()).thenReturn(List.of());

        List<Team> result = teamService.getAllTeams();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(teamRepository).findAll();
    }

    @Test
    void getAllTeams_shouldReturnTeams() {
        Team t1 = new Team(1L, "Barcelona", "/barca.png", spain);
        Team t2 = new Team(2L, "Madrid", "/madrid.png", spain);

        when(teamRepository.findAll()).thenReturn(List.of(t1, t2));

        List<Team> result = teamService.getAllTeams();

        assertEquals(2, result.size());
        assertEquals("Barcelona", result.get(0).getName());
        assertEquals("Madrid", result.get(1).getName());
    }

    @Test
    void updateTeam_shouldUpdateSuccessfully() {
        Team existing = new Team();
        existing.setId(5L);
        existing.setName("Old Name");
        existing.setLogoUrl("/old.png");
        existing.setCountry(spain);

        Country england = new Country();
        england.setId(2L);
        england.setName("England");

        TeamRequestDTO dto = new TeamRequestDTO();
        dto.setName(" Arsenal ");
        dto.setLogoUrl(" /arsenal.png ");
        dto.setCountryId(2L);

        when(teamRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(countryRepository.findById(2L)).thenReturn(Optional.of(england));
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));

        Team result = teamService.updateTeam(5L, dto);

        assertEquals("Arsenal", result.getName());
        assertEquals("/arsenal.png", result.getLogoUrl());
        assertEquals("England", result.getCountry().getName());
    }

    @Test
    void updateTeam_shouldThrowExceptionWhenTeamNotFound() {
        when(teamRepository.findById(404L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.updateTeam(404L, validDto));

        assertEquals("Team not found with id: 404", ex.getMessage());
        verify(countryRepository, never()).findById(anyLong());
        verify(teamRepository, never()).save(any());
    }

    @Test
    void updateTeam_shouldThrowExceptionWhenCountryNotFound() {
        Team existing = new Team();
        existing.setId(5L);

        when(teamRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(countryRepository.findById(999L)).thenReturn(Optional.empty());

        TeamRequestDTO dto = new TeamRequestDTO("Team X", "/x.png", 999L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.updateTeam(5L, dto));

        assertEquals("Country not found with id: 999", ex.getMessage());
        verify(teamRepository, never()).save(any());
    }

    @Test
    void deleteTeam_shouldDeleteSuccessfully() {
        Team existing = new Team();
        existing.setId(7L);

        when(teamRepository.findById(7L)).thenReturn(Optional.of(existing));

        teamService.deleteTeam(7L);

        verify(teamRepository).delete(existing);
    }

    @Test
    void deleteTeam_shouldThrowExceptionWhenTeamNotFound() {
        when(teamRepository.findById(123L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> teamService.deleteTeam(123L));

        assertEquals("Team not found with id: 123", ex.getMessage());
        verify(teamRepository, never()).delete(any());
    }

    @Test
    void getTeamById_shouldReturnOptional() {
        Team team = new Team(1L, "Valencia", "/valencia.png", spain);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        Optional<Team> result = teamService.getTeamById(1L);

        assertTrue(result.isPresent());
        assertEquals("Valencia", result.get().getName());
    }

    @Test
    void getTeamsContainingName_shouldReturnEmptyWhenNameIsNull() {
        List<Team> result = teamService.getTeamsContainingName(null);

        assertTrue(result.isEmpty());
        verify(teamRepository, never()).findByNameContainingIgnoreCase(anyString());
    }

    @Test
    void getTeamsContainingName_shouldReturnFilteredTeams() {
        Team team = new Team(1L, "Sevilla", "/sevilla.png", spain);
        when(teamRepository.findByNameContainingIgnoreCase("vil"))
                .thenReturn(List.of(team));

        List<Team> result = teamService.getTeamsContainingName(" vil ");

        assertEquals(1, result.size());
        assertEquals("Sevilla", result.get(0).getName());
    }
}