package com.example.football_manager.controller;

import com.example.football_manager.dto.MatchRequestDTO;
import com.example.football_manager.dto.MatchResultDTO;
import com.example.football_manager.dto.MatchResultRequestDTO;
import com.example.football_manager.model.Match;
import com.example.football_manager.service.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchControllerTest {

    private MatchService matchService;
    private MatchController matchController;

    @BeforeEach
    void setUp() {
        matchService = mock(MatchService.class);
        matchController = new MatchController();
        ReflectionTestUtils.setField(matchController, "matchService", matchService);
    }

    @Test
    void getMatches_shouldReturnFilteredMatches() {
        Match match = new Match();
        match.setId(1L);

        when(matchService.getMatches(1L, MatchRequestDTO.MatchStatus.FINISHED, 2L))
                .thenReturn(List.of(match));

        ResponseEntity<List<Match>> response = matchController.getMatches(
                1L,
                MatchRequestDTO.MatchStatus.FINISHED,
                2L
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(match), response.getBody());
        verify(matchService).getMatches(1L, MatchRequestDTO.MatchStatus.FINISHED, 2L);
    }

    @Test
    void getMatches_whenInvalidFilter_shouldThrowBadRequest() {
        when(matchService.getMatches(null, MatchRequestDTO.MatchStatus.IN_PROGRESS, null))
                .thenThrow(new IllegalArgumentException("Match status filter not supported"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> matchController.getMatches(
                        null,
                        MatchRequestDTO.MatchStatus.IN_PROGRESS,
                        null
                )
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(matchService).getMatches(null, MatchRequestDTO.MatchStatus.IN_PROGRESS, null);
    }

    @Test
    void getUpcomingMatches_shouldReturnUpcomingMatches() {
        Match match = new Match();
        match.setId(1L);

        when(matchService.getUpcomingMatches()).thenReturn(List.of(match));

        ResponseEntity<List<Match>> response = matchController.getUpcomingMatches();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(match), response.getBody());
        verify(matchService).getUpcomingMatches();
    }

    @Test
    void createMatchFromJson_whenValid_shouldReturnCreated() {
        MatchRequestDTO dto = createMatchRequestDTO();

        when(matchService.createMatch(dto)).thenReturn("Match scheduled successfully.");

        ResponseEntity<String> response = matchController.createMatchFromJson(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Match scheduled successfully.", response.getBody());
        verify(matchService).createMatch(dto);
    }

    @Test
    void createMatchFromJson_whenServiceThrows_shouldReturnBadRequest() {
        MatchRequestDTO dto = createMatchRequestDTO();

        when(matchService.createMatch(dto))
                .thenThrow(new IllegalArgumentException("Teams must be different."));

        ResponseEntity<String> response = matchController.createMatchFromJson(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Teams must be different.", response.getBody());
        verify(matchService).createMatch(dto);
    }

    @Test
    void createMatchFromForm_whenValid_shouldReturnCreated() {
        MatchRequestDTO dto = createMatchRequestDTO();

        when(matchService.createMatch(dto)).thenReturn("Match scheduled successfully.");

        ResponseEntity<String> response = matchController.createMatchFromForm(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Match scheduled successfully.", response.getBody());
        verify(matchService).createMatch(dto);
    }

    @Test
    void getMatchResults_shouldReturnFinishedResults() {
        MatchResultDTO resultDTO = new MatchResultDTO(
                1L,
                "Real Sociedad",
                "Athletic Club",
                (short) 2,
                (short) 1,
                OffsetDateTime.parse("2026-05-17T20:00:00Z")
        );

        when(matchService.getFinishedMatchResults()).thenReturn(List.of(resultDTO));

        ResponseEntity<List<MatchResultDTO>> response = matchController.getMatchResults();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(resultDTO), response.getBody());
        verify(matchService).getFinishedMatchResults();
    }

    @Test
    void updateMatch_whenValid_shouldReturnOk() {
        MatchRequestDTO dto = createMatchRequestDTO();

        when(matchService.updateMatch(1L, dto)).thenReturn("Match updated successfully.");

        ResponseEntity<String> response = matchController.updateMatch(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Match updated successfully.", response.getBody());
        verify(matchService).updateMatch(1L, dto);
    }

    @Test
    void updateMatch_whenServiceThrows_shouldReturnBadRequest() {
        MatchRequestDTO dto = createMatchRequestDTO();

        when(matchService.updateMatch(1L, dto))
                .thenThrow(new IllegalArgumentException("Match not found."));

        ResponseEntity<String> response = matchController.updateMatch(1L, dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Match not found.", response.getBody());
        verify(matchService).updateMatch(1L, dto);
    }

    @Test
    void deleteMatch_whenValid_shouldReturnOk() {
        when(matchService.deleteMatch(1L)).thenReturn("Match deleted successfully.");

        ResponseEntity<String> response = matchController.deleteMatch(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Match deleted successfully.", response.getBody());
        verify(matchService).deleteMatch(1L);
    }

    @Test
    void deleteMatch_whenServiceThrows_shouldReturnBadRequest() {
        when(matchService.deleteMatch(1L))
                .thenThrow(new IllegalArgumentException("Match not found."));

        ResponseEntity<String> response = matchController.deleteMatch(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Match not found.", response.getBody());
        verify(matchService).deleteMatch(1L);
    }

    @Test
    void registerResult_whenValid_shouldReturnOk() {
        MatchResultRequestDTO dto = new MatchResultRequestDTO();
        dto.setGoals(List.of());

        when(matchService.registerResult(1L, dto)).thenReturn("Result registered successfully.");

        ResponseEntity<String> response = matchController.registerResult(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Result registered successfully.", response.getBody());
        verify(matchService).registerResult(1L, dto);
    }

    @Test
    void registerResult_whenServiceThrows_shouldReturnBadRequest() {
        MatchResultRequestDTO dto = new MatchResultRequestDTO();
        dto.setGoals(List.of());

        when(matchService.registerResult(1L, dto))
                .thenThrow(new IllegalArgumentException("Invalid result."));

        ResponseEntity<String> response = matchController.registerResult(1L, dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid result.", response.getBody());
        verify(matchService).registerResult(1L, dto);
    }

    private MatchRequestDTO createMatchRequestDTO() {
        MatchRequestDTO dto = new MatchRequestDTO();
        dto.setHomeTeamId(1L);
        dto.setAwayTeamId(2L);
        dto.setCompetitionId(1L);
        dto.setKickoffTime(LocalDateTime.of(2026, 5, 17, 20, 0));
        dto.setVenue("Anoeta");
        dto.setStatus(MatchRequestDTO.MatchStatus.SCHEDULED);
        return dto;
    }
}