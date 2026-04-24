package com.example.football_manager.service;

import com.example.football_manager.dto.MatchRequestDTO;
import com.example.football_manager.repository.MatchRepository; 
// import com.example.football_manager.repository.TeamRepository;  
import com.example.football_manager.dto.MatchResultDTO;
import com.example.football_manager.model.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

    // @Autowired
    // private MatchRepository matchRepository;
    
    // @Autowired
    // private TeamRepository teamRepository;
    private MatchRepository matchRepository;

    public MatchService() {
    }

    @Autowired
    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    /**
     * Schedule a new match with manual validations.
     */
    public String createMatch(MatchRequestDTO request) {
        // 1. Manual Validation: Check for required fields (Replacing @NotNull)
        if (request.getHomeTeamId() == null || request.getAwayTeamId() == null) {
            throw new IllegalArgumentException("Validation Error: Both Home and Away team IDs are required.");
        }

        if (request.getKickoffTime() == null || request.getVenue() == null) {
            throw new IllegalArgumentException("Validation Error: Kickoff time and Venue are required.");
        }

        // 2. Requirement 2.6: Check if home and away teams are the same
        if (request.getHomeTeamId().equals(request.getAwayTeamId())) {
            throw new IllegalArgumentException("Validation Error: Home and Away teams must be different.");
        }

        // Existence Validation 
        // teamRepository.findById(request.getHomeTeamId()).orElseThrow(() -> new RuntimeException("Home team not found"));
        // teamRepository.findById(request.getAwayTeamId()).orElseThrow(() -> new RuntimeException("Away team not found"));

        // 3. Set default status if not provided
        if (request.getStatus() == null) {
            request.setStatus(MatchRequestDTO.MatchStatus.SCHEDULED);
        }

        // 4. Save to database 
        // Match match = new Match(request...);
        // matchRepository.save(match);
        
        return "Match scheduled successfully.";
    }

    /**
     * Update match details (time, venue, status).
     */
    public String updateMatch(Long id, MatchRequestDTO request) {
        validateMatchUpdate(request);
        return "Match with ID " + id + " has been updated.";
    }

    /**
     * Delete a match from the system.
     */
    public String deleteMatch(Long id) {
        // TODO: check if exists then matchRepository.deleteById(id);
        return "Match with ID " + id + " has been deleted.";
    }

    /**
     * Register final match result.
     */
    public String registerResult(Long id, Integer homeScore, Integer awayScore) {
        // Requirement 2.6: Data integrity for finished matches
        // TODO: Update scores and set status to FINISHED
        return "Result registered for match " + id + ": " + homeScore + " - " + awayScore;
    }
    
    /**
     * Display teams and final score for finished matches.
     */
    public List<MatchResultDTO> getFinishedMatchResults() {
        if (matchRepository == null) {
            return Collections.emptyList();
        }

        List<Match> matches = matchRepository.findByFinishedTrueOrderByDatetimeDesc();

        return matches.stream()
                .map(match -> new MatchResultDTO(
                        match.getId(),
                        match.getLeftTeam().getName(),
                        match.getRightTeam().getName(),
                        match.getLeftScore(),
                        match.getRightScore(),
                        match.getDatetime()
                ))
                .toList();
    }
    public Optional<MatchRequestDTO> getMatchForEdit(Long id) {
        if (matchRepository == null) {
            return Optional.empty();
        }

        return matchRepository.findById(id).map(match -> {
            MatchRequestDTO dto = new MatchRequestDTO();
            dto.setHomeTeamId(match.getLeftTeam().getId());
            dto.setAwayTeamId(match.getRightTeam().getId());
            dto.setKickoffTime(match.getDatetime().toLocalDateTime());
            dto.setVenue("TBD");
            dto.setStatus(match.isFinished()
                    ? MatchRequestDTO.MatchStatus.FINISHED
                    : MatchRequestDTO.MatchStatus.SCHEDULED);
            dto.setHomeScore((int) match.getLeftScore());
            dto.setAwayScore((int) match.getRightScore());
            return dto;
        });
    }

    private void validateMatchUpdate(MatchRequestDTO request) {
            if (request.getHomeTeamId() == null || request.getAwayTeamId() == null) {
            throw new IllegalArgumentException("Validation Error: Both Home and Away team IDs are required.");
        }

        if (request.getKickoffTime() == null || request.getVenue() == null || request.getVenue().isBlank()) {
            throw new IllegalArgumentException("Validation Error: Kickoff time and Venue are required.");
        }

        if (request.getStatus() == null) {
            throw new IllegalArgumentException("Validation Error: Match status is required.");
        }

        if (request.getHomeTeamId().equals(request.getAwayTeamId())) {
            throw new IllegalArgumentException("Validation Error: Home and Away teams must be different.");
        }

        if (request.getHomeScore() != null && request.getAwayScore() == null) {
            throw new IllegalArgumentException("Validation Error: Away score is required when home score is provided.");
        }

        if (request.getAwayScore() != null && request.getHomeScore() == null) {
            throw new IllegalArgumentException("Validation Error: Home score is required when away score is provided.");
        }

        if (request.getStatus() == MatchRequestDTO.MatchStatus.FINISHED
                && (request.getHomeScore() == null || request.getAwayScore() == null)) {
            throw new IllegalArgumentException("Validation Error: Both scores are required when status is FINISHED.");
        }

        if (request.getStatus() != MatchRequestDTO.MatchStatus.FINISHED
                && (request.getHomeScore() != null || request.getAwayScore() != null)) {
            throw new IllegalArgumentException("Validation Error: Scores can only be submitted when status is FINISHED.");
        }
    }
}