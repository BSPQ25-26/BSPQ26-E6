package com.example.football_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public class CompetitionRequestDTO {

    @NotBlank(message = "Competition name is required")
    @Size(max = 50, message = "Competition name must be at most 50 characters")
    @Schema(description = "Competition name", example = "LaLiga")
    private String name;

    public CompetitionRequestDTO() {
    }

    public CompetitionRequestDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
