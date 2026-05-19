package com.example.football_manager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinFantasyLeagueRequestDTO {

    @NotBlank(message = "League code is required")
    private String code;
}