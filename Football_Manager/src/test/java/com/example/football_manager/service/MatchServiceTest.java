package com.example.football_manager.service;

import com.example.football_manager.dto.MatchRequestDTO;
import com.example.football_manager.dto.MatchResultDTO;
import com.example.football_manager.model.Match;
import com.example.football_manager.model.Team;
import com.example.football_manager.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class MatchServiceTest {

    private MatchService matchService;
    private MatchRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        matchService = new MatchService();

        validRequest = new MatchRequestDTO();
        validRequest.setHomeTeamId(1L);
        validRequest.setAwayTeamId(2L);
        validRequest.setKickoffTime(LocalDateTime.of(2026, 4, 1, 20, 30));
        validRequest.setVenue("Bernabeu");
    }

    @Test
    void createMatch_shouldScheduleSuccessfully() {
        String result = matchService.createMatch(validRequest);

        assertEquals("Match scheduled successfully.", result);
        assertEquals(MatchRequestDTO.MatchStatus.SCHEDULED, validRequest.getStatus());
    }

    @Test
    void createMatch_shouldKeepProvidedStatus() {
        validRequest.setStatus(MatchRequestDTO.MatchStatus.CANCELLED);

        String result = matchService.createMatch(validRequest);

        assertEquals("Match scheduled successfully.", result);
        assertEquals(MatchRequestDTO.MatchStatus.CANCELLED, validRequest.getStatus());
    }

    @Test
    void createMatch_shouldThrowWhenHomeTeamIdIsMissing() {
        validRequest.setHomeTeamId(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> matchService.createMatch(validRequest));

        assertEquals("Validation Error: Both Home and Away team IDs are required.", ex.getMessage());
    }

    @Test
    void createMatch_shouldThrowWhenAwayTeamIdIsMissing() {
        validRequest.setAwayTeamId(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> matchService.createMatch(validRequest));

        assertEquals("Validation Error: Both Home and Away team IDs are required.", ex.getMessage());
    }

    @Test
    void createMatch_shouldThrowWhenKickoffTimeIsMissing() {
        validRequest.setKickoffTime(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> matchService.createMatch(validRequest));

        assertEquals("Validation Error: Kickoff time and Venue are required.", ex.getMessage());
    }

    @Test
    void createMatch_shouldThrowWhenVenueIsMissing() {
        validRequest.setVenue(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> matchService.createMatch(validRequest));

        assertEquals("Validation Error: Kickoff time and Venue are required.", ex.getMessage());
    }

    @Test
    void createMatch_shouldThrowWhenTeamsAreTheSame() {
        validRequest.setAwayTeamId(1L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> matchService.createMatch(validRequest));

        assertEquals("Validation Error: Home and Away teams must be different.", ex.getMessage());
    }

    @Test
    void updateMatch_shouldReturnSuccessMessage() {
        String result = matchService.updateMatch(8L, validRequest);

        assertEquals("Match with ID 8 has been updated.", result);
    }

    @Test
    void updateMatch_shouldThrowWhenBothTeamsAreEqual() {
        validRequest.setHomeTeamId(5L);
        validRequest.setAwayTeamId(5L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> matchService.updateMatch(8L, validRequest));

        assertEquals("Validation Error: Home and Away teams must be different.", ex.getMessage());
    }

    @Test
    void deleteMatch_shouldReturnSuccessMessage() {
        String result = matchService.deleteMatch(3L);

        assertEquals("Match with ID 3 has been deleted.", result);
    }

    @Test
    void deleteMatch_shouldDeleteFromRepositoryWhenMatchExists() {
        MatchRepository repository = Mockito.mock(MatchRepository.class);
        Mockito.when(repository.existsById(3L)).thenReturn(true);

        MatchService serviceWithRepository = new MatchService(repository);

        String result = serviceWithRepository.deleteMatch(3L);

        assertEquals("Match with ID 3 has been deleted.", result);
        verify(repository).deleteById(3L);
    }

    @Test
    void deleteMatch_shouldThrowWhenMatchDoesNotExist() {
        MatchRepository repository = Mockito.mock(MatchRepository.class);
        Mockito.when(repository.existsById(99L)).thenReturn(false);

        MatchService serviceWithRepository = new MatchService(repository);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> serviceWithRepository.deleteMatch(99L));

        assertEquals("Match with ID 99 was not found.", ex.getMessage());
        verify(repository, never()).deleteById(99L);
    }

    @Test
    void registerResult_shouldReturnExpectedMessage() {
        String result = matchService.registerResult(9L, 2, 1);

        assertEquals("Result registered for match 9: 2 - 1", result);
    }
    
    @Test
    void getFinishedMatchResults_shouldReturnTeamsAndScore() {
        MatchRepository repository = Mockito.mock(MatchRepository.class);

        Team homeTeam = new Team();
        homeTeam.setName("Arsenal");

        Team awayTeam = new Team();
        awayTeam.setName("Chelsea");

        Match finishedMatch = new Match();
        finishedMatch.setId(11L);
        finishedMatch.setLeftTeam(homeTeam);
        finishedMatch.setRightTeam(awayTeam);
        finishedMatch.setLeftScore((short) 3);
        finishedMatch.setRightScore((short) 2);
        finishedMatch.setDatetime(OffsetDateTime.parse("2026-04-19T14:00:00Z"));
        finishedMatch.setFinished(true);

        Mockito.when(repository.findByFinishedTrueOrderByDatetimeDesc()).thenReturn(List.of(finishedMatch));

        MatchService serviceWithRepository = new MatchService(repository);

        List<MatchResultDTO> results = serviceWithRepository.getFinishedMatchResults();

        assertEquals(1, results.size());
        MatchResultDTO result = results.get(0);
        assertEquals(11L, result.matchId());
        assertEquals("Arsenal", result.homeTeamName());
        assertEquals("Chelsea", result.awayTeamName());
        assertEquals(3, result.homeScore());
        assertEquals(2, result.awayScore());
    }
}
