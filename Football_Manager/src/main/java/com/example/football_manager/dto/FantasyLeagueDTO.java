package com.example.football_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FantasyLeagueDTO {

    private Long id;
    private String name;
    private String code;
    private Long ownerId;
    private String ownerUsername;
}