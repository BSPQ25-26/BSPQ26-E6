package com.example.football_manager.controller;

import com.example.football_manager.dto.MatchRequestDTO;
import com.example.football_manager.dto.MatchResultDTO;
import com.example.football_manager.dto.MatchResultRequestDTO;
import com.example.football_manager.model.Match;
import com.example.football_manager.service.MatchService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for managing matches and match results.
 */
@RestController
@RequestMapping("/api/matches")
@Tag(name = "Matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    /**
     * Lists matches with optional filters.
     *
     * @param teamId optional team filter
     * @param status optional match status filter
     * @param competitionId optional competition filter
     * @return matching matches
     */
    @GetMapping
    @Operation(
            summary = "List matches",
            description = "Returns matches, optionally filtered by team, status, or competition."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Match list"),
            @ApiResponse(responseCode = "400", description = "Unsupported filter value")
    })
    public ResponseEntity<List<Match>> getMatches(
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) MatchRequestDTO.MatchStatus status,
            @RequestParam(required = false) Long competitionId) {
        try {
            return ResponseEntity.ok(matchService.getMatches(teamId, status, competitionId));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /**
     * Lists upcoming matches.
     *
     * @return upcoming matches ordered by date
     */
    @GetMapping("/upcoming")
    @Operation(
            summary = "List all upcoming matches",
            description = "Returns matches that have not finished yet, sorted by date ascending."
    )
    public ResponseEntity<List<Match>> getUpcomingMatches() {
        return ResponseEntity.ok(matchService.getUpcomingMatches());
    }

    /**
     * Creates a match using JSON payload.
     *
     * @param matchDTO match details
     * @return creation message
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create a match (JSON)",
            description = "Creates a match using a JSON body. Use this for API clients.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Match details",
                    content = @Content(schema = @Schema(implementation = MatchRequestDTO.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Match created"),
            @ApiResponse(responseCode = "400", description = "Validation or business rule error")
    })
    public ResponseEntity<String> createMatchFromJson(@Valid @RequestBody MatchRequestDTO matchDTO) {
        return createMatchResponse(matchDTO);
    }

    /**
     * Creates a match using form-encoded payload.
     *
     * @param matchDTO match form fields
     * @return creation message
     */
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Operation(
            summary = "Create a match (form)",
            description = "Creates a match using form-encoded fields. Useful for HTML form submissions.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Match form fields",
                    content = @Content(schema = @Schema(implementation = MatchRequestDTO.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Match created"),
            @ApiResponse(responseCode = "400", description = "Validation or business rule error")
    })
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

    /**
     * Lists results for finished matches.
     *
     * @return finished match results
     */
    @GetMapping("/results")
    @Operation(
            summary = "List finished match results",
            description = "Returns results for matches with registered scores."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Match results")
    })
    public ResponseEntity<List<MatchResultDTO>> getMatchResults() {
        return ResponseEntity.ok(matchService.getFinishedMatchResults());
    }

    /**
     * Updates match details.
     *
     * @param id match identifier
     * @param matchDTO match update payload
     * @return update message
     */
    @PostMapping("/{id}")
    @Operation(
            summary = "Update match details",
            description = "Edits match details such as kickoff time, venue, or participants.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Match updates",
                    content = @Content(schema = @Schema(implementation = MatchRequestDTO.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Match updated"),
            @ApiResponse(responseCode = "400", description = "Validation or business rule error")
    })
    public ResponseEntity<String> updateMatch(@PathVariable Long id, @RequestBody MatchRequestDTO matchDTO) {
        try {
            return ResponseEntity.ok(matchService.updateMatch(id, matchDTO));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Deletes a match.
     *
     * @param id match identifier
     * @return delete message
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a match",
            description = "Removes a match by its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Match deleted"),
            @ApiResponse(responseCode = "400", description = "Match not found or cannot be deleted")
    })
    public ResponseEntity<String> deleteMatch(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(matchService.deleteMatch(id));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Registers a match result.
     *
     * @param id match identifier
     * @param resultDTO goal list payload
     * @return result registration message
     */
    @PatchMapping("/{id}/result")
    @Operation(
            summary = "Register match result",
            description = "Registers goals and final score for a match.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "List of goals to register",
                    content = @Content(schema = @Schema(implementation = MatchResultRequestDTO.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Result registered"),
            @ApiResponse(responseCode = "400", description = "Validation or business rule error")
    })
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
