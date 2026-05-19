package com.example.football_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "fantasy_league",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_fantasy_league_code",
                columnNames = "code"
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FantasyLeague {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(nullable = false, length = 12, unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_owner_user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User owner;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<FantasyLeagueMember> members = new HashSet<>();
}