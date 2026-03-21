package com.example.football_manager.controller;

import com.example.football_manager.dto.MatchRequestDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MatchViewController {

    @GetMapping("/matches/schedule")
    public String showScheduleForm(Model model) {
        model.addAttribute("matchRequest", new MatchRequestDTO());
        return "schedule-match";
    }
}