package com.example.football_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private UUID id; // comes from auth.users

    @Column(nullable = false, length = 30)
    private String username;

    @ManyToMany
    @JoinTable(
        name = "favourite_team",
        joinColumns = @JoinColumn(name = "fk_user_id"),
        inverseJoinColumns = @JoinColumn(name = "fk_team_id")
    )
    private Set<Team> favouriteTeams;
}