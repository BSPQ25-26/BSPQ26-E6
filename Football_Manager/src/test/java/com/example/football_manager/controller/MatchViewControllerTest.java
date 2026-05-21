package com.example.football_manager.controller;

import com.example.football_manager.dto.MatchRequestDTO;
import com.example.football_manager.dto.MatchResultDTO;
import com.example.football_manager.model.Competition;
import com.example.football_manager.model.Country;
import com.example.football_manager.model.Match;
import com.example.football_manager.model.Team;
import com.example.football_manager.service.MatchService;
import com.example.football_manager.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

import static com.example.football_manager.dto.MatchRequestDTO.MatchStatus.FINISHED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchViewControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(MatchViewControllerTest.class);

    @Mock
    private MatchService matchService;

    @Mock
    private TeamService teamService;

    private MatchViewController matchViewController;

    @BeforeEach
    void setUp() {
        matchViewController = new MatchViewController(matchService, teamService);
    }

    @Test
    void showResultsPage_addsFinishedResultsAndReturnsResultsView() {
        logger.info("Match results view test: expect finished results");
        MatchResultDTO resultDTO = new MatchResultDTO(
                1L,
                "Real Sociedad",
                "Athletic Club",
                (short) 2,
                (short) 1,
                OffsetDateTime.parse("2026-05-17T20:00:00Z")
        );

        when(matchService.getFinishedMatchResults()).thenReturn(List.of(resultDTO));

        Model model = new ConcurrentModel();

        String viewName = matchViewController.showResultsPage(model);

        assertEquals("match-results", viewName);
        assertEquals(List.of(resultDTO), model.getAttribute("results"));

        verify(matchService).getFinishedMatchResults();
    }

    @Test
    void showMatchesPage_addsMatchesTeamsAndSelectedFilters() {
        logger.info("Matches view test: competitionId={}, status={}, teamId={}", 1L, FINISHED, 2L);
        Match match = createFinishedMatch();
        Team team = match.getLeftTeam();

        when(matchService.getMatches(1L, FINISHED, 2L)).thenReturn(List.of(match));
        when(teamService.getAllTeams()).thenReturn(List.of(team));

        Model model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("isAdmin", true);

        String viewName = matchViewController.showMatchesPage(
                1L,
                FINISHED,
                2L,
                model,
                session
        );

        assertEquals("matches", viewName);
        assertEquals(List.of(match), model.getAttribute("matches"));
        assertEquals(List.of(team), model.getAttribute("teams"));
        assertEquals(1L, model.getAttribute("selectedTeamId"));
        assertEquals(true, model.getAttribute("isAdmin"));

        verify(matchService).getMatches(1L, FINISHED, 2L);
        verify(teamService).getAllTeams();
    }

    @Test
    void showMatchesPage_whenServiceThrowsInvalidFilter_returnsBadRequest() {
        logger.info("Matches view invalid filter test: status={}", MatchRequestDTO.MatchStatus.IN_PROGRESS);
        when(matchService.getMatches(null, MatchRequestDTO.MatchStatus.IN_PROGRESS, null))
                .thenThrow(new IllegalArgumentException("Invalid status filter"));

        Model model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> matchViewController.showMatchesPage(
                        null,
                        MatchRequestDTO.MatchStatus.IN_PROGRESS,
                        null,
                        model,
                        session
                )
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(matchService).getMatches(null, MatchRequestDTO.MatchStatus.IN_PROGRESS, null);
    }

    @Test
    void showUpcomingMatchesPage_addsUpcomingMatchesAndReturnsUpcomingView() {
        logger.info("Upcoming matches view test: adminSession=true");
        Match upcomingMatch = createUpcomingMatch();

        when(matchService.getUpcomingMatches()).thenReturn(List.of(upcomingMatch));

        Model model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("isAdmin", true);

        String viewName = matchViewController.showUpcomingMatchesPage(model, session);

        assertEquals("upcoming-matches", viewName);
        assertEquals(List.of(upcomingMatch), model.getAttribute("matches"));
        assertEquals(true, model.getAttribute("isAdmin"));

        verify(matchService).getUpcomingMatches();
    }

    @Test
    void showUpcomingMatchesPage_withoutAdminSession_shouldSetIsAdminFalse() {
        logger.info("Upcoming matches view test: adminSession=false");
        Match upcomingMatch = createUpcomingMatch();

        when(matchService.getUpcomingMatches()).thenReturn(List.of(upcomingMatch));

        Model model = new ConcurrentModel();
        MockHttpSession session = new MockHttpSession();

        String viewName = matchViewController.showUpcomingMatchesPage(model, session);

        assertEquals("upcoming-matches", viewName);
        assertEquals(List.of(upcomingMatch), model.getAttribute("matches"));
        assertEquals(false, model.getAttribute("isAdmin"));

        verify(matchService).getUpcomingMatches();
    }

    @Test
    void showScheduleForm_shouldAddMatchRequestAndTeamsAndReturnScheduleView() {
        logger.info("Schedule form view test: expect teams list");
        Team team = createTeam(1L, "Real Sociedad", "real.png");

        when(teamService.getAllTeams()).thenReturn(List.of(team));

        Model model = new ConcurrentModel();

        String viewName = matchViewController.showScheduleForm(model);

        assertEquals("schedule-match", viewName);
        assertNotNull(model.getAttribute("matchRequest"));
        assertEquals(List.of(team), model.getAttribute("teams"));

        verify(teamService).getAllTeams();
    }

    private Match createFinishedMatch() {
        Match match = createBaseMatch();
        match.setLeftScore((short) 2);
        match.setRightScore((short) 1);
        match.setFinished(true);
        return match;
    }

    private Match createUpcomingMatch() {
        Match match = createBaseMatch();
        match.setDatetime(OffsetDateTime.parse("2026-06-17T20:00:00Z"));
        match.setLeftScore((short) 0);
        match.setRightScore((short) 0);
        match.setFinished(false);
        return match;
    }

    private Match createBaseMatch() {
        Team homeTeam = createTeam(1L, "Real Sociedad", "home-logo.png");
        Team awayTeam = createTeam(2L, "Athletic Club", "away-logo.png");
        Competition competition = new Competition(1L, "LaLiga");

        Match match = new Match();
        match.setId(1L);
        match.setLeftTeam(homeTeam);
        match.setRightTeam(awayTeam);
        match.setCompetition(competition);
        match.setDatetime(OffsetDateTime.parse("2026-05-17T20:00:00Z"));
        match.setVenue("Anoeta");

        return match;
    }

    private Team createTeam(Long id, String name, String logoUrl) {
        Country country = new Country(1L, "Spain");
        return new Team(id, name, logoUrl, country);
    }
}