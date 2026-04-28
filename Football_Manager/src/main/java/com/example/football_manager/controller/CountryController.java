package com.example.football_manager.controller;

import com.example.football_manager.dto.CountryRequestDTO;
import com.example.football_manager.model.Country;
import com.example.football_manager.service.CountryService;
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
@RequestMapping("/api/countries")
@Tag(name = "Countries")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @PostMapping
    @Operation(
            summary = "Create a country",
            description = "Creates a new country entry.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Country details",
                    content = @Content(schema = @Schema(implementation = CountryRequestDTO.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Country created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<Country> createCountry(@Valid @RequestBody CountryRequestDTO dto) {
        Country createdCountry = countryService.createCountry(dto);
        return new ResponseEntity<>(createdCountry, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
            summary = "List all countries",
            description = "Returns every country currently in the system."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Country list")
    })
    public ResponseEntity<List<Country>> getAllCountries() {
        List<Country> countries = countryService.getAllCountries();
        return ResponseEntity.ok(countries);
    }
    
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a country",
            description = "Deletes a country by its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Country deleted"),
            @ApiResponse(responseCode = "404", description = "Country not found")
    })
    public ResponseEntity<Void> deleteCountry(@PathVariable Long id) {
        countryService.deleteCountry(id);
        return ResponseEntity.noContent().build();
    }
}