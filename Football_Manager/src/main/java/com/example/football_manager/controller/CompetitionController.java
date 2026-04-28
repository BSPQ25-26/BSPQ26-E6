package com.example.football_manager.controller;

import com.example.football_manager.dto.CompetitionRequestDTO;
import com.example.football_manager.model.Competition;
import com.example.football_manager.service.CompetitionService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
@Tag(name = "Competitions")
public class CompetitionController {

    private final CompetitionService competitionService;

    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @PostMapping
    @Operation(
            summary = "Create a competition",
            description = "Creates a new competition entry.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Competition details",
                    content = @Content(schema = @Schema(implementation = CompetitionRequestDTO.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Competition created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<Competition> createCompetition(@Valid @RequestBody CompetitionRequestDTO dto) {
        Competition createdCompetition = competitionService.createCompetition(dto);
        return new ResponseEntity<>(createdCompetition, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a competition",
            description = "Updates the competition name.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Updated competition details",
                    content = @Content(schema = @Schema(implementation = CompetitionRequestDTO.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Competition updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Competition not found")
    })
    public ResponseEntity<Competition> updateCompetition(@PathVariable Long id,
                                                         @Valid @RequestBody CompetitionRequestDTO dto) {
        Competition updatedCompetition = competitionService.updateCompetition(id, dto);
        return ResponseEntity.ok(updatedCompetition);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get competition by id",
            description = "Returns competition details for the specified id."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Competition found"),
            @ApiResponse(responseCode = "404", description = "Competition not found")
    })
    public ResponseEntity<Competition> getCompetitionById(@PathVariable Long id) {
        return ResponseEntity.of(competitionService.getCompetitionById(id));
    }

    @GetMapping
    @Operation(
            summary = "List all competitions",
            description = "Returns every competition in the system."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Competition list")
    })
    public ResponseEntity<List<Competition>> getAllCompetitions() {
        return ResponseEntity.ok(competitionService.getAllCompetitions());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a competition",
            description = "Deletes a competition by its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Competition deleted"),
            @ApiResponse(responseCode = "404", description = "Competition not found")
    })
    public ResponseEntity<Void> deleteCompetition(@PathVariable Long id) {
        competitionService.deleteCompetition(id);
        return ResponseEntity.noContent().build();
    }
}
