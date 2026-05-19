package com.example.football_manager.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class FantasyLineupRequestDTO {

    @NotEmpty(message = "At least one player must be selected")
    private List<Long> playerIds;
}