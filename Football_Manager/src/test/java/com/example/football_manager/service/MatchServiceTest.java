package com.example.football_manager.service;

import com.example.football_manager.dto.MatchRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

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
    void registerResult_shouldReturnExpectedMessage() {
        String result = matchService.registerResult(9L, 2, 1);

        assertEquals("Result registered for match 9: 2 - 1", result);
    }
}