package com.example.football_manager.dto;

import jakarta.validation.constraints.NotBlank;

public class TeamRequestDTO {

    @NotBlank(message = "Team name is required")
    private String name;

    @NotBlank(message = "City is required")
    private String city;

    private String stadium;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStadium() { return stadium; }
    public void setStadium(String stadium) { this.stadium = stadium; }
}