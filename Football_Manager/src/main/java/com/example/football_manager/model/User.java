package com.example.football_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String username;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String email;

    @Column(name = "hashed_password", nullable = false, columnDefinition = "TEXT")
    private String hashedPassword;

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;

    @ManyToMany
    @JoinTable(
        name = "favourite_team",
        joinColumns = @JoinColumn(name = "fk_user_id"),
        inverseJoinColumns = @JoinColumn(name = "fk_team_id")
    )
    private Set<Team> favouriteTeams;
}