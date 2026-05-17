package com.example.football_manager.service;

import com.example.football_manager.dto.PlayerRequestDTO;
import com.example.football_manager.model.Player;
import com.example.football_manager.model.Team;
import com.example.football_manager.repository.PlayerRepository;
import com.example.football_manager.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public PlayerService(PlayerRepository playerRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
    }

    public Player createPlayer(Long teamId, PlayerRequestDTO dto) {
        validatePlayerRequest(teamId, dto);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found with id: " + teamId));

        if (playerRepository.existsByTeamIdAndNumber(teamId, dto.getNumber())) {
            throw new IllegalArgumentException(
                    "Validation Error: Team already has a player with number " + dto.getNumber() + "."
            );
        }

        Player player = new Player();
        player.setName(dto.getName().trim());
        player.setNumber(dto.getNumber());
        player.setPosition(dto.getPosition());
        player.setTeam(team);

        Player savedPlayer = playerRepository.save(player);
        team.getPlayers().add(savedPlayer);

        return savedPlayer;
    }

    public List<Player> getPlayersByTeamId(Long teamId) {
        if (teamId == null) {
            throw new IllegalArgumentException("Validation Error: Team ID is required.");
        }

        if (!teamRepository.existsById(teamId)) {
            throw new IllegalArgumentException("Team not found with id: " + teamId);
        }

        return playerRepository.findByTeamIdOrderByNumberAsc(teamId);
    }

    public Player getPlayerByTeamId(Long teamId, Long playerId) {
        if (teamId == null) {
            throw new IllegalArgumentException("Validation Error: Team ID is required.");
        }

        if (playerId == null) {
            throw new IllegalArgumentException("Validation Error: Player ID is required.");
        }

        if (!teamRepository.existsById(teamId)) {
            throw new IllegalArgumentException("Team not found with id: " + teamId);
        }

        return playerRepository.findByIdAndTeamId(playerId, teamId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Player not found with id: " + playerId + " for team id: " + teamId
                ));
    }

    public Player updatePlayer(Long teamId, Long playerId, PlayerRequestDTO dto) {
        validatePlayerRequest(teamId, dto);

        if (playerId == null) {
            throw new IllegalArgumentException("Validation Error: Player ID is required.");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found with id: " + teamId));

        Player player = playerRepository.findByIdAndTeamId(playerId, teamId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Player not found with id: " + playerId + " for team id: " + teamId
                ));

        if (playerRepository.existsByTeamIdAndNumberAndIdNot(teamId, dto.getNumber(), playerId)) {
            throw new IllegalArgumentException(
                    "Validation Error: Team already has a player with number " + dto.getNumber() + "."
            );
        }

        player.setName(dto.getName().trim());
        player.setNumber(dto.getNumber());
        player.setPosition(dto.getPosition());
        player.setTeam(team);

        return playerRepository.save(player);
    }

    public void deletePlayer(Long teamId, Long playerId) {
        if (teamId == null) {
            throw new IllegalArgumentException("Validation Error: Team ID is required.");
        }

        if (playerId == null) {
            throw new IllegalArgumentException("Validation Error: Player ID is required.");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found with id: " + teamId));

        Player player = playerRepository.findByIdAndTeamId(playerId, teamId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Player not found with id: " + playerId + " for team id: " + teamId
                ));

        team.getPlayers().remove(player);
        playerRepository.delete(player);
    }

    private void validatePlayerRequest(Long teamId, PlayerRequestDTO dto) {
        if (teamId == null) {
            throw new IllegalArgumentException("Validation Error: Team ID is required.");
        }

        if (dto == null) {
            throw new IllegalArgumentException("Validation Error: Player data is required.");
        }

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Validation Error: Player name is required.");
        }

        if (dto.getName().trim().length() > 30) {
            throw new IllegalArgumentException("Validation Error: Player name must be at most 30 characters.");
        }

        if (dto.getNumber() == null) {
            throw new IllegalArgumentException("Validation Error: Player number is required.");
        }

        if (dto.getNumber() <= 0) {
            throw new IllegalArgumentException("Validation Error: Player number must be greater than 0.");
        }

        if (dto.getPosition() == null) {
            throw new IllegalArgumentException("Validation Error: Player position is required.");
        }
    }
}
