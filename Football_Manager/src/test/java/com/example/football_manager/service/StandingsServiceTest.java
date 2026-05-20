package com.example.football_manager.service;

import com.example.football_manager.dto.StandingDTO;
import com.example.football_manager.model.Competition;
import com.example.football_manager.model.Country;
import com.example.football_manager.model.Match;
import com.example.football_manager.model.Team;
import com.example.football_manager.repository.CompetitionRepository;
import com.example.football_manager.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StandingsServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(StandingsServiceTest.class);

    private StandingsService standingsService;

    private CompetitionRepository competitionRepository;
    private MatchRepository matchRepository;

    private Country country;
    private Competition competition;
    private Team arsenal;
    private Team chelsea;
    private Team city;

    @BeforeEach
    void setUp() {
        competitionRepository = Mockito.mock(CompetitionRepository.class);
        matchRepository = Mockito.mock(MatchRepository.class);

        standingsService = new StandingsService(competitionRepository, matchRepository);

        country = new Country(1L, "England");
        competition = new Competition(1L, "Premier League");

        arsenal = new Team(1L, "Arsenal", "arsenal.png", country);
        chelsea = new Team(2L, "Chelsea", "chelsea.png", country);
        city = new Team(3L, "Manchester City", "city.png", country);
    }

    @Test
    void getCompetitions_shouldReturnAllCompetitions() {
        logger.info("Get competitions service test: expectedCount={}", 1);
        when(competitionRepository.findAll()).thenReturn(List.of(competition));

        List<Competition> result = standingsService.getCompetitions();

        assertEquals(1, result.size());
        assertEquals("Premier League", result.get(0).getName());

        verify(competitionRepository).findAll();
    }

    @Test
    void getStandingsForCompetition_shouldCalculatePointsAndStatsCorrectly() {
        logger.info("Standings calculation test: competitionId={}", 1L);
        Match arsenalBeatsChelsea = createMatch(
                1L,
                arsenal,
                chelsea,
                (short) 2,
                (short) 1,
                true
        );

        Match arsenalDrawsCity = createMatch(
                2L,
                arsenal,
                city,
                (short) 1,
                (short) 1,
                true
        );

        when(matchRepository.findByFilters(null, null, 1L))
                .thenReturn(List.of(arsenalBeatsChelsea, arsenalDrawsCity));

        List<StandingDTO> standings = standingsService.getStandingsForCompetition(1L);

        assertEquals(3, standings.size());

        StandingDTO arsenalRow = findByTeamName(standings, "Arsenal");
        assertEquals(2, arsenalRow.getMatchesPlayed());
        assertEquals(1, arsenalRow.getWon());
        assertEquals(1, arsenalRow.getDrawn());
        assertEquals(0, arsenalRow.getLost());
        assertEquals(3, arsenalRow.getGoalsFor());
        assertEquals(2, arsenalRow.getGoalsAgainst());
        assertEquals(1, arsenalRow.getGoalDifference());
        assertEquals(4, arsenalRow.getPoints());

        StandingDTO chelseaRow = findByTeamName(standings, "Chelsea");
        assertEquals(1, chelseaRow.getMatchesPlayed());
        assertEquals(0, chelseaRow.getWon());
        assertEquals(0, chelseaRow.getDrawn());
        assertEquals(1, chelseaRow.getLost());
        assertEquals(1, chelseaRow.getGoalsFor());
        assertEquals(2, chelseaRow.getGoalsAgainst());
        assertEquals(-1, chelseaRow.getGoalDifference());
        assertEquals(0, chelseaRow.getPoints());

        StandingDTO cityRow = findByTeamName(standings, "Manchester City");
        assertEquals(1, cityRow.getMatchesPlayed());
        assertEquals(0, cityRow.getWon());
        assertEquals(1, cityRow.getDrawn());
        assertEquals(0, cityRow.getLost());
        assertEquals(1, cityRow.getGoalsFor());
        assertEquals(1, cityRow.getGoalsAgainst());
        assertEquals(0, cityRow.getGoalDifference());
        assertEquals(1, cityRow.getPoints());

        verify(matchRepository).findByFilters(null, null, 1L);
    }

    @Test
    void getStandingsForCompetition_shouldSortByPointsGoalDifferenceGoalsForAndName() {
        logger.info("Standings sort order test: competitionId={}", 1L);
        Team alpha = new Team(10L, "Alpha FC", "alpha.png", country);
        Team beta = new Team(11L, "Beta FC", "beta.png", country);
        Team gamma = new Team(12L, "Gamma FC", "gamma.png", country);
        Team delta = new Team(13L, "Delta FC", "delta.png", country);

        Match alphaWins = createMatch(1L, alpha, delta, (short) 2, (short) 0, true);
        Match betaWins = createMatch(2L, beta, gamma, (short) 3, (short) 0, true);

        when(matchRepository.findByFilters(null, null, 1L))
                .thenReturn(List.of(alphaWins, betaWins));

        List<StandingDTO> standings = standingsService.getStandingsForCompetition(1L);

        assertEquals("Beta FC", standings.get(0).getTeamName());
        assertEquals("Alpha FC", standings.get(1).getTeamName());

        assertEquals(3, standings.get(0).getPoints());
        assertEquals(3, standings.get(1).getPoints());

        assertEquals(3, standings.get(0).getGoalDifference());
        assertEquals(2, standings.get(1).getGoalDifference());
    }

    @Test
    void getStandingsForCompetition_shouldIgnoreUnfinishedMatchesInStats() {
        logger.info("Standings ignore unfinished matches test: competitionId={}", 1L);
        Match unfinishedMatch = createMatch(
                1L,
                arsenal,
                chelsea,
                (short) 4,
                (short) 3,
                false
        );

        when(matchRepository.findByFilters(null, null, 1L))
                .thenReturn(List.of(unfinishedMatch));

        List<StandingDTO> standings = standingsService.getStandingsForCompetition(1L);

        assertEquals(2, standings.size());

        StandingDTO arsenalRow = findByTeamName(standings, "Arsenal");
        assertEquals(0, arsenalRow.getMatchesPlayed());
        assertEquals(0, arsenalRow.getWon());
        assertEquals(0, arsenalRow.getDrawn());
        assertEquals(0, arsenalRow.getLost());
        assertEquals(0, arsenalRow.getGoalsFor());
        assertEquals(0, arsenalRow.getGoalsAgainst());
        assertEquals(0, arsenalRow.getGoalDifference());
        assertEquals(0, arsenalRow.getPoints());

        StandingDTO chelseaRow = findByTeamName(standings, "Chelsea");
        assertEquals(0, chelseaRow.getMatchesPlayed());
        assertEquals(0, chelseaRow.getWon());
        assertEquals(0, chelseaRow.getDrawn());
        assertEquals(0, chelseaRow.getLost());
        assertEquals(0, chelseaRow.getGoalsFor());
        assertEquals(0, chelseaRow.getGoalsAgainst());
        assertEquals(0, chelseaRow.getGoalDifference());
        assertEquals(0, chelseaRow.getPoints());
    }

    @Test
    void getStandingsForCompetition_whenNoMatches_shouldReturnEmptyList() {
        logger.info("Standings no matches test: competitionId={}", 99L);
        when(matchRepository.findByFilters(null, null, 99L)).thenReturn(List.of());

        List<StandingDTO> standings = standingsService.getStandingsForCompetition(99L);

        assertTrue(standings.isEmpty());
        verify(matchRepository).findByFilters(null, null, 99L);
    }

    private Match createMatch(Long id, Team homeTeam, Team awayTeam, short homeScore, short awayScore, boolean finished) {
        Match match = new Match();
        match.setId(id);
        match.setLeftTeam(homeTeam);
        match.setRightTeam(awayTeam);
        match.setCompetition(competition);
        match.setDatetime(OffsetDateTime.parse("2026-05-17T20:00:00Z"));
        match.setVenue("Test Stadium");
        match.setLeftScore(homeScore);
        match.setRightScore(awayScore);
        match.setFinished(finished);
        return match;
    }

    private StandingDTO findByTeamName(List<StandingDTO> standings, String teamName) {
        return standings.stream()
                .filter(row -> row.getTeamName().equals(teamName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Team not found in standings: " + teamName));
    }
}