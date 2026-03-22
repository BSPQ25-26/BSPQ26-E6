package com.example.football_manager.controller;

import com.example.football_manager.dto.TeamRequestDTO;
import com.example.football_manager.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    // Receive form submission to add a team
    @PostMapping
    public ResponseEntity<String> createTeam(@Valid @ModelAttribute TeamRequestDTO teamDTO) {
        try {
            String response = teamService.createTeam(teamDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error adding team: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}