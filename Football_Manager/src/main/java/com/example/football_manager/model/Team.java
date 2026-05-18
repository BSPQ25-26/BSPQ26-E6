package com.example.football_manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("number ASC")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Player> players = new ArrayList<>();

    public Team(Long id, String name, String logoUrl, Country country) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
        this.country = country;
    }
}
