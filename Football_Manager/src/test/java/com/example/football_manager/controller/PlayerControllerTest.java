package com.example.football_manager.controller;

import com.example.football_manager.dto.PlayerBulkRequestDTO;
import com.example.football_manager.dto.PlayerRequestDTO;
import com.example.football_manager.model.Player;
import com.example.football_manager.model.PlayerPosition;
import com.example.football_manager.model.Team;
import com.example.football_manager.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlayerControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(PlayerControllerTest.class);

    private PlayerService playerService;
    private PlayerController playerController;

    @BeforeEach
    void setUp() {
        playerService = mock(PlayerService.class);
        playerController = new PlayerController(playerService);
    }

    @Test
    void createPlayer_shouldReturnCreatedWhenPlayerIsCreated() {
        Team team = new Team();
        team.setId(10L);

        PlayerRequestDTO dto = new PlayerRequestDTO("Take Kubo", 14, PlayerPosition.FORWARD);
        Player player = new Player(1L, "Take Kubo", 14, PlayerPosition.FORWARD, team);

        logger.info("Create player test: teamId={}, name={}, number={}", 10L, dto.getName(), dto.getNumber());

        when(playerService.createPlayer(10L, dto)).thenReturn(player);

        ResponseEntity<?> response = playerController.createPlayer(10L, dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(Player.class, response.getBody());
        assertEquals("Take Kubo", ((Player) response.getBody()).getName());
        verify(playerService).createPlayer(10L, dto);
    }

    @Test
    void createPlayer_shouldReturnBadRequestWhenValidationFails() {
        PlayerRequestDTO dto = new PlayerRequestDTO("Take Kubo", 14, PlayerPosition.FORWARD);
        logger.info("Create player validation failure test: teamId={}, number={}", 10L, dto.getNumber());
        when(playerService.createPlayer(10L, dto))
                .thenThrow(new IllegalArgumentException("Validation Error: Team already has a player with number 14."));

        ResponseEntity<?> response = playerController.createPlayer(10L, dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Error: Team already has a player with number 14.", response.getBody());
    }

    @Test
    void createPlayers_shouldReturnCreatedWhenPlayersAreCreated() {
        Team team = new Team();
        team.setId(10L);

        PlayerRequestDTO player1 = new PlayerRequestDTO("Unai Simon", 1, PlayerPosition.GOALKEEPER);
        PlayerRequestDTO player2 = new PlayerRequestDTO("Inaki Williams", 9, PlayerPosition.FORWARD);
        PlayerBulkRequestDTO dto = new PlayerBulkRequestDTO(List.of(player1, player2));

        Player savedPlayer1 = new Player(1L, "Unai Simon", 1, PlayerPosition.GOALKEEPER, team);
        Player savedPlayer2 = new Player(2L, "Inaki Williams", 9, PlayerPosition.FORWARD, team);

        logger.info("Create players bulk test: teamId={}, count={}", 10L, dto.getPlayers().size());

        when(playerService.createPlayers(10L, dto.getPlayers())).thenReturn(List.of(savedPlayer1, savedPlayer2));

        ResponseEntity<?> response = playerController.createPlayers(10L, dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertInstanceOf(List.class, response.getBody());
        assertEquals(2, ((List<?>) response.getBody()).size());
        verify(playerService).createPlayers(10L, dto.getPlayers());
    }

    @Test
    void createPlayers_shouldReturnBadRequestWhenBulkValidationFails() {
        PlayerBulkRequestDTO dto = new PlayerBulkRequestDTO(List.of(
                new PlayerRequestDTO("Unai Simon", 1, PlayerPosition.GOALKEEPER),
                new PlayerRequestDTO("Julen Agirrezabala", 1, PlayerPosition.GOALKEEPER)
        ));

        logger.info("Create players bulk validation failure test: teamId={}, count={}", 10L, dto.getPlayers().size());

        when(playerService.createPlayers(10L, dto.getPlayers()))
                .thenThrow(new IllegalArgumentException("Validation Error: Request contains duplicated player number 1."));

        ResponseEntity<?> response = playerController.createPlayers(10L, dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Error: Request contains duplicated player number 1.", response.getBody());
    }

    @Test
    void getPlayersByTeamId_shouldReturnOkWhenPlayersExist() {
        Team team = new Team();
        team.setId(10L);

        Player player = new Player(1L, "Alex Remiro", 1, PlayerPosition.GOALKEEPER, team);
        logger.info("Get players by team test: teamId={}, expectedCount={}", 10L, 1);
        when(playerService.getPlayersByTeamId(10L)).thenReturn(List.of(player));

        ResponseEntity<?> response = playerController.getPlayersByTeamId(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(List.class, response.getBody());
        verify(playerService).getPlayersByTeamId(10L);
    }

    @Test
    void getPlayersByTeamId_shouldReturnBadRequestWhenTeamIsInvalid() {
        logger.info("Get players by team invalid test: teamId={}", 404L);
        when(playerService.getPlayersByTeamId(404L))
                .thenThrow(new IllegalArgumentException("Team not found with id: 404"));

        ResponseEntity<?> response = playerController.getPlayersByTeamId(404L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Team not found with id: 404", response.getBody());
    }

    @Test
    void updatePlayer_shouldReturnOkWhenPlayerIsUpdated() {
        Team team = new Team();
        team.setId(10L);

        PlayerRequestDTO dto = new PlayerRequestDTO("Oihan Sancet", 8, PlayerPosition.MIDFIELDER);
        Player player = new Player(1L, "Oihan Sancet", 8, PlayerPosition.MIDFIELDER, team);

        logger.info("Update player test: teamId={}, playerId={}, number={}", 10L, 1L, dto.getNumber());

        when(playerService.updatePlayer(10L, 1L, dto)).thenReturn(player);

        ResponseEntity<?> response = playerController.updatePlayer(10L, 1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(Player.class, response.getBody());
        assertEquals("Oihan Sancet", ((Player) response.getBody()).getName());
        verify(playerService).updatePlayer(10L, 1L, dto);
    }

    @Test
    void updatePlayer_shouldReturnBadRequestWhenValidationFails() {
        PlayerRequestDTO dto = new PlayerRequestDTO("Oihan Sancet", 8, PlayerPosition.MIDFIELDER);
        logger.info("Update player validation failure test: teamId={}, playerId={}, number={}", 10L, 1L, dto.getNumber());
        when(playerService.updatePlayer(10L, 1L, dto))
                .thenThrow(new IllegalArgumentException("Validation Error: Team already has a player with number 8."));

        ResponseEntity<?> response = playerController.updatePlayer(10L, 1L, dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation Error: Team already has a player with number 8.", response.getBody());
    }

    @Test
    void deletePlayer_shouldReturnNoContentWhenPlayerIsDeleted() {
        logger.info("Delete player test: teamId={}, playerId={}", 10L, 1L);
        ResponseEntity<?> response = playerController.deletePlayer(10L, 1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(playerService).deletePlayer(10L, 1L);
    }

    @Test
    void deletePlayer_shouldReturnBadRequestWhenDeleteFails() {
        logger.info("Delete player failure test: teamId={}, playerId={}", 10L, 1L);
        doThrow(new IllegalArgumentException("Player not found with id: 1 for team id: 10"))
                .when(playerService).deletePlayer(10L, 1L);

        ResponseEntity<?> response = playerController.deletePlayer(10L, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Player not found with id: 1 for team id: 10", response.getBody());
    }
}
