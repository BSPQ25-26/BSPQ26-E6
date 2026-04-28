package com.example.football_manager.controller;

import com.example.football_manager.service.UserService;
import jakarta.servlet.http.HttpSession;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me/favourites/teams")
@Tag(name = "Favourites")
public class FavouriteTeamController {

    private final UserService userService;

    public FavouriteTeamController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{teamId}")
    @Operation(
            summary = "Add favourite team",
            description = "Adds the team to the current user's favourites. Requires a logged-in session."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Team added to favourites"),
            @ApiResponse(responseCode = "401", description = "User not logged in"),
            @ApiResponse(responseCode = "404", description = "User or team not found"),
            @ApiResponse(responseCode = "409", description = "Team already in favourites")
    })
    public ResponseEntity<String> addFavouriteTeam(@PathVariable Long teamId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be logged in");
        }

        try {
            userService.addFavouriteTeam(userId, teamId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Team added to favourites");
        } catch (IllegalArgumentException ex) {
            return mapError(ex.getMessage());
        }
    }

    @DeleteMapping("/{teamId}")
    @Operation(
            summary = "Remove favourite team",
            description = "Removes the team from the current user's favourites. Requires a logged-in session."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Team removed from favourites"),
            @ApiResponse(responseCode = "401", description = "User not logged in"),
            @ApiResponse(responseCode = "404", description = "User or team not found"),
            @ApiResponse(responseCode = "409", description = "Team not in favourites")
    })
    public ResponseEntity<String> removeFavouriteTeam(@PathVariable Long teamId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be logged in");
        }

        try {
            userService.removeFavouriteTeam(userId, teamId);
            return ResponseEntity.ok("Team removed from favourites");
        } catch (IllegalArgumentException ex) {
            return mapError(ex.getMessage());
        }
    }

    private ResponseEntity<String> mapError(String message) {
        if ("User not found".equals(message) || "Team not found".equals(message)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }

        if ("Team already in favourites".equals(message) || "Team not in favourites".equals(message)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }

        return ResponseEntity.badRequest().body(message);
    }
}

