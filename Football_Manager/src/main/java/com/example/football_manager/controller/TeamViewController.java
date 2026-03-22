package com.example.football_manager.controller;

import com.example.football_manager.dto.TeamRequestDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TeamViewController {

    @GetMapping("/teams/add")
    public String showAddTeamForm(Model model) {
        model.addAttribute("teamRequest", new TeamRequestDTO());
        return "add-team";
    }
}