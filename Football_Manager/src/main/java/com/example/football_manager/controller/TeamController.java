package com.example.football_manager.controller;

import com.example.football_manager.dto.TeamRequestDTO;
import com.example.football_manager.model.Team;
import com.example.football_manager.service.TeamService;
import jakarta.validation.Valid;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
@Tag(name = "Teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    @Operation(
            summary = "Create a team",
            description = "Creates a new team linked to a country.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Team details",
                    content = @Content(schema = @Schema(implementation = TeamRequestDTO.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Team created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<Team> createTeam(@Valid @RequestBody TeamRequestDTO dto) {
        Team createdTeam = teamService.createTeam(dto);
        return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a team",
            description = "Updates the name, logo, or country of an existing team.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Updated team details",
                    content = @Content(schema = @Schema(implementation = TeamRequestDTO.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Team updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Team not found")
    })
    public ResponseEntity<Team> updateTeam(@PathVariable Long id,
                                           @Valid @RequestBody TeamRequestDTO dto) {
        Team updatedTeam = teamService.updateTeam(id, dto);
        return ResponseEntity.ok(updatedTeam);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a team",
            description = "Deletes a team by its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Team deleted"),
            @ApiResponse(responseCode = "404", description = "Team not found")
    })
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get team by id",
            description = "Returns a team with its persisted details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Team found"),
            @ApiResponse(responseCode = "404", description = "Team not found")
    })
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
        return ResponseEntity.of(teamService.getTeamById(id));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search teams by name",
            description = "Returns teams whose name contains the provided text (case-insensitive)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Teams matching query")
    })
    public ResponseEntity<List<Team>> getTeamsContainingName(@RequestParam String name) {
        return ResponseEntity.ok(teamService.getTeamsContainingName(name));
    }
    
    @GetMapping
    @Operation(
            summary = "List all teams",
            description = "Returns all teams currently registered in the system."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Team list")
    })
    public ResponseEntity<List<Team>> getAllTeams() {
        List<Team> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }
}