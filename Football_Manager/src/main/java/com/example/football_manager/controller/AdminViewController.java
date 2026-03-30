package com.example.football_manager.controller;

import com.example.football_manager.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminViewController {

    @Autowired
    private TeamService teamService; 

    @GetMapping("/admin")
    public String adminPanel() {
        return "admin-panel";
    }

    @GetMapping("/admin/manage-teams")
    public String manageTeams(Model model) {
        model.addAttribute("teams", teamService.getAllTeams());
        return "admin-manage-teams";
    }
}