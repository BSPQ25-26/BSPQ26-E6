package com.example.football_manager.dto;

import com.example.football_manager.model.PlayerPosition;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PlayerRequestDTO {

    @NotBlank(message = "Player name is required")
    @Size(max = 30, message = "Player name must be at most 30 characters")
    @Schema(description = "Player name", example = "Mikel Oyarzabal")
    private String name;

    @NotNull(message = "Player number is required")
    @Min(value = 1, message = "Player number must be greater than 0")
    @Schema(description = "Squad number", example = "10")
    private Integer number;

    @NotNull(message = "Player position is required")
    @Schema(description = "Player position", example = "FORWARD")
    private PlayerPosition position;

    public PlayerRequestDTO() {
    }

    public PlayerRequestDTO(String name, Integer number, PlayerPosition position) {
        this.name = name;
        this.number = number;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public PlayerPosition getPosition() {
        return position;
    }

    public void setPosition(PlayerPosition position) {
        this.position = position;
    }
}
