package com.example.football_manager.service;

import com.example.football_manager.model.User;
import com.example.football_manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User normalUser;

    @BeforeEach
    void setUp() {
        normalUser = new User();
        normalUser.setId(1L);
        normalUser.setUsername("elena");
        normalUser.setEmail("elena@test.com");
        normalUser.setHashedPassword("hashedPass");
        normalUser.setIsAdmin(false);
    }

    @Test
    void registerUser_shouldCreateNormalUser() {
        when(userRepository.existsByUsername("elena")).thenReturn(false);
        when(userRepository.existsByEmail("elena@test.com")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("hashed1234");

        User saved = new User();
        saved.setId(1L);
        saved.setUsername("elena");
        saved.setEmail("elena@test.com");
        saved.setHashedPassword("hashed1234");
        saved.setIsAdmin(false);

        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.registerUser("elena", "elena@test.com", "1234", false);

        assertNotNull(result);
        assertEquals("elena", result.getUsername());
        assertFalse(result.getIsAdmin());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("hashed1234", captor.getValue().getHashedPassword());
    }

    @Test
    void registerUser_shouldCreateAdminUser() {
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(userRepository.existsByEmail("admin@test.com")).thenReturn(false);
        when(passwordEncoder.encode("adminpass")).thenReturn("hashedAdmin");

        User saved = new User();
        saved.setId(2L);
        saved.setUsername("admin");
        saved.setEmail("admin@test.com");
        saved.setHashedPassword("hashedAdmin");
        saved.setIsAdmin(true);

        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.registerUser("admin", "admin@test.com", "adminpass", true);

        assertTrue(result.getIsAdmin());
    }

    @Test
    void registerUser_shouldThrowWhenUsernameAlreadyExists() {
        when(userRepository.existsByUsername("elena")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser("elena", "other@test.com", "1234", false));

        assertEquals("Username already in use", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_shouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByUsername("elena")).thenReturn(false);
        when(userRepository.existsByEmail("elena@test.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser("elena", "elena@test.com", "1234", false));

        assertEquals("Email already in use", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnUserWhenCredentialsAreValid() {
        when(userRepository.findByUsername("elena")).thenReturn(Optional.of(normalUser));
        when(passwordEncoder.matches("1234", "hashedPass")).thenReturn(true);

        User result = userService.login("elena", "1234");

        assertEquals("elena", result.getUsername());
    }

    @Test
    void login_shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.login("ghost", "1234"));

        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void login_shouldThrowWhenPasswordDoesNotMatch() {
        when(userRepository.findByUsername("elena")).thenReturn(Optional.of(normalUser));
        when(passwordEncoder.matches("badpass", "hashedPass")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.login("elena", "badpass"));

        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void getUserById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(normalUser));

        User result = userService.getUserById(1L);

        assertEquals("elena", result.getUsername());
    }

    @Test
    void getUserById_shouldThrowWhenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.getUserById(99L));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getUserByUsername_shouldReturnUser() {
        when(userRepository.findByUsername("elena")).thenReturn(Optional.of(normalUser));

        User result = userService.getUserByUsername("elena");

        assertEquals("elena@test.com", result.getEmail());
    }

    @Test
    void getUserByUsername_shouldThrowWhenMissing() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.getUserByUsername("ghost"));

        assertEquals("User not found", ex.getMessage());
    }
}