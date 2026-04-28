package com.example.football_manager.service;

import com.example.football_manager.dto.CompetitionRequestDTO;
import com.example.football_manager.model.Competition;
import com.example.football_manager.repository.CompetitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompetitionServiceTest {

    private CompetitionRepository competitionRepository;
    private CompetitionService competitionService;

    @BeforeEach
    void setUp() {
        competitionRepository = mock(CompetitionRepository.class);
        competitionService = new CompetitionService(competitionRepository);
    }

    @Test
    void createCompetition_shouldCreateCompetitionSuccessfully() {
        CompetitionRequestDTO dto = new CompetitionRequestDTO(" LaLiga ");

        Competition savedCompetition = new Competition();
        savedCompetition.setId(1L);
        savedCompetition.setName("LaLiga");

        when(competitionRepository.save(any(Competition.class))).thenReturn(savedCompetition);

        Competition result = competitionService.createCompetition(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("LaLiga", result.getName());

        verify(competitionRepository).save(any(Competition.class));
    }

    @Test
    void updateCompetition_shouldUpdateSuccessfully() {
        Competition existingCompetition = new Competition();
        existingCompetition.setId(1L);
        existingCompetition.setName("Old League");

        CompetitionRequestDTO dto = new CompetitionRequestDTO(" Champions League ");

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(existingCompetition));
        when(competitionRepository.save(any(Competition.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Competition result = competitionService.updateCompetition(1L, dto);

        assertEquals("Champions League", result.getName());

        verify(competitionRepository).findById(1L);
        verify(competitionRepository).save(existingCompetition);
    }

    @Test
    void updateCompetition_shouldThrowExceptionWhenCompetitionNotFound() {
        CompetitionRequestDTO dto = new CompetitionRequestDTO("Europa League");

        when(competitionRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> competitionService.updateCompetition(99L, dto)
        );

        assertEquals("Competition not found with id: 99", ex.getMessage());

        verify(competitionRepository).findById(99L);
        verify(competitionRepository, never()).save(any(Competition.class));
    }

    @Test
    void getCompetitionById_shouldReturnCompetition() {
        Competition competition = new Competition();
        competition.setId(1L);
        competition.setName("Premier League");

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        Optional<Competition> result = competitionService.getCompetitionById(1L);

        assertTrue(result.isPresent());
        assertEquals("Premier League", result.get().getName());

        verify(competitionRepository).findById(1L);
    }

    @Test
    void getAllCompetitions_shouldReturnCompetitions() {
        Competition c1 = new Competition();
        c1.setId(1L);
        c1.setName("LaLiga");

        Competition c2 = new Competition();
        c2.setId(2L);
        c2.setName("Premier League");

        when(competitionRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Competition> result = competitionService.getAllCompetitions();

        assertEquals(2, result.size());
        assertEquals("LaLiga", result.get(0).getName());
        assertEquals("Premier League", result.get(1).getName());

        verify(competitionRepository).findAll();
    }

    @Test
    void getAllCompetitions_shouldReturnEmptyList() {
        when(competitionRepository.findAll()).thenReturn(List.of());

        List<Competition> result = competitionService.getAllCompetitions();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(competitionRepository).findAll();
    }

    @Test
    void deleteCompetition_shouldDeleteSuccessfully() {
        Competition competition = new Competition();
        competition.setId(1L);
        competition.setName("LaLiga");

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        competitionService.deleteCompetition(1L);

        verify(competitionRepository).findById(1L);
        verify(competitionRepository).delete(competition);
    }

    @Test
    void deleteCompetition_shouldThrowExceptionWhenCompetitionNotFound() {
        when(competitionRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> competitionService.deleteCompetition(99L)
        );

        assertEquals("Competition not found with id: 99", ex.getMessage());

        verify(competitionRepository).findById(99L);
        verify(competitionRepository, never()).delete(any(Competition.class));
    }
}