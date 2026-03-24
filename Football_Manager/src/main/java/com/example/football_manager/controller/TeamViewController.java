package com.example.football_manager.controller;

import com.example.football_manager.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TeamViewController {

    @Autowired
    private TeamService teamService;

    @GetMapping("/teams")
    public String teamsPage(Model model) {
        model.addAttribute("teams", teamService.getAllTeams());
        return "teams";
    }

    @PostMapping("/teams/delete/{id}")
    public String deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return "redirect:/teams";
    }
}
