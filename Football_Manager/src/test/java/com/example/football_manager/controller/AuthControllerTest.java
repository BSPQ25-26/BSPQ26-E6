package com.example.football_manager.controller;

import com.example.football_manager.dto.AuthResponseDTO;
import com.example.football_manager.dto.LoginDTO;
import com.example.football_manager.dto.RegisterDTO;
import com.example.football_manager.model.User;
import com.example.football_manager.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private UserService userService;
    private AuthController authController;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        session = mock(HttpSession.class);

        authController = new AuthController();
        ReflectionTestUtils.setField(authController, "userService", userService);
    }

    @Test
    void register_shouldReturnCreatedWhenUserIsRegistered() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("elena");
        registerDTO.setEmail("elena@test.com");
        registerDTO.setPassword("1234");
        registerDTO.setIsAdmin(false);

        User user = new User();
        user.setId(1L);
        user.setUsername("elena");
        user.setEmail("elena@test.com");
        user.setIsAdmin(false);

        when(userService.registerUser("elena", "elena@test.com", "1234", false))
                .thenReturn(user);

        ResponseEntity<AuthResponseDTO> response = authController.register(registerDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("elena", response.getBody().getUsername());
        assertEquals("elena@test.com", response.getBody().getEmail());
        assertFalse(response.getBody().getIsAdmin());
        assertEquals("User registered", response.getBody().getMessage());

        verify(userService).registerUser("elena", "elena@test.com", "1234", false);
    }

    @Test
    void register_shouldReturnConflictWhenUserAlreadyExists() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("elena");
        registerDTO.setEmail("elena@test.com");
        registerDTO.setPassword("1234");
        registerDTO.setIsAdmin(false);

        when(userService.registerUser("elena", "elena@test.com", "1234", false))
                .thenThrow(new IllegalArgumentException("Username already exists"));

        ResponseEntity<AuthResponseDTO> response = authController.register(registerDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getId());
        assertNull(response.getBody().getUsername());
        assertNull(response.getBody().getEmail());
        assertNull(response.getBody().getIsAdmin());
        assertEquals("Username already exists", response.getBody().getMessage());

        verify(userService).registerUser("elena", "elena@test.com", "1234", false);
    }

    @Test
    void login_shouldReturnOkWhenCredentialsAreCorrect() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("elena");
        loginDTO.setPassword("1234");

        User user = new User();
        user.setId(1L);
        user.setUsername("elena");
        user.setEmail("elena@test.com");
        user.setIsAdmin(true);

        when(userService.login("elena", "1234")).thenReturn(user);

        ResponseEntity<AuthResponseDTO> response = authController.login(loginDTO, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("elena", response.getBody().getUsername());
        assertEquals("elena@test.com", response.getBody().getEmail());
        assertTrue(response.getBody().getIsAdmin());
        assertEquals("Login successful", response.getBody().getMessage());

        verify(userService).login("elena", "1234");
        verify(session).setAttribute("userId", 1L);
        verify(session).setAttribute("isAdmin", true);
    }

    @Test
    void login_shouldReturnUnauthorizedWhenCredentialsAreWrong() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("elena");
        loginDTO.setPassword("wrong");

        when(userService.login("elena", "wrong"))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        ResponseEntity<AuthResponseDTO> response = authController.login(loginDTO, session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getId());
        assertNull(response.getBody().getUsername());
        assertNull(response.getBody().getEmail());
        assertNull(response.getBody().getIsAdmin());
        assertEquals("Invalid credentials", response.getBody().getMessage());

        verify(userService).login("elena", "wrong");
        verify(session, never()).setAttribute(anyString(), any());
    }
}