package com.example.football_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "match_goal")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_match_id", nullable = false)
    private Match match;

    @ManyToOne
    @JoinColumn(name = "fk_team_id", nullable = false)
    private Team team;

    @Column(name = "goal_minute", nullable = false)
    private short minute;

    @Column(name = "stoppage_minute")
    private Short stoppageMinute;
}
