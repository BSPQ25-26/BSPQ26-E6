package com.example.football_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FantasyScoreDTO {

    private Long userId;
    private String username;
    private Integer totalPoints;
    private List<FantasyLineupPlayerDTO> lineup;
}