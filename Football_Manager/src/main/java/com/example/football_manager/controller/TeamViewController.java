package com.example.football_manager.controller;

import com.example.football_manager.dto.TeamRequestDTO;
import com.example.football_manager.service.CountryService;
import com.example.football_manager.service.TeamService;
import com.example.football_manager.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@Controller
public class TeamViewController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private UserService userService;

    @GetMapping("/teams")
    public String teamsPage(Model model, HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        Long userId = (Long) session.getAttribute("userId");

        model.addAttribute("isAdmin", isAdmin != null && isAdmin);
        model.addAttribute("teams", teamService.getAllTeams());

        Set<Long> favouriteTeamIds = Set.of();
        try {
            favouriteTeamIds = userService.getFavouriteTeamIdsByUserId(userId);
        } catch (IllegalArgumentException ignored) {
            // If session contains an invalid user id, render page without favourites.
        }
        model.addAttribute("favouriteTeamIds", favouriteTeamIds);

        return "teams";
    }

    @GetMapping("/teams/add")
    public String showAddTeamForm(Model model) {
        model.addAttribute("teamRequest", new TeamRequestDTO());
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

        model.addAttribute("countries", countryService.getAllCountries());
        return "edit-team";
    }

    @PostMapping("/teams/edit/{id}")
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