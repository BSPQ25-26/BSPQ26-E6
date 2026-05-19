package com.example.football_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "fantasy_league_member",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_fantasy_league_member_user",
                columnNames = {"fk_league_id", "fk_user_id"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FantasyLeagueMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_league_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private FantasyLeague league;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "joined_at", nullable = false)
    private OffsetDateTime joinedAt = OffsetDateTime.now();
}