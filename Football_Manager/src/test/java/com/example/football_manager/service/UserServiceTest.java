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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.football_manager.model.Team;
import com.example.football_manager.repository.TeamRepository;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
    @Mock
    private TeamRepository teamRepository;
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
        logger.info("Register user service test: username={}, isAdmin={}", "elena", false);
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
        logger.info("Register admin service test: username={}, isAdmin={}", "admin", true);
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
        logger.info("Register user conflict test: username={}", "elena");
        when(userRepository.existsByUsername("elena")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser("elena", "other@test.com", "1234", false));

        assertEquals("Username already in use", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_shouldThrowWhenEmailAlreadyExists() {
        logger.info("Register user email conflict test: email={}", "elena@test.com");
        when(userRepository.existsByUsername("elena")).thenReturn(false);
        when(userRepository.existsByEmail("elena@test.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser("elena", "elena@test.com", "1234", false));

        assertEquals("Email already in use", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnUserWhenCredentialsAreValid() {
        logger.info("Login service test: username={}", "elena");
        when(userRepository.findByUsername("elena")).thenReturn(Optional.of(normalUser));
        when(passwordEncoder.matches("1234", "hashedPass")).thenReturn(true);

        User result = userService.login("elena", "1234");

        assertEquals("elena", result.getUsername());
    }

    @Test
    void login_shouldThrowWhenUserNotFound() {
        logger.info("Login user not found test: username={}", "ghost");
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.login("ghost", "1234"));

        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void login_shouldThrowWhenPasswordDoesNotMatch() {
        logger.info("Login bad password test: username={}", "elena");
        when(userRepository.findByUsername("elena")).thenReturn(Optional.of(normalUser));
        when(passwordEncoder.matches("badpass", "hashedPass")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.login("elena", "badpass"));

        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void getUserById_shouldReturnUser() {
        logger.info("Get user by id service test: id={}", 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(normalUser));

        User result = userService.getUserById(1L);

        assertEquals("elena", result.getUsername());
    }

    @Test
    void getUserById_shouldThrowWhenMissing() {
        logger.info("Get user by id not found test: id={}", 99L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.getUserById(99L));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getUserByUsername_shouldReturnUser() {
        logger.info("Get user by username service test: username={}", "elena");
        when(userRepository.findByUsername("elena")).thenReturn(Optional.of(normalUser));

        User result = userService.getUserByUsername("elena");

        assertEquals("elena@test.com", result.getEmail());
    }

    @Test
    void getUserByUsername_shouldThrowWhenMissing() {
        logger.info("Get user by username not found test: username={}", "ghost");
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.getUserByUsername("ghost"));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void addFavouriteTeam_shouldAddTeamSuccessfully() {
        logger.info("Add favourite team test: userId={}, teamId={}", 1L, 10L);
        Team team = new Team();
        team.setId(10L);
        team.setName("Real Madrid");

        normalUser.setFavouriteTeams(new HashSet<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(normalUser));
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.addFavouriteTeam(1L, 10L);

        assertEquals(1, result.getFavouriteTeams().size());
        assertTrue(result.getFavouriteTeams().contains(team));
        verify(userRepository).save(normalUser);
    }

    @Test
    void addFavouriteTeam_shouldThrowWhenTeamAlreadyFavourite() {
        logger.info("Add favourite team conflict test: userId={}, teamId={}", 1L, 10L);
        Team team = new Team();
        team.setId(10L);
        team.setName("Real Madrid");

        normalUser.setFavouriteTeams(new HashSet<>());
        normalUser.getFavouriteTeams().add(team);

        when(userRepository.findById(1L)).thenReturn(Optional.of(normalUser));
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.addFavouriteTeam(1L, 10L)
        );

        assertEquals("Team already in favourites", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void removeFavouriteTeam_shouldRemoveTeamSuccessfully() {
        logger.info("Remove favourite team test: userId={}, teamId={}", 1L, 10L);
        Team team = new Team();
        team.setId(10L);
        team.setName("Real Madrid");

        normalUser.setFavouriteTeams(new HashSet<>());
        normalUser.getFavouriteTeams().add(team);

        when(userRepository.findById(1L)).thenReturn(Optional.of(normalUser));
        when(teamRepository.existsById(10L)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.removeFavouriteTeam(1L, 10L);

        assertTrue(result.getFavouriteTeams().isEmpty());
        verify(userRepository).save(normalUser);
    }

    @Test
    void removeFavouriteTeam_shouldThrowWhenTeamNotInFavourites() {
        logger.info("Remove favourite team missing test: userId={}, teamId={}", 1L, 10L);
        normalUser.setFavouriteTeams(new HashSet<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(normalUser));
        when(teamRepository.existsById(10L)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.removeFavouriteTeam(1L, 10L)
        );

        assertEquals("Team not in favourites", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getFavouriteTeamIdsByUserId_shouldReturnFavouriteIds() {
        logger.info("Get favourite team ids test: userId={}", 1L);
        Team team1 = new Team();
        team1.setId(10L);

        Team team2 = new Team();
        team2.setId(20L);

        normalUser.setFavouriteTeams(Set.of(team1, team2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(normalUser));

        Set<Long> result = userService.getFavouriteTeamIdsByUserId(1L);

        assertEquals(Set.of(10L, 20L), result);
    }

    @Test
    void getFavouriteTeamIdsByUserId_shouldReturnEmptyWhenUserIdIsNull() {
        logger.info("Get favourite team ids null user test");
        Set<Long> result = userService.getFavouriteTeamIdsByUserId(null);

        assertTrue(result.isEmpty());
        verify(userRepository, never()).findById(anyLong());
    }
}