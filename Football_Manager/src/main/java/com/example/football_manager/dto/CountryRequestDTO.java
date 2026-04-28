package com.example.football_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public class CountryRequestDTO {

    @NotBlank(message = "Country name is required")
    @Size(max = 100, message = "Country name must be at most 100 characters")
    @Schema(description = "Country name", example = "Spain")
    private String name;

    public CountryRequestDTO() {
    }

    public CountryRequestDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}