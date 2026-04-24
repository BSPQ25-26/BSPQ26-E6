package com.example.football_manager.controller;

import com.example.football_manager.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me/favourites/teams")
public class FavouriteTeamController {

    private final UserService userService;

    public FavouriteTeamController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{teamId}")
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

