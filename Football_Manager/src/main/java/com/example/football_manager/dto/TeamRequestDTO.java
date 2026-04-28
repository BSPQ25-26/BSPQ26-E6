package com.example.football_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public class TeamRequestDTO {

    @NotBlank(message = "Team name is required")
    @Size(max = 30, message = "Team name must be at most 30 characters")
    @Schema(description = "Team name", example = "Real Sociedad")
    private String name;

    @NotBlank(message = "Logo URL is required")
    @Schema(description = "Public logo URL", example = "https://cdn.example.com/logos/real-sociedad.png")
    private String logoUrl;

    @NotNull(message = "Country ID is required")
    @Schema(description = "Identifier of the country the team belongs to", example = "34")
    private Long countryId;

    public TeamRequestDTO() {
    }

    public TeamRequestDTO(String name, String logoUrl, Long countryId) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
}