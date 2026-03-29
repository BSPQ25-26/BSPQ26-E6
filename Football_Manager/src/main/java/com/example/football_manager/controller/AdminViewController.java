package com.example.football_manager.controller;

import com.example.football_manager.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminViewController {

    @Autowired
    private TeamService teamService; // Inyectamos el servicio para obtener los equipos

    @GetMapping("/admin")
    public String adminPanel() {
        return "admin-panel";
    }

    // Nuevo endpoint para gestionar los equipos desde el panel de control
    @GetMapping("/admin/manage-teams")
    public String manageTeams(Model model) {
        model.addAttribute("teams", teamService.getAllTeams());
        return "admin-manage-teams"; // El nombre de la nueva vista HTML
    }
}