package com.example.football_manager.controller;

import com.example.football_manager.dto.ProfileUpdateDTO;
import com.example.football_manager.model.User;
import com.example.football_manager.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@Controller
public class ProfileViewController {

    private final UserService userService;

    public ProfileViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profilePage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user;
        try {
            user = userService.getUserProfile(userId);
        } catch (IllegalArgumentException ex) {
            session.invalidate();
            return "redirect:/login";
        }

        populateProfileModel(model, user);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileForm") ProfileUpdateDTO profileForm,
                                BindingResult bindingResult,
                                Model model,
                                HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User currentUser;
        try {
            currentUser = userService.getUserProfile(userId);
        } catch (IllegalArgumentException ex) {
            session.invalidate();
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            populateProfileModel(model, currentUser);
            return "profile";
        }

        try {
            User updatedUser = userService.updateProfile(
                    userId,
                    profileForm.getUsername(),
                    profileForm.getEmail(),
                    profileForm.getCurrentPassword(),
                    profileForm.getNewPassword(),
                    profileForm.getConfirmNewPassword()
            );

            ProfileUpdateDTO refreshedForm = new ProfileUpdateDTO();
            refreshedForm.setUsername(updatedUser.getUsername());
            refreshedForm.setEmail(updatedUser.getEmail());
            model.addAttribute("profileForm", refreshedForm);
            model.addAttribute("favouriteTeams", updatedUser.getFavouriteTeams() != null ? updatedUser.getFavouriteTeams() : Set.of());
            model.addAttribute("successMessage", "Profile updated successfully");
            return "profile";
        } catch (IllegalArgumentException ex) {
            populateProfileModel(model, currentUser);
            model.addAttribute("errorMessage", ex.getMessage());
            return "profile";
        }
    }

    private void populateProfileModel(Model model, User user) {
        if (!model.containsAttribute("profileForm")) {
            ProfileUpdateDTO profileForm = new ProfileUpdateDTO();
            profileForm.setUsername(user.getUsername());
            profileForm.setEmail(user.getEmail());
            model.addAttribute("profileForm", profileForm);
        }

        model.addAttribute("favouriteTeams", user.getFavouriteTeams() != null ? user.getFavouriteTeams() : Set.of());
    }
}






