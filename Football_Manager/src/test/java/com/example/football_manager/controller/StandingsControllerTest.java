package com.example.football_manager.controller;

import com.example.football_manager.dto.StandingDTO;
import com.example.football_manager.repository.CompetitionRepository;
import com.example.football_manager.service.StandingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StandingsControllerTest {

    @Mock
    private StandingsService standingsService;

    @Mock
    private CompetitionRepository competitionRepository;

    private StandingsController standingsController;

    @BeforeEach
    void setUp() {
        standingsController = new StandingsController(standingsService, competitionRepository);
    }

    @Test
    void getStandings_whenCompetitionExists_shouldReturnStandings() {
        StandingDTO row = new StandingDTO(
                "Arsenal",
                "arsenal.png",
                3,
                2,
                1,
                0,
                7,
                3,
                4,
                7
        );

        when(competitionRepository.existsById(1L)).thenReturn(true);
        when(standingsService.getStandingsForCompetition(1L)).thenReturn(List.of(row));

        ResponseEntity<List<StandingDTO>> response = standingsController.getStandings(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(row), response.getBody());

        verify(competitionRepository).existsById(1L);
        verify(standingsService).getStandingsForCompetition(1L);
    }

    @Test
    void getStandings_whenCompetitionDoesNotExist_shouldThrowNotFound() {
        when(competitionRepository.existsById(99L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> standingsController.getStandings(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Competition not found"));

        verify(competitionRepository).existsById(99L);
        verify(standingsService, never()).getStandingsForCompetition(anyLong());
    }
}