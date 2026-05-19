package com.example.football_manager.dto;

import com.example.football_manager.model.PlayerPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FantasyAvailablePlayerDTO {

    private Long playerId;
    private String playerName;
    private Integer number;
    private PlayerPosition position;
    private Long teamId;
    private String teamName;
    private String teamLogoUrl;
}