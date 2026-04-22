package com.example.football_manager.service;

import com.example.football_manager.dto.CompetitionRequestDTO;
import com.example.football_manager.model.Competition;
import com.example.football_manager.repository.CompetitionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompetitionService {

    private final CompetitionRepository competitionRepository;

    public CompetitionService(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    public Competition createCompetition(CompetitionRequestDTO dto) {
        Competition competition = new Competition();
        competition.setName(dto.getName().trim());
        return competitionRepository.save(competition);
    }

    public Competition updateCompetition(Long id, CompetitionRequestDTO dto) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competition not found with id: " + id));

        competition.setName(dto.getName().trim());
        return competitionRepository.save(competition);
    }

    public Optional<Competition> getCompetitionById(Long id) {
        return competitionRepository.findById(id);
    }

    public List<Competition> getAllCompetitions() {
        return competitionRepository.findAll();
    }

    public void deleteCompetition(Long id) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competition not found with id: " + id));

        competitionRepository.delete(competition);
    }
}
