package com.example.football_manager.controller;

import com.example.football_manager.dto.TeamRequestDTO;
import com.example.football_manager.model.Team;
import com.example.football_manager.service.TeamService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<Team> createTeam(@Valid @RequestBody TeamRequestDTO dto) {
        Team createdTeam = teamService.createTeam(dto);
        return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeam(@PathVariable Long id,
                                           @Valid @RequestBody TeamRequestDTO dto) {
        Team updatedTeam = teamService.updateTeam(id, dto);
        return ResponseEntity.ok(updatedTeam);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
        return ResponseEntity.of(teamService.getTeamById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Team>> getTeamsContainingName(@RequestParam String name) {
        return ResponseEntity.ok(teamService.getTeamsContainingName(name));
    }
    
    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        List<Team> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }
}