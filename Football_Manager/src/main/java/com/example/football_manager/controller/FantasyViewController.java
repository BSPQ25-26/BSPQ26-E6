package com.example.football_manager.controller;

import com.example.football_manager.dto.CreateFantasyLeagueRequestDTO;
import com.example.football_manager.dto.FantasyLeagueDTO;
import com.example.football_manager.dto.FantasyLineupPlayerDTO;
import com.example.football_manager.dto.FantasyLineupRequestDTO;
import com.example.football_manager.dto.JoinFantasyLeagueRequestDTO;
import com.example.football_manager.service.FantasyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/fantasy")
public class FantasyViewController {

    private final FantasyService fantasyService;

    public FantasyViewController(FantasyService fantasyService) {
        this.fantasyService = fantasyService;
    }

    @GetMapping
    public String fantasyHome(Model model, HttpSession session) {
        Long userId = getLoggedUserIdOrNull(session);

        if (userId == null) {
            return "redirect:/login";
        }

        populateFantasyModel(model, session, userId, null);
        return "fantasy";
    }

    @GetMapping("/leagues/{leagueId}")
    public String fantasyLeague(
            @PathVariable Long leagueId,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long userId = getLoggedUserIdOrNull(session);

        if (userId == null) {
            return "redirect:/login";
        }

        try {
            populateFantasyModel(model, session, userId, leagueId);
            return "fantasy";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/fantasy";
        }
    }

    @PostMapping("/leagues")
    public String createLeague(
            @ModelAttribute CreateFantasyLeagueRequestDTO request,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long userId = getLoggedUserIdOrNull(session);

        if (userId == null) {
            return "redirect:/login";
        }

        try {
            FantasyLeagueDTO createdLeague = fantasyService.createLeague(userId, request);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Fantasy league created. Invite code: " + createdLeague.getCode()
            );
            return "redirect:/fantasy/leagues/" + createdLeague.getId();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/fantasy";
        }
    }

    @PostMapping("/leagues/join")
    public String joinLeague(
            @ModelAttribute JoinFantasyLeagueRequestDTO request,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long userId = getLoggedUserIdOrNull(session);

        if (userId == null) {
            return "redirect:/login";
        }

        try {
            FantasyLeagueDTO joinedLeague = fantasyService.joinLeague(userId, request);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "You joined " + joinedLeague.getName() + "."
            );
            return "redirect:/fantasy/leagues/" + joinedLeague.getId();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/fantasy";
        }
    }

    @PostMapping("/lineup")
    public String saveLineup(
            @RequestParam(value = "playerIds", required = false) List<Long> playerIds,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Long userId = getLoggedUserIdOrNull(session);

        if (userId == null) {
            return "redirect:/login";
        }

        FantasyLineupRequestDTO request = new FantasyLineupRequestDTO();
        request.setPlayerIds(playerIds);

        try {
            fantasyService.saveLineup(userId, request);
            redirectAttributes.addFlashAttribute("successMessage", "Fantasy lineup saved.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/fantasy";
    }

    private void populateFantasyModel(Model model, HttpSession session, Long userId, Long selectedLeagueId) {
        List<FantasyLeagueDTO> myLeagues = fantasyService.getMyLeagues(userId);

        FantasyLeagueDTO selectedLeague = null;

        if (selectedLeagueId != null) {
            selectedLeague = fantasyService.getLeagueForMember(selectedLeagueId, userId);
        } else if (!myLeagues.isEmpty()) {
            selectedLeague = myLeagues.get(0);
        }

        List<FantasyLineupPlayerDTO> lineup = fantasyService.getLineup(userId);
        List<Long> selectedPlayerIds = lineup.stream()
                .map(FantasyLineupPlayerDTO::getPlayerId)
                .toList();

        model.addAttribute("isAdmin", Boolean.TRUE.equals(session.getAttribute("isAdmin")));
        model.addAttribute("myLeagues", myLeagues);
        model.addAttribute("selectedLeague", selectedLeague);
        model.addAttribute("leaderboard", selectedLeague != null ? fantasyService.getLeaderboard(selectedLeague.getId()) : List.of());
        model.addAttribute("availablePlayers", fantasyService.getAvailablePlayers());
        model.addAttribute("lineup", lineup);
        model.addAttribute("selectedPlayerIds", selectedPlayerIds);
        model.addAttribute("myScore", fantasyService.getMyScore(userId));
        model.addAttribute("createLeagueRequest", new CreateFantasyLeagueRequestDTO());
        model.addAttribute("joinLeagueRequest", new JoinFantasyLeagueRequestDTO());
    }

    private Long getLoggedUserIdOrNull(HttpSession session) {
        Object userId = session.getAttribute("userId");

        if (userId instanceof Long id) {
            return id;
        }

        return null;
    }
}