package com.example.football_manager.controller;

import com.example.football_manager.dto.StandingDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class StandingsViewController {

    @GetMapping("/standings")
    public String standingsPage(Model model) {

        // =====================================================================
        // MOCK DATA BLOCK - TEMPORARY (Issue #77)
        // ---------------------------------------------------------------------
        // The real backend service is not implemented yet. The block below
        // hardcodes a few standings rows so the view can be developed and
        // tested in isolation.
        //
        // TRANSITION TO REAL BACKEND:
        //   1) Delete every line between "MOCK DATA - START" and
        //      "MOCK DATA - END" (the ArrayList creation and the .add() calls).
        //   2) Replace them with a call to the real service, e.g.:
        //         List<StandingDTO> standings = standingsService.getStandings();
        //   3) Inject StandingsService via @Autowired at the top of this class.
        //   4) Keep the sort-by-points line below the mock block - it is part
        //      of the controller contract for this view.
        // =====================================================================

        // MOCK DATA - START
        List<StandingDTO> standings = new ArrayList<>();
        standings.add(new StandingDTO("Real Sociedad",
                "https://cdn.example.com/logos/real-sociedad.png",
                10, 7, 2, 1, 22, 9, 13, 23));
        standings.add(new StandingDTO("Athletic Club",
                "https://cdn.example.com/logos/athletic-club.png",
                10, 6, 3, 1, 19, 10, 9, 21));
        standings.add(new StandingDTO("Deportivo Alaves",
                "https://cdn.example.com/logos/deportivo-alaves.png",
                10, 4, 2, 4, 12, 14, -2, 14));
        standings.add(new StandingDTO("Osasuna",
                "https://cdn.example.com/logos/osasuna.png",
                10, 3, 4, 3, 11, 12, -1, 13));
        standings.add(new StandingDTO("SD Eibar",
                "https://cdn.example.com/logos/sd-eibar.png",
                10, 2, 2, 6, 8, 18, -10, 8));
        // MOCK DATA - END

        // Sort by points descending (Issue #77). When the real service is wired
        // up this sort can stay here as a defensive guarantee, or be moved to
        // the service layer - the view should never assume an arbitrary order.
        standings.sort(Comparator.comparingInt(StandingDTO::getPoints).reversed());

        model.addAttribute("standings", standings);
        return "standings";
    }
}
