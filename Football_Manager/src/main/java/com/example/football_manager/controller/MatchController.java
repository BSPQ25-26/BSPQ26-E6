package com.example.football_manager.controller;

import com.example.football_manager.dto.MatchRequestDTO;
import com.example.football_manager.dto.MatchResultDTO;
import com.example.football_manager.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    // Schedule a match
    @PostMapping
    public ResponseEntity<String> createMatch(@Valid @ModelAttribute MatchRequestDTO matchDTO) {
        try {
            String response = matchService.createMatch(matchDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // Retrieve finished match results
    @GetMapping("/results")
    public ResponseEntity<List<MatchResultDTO>> getMatchResults() {
        return ResponseEntity.ok(matchService.getFinishedMatchResults());
    }

    // Edit match details
    @PutMapping("/{id}")
    public ResponseEntity<String> updateMatch(@PathVariable Long id, @RequestBody MatchRequestDTO matchDTO) {
        return ResponseEntity.ok(matchService.updateMatch(id, matchDTO));
    }

    // Delete a match
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMatch(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(matchService.deleteMatch(id));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Register result (Score)
    @PatchMapping("/{id}/result")
    public ResponseEntity<String> registerResult(
            @PathVariable Long id, 
            @RequestParam Integer homeScore, 
            @RequestParam Integer awayScore) {
        return ResponseEntity.ok(matchService.registerResult(id, homeScore, awayScore));
    }
}
