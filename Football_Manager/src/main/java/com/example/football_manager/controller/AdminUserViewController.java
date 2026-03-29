package com.example.football_manager.controller;

import com.example.football_manager.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminUserViewController {

    private final UserService userService;

    public AdminUserViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/users")
    public String usersPage() {
        return "admin-users";
    }

    @PostMapping("/admin/users")
    public String createUser(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam(defaultValue = "false") Boolean isAdmin) {
        userService.registerUser(username, email, password, isAdmin);
        return "redirect:/admin/users";
    }
}