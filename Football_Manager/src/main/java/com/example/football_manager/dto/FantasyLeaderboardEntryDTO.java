package com.example.football_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FantasyLeaderboardEntryDTO {

    private Integer position;
    private Long userId;
    private String username;
    private Integer totalPoints;
}