package com.example.football_manager.service;

import com.example.football_manager.dto.TeamRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    // @Autowired
    // private TeamRepository teamRepository;

    /**
     * Create a new team.
     */
    public String createTeam(TeamRequestDTO request) {
        
        // TODO: Save to database 
        // Team team = new Team();
        // team.setName(request.getName());
        // team.setCity(request.getCity());
        // team.setStadium(request.getStadium());
        // teamRepository.save(team);
        
        return "Team '" + request.getName() + "' added successfully.";
    }
}