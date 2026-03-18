package com.example.football_manager.service;

import com.example.football_manager.dto.MatchRequestDTO;
// import com.example.football_manager.repository.MatchRepository; 
// import com.example.football_manager.repository.TeamRepository;  
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchService {

    // @Autowired
    // private MatchRepository matchRepository;
    
    // @Autowired
    // private TeamRepository teamRepository;

    /**
     * Schedule a new match with validations.
     */
    public String createMatch(MatchRequestDTO request) {
        // Check if home and away teams are the same
        if (request.getHomeTeamId().equals(request.getAwayTeamId())) {
            throw new IllegalArgumentException("Validation Error: Home and Away teams must be different.");
        }

        // Existence Validation
        // teamRepository.findById(request.getHomeTeamId()).orElseThrow(() -> new RuntimeException("Home team not found"));
        // teamRepository.findById(request.getAwayTeamId()).orElseThrow(() -> new RuntimeException("Away team not found"));

        // Set default status if not provided
        if (request.getStatus() == null) {
            request.setStatus(MatchRequestDTO.MatchStatus.SCHEDULED);
        }

        // Save to database 
        // Match match = new Match(request...);
        // matchRepository.save(match);
        
        return "Match scheduled successfully.";
    }

    /**
     * Update match details (time, venue, status).
     */
    public String updateMatch(Long id, MatchRequestDTO request) {
        // TODO: Find match by ID, update fields, and save
        // Check again for team integrity if teams are being updated
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
     *  Register final match result.
     */
    public String registerResult(Long id, Integer homeScore, Integer awayScore) {
        // Requirement: Data integrity for finished matches
        // TODO: Update scores and set status to FINISHED
        return "Result registered for match " + id + ": " + homeScore + " - " + awayScore;
    }
}