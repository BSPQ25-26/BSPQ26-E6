package com.example.football_manager.controller;

import com.example.football_manager.dto.*;
import com.example.football_manager.service.FantasyService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fantasy")
public class FantasyController {

    private final FantasyService fantasyService;

    public FantasyController(FantasyService fantasyService) {
        this.fantasyService = fantasyService;
    }

    @PostMapping("/leagues")
    public ResponseEntity<?> createLeague(
            @Valid @RequestBody CreateFantasyLeagueRequestDTO request,
            HttpSession session
    ) {
        try {
            Long userId = getLoggedUserId(session);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(fantasyService.createLeague(userId, request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/leagues/join")
    public ResponseEntity<?> joinLeague(
            @Valid @RequestBody JoinFantasyLeagueRequestDTO request,
            HttpSession session
    ) {
        try {
            Long userId = getLoggedUserId(session);
            return ResponseEntity.ok(fantasyService.joinLeague(userId, request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/leagues/my")
    public ResponseEntity<?> getMyLeagues(HttpSession session) {
        try {
            Long userId = getLoggedUserId(session);
            return ResponseEntity.ok(fantasyService.getMyLeagues(userId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @GetMapping("/leagues/{leagueId}/leaderboard")
    public ResponseEntity<?> getLeaderboard(@PathVariable Long leagueId) {
        try {
            return ResponseEntity.ok(fantasyService.getLeaderboard(leagueId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/lineup")
    public ResponseEntity<?> saveLineup(
            @Valid @RequestBody FantasyLineupRequestDTO request,
            HttpSession session
    ) {
        try {
            Long userId = getLoggedUserId(session);
            return ResponseEntity.ok(fantasyService.saveLineup(userId, request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/lineup")
    public ResponseEntity<?> getLineup(HttpSession session) {
        try {
            Long userId = getLoggedUserId(session);
            return ResponseEntity.ok(fantasyService.getLineup(userId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @GetMapping("/score")
    public ResponseEntity<?> getMyScore(HttpSession session) {
        try {
            Long userId = getLoggedUserId(session);
            return ResponseEntity.ok(fantasyService.getMyScore(userId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    private Long getLoggedUserId(HttpSession session) {
        Object userId = session.getAttribute("userId");

        if (userId == null) {
            throw new IllegalArgumentException("User must be logged in.");
        }

        return (Long) userId;
    }
}