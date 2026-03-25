package com.example.football_manager.controller;

import com.example.football_manager.dto.TeamRequestDTO;
import com.example.football_manager.service.CountryService;
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

    // INYECTAMOS EL SERVICIO DE PAÍSES
    @Autowired
    private CountryService countryService; 

    @GetMapping("/teams")
    public String teamsPage(Model model) {
        model.addAttribute("teams", teamService.getAllTeams());
        return "teams";
    }

    @GetMapping("/teams/add")
    public String showAddTeamForm(Model model) {
        model.addAttribute("teamRequest", new TeamRequestDTO());
        // ENVIAMOS LA LISTA DE PAÍSES A LA VISTA
        model.addAttribute("countries", countryService.getAllCountries()); 
        return "add-team";
    }

    @PostMapping("/teams/add")
    public String createTeam(@ModelAttribute TeamRequestDTO teamDTO) {
        teamService.createTeam(teamDTO);
        return "redirect:/teams";
    }

    @GetMapping("/teams/edit/{id}")
    public String showEditTeamForm(@PathVariable Long id, Model model) {
        com.example.football_manager.model.Team existingTeam = teamService.getTeamById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        TeamRequestDTO teamDTO = new TeamRequestDTO();
        teamDTO.setName(existingTeam.getName());
        teamDTO.setLogoUrl(existingTeam.getLogoUrl());
        
        if (existingTeam.getCountry() != null) {
            teamDTO.setCountryId(existingTeam.getCountry().getId());
        }

        model.addAttribute("teamRequest", teamDTO);
        model.addAttribute("teamId", id);
        // ENVIAMOS LA LISTA DE PAÍSES TAMBIÉN A LA VISTA DE EDICIÓN
        model.addAttribute("countries", countryService.getAllCountries()); 
        return "edit-team";
    }

    @PutMapping("/teams/edit/{id}")
    public String updateTeam(@PathVariable Long id, @ModelAttribute TeamRequestDTO teamDTO) {
        teamService.updateTeam(id, teamDTO);
        return "redirect:/teams";
    }

    @PostMapping("/teams/delete/{id}")
    public String deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return "redirect:/teams";
    }
}