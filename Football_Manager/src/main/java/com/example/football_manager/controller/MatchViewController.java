package com.example.football_manager.controller;

import com.example.football_manager.dto.MatchRequestDTO;
import com.example.football_manager.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MatchViewController {
    
    @Autowired
    private MatchService matchService;

    @GetMapping("/matches/schedule")
    public String showScheduleForm(Model model) {
        model.addAttribute("matchRequest", new MatchRequestDTO());
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
        return "edit-match";
    }
}
