package com.example.football_manager.controller;

import com.example.football_manager.dto.AuthResponseDTO;
import com.example.football_manager.dto.LoginDTO;
import com.example.football_manager.dto.RegisterDTO;
import com.example.football_manager.model.User;
import com.example.football_manager.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a user",
            description = "Creates a new user account. Returns a message indicating success or conflict.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Registration details",
                    content = @Content(schema = @Schema(implementation = RegisterDTO.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
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
    @Operation(
            summary = "Login",
            description = "Authenticates the user and stores the session attributes for subsequent requests.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Login credentials",
                    content = @Content(schema = @Schema(implementation = LoginDTO.class))
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO, HttpSession session) {
        try {
            User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());

            session.setAttribute("userId", user.getId());
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