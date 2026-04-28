package com.example.football_manager.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public class LoginDTO {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username", example = "user123")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "User password", example = "P@ssw0rd123")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

