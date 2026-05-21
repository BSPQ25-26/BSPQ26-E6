package com.example.football_manager.controller;

import com.example.football_manager.dto.CompetitionRequestDTO;
import com.example.football_manager.model.Competition;
import com.example.football_manager.service.CompetitionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompetitionControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(CompetitionControllerTest.class);

    @Mock
    private CompetitionService competitionService;

    private CompetitionController competitionController;

    @BeforeEach
    void setUp() {
        competitionController = new CompetitionController(competitionService);
    }

    @Test
    void createCompetition_shouldReturnCreatedCompetition() {
        CompetitionRequestDTO dto = new CompetitionRequestDTO("LaLiga");
        Competition competition = new Competition(1L, "LaLiga");

        logger.info("Create competition test: name={}", dto.getName());

        when(competitionService.createCompetition(dto)).thenReturn(competition);

        ResponseEntity<Competition> response = competitionController.createCompetition(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(competition, response.getBody());
        verify(competitionService).createCompetition(dto);
    }

    @Test
    void updateCompetition_shouldReturnUpdatedCompetition() {
        CompetitionRequestDTO dto = new CompetitionRequestDTO("Premier League");
        Competition competition = new Competition(1L, "Premier League");

        logger.info("Update competition test: id={}, name={}", 1L, dto.getName());

        when(competitionService.updateCompetition(1L, dto)).thenReturn(competition);

        ResponseEntity<Competition> response = competitionController.updateCompetition(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(competition, response.getBody());
        verify(competitionService).updateCompetition(1L, dto);
    }

    @Test
    void getCompetitionById_whenExists_shouldReturnOk() {
        Competition competition = new Competition(1L, "LaLiga");

        logger.info("Get competition by id test: id={}", 1L);

        when(competitionService.getCompetitionById(1L)).thenReturn(Optional.of(competition));

        ResponseEntity<Competition> response = competitionController.getCompetitionById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(competition, response.getBody());
        verify(competitionService).getCompetitionById(1L);
    }

    @Test
    void getCompetitionById_whenDoesNotExist_shouldReturnNotFound() {
        logger.info("Get competition by id not found test: id={}", 99L);
        when(competitionService.getCompetitionById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Competition> response = competitionController.getCompetitionById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(competitionService).getCompetitionById(99L);
    }

    @Test
    void getAllCompetitions_shouldReturnCompetitions() {
        Competition competition = new Competition(1L, "LaLiga");

        logger.info("Get all competitions test: expectedCount={}", 1);

        when(competitionService.getAllCompetitions()).thenReturn(List.of(competition));

        ResponseEntity<List<Competition>> response = competitionController.getAllCompetitions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(competition), response.getBody());
        verify(competitionService).getAllCompetitions();
    }

    @Test
    void deleteCompetition_shouldReturnNoContent() {
        logger.info("Delete competition test: id={}", 1L);
        ResponseEntity<Void> response = competitionController.deleteCompetition(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(competitionService).deleteCompetition(1L);
    }
}