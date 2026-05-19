package com.example.football_manager.controller;

import com.example.football_manager.dto.StandingDTO;
import com.example.football_manager.model.Competition;
import com.example.football_manager.service.StandingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StandingsViewControllerTest {

    @Mock
    private StandingsService standingsService;

    private StandingsViewController standingsViewController;

    @BeforeEach
    void setUp() {
        standingsViewController = new StandingsViewController(standingsService);
    }

    @Test
    void standingsPage_whenCompetitionsAreEmpty_shouldReturnEmptyStandingsView() {
        when(standingsService.getCompetitions()).thenReturn(List.of());

        Model model = new ConcurrentModel();

        String viewName = standingsViewController.standingsPage(null, model);

        assertEquals("standings", viewName);
        assertEquals(List.of(), model.getAttribute("competitions"));
        assertEquals(List.of(), model.getAttribute("standings"));
        assertNull(model.getAttribute("selectedCompetitionId"));

        verify(standingsService).getCompetitions();
        verify(standingsService, never()).getStandingsForCompetition(anyLong());
    }

    @Test
    void standingsPage_withoutCompetitionId_shouldUseFirstCompetition() {
        Competition premierLeague = new Competition(1L, "Premier League");
        Competition laLiga = new Competition(2L, "LaLiga");

        StandingDTO row = new StandingDTO(
                "Arsenal",
                "arsenal.png",
                1,
                1,
                0,
                0,
                2,
                1,
                1,
                3
        );

        when(standingsService.getCompetitions()).thenReturn(List.of(premierLeague, laLiga));
        when(standingsService.getStandingsForCompetition(1L)).thenReturn(List.of(row));

        Model model = new ConcurrentModel();

        String viewName = standingsViewController.standingsPage(null, model);

        assertEquals("standings", viewName);
        assertEquals(List.of(premierLeague, laLiga), model.getAttribute("competitions"));
        assertEquals(List.of(row), model.getAttribute("standings"));
        assertEquals(1L, model.getAttribute("selectedCompetitionId"));

        verify(standingsService).getCompetitions();
        verify(standingsService).getStandingsForCompetition(1L);
    }

    @Test
    void standingsPage_withValidCompetitionId_shouldUseRequestedCompetition() {
        Competition premierLeague = new Competition(1L, "Premier League");
        Competition laLiga = new Competition(2L, "LaLiga");

        StandingDTO row = new StandingDTO(
                "Real Sociedad",
                "real.png",
                2,
                2,
                0,
                0,
                5,
                1,
                4,
                6
        );

        when(standingsService.getCompetitions()).thenReturn(List.of(premierLeague, laLiga));
        when(standingsService.getStandingsForCompetition(2L)).thenReturn(List.of(row));

        Model model = new ConcurrentModel();

        String viewName = standingsViewController.standingsPage(2L, model);

        assertEquals("standings", viewName);
        assertEquals(List.of(premierLeague, laLiga), model.getAttribute("competitions"));
        assertEquals(List.of(row), model.getAttribute("standings"));
        assertEquals(2L, model.getAttribute("selectedCompetitionId"));

        verify(standingsService).getStandingsForCompetition(2L);
    }

    @Test
    void standingsPage_withInvalidCompetitionId_shouldFallbackToFirstCompetition() {
        Competition premierLeague = new Competition(1L, "Premier League");
        Competition laLiga = new Competition(2L, "LaLiga");

        StandingDTO row = new StandingDTO(
                "Arsenal",
                "arsenal.png",
                1,
                1,
                0,
                0,
                3,
                0,
                3,
                3
        );

        when(standingsService.getCompetitions()).thenReturn(List.of(premierLeague, laLiga));
        when(standingsService.getStandingsForCompetition(1L)).thenReturn(List.of(row));

        Model model = new ConcurrentModel();

        String viewName = standingsViewController.standingsPage(99L, model);

        assertEquals("standings", viewName);
        assertEquals(List.of(row), model.getAttribute("standings"));
        assertEquals(1L, model.getAttribute("selectedCompetitionId"));

        verify(standingsService).getStandingsForCompetition(1L);
        verify(standingsService, never()).getStandingsForCompetition(99L);
    }
}