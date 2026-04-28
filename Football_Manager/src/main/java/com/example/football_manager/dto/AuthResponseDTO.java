package com.example.football_manager.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthResponseDTO {

    @Schema(description = "User identifier", example = "101")
    private Long id;
    @Schema(description = "Username", example = "user123")
    private String username;
    @Schema(description = "Email address", example = "user123@example.com")
    private String email;
    @Schema(description = "Whether the user is an administrator", example = "false")
    private Boolean isAdmin;
    @Schema(description = "Status message")
    private String message;

    public AuthResponseDTO(Long id, String username, String email, Boolean isAdmin, String message) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public String getMessage() {
        return message;
    }
}

