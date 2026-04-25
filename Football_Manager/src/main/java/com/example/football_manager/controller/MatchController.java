package com.example.football_manager.controller;

import com.example.football_manager.dto.MatchRequestDTO;
import com.example.football_manager.dto.MatchResultDTO;
import com.example.football_manager.dto.MatchResultRequestDTO;
import com.example.football_manager.model.Match;
import com.example.football_manager.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @GetMapping
    public ResponseEntity<List<Match>> getAllMatches() {
        return ResponseEntity.ok(matchService.getAllMatches());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createMatchFromJson(@Valid @RequestBody MatchRequestDTO matchDTO) {
        return createMatchResponse(matchDTO);
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> createMatchFromForm(@Valid @ModelAttribute MatchRequestDTO matchDTO) {
        return createMatchResponse(matchDTO);
    }

    private ResponseEntity<String> createMatchResponse(MatchRequestDTO matchDTO) {
        try {
            String response = matchService.createMatch(matchDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/results")
    public ResponseEntity<List<MatchResultDTO>> getMatchResults() {
        return ResponseEntity.ok(matchService.getFinishedMatchResults());
    }

    // Edit match details
    @PostMapping("/{id}")
    public ResponseEntity<String> updateMatch(@PathVariable Long id, @RequestBody MatchRequestDTO matchDTO) {
        try {
            return ResponseEntity.ok(matchService.updateMatch(id, matchDTO));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMatch(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(matchService.deleteMatch(id));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/result")
    public ResponseEntity<String> registerResult(
            @PathVariable Long id,
            @Valid @RequestBody MatchResultRequestDTO resultDTO) {
        try {
            return ResponseEntity.ok(matchService.registerResult(id, resultDTO));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}