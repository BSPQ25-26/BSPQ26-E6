package com.example.football_manager.service;

import com.example.football_manager.dto.PlayerRequestDTO;
import com.example.football_manager.model.Country;
import com.example.football_manager.model.Player;
import com.example.football_manager.model.PlayerPosition;
import com.example.football_manager.model.Team;
import com.example.football_manager.repository.PlayerRepository;
import com.example.football_manager.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private PlayerService playerService;

    private Team team;
    private PlayerRequestDTO validDto;

    @BeforeEach
    void setUp() {
        Country country = new Country();
        country.setId(1L);
        country.setName("Spain");

        team = new Team();
        team.setId(10L);
        team.setName("Real Sociedad");
        team.setLogoUrl("/img/rs.png");
        team.setCountry(country);

        validDto = new PlayerRequestDTO(" Mikel Oyarzabal ", 10, PlayerPosition.FORWARD);
    }

    @Test
    void createPlayer_shouldCreatePlayerSuccessfully() {
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(playerRepository.existsByTeamIdAndNumber(10L, 10)).thenReturn(false);
        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> {
            Player player = invocation.getArgument(0);
            player.setId(99L);
            return player;
        });

        Player result = playerService.createPlayer(10L, validDto);

        assertNotNull(result);
        assertEquals(99L, result.getId());
        assertEquals("Mikel Oyarzabal", result.getName());
        assertEquals(10, result.getNumber());
        assertEquals(PlayerPosition.FORWARD, result.getPosition());
        assertEquals(team, result.getTeam());

        ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);
        verify(playerRepository).save(captor.capture());

        Player playerToSave = captor.getValue();
        assertEquals("Mikel Oyarzabal", playerToSave.getName());
        assertEquals(10, playerToSave.getNumber());
        assertEquals(PlayerPosition.FORWARD, playerToSave.getPosition());
        assertEquals(team, playerToSave.getTeam());
        assertEquals(1, team.getPlayers().size());
    }

    @Test
    void createPlayer_shouldThrowExceptionWhenTeamNotFound() {
        when(teamRepository.findById(55L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> playerService.createPlayer(55L, validDto));

        assertEquals("Team not found with id: 55", ex.getMessage());
        verify(playerRepository, never()).save(any());
    }

    @Test
    void createPlayer_shouldThrowExceptionWhenNumberAlreadyExistsInTeam() {
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(playerRepository.existsByTeamIdAndNumber(10L, 10)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> playerService.createPlayer(10L, validDto));

        assertEquals("Validation Error: Team already has a player with number 10.", ex.getMessage());
        verify(playerRepository, never()).save(any());
    }

    @Test
    void createPlayer_shouldThrowExceptionWhenNameIsBlank() {
        PlayerRequestDTO dto = new PlayerRequestDTO("   ", 8, PlayerPosition.MIDFIELDER);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> playerService.createPlayer(10L, dto));

        assertEquals("Validation Error: Player name is required.", ex.getMessage());
        verify(teamRepository, never()).findById(any());
    }

    @Test
    void createPlayer_shouldThrowExceptionWhenPositionIsMissing() {
        PlayerRequestDTO dto = new PlayerRequestDTO("Take Kubo", 14, null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> playerService.createPlayer(10L, dto));

        assertEquals("Validation Error: Player position is required.", ex.getMessage());
        verify(teamRepository, never()).findById(any());
    }

    @Test
    void getPlayersByTeamId_shouldReturnOrderedPlayers() {
        Player player1 = new Player(1L, "Alex Remiro", 1, PlayerPosition.GOALKEEPER, team);
        Player player2 = new Player(2L, "Mikel Oyarzabal", 10, PlayerPosition.FORWARD, team);

        when(teamRepository.existsById(10L)).thenReturn(true);
        when(playerRepository.findByTeamIdOrderByNumberAsc(10L)).thenReturn(List.of(player1, player2));

        List<Player> result = playerService.getPlayersByTeamId(10L);

        assertEquals(2, result.size());
        assertEquals("Alex Remiro", result.get(0).getName());
        assertEquals("Mikel Oyarzabal", result.get(1).getName());
    }

    @Test
    void getPlayersByTeamId_shouldThrowExceptionWhenTeamIdIsMissing() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> playerService.getPlayersByTeamId(null));

        assertEquals("Validation Error: Team ID is required.", ex.getMessage());
    }

    @Test
    void getPlayersByTeamId_shouldThrowExceptionWhenTeamDoesNotExist() {
        when(teamRepository.existsById(404L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> playerService.getPlayersByTeamId(404L));

        assertEquals("Team not found with id: 404", ex.getMessage());
    }

    @Test
    void updatePlayer_shouldUpdatePlayerSuccessfully() {
        Player existingPlayer = new Player(1L, "Mikel Oyarzabal", 10, PlayerPosition.FORWARD, team);

        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(playerRepository.findByIdAndTeamId(1L, 10L)).thenReturn(Optional.of(existingPlayer));
        when(playerRepository.existsByTeamIdAndNumberAndIdNot(10L, 8, 1L)).thenReturn(false);
        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlayerRequestDTO updateDto = new PlayerRequestDTO(" Oihan Sancet ", 8, PlayerPosition.MIDFIELDER);

        Player result = playerService.updatePlayer(10L, 1L, updateDto);

        assertEquals("Oihan Sancet", result.getName());
        assertEquals(8, result.getNumber());
        assertEquals(PlayerPosition.MIDFIELDER, result.getPosition());
        assertEquals(team, result.getTeam());
    }

    @Test
    void updatePlayer_shouldThrowExceptionWhenPlayerNotFound() {
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(playerRepository.findByIdAndTeamId(999L, 10L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> playerService.updatePlayer(10L, 999L, validDto));

        assertEquals("Player not found with id: 999 for team id: 10", ex.getMessage());
        verify(playerRepository, never()).save(any());
    }

    @Test
    void updatePlayer_shouldThrowExceptionWhenNumberAlreadyExistsInAnotherPlayer() {
        Player existingPlayer = new Player(1L, "Mikel Oyarzabal", 10, PlayerPosition.FORWARD, team);

        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(playerRepository.findByIdAndTeamId(1L, 10L)).thenReturn(Optional.of(existingPlayer));
        when(playerRepository.existsByTeamIdAndNumberAndIdNot(10L, 8, 1L)).thenReturn(true);

        PlayerRequestDTO updateDto = new PlayerRequestDTO("Oihan Sancet", 8, PlayerPosition.MIDFIELDER);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> playerService.updatePlayer(10L, 1L, updateDto));

        assertEquals("Validation Error: Team already has a player with number 8.", ex.getMessage());
        verify(playerRepository, never()).save(any());
    }

    @Test
    void deletePlayer_shouldDeleteSuccessfully() {
        Player existingPlayer = new Player(1L, "Unai Simon", 1, PlayerPosition.GOALKEEPER, team);
        team.getPlayers().add(existingPlayer);

        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(playerRepository.findByIdAndTeamId(1L, 10L)).thenReturn(Optional.of(existingPlayer));

        playerService.deletePlayer(10L, 1L);

        verify(playerRepository).delete(existingPlayer);
        assertEquals(0, team.getPlayers().size());
    }

    @Test
    void deletePlayer_shouldThrowExceptionWhenPlayerNotFound() {
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(playerRepository.findByIdAndTeamId(123L, 10L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> playerService.deletePlayer(10L, 123L));

        assertEquals("Player not found with id: 123 for team id: 10", ex.getMessage());
        verify(playerRepository, never()).delete(any());
    }
}
