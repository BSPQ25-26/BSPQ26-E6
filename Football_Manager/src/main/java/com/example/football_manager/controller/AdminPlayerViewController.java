package com.example.football_manager.controller;

import com.example.football_manager.dto.PlayerRequestDTO;
import com.example.football_manager.model.Player;
import com.example.football_manager.model.PlayerPosition;
import com.example.football_manager.model.Team;
import com.example.football_manager.service.PlayerService;
import com.example.football_manager.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminPlayerViewController {

    private final TeamService teamService;
    private final PlayerService playerService;

    public AdminPlayerViewController(TeamService teamService, PlayerService playerService) {
        this.teamService = teamService;
        this.playerService = playerService;
    }

    @GetMapping("/admin/players")
    public String managePlayersLanding(Model model) {
        return "redirect:/admin/manage-teams";
    }

    @GetMapping("/admin/teams/{teamId}/players")
    public String manageTeamPlayers(@PathVariable Long teamId, Model model, RedirectAttributes redirectAttributes) {
        Team team = teamService.getTeamById(teamId).orElse(null);
        if (team == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Team not found.");
            return "redirect:/admin/manage-teams";
        }

        if (!model.containsAttribute("playerRequest")) {
            model.addAttribute("playerRequest", new PlayerRequestDTO());
        }

        populateTeamPlayersModel(model, team);
        return "admin-team-players";
    }

    @PostMapping("/admin/teams/{teamId}/players")
    public String createPlayer(
            @PathVariable Long teamId,
            @Valid @ModelAttribute("playerRequest") PlayerRequestDTO playerRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        Team team = teamService.getTeamById(teamId).orElse(null);
        if (team == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Team not found.");
            return "redirect:/admin/manage-teams";
        }

        if (bindingResult.hasErrors()) {
            populateTeamPlayersModel(model, team);
            return "admin-team-players";
        }

        try {
            playerService.createPlayer(teamId, playerRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Player created successfully.");
            return "redirect:/admin/teams/" + teamId + "/players";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            populateTeamPlayersModel(model, team);
            return "admin-team-players";
        }
    }

    @GetMapping("/admin/teams/{teamId}/players/edit/{playerId}")
    public String showEditPlayerForm(
            @PathVariable Long teamId,
            @PathVariable Long playerId,
            Model model,
            RedirectAttributes redirectAttributes) {

        Team team = teamService.getTeamById(teamId).orElse(null);
        if (team == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Team not found.");
            return "redirect:/admin/manage-teams";
        }

        try {
            Player player = playerService.getPlayerByTeamId(teamId, playerId);
            if (!model.containsAttribute("playerRequest")) {
                PlayerRequestDTO playerRequest = new PlayerRequestDTO(
                        player.getName(),
                        player.getNumber(),
                        player.getPosition()
                );
                model.addAttribute("playerRequest", playerRequest);
            }
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/teams/" + teamId + "/players";
        }

        model.addAttribute("team", team);
        model.addAttribute("playerId", playerId);
        model.addAttribute("positions", PlayerPosition.values());
        return "edit-player";
    }

    @PostMapping("/admin/teams/{teamId}/players/edit/{playerId}")
    public String updatePlayer(
            @PathVariable Long teamId,
            @PathVariable Long playerId,
            @Valid @ModelAttribute("playerRequest") PlayerRequestDTO playerRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        Team team = teamService.getTeamById(teamId).orElse(null);
        if (team == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Team not found.");
            return "redirect:/admin/manage-teams";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("team", team);
            model.addAttribute("playerId", playerId);
            model.addAttribute("positions", PlayerPosition.values());
            return "edit-player";
        }

        try {
            playerService.updatePlayer(teamId, playerId, playerRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Player updated successfully.");
            return "redirect:/admin/teams/" + teamId + "/players";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("team", team);
            model.addAttribute("playerId", playerId);
            model.addAttribute("positions", PlayerPosition.values());
            model.addAttribute("errorMessage", ex.getMessage());
            return "edit-player";
        }
    }

    @PostMapping("/admin/teams/{teamId}/players/delete/{playerId}")
    public String deletePlayer(
            @PathVariable Long teamId,
            @PathVariable Long playerId,
            RedirectAttributes redirectAttributes) {
        try {
            playerService.deletePlayer(teamId, playerId);
            redirectAttributes.addFlashAttribute("successMessage", "Player deleted successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/teams/" + teamId + "/players";
    }

    private void populateTeamPlayersModel(Model model, Team team) {
        model.addAttribute("team", team);
        model.addAttribute("players", playerService.getPlayersByTeamId(team.getId()));
        model.addAttribute("positions", PlayerPosition.values());
    }
}
