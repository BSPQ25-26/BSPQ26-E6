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
        // TODO: Find match by ID, update fields, and save
        
        // Manual validation for team integrity during updates
        if (request.getHomeTeamId() != null && request.getAwayTeamId() != null) {
            if (request.getHomeTeamId().equals(request.getAwayTeamId())) {
                throw new IllegalArgumentException("Validation Error: Home and Away teams must be different.");
            }
        }
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
}