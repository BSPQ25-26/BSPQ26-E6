package com.example.football_manager.controller;

import com.example.football_manager.dto.AuthResponseDTO;
import com.example.football_manager.dto.LoginDTO;
import com.example.football_manager.dto.RegisterDTO;
import com.example.football_manager.model.User;
import com.example.football_manager.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            User user = userService.registerUser(
                    registerDTO.getUsername(),
                    registerDTO.getEmail(),
                    registerDTO.getPassword(),
                    registerDTO.getIsAdmin()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponseDTO(user.getId(), user.getUsername(), user.getEmail(), user.getIsAdmin(), "User registered"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AuthResponseDTO(null, null, null, null, ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO, HttpSession session) {
        try {
            User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());

            session.setAttribute("isAdmin", user.getIsAdmin());

            return ResponseEntity.ok(
                    new AuthResponseDTO(user.getId(), user.getUsername(), user.getEmail(), user.getIsAdmin(), "Login successful")
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponseDTO(null, null, null, null, ex.getMessage()));
        }
    }
}