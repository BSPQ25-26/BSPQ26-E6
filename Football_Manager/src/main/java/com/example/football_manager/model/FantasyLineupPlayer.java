package com.example.football_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(
        name = "fantasy_lineup_player",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_fantasy_lineup_user_player",
                columnNames = {"fk_user_id", "fk_player_id"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FantasyLineupPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_player_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Player player;

    @Column(name = "lineup_order", nullable = false)
    private Integer lineupOrder;
}