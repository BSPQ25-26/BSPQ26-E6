package com.example.football_manager.service;

import com.example.football_manager.dto.TeamRequestDTO;
import com.example.football_manager.model.Country;
import com.example.football_manager.model.Team;
import com.example.football_manager.repository.CountryRepository;
import com.example.football_manager.repository.TeamRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final CountryRepository countryRepository;

    public TeamService(TeamRepository teamRepository, CountryRepository countryRepository) {
        this.teamRepository = teamRepository;
        this.countryRepository = countryRepository;
    }

    public Team createTeam(TeamRequestDTO dto) {
        Country country = countryRepository.findById(dto.getCountryId())
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + dto.getCountryId()));

        Team team = new Team();
        team.setName(dto.getName().trim());
        team.setLogoUrl(dto.getLogoUrl().trim());
        team.setCountry(country);

        return teamRepository.save(team);
    }

    public Team updateTeam(Long id, TeamRequestDTO dto) {
        Team existingTeam = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + id));

        Country country = countryRepository.findById(dto.getCountryId())
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + dto.getCountryId()));

        existingTeam.setName(dto.getName().trim());
        existingTeam.setLogoUrl(dto.getLogoUrl().trim());
        existingTeam.setCountry(country);

        return teamRepository.save(existingTeam);
    }

    public void deleteTeam(Long id) {
        Team existingTeam = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + id));

        teamRepository.delete(existingTeam);
    }

    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
    }

    public List<Team> getTeamsContainingName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return List.of();
        }
        return teamRepository.findByNameContainingIgnoreCase(name.trim());
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
}