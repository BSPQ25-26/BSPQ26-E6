package com.example.football_manager.controller;

import com.example.football_manager.dto.TeamRequestDTO;
import com.example.football_manager.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
public class TeamViewController {

    @Autowired
    private TeamService teamService;

    // 1. Mostrar lista de equipos
    @GetMapping("/teams")
    public String teamsPage(Model model) {
        model.addAttribute("teams", teamService.getAllTeams());
        return "teams";
    }

    // 2. MOSTRAR FORMULARIO DE AÑADIR (Esta es la ruta que te daba error 404)
    @GetMapping("/teams/add")
    public String showAddTeamForm(Model model) {
        model.addAttribute("teamRequest", new TeamRequestDTO());
        return "add-team";
    }

    // 3. RECIBIR DATOS DEL FORMULARIO DE AÑADIR Y GUARDAR
    @PostMapping("/teams/add")
    public String createTeam(@ModelAttribute TeamRequestDTO teamDTO) {
        teamService.createTeam(teamDTO);
        return "redirect:/teams";
    }

    // 4. MOSTRAR FORMULARIO DE EDITAR
    @GetMapping("/teams/edit/{id}")
    public String showEditTeamForm(@PathVariable Long id, Model model) {
        // 1. Buscamos el equipo en la base de datos y si no existe lanzamos error
        com.example.football_manager.model.Team existingTeam = teamService.getTeamById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // 2. Transformamos la entidad Team a un TeamRequestDTO para el formulario
        TeamRequestDTO teamDTO = new TeamRequestDTO();
        teamDTO.setName(existingTeam.getName());
        teamDTO.setLogoUrl(existingTeam.getLogoUrl());
        
        // Sacamos el ID del país asociado a este equipo
        if (existingTeam.getCountry() != null) {
            teamDTO.setCountryId(existingTeam.getCountry().getId());
        }

        // 3. Lo mandamos a la vista
        model.addAttribute("teamRequest", teamDTO);
        model.addAttribute("teamId", id);
        return "edit-team";
    }

    // 5. RECIBIR DATOS DEL FORMULARIO DE EDITAR Y ACTUALIZAR
    @PutMapping("/teams/edit/{id}")
    public String updateTeam(@PathVariable Long id, @ModelAttribute TeamRequestDTO teamDTO) {
        teamService.updateTeam(id, teamDTO);
        return "redirect:/teams";
    }

    // 6. BORRAR EQUIPO (El que ya tenías)
    @PostMapping("/teams/delete/{id}")
    public String deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return "redirect:/teams";
    }
}