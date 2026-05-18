package com.example.football_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(name = "logo_url", nullable = false, columnDefinition = "TEXT")
    private String logoUrl;

    @ManyToOne
    @JoinColumn(name = "fk_country_id", nullable = false)
    private Country country;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int pj;  // Partidos Jugados

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int pg;  // Partidos Ganados

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int pe;  // Partidos Empatados

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int pp;  // Partidos Perdidos

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int gf;  // Goles a Favor

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int gc;  // Goles en Contra

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int dg;  // Diferencia de Goles

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int pts; // Puntos
}

