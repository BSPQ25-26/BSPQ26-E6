package com.example.football_manager.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class PlayerBulkRequestDTO {

    @NotEmpty(message = "Player list is required")
    private List<@Valid PlayerRequestDTO> players;

    public PlayerBulkRequestDTO() {
    }

    public PlayerBulkRequestDTO(List<PlayerRequestDTO> players) {
        this.players = players;
    }

    public List<PlayerRequestDTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerRequestDTO> players) {
        this.players = players;
    }
}
