package com.example.football_manager.controller;

import com.example.football_manager.dto.PlayerRequestDTO;
import com.example.football_manager.model.Player;
import com.example.football_manager.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams/{teamId}/players")
@Tag(name = "Players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    @Operation(
            summary = "Create a player for a team",
            description = "Adds a new player to the given team squad."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Player created"),
            @ApiResponse(responseCode = "400", description = "Validation or business rule error")
    })
    public ResponseEntity<?> createPlayer(
            @PathVariable Long teamId,
            @Valid @RequestBody PlayerRequestDTO dto) {
        try {
            Player createdPlayer = playerService.createPlayer(teamId, dto);
            return new ResponseEntity<>(createdPlayer, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping
    @Operation(
            summary = "List players of a team",
            description = "Returns the squad players for the given team ordered by number."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Player list"),
            @ApiResponse(responseCode = "400", description = "Validation or business rule error")
    })
    public ResponseEntity<?> getPlayersByTeamId(@PathVariable Long teamId) {
        try {
            List<Player> players = playerService.getPlayersByTeamId(teamId);
            return ResponseEntity.ok(players);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{playerId}")
    @Operation(
            summary = "Update a player of a team",
            description = "Updates the details of a player that belongs to the given team."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Player updated"),
            @ApiResponse(responseCode = "400", description = "Validation or business rule error")
    })
    public ResponseEntity<?> updatePlayer(
            @PathVariable Long teamId,
            @PathVariable Long playerId,
            @Valid @RequestBody PlayerRequestDTO dto) {
        try {
            Player updatedPlayer = playerService.updatePlayer(teamId, playerId, dto);
            return ResponseEntity.ok(updatedPlayer);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{playerId}")
    @Operation(
            summary = "Delete a player from a team",
            description = "Deletes a player that belongs to the given team."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Player deleted"),
            @ApiResponse(responseCode = "400", description = "Validation or business rule error")
    })
    public ResponseEntity<?> deletePlayer(@PathVariable Long teamId, @PathVariable Long playerId) {
        try {
            playerService.deletePlayer(teamId, playerId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
