package com.example.football_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TeamRequestDTO {

    @NotBlank(message = "Team name is required")
    @Size(max = 30, message = "Team name must be at most 30 characters")
    private String name;

    @NotBlank(message = "Logo URL is required")
    private String logoUrl;

    @NotNull(message = "Country ID is required")
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