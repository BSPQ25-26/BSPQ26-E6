package com.example.football_manager.dto;

public class AuthResponseDTO {

    private Long id;
    private String username;
    private String email;
    private Boolean isAdmin;
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

