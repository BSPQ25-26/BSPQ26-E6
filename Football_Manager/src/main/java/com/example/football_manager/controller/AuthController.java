package com.example.football_manager.controller;

import com.example.football_manager.model.User;
import com.example.football_manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User register(@RequestParam UUID id,
                         @RequestParam String username) {
        return userService.createUser(id, username);
    }
}