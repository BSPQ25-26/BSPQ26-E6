package com.example.football_manager.service;

import com.example.football_manager.dto.MatchRequestDTO;
import com.example.football_manager.dto.MatchResultDTO;
import com.example.football_manager.dto.MatchResultRequestDTO;
import com.example.football_manager.model.Competition;
import com.example.football_manager.model.Match;
import com.example.football_manager.model.Team;
import com.example.football_manager.repository.CompetitionRepository;
import com.example.football_manager.repository.MatchGoalRepository;
import com.example.football_manager.repository.MatchRepository;
import com.example.football_manager.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchServiceTest {

    private MatchService matchService;

    private MatchRepository matchRepository;
    private MatchGoalRepository matchGoalRepository;
    private TeamRepository teamRepository;
    private CompetitionRepository competitionRepository;

    private MatchRequestDTO validRequest;
    private Team homeTeam;
    private Team awayTeam;
    private Competition competition;

    @BeforeEach
    void setUp() {
        matchRepository = Mockito.mock(MatchRepository.class);
        matchGoalRepository = Mockito.mock(MatchGoalRepository.class);
        teamRepository = Mockito.mock(TeamRepository.class);
        competitionRepository = Mockito.mock(CompetitionRepository.class);

        matchService = new MatchService(
                matchRepository,
                matchGoalRepository,
                teamRepository,
                competitionRepository
        );

        homeTeam = new Team();
        homeTeam.setId(1L);
        homeTeam.setName("Arsenal");

        awayTeam = new Team();
        awayTeam.setId(2L);
        awayTeam.setName("Chelsea");

        competition = new Competition();
        competition.setId(1L);
        competition.setName("Premier League");

        validRequest = new MatchRequestDTO();
        validRequest.setHomeTeamId(1L);
        validRequest.setAwayTeamId(2L);
        validRequest.setCompetitionId(1L);
        validRequest.setKickoffTime(LocalDateTime.of(2026, 4, 1, 20, 30));
        validRequest.setVenue("Emirates Stadium");

        when(teamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(awayTeam));
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
    }

    @Test
    void createMatch_shouldScheduleSuccessfully() {
        String result = matchService.createMatch(validRequest);

        assertEquals("Match scheduled successfully.", result);
        assertEquals(MatchRequestDTO.MatchStatus.SCHEDULED, validRequest.getStatus());
        verify(matchRepository).save(any(Match.class));
    }

    @Test
    void createMatch_shouldKeepProvidedStatus() {
        validRequest.setStatus(MatchRequestDTO.MatchStatus.CANCELLED);

        String result = matchService.createMatch(validRequest);

        assertEquals("Match scheduled successfully.", result);
        assertEquals(MatchRequestDTO.MatchStatus.CANCELLED, validRequest.getStatus());
        verify(matchRepository).save(any(Match.class));
    }

    @Test
    void createMatch_shouldThrowWhenHomeTeamIdIsMissing() {
        validRequest.setHomeTeamId(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> matchService.createMatch(validRequest)
        );

        assertEquals("Validation Error: Both Home and Away team IDs are required.", ex.getMessage());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void createMatch_shouldThrowWhenAwayTeamIdIsMissing() {
        validRequest.setAwayTeamId(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> matchService.createMatch(validRequest)
        );

        assertEquals("Validation Error: Both Home and Away team IDs are required.", ex.getMessage());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void createMatch_shouldThrowWhenCompetitionIdIsMissing() {
        validRequest.setCompetitionId(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> matchService.createMatch(validRequest)
        );

        assertEquals("Validation Error: Competition ID is required.", ex.getMessage());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void createMatch_shouldThrowWhenKickoffTimeIsMissing() {
        validRequest.setKickoffTime(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> matchService.createMatch(validRequest)
        );

        assertEquals("Validation Error: Kickoff time and Venue are required.", ex.getMessage());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void createMatch_shouldThrowWhenVenueIsMissing() {
        validRequest.setVenue(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> matchService.createMatch(validRequest)
        );

        assertEquals("Validation Error: Kickoff time and Venue are required.", ex.getMessage());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void createMatch_shouldThrowWhenTeamsAreTheSame() {
        validRequest.setAwayTeamId(1L);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> matchService.createMatch(validRequest)
        );

        assertEquals("Validation Error: Home and Away teams must be different.", ex.getMessage());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void getAllMatches_shouldReturnMatchesList() {
        Match match = new Match();
        match.setId(11L);
        match.setLeftTeam(homeTeam);
        match.setRightTeam(awayTeam);
        match.setCompetition(competition);
        match.setDatetime(OffsetDateTime.parse("2026-04-19T14:00:00Z"));
        match.setVenue("Emirates Stadium");
        match.setLeftScore((short) 0);
        match.setRightScore((short) 0);
        match.setFinished(false);

        when(matchRepository.findAll()).thenReturn(List.of(match));

        List<Match> result = matchService.getAllMatches();

        assertEquals(1, result.size());
        assertEquals(11L, result.get(0).getId());
        assertEquals("Arsenal", result.get(0).getLeftTeam().getName());
        verify(matchRepository).findAll();
    }

    @Test
    void updateMatch_shouldReturnSuccessMessage() {
        Match existingMatch = new Match();
        existingMatch.setId(8L);

        when(matchRepository.findById(8L)).thenReturn(Optional.of(existingMatch));

        String result = matchService.updateMatch(8L, validRequest);

        assertEquals("Match with ID 8 has been updated.", result);
        assertEquals(homeTeam, existingMatch.getLeftTeam());
        assertEquals(awayTeam, existingMatch.getRightTeam());
        assertEquals("Emirates Stadium", existingMatch.getVenue());
        verify(matchRepository).save(existingMatch);
    }

    @Test
    void updateMatch_shouldThrowWhenBothTeamsAreEqual() {
        validRequest.setHomeTeamId(5L);
        validRequest.setAwayTeamId(5L);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> matchService.updateMatch(8L, validRequest)
        );

        assertEquals("Validation Error: Home and Away teams must be different.", ex.getMessage());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void deleteMatch_shouldReturnSuccessMessage() {
        Match existingMatch = new Match();
        existingMatch.setId(3L);

        when(matchRepository.findById(3L)).thenReturn(Optional.of(existingMatch));

        String result = matchService.deleteMatch(3L);

        assertEquals("Match with ID 3 has been deleted.", result);
        verify(matchGoalRepository).deleteByMatchId(3L);
        verify(matchRepository).delete(existingMatch);
    }

    @Test
    void deleteMatch_shouldThrowWhenMatchDoesNotExist() {
        when(matchRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> matchService.deleteMatch(99L)
        );

        assertEquals("Match not found with id: 99", ex.getMessage());
        verify(matchGoalRepository, never()).deleteByMatchId(99L);
        verify(matchRepository, never()).delete(any(Match.class));
    }

    @Test
    void registerResult_shouldReturnExpectedMessage() {
        Match match = new Match();
        match.setId(9L);
        match.setLeftTeam(homeTeam);
        match.setRightTeam(awayTeam);

        when(matchRepository.findById(9L)).thenReturn(Optional.of(match));

        MatchResultRequestDTO request = new MatchResultRequestDTO();

        MatchResultRequestDTO.GoalDTO goal1 = new MatchResultRequestDTO.GoalDTO();
        goal1.setTeamId(1L);
        goal1.setMinute(10);

        MatchResultRequestDTO.GoalDTO goal2 = new MatchResultRequestDTO.GoalDTO();
        goal2.setTeamId(1L);
        goal2.setMinute(30);

        MatchResultRequestDTO.GoalDTO goal3 = new MatchResultRequestDTO.GoalDTO();
        goal3.setTeamId(2L);
        goal3.setMinute(70);

        request.setGoals(List.of(goal1, goal2, goal3));

        String result = matchService.registerResult(9L, request);

        assertEquals("Result registered for match 9: 2 - 1", result);
        assertEquals(2, match.getLeftScore());
        assertEquals(1, match.getRightScore());
        assertTrue(match.isFinished());

        verify(matchGoalRepository).deleteByMatchId(9L);
        verify(matchGoalRepository).saveAll(anyList());
        verify(matchRepository).save(match);
    }

    @Test
    void getFinishedMatchResults_shouldReturnTeamsAndScore() {
        Match finishedMatch = new Match();
        finishedMatch.setId(11L);
        finishedMatch.setLeftTeam(homeTeam);
        finishedMatch.setRightTeam(awayTeam);
        finishedMatch.setLeftScore((short) 3);
        finishedMatch.setRightScore((short) 2);
        finishedMatch.setDatetime(OffsetDateTime.parse("2026-04-19T14:00:00Z"));
        finishedMatch.setFinished(true);

        when(matchRepository.findByFinishedTrueOrderByDatetimeDesc())
                .thenReturn(List.of(finishedMatch));

        List<MatchResultDTO> results = matchService.getFinishedMatchResults();

        assertEquals(1, results.size());

        MatchResultDTO result = results.get(0);
        assertEquals(11L, result.matchId());
        assertEquals("Arsenal", result.homeTeamName());
        assertEquals("Chelsea", result.awayTeamName());
        assertEquals(3, result.homeScore());
        assertEquals(2, result.awayScore());
    }

    @Test
    void getRecentMatchesByTeamId_shouldReturnFinishedMatchesForTeam() {
        Match finishedMatch = new Match();
        finishedMatch.setId(20L);

        when(matchRepository.findByFinishedTrueAndLeftTeamIdOrFinishedTrueAndRightTeamIdOrderByDatetimeDesc(1L, 1L))
                .thenReturn(List.of(finishedMatch));

        List<Match> result = matchService.getRecentMatchesByTeamId(1L);

        assertEquals(1, result.size());
        assertEquals(20L, result.get(0).getId());
    }

    @Test
    void getFutureMatchesByTeamId_shouldReturnUpcomingMatchesForTeam() {
        Match futureMatch = new Match();
        futureMatch.setId(30L);

        when(matchRepository.findByFinishedFalseAndDatetimeAfterAndLeftTeamIdOrFinishedFalseAndDatetimeAfterAndRightTeamIdOrderByDatetimeAsc(
                any(OffsetDateTime.class),
                eq(1L),
                any(OffsetDateTime.class),
                eq(1L)
        )).thenReturn(List.of(futureMatch));

        List<Match> result = matchService.getFutureMatchesByTeamId(1L);

        assertEquals(1, result.size());
        assertEquals(30L, result.get(0).getId());
    }

    @Test
    void getFutureMatchesByFavouriteTeamIds_shouldReturnEmptyWhenNoFavourites() {
        List<Match> result = matchService.getFutureMatchesByFavouriteTeamIds(Set.of());

        assertTrue(result.isEmpty());
        verify(matchRepository, never())
                .findByFinishedFalseAndDatetimeAfterAndLeftTeamIdInOrFinishedFalseAndDatetimeAfterAndRightTeamIdInOrderByDatetimeAsc(
                        any(),
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void getFutureMatchesByFavouriteTeamIds_shouldReturnUpcomingMatches() {
        Match futureMatch = new Match();
        futureMatch.setId(40L);

        Set<Long> favouriteIds = Set.of(1L, 2L);

        when(matchRepository.findByFinishedFalseAndDatetimeAfterAndLeftTeamIdInOrFinishedFalseAndDatetimeAfterAndRightTeamIdInOrderByDatetimeAsc(
                any(OffsetDateTime.class),
                eq(favouriteIds),
                any(OffsetDateTime.class),
                eq(favouriteIds)
        )).thenReturn(List.of(futureMatch));

        List<Match> result = matchService.getFutureMatchesByFavouriteTeamIds(favouriteIds);

        assertEquals(1, result.size());
        assertEquals(40L, result.get(0).getId());
    }
}