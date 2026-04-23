package com.example.football_manager.controller;

import com.example.football_manager.dto.MatchRequestDTO;
import com.example.football_manager.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}