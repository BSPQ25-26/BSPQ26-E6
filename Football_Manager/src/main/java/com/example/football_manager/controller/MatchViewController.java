package com.example.football_manager.controller;

import com.example.football_manager.dto.MatchRequestDTO;
import com.example.football_manager.dto.MatchResultRequestDTO;
import com.example.football_manager.model.Match;
import com.example.football_manager.model.MatchGoal;
import com.example.football_manager.service.MatchService;
import com.example.football_manager.service.TeamService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MatchViewController {

    private final MatchService matchService;


    public MatchViewController(MatchService matchService, TeamService teamService) {
        this.matchService = matchService;
        this.teamService = teamService;
    }

    @Autowired
    private TeamService teamService;

    @GetMapping("/matches/schedule")
    public String showScheduleForm(Model model) {
        model.addAttribute("matchRequest", new MatchRequestDTO());
        model.addAttribute("teams", teamService.getAllTeams());
        return "schedule-match";
    }

    @GetMapping("/matches/results")
    public String showResultsPage(Model model) {
        model.addAttribute("results", matchService.getFinishedMatchResults());
        return "match-results";
    }
    
@GetMapping("/matches/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        MatchRequestDTO matchRequestDTO = matchService.getMatchForEdit(id)
                .orElseThrow(() -> new IllegalArgumentException("Match not found."));
        
        model.addAttribute("matchRequest", matchRequestDTO);
        model.addAttribute("matchId", id);
        // PASAR LA LISTA DE EQUIPOS PARA LOS DESPLEGABLES
        model.addAttribute("teams", teamService.getAllTeams()); 
        return "edit-match";
    }

    @PostMapping("/matches/edit/{id}")
    public String updateMatch(@PathVariable Long id, @ModelAttribute("matchRequest") MatchRequestDTO matchDTO) {
        matchService.updateMatch(id, matchDTO);
        return "redirect:/matches/results";
    }


    @GetMapping("/matches/{id}/score")
    public String showScoreForm(@PathVariable Long id, Model model) {
        Match match = matchService.getMatchById(id);

        if (!model.containsAttribute("resultRequest")) {
            model.addAttribute("resultRequest", toResultRequest(matchService.getGoalsByMatchId(id)));
        }

        model.addAttribute("match", match);
        return "enter-match-score";
    }

    @PostMapping("/matches/{id}/score")
    public String submitScoreForm(
            @PathVariable Long id,
            @ModelAttribute("resultRequest") MatchResultRequestDTO resultRequest,
            Model model,
            RedirectAttributes redirectAttributes) {

        Match match = matchService.getMatchById(id);
        model.addAttribute("match", match);

        if (resultRequest.getGoals() == null) {
            resultRequest.setGoals(new ArrayList<>());
        }

        try {
            matchService.registerResult(id, resultRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Result saved successfully.");
            return "redirect:/matches/" + id + "/score";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "enter-match-score";
        }
    }

    private MatchResultRequestDTO toResultRequest(List<MatchGoal> goals) {
        MatchResultRequestDTO resultRequest = new MatchResultRequestDTO();
        List<MatchResultRequestDTO.GoalDTO> goalDTOs = new ArrayList<>();

        for (MatchGoal goal : goals) {
            MatchResultRequestDTO.GoalDTO goalDTO = new MatchResultRequestDTO.GoalDTO();
            goalDTO.setTeamId(goal.getTeam().getId());
            goalDTO.setMinute((int) goal.getMinute());
            goalDTO.setStoppageMinute(
                    goal.getStoppageMinute() == null ? null : (int) goal.getStoppageMinute()
            );
            goalDTOs.add(goalDTO);
        }

        resultRequest.setGoals(goalDTOs);
        return resultRequest;
    }

    @PostMapping("/matches/{id}/results")
    public String saveMatchResult(@PathVariable Long id, @ModelAttribute MatchResultRequestDTO requestDTO) {
        try {
            matchService.registerResult(id, requestDTO);
            
            return "redirect:/admin/matches"; 
        } catch (IllegalArgumentException e) {
            return "redirect:/matches/" + id + "/results?error=true";
        }
    }
}
