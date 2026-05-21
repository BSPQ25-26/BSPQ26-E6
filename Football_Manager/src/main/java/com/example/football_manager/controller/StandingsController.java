package com.example.football_manager.controller;

import com.example.football_manager.dto.StandingDTO;
import com.example.football_manager.repository.CompetitionRepository;
import com.example.football_manager.service.StandingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST endpoints for competition standings.
 */
@RestController
@RequestMapping("/api/standings")
@Tag(name = "Standings")
public class StandingsController {

    private final StandingsService standingsService;
    private final CompetitionRepository competitionRepository;

    public StandingsController(StandingsService standingsService,
                               CompetitionRepository competitionRepository) {
        this.standingsService = standingsService;
        this.competitionRepository = competitionRepository;
    }

    /**
     * Retrieves standings for a competition.
     *
     * @param competitionId competition identifier
     * @return standings table entries
     */
    @GetMapping
    @Operation(
            summary = "Get standings by competition",
            description = "Returns the standings table for the specified competition."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Standings list"),
            @ApiResponse(responseCode = "404", description = "Competition not found")
    })
    public ResponseEntity<List<StandingDTO>> getStandings(@RequestParam Long competitionId) {
        if (!competitionRepository.existsById(competitionId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Competition not found with id: " + competitionId);
        }

        return ResponseEntity.ok(standingsService.getStandingsForCompetition(competitionId));
    }
}