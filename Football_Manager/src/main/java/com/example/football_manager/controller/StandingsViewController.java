package com.example.football_manager.controller;

import com.example.football_manager.dto.StandingDTO;
import com.example.football_manager.model.Competition;
import com.example.football_manager.service.StandingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class StandingsViewController {

    private final StandingsService standingsService;

    public StandingsViewController(StandingsService standingsService) {
        this.standingsService = standingsService;
    }

    @GetMapping("/standings")
    public String standingsPage(@RequestParam(required = false) Long competitionId,
                                Model model) {

        List<Competition> competitions = standingsService.getCompetitions();

        Long selectedCompetitionId = null;
        List<StandingDTO> standings = List.of();

        if (!competitions.isEmpty()) {
            // Fall back to the first available competition when no valid
            // selection is provided. Keeps the view stable on first load and
            // against unknown query values.
            boolean requestedIsValid = competitionId != null
                    && competitions.stream().anyMatch(c -> c.getId().equals(competitionId));
            selectedCompetitionId = requestedIsValid ? competitionId : competitions.get(0).getId();
            standings = standingsService.getStandingsForCompetition(selectedCompetitionId);
        }

        model.addAttribute("standings", standings);
        model.addAttribute("competitions", competitions);
        model.addAttribute("selectedCompetitionId", selectedCompetitionId);
        return "standings";
    }
}
