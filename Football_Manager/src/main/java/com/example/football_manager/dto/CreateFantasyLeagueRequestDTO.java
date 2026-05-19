package com.example.football_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateFantasyLeagueRequestDTO {

    @NotBlank(message = "League name is required")
    @Size(max = 60, message = "League name cannot exceed 60 characters")
    private String name;
}