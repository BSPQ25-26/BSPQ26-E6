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

/**
 * MVC controller responsible for rendering and processing Fantasy mode pages.
 *
 * <p>This controller connects the Thymeleaf frontend with the Fantasy service layer.
 * It handles the Fantasy home page, league creation, league joining and lineup saving.</p>
 */
@Controller
@RequestMapping("/fantasy")
public class FantasyViewController {

    private final FantasyService fantasyService;

    /**
     * Creates a new Fantasy view controller.
     *
     * @param fantasyService service containing Fantasy business logic
     */
    public FantasyViewController(FantasyService fantasyService) {
        this.fantasyService = fantasyService;
    }

    /**
     * Displays the main Fantasy page for the logged-in user.
     *
     * @param model Spring MVC model used to pass Fantasy data to the Thymeleaf view
     * @param session current HTTP session containing logged user information
     * @return Fantasy page view name or login redirect
     */
    @GetMapping
    public String fantasyHome(Model model, HttpSession session) {
        Long userId = getLoggedUserIdOrNull(session);

        if (userId == null) {
            return "redirect:/login";
        }

        populateFantasyModel(model, session, userId, null);
        return "fantasy";
    }

    /**
     * Displays the Fantasy page with a selected league.
     *
     * @param leagueId id of the selected fantasy league
     * @param model Spring MVC model used to pass Fantasy data to the Thymeleaf view
     * @param session current HTTP session containing logged user information
     * @param redirectAttributes redirect attributes used to show error messages
     * @return Fantasy page view name or redirect to the Fantasy home page
     */
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

    /**
     * Creates a new fantasy league from the submitted form.
     *
     * @param request form data containing the league name
     * @param session current HTTP session containing logged user information
     * @param redirectAttributes redirect attributes used to show success or error messages
     * @return redirect to the created league or to the Fantasy home page
     */
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

    /**
     * Joins an existing fantasy league using an invite code.
     *
     * @param request form data containing the invite code
     * @param session current HTTP session containing logged user information
     * @param redirectAttributes redirect attributes used to show success or error messages
     * @return redirect to the joined league or to the Fantasy home page
     */
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

    /**
     * Saves the selected fantasy lineup for the logged-in user.
     *
     * @param playerIds ids of the selected players
     * @param session current HTTP session containing logged user information
     * @param redirectAttributes redirect attributes used to show success or error messages
     * @return redirect to the Fantasy home page
     */
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

    /**
     * Populates the model with all data required by the Fantasy page.
     *
     * <p>This includes user leagues, selected league, leaderboard, available players,
     * selected lineup, selected player ids and user score.</p>
     *
     * @param model Spring MVC model
     * @param session current HTTP session
     * @param userId id of the logged-in user
     * @param selectedLeagueId optional selected fantasy league id
     */
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

    /**
     * Reads the logged-in user id from the current HTTP session.
     *
     * @param session current HTTP session
     * @return user id if present, otherwise null
     */
    private Long getLoggedUserIdOrNull(HttpSession session) {
        Object userId = session.getAttribute("userId");

        if (userId instanceof Long id) {
            return id;
        }

        return null;
    }
}