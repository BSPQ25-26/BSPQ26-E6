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
        Match match = createMatch();
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

    private Match createMatch() {
        Country country = new Country(1L, "Spain");
        Team homeTeam = new Team(1L, "Real Sociedad", "home-logo.png", country);
        Team awayTeam = new Team(2L, "Athletic Club", "away-logo.png", country);
        Competition competition = new Competition(1L, "LaLiga");

        Match match = new Match();
        match.setId(1L);
        match.setLeftTeam(homeTeam);
        match.setRightTeam(awayTeam);
        match.setCompetition(competition);
        match.setDatetime(OffsetDateTime.parse("2026-05-17T20:00:00Z"));
        match.setVenue("Anoeta");
        match.setLeftScore((short) 2);
        match.setRightScore((short) 1);
        match.setFinished(true);

        return match;
    }
}