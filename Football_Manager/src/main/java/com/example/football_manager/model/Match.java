package com.example.football_manager.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "match")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_left_team_id", nullable = false)
    private Team leftTeam;

    @ManyToOne
    @JoinColumn(name = "fk_right_team_id", nullable = false)
    private Team rightTeam;

    @ManyToOne
    @JoinColumn(name = "fk_league_id", nullable = false)
    private Competition competition;

    @Column(nullable = false)
    private OffsetDateTime datetime;

    @Column(name = "left_score", nullable = false)
    private short leftScore;

    @Column(name = "right_score", nullable = false)
    private short rightScore;

    @Column(nullable = false)
    private boolean finished;
}