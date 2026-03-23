package com.example.football_manager.service;

import com.example.football_manager.entity.Team;
import com.example.football_manager.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }
}
