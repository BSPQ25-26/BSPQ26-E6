package com.example.football_manager.service;

import com.example.football_manager.model.Team;
import com.example.football_manager.model.User;
import com.example.football_manager.repository.TeamRepository;
import com.example.football_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String username, String email, String rawPassword, Boolean isAdmin) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already in use");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setHashedPassword(passwordEncoder.encode(rawPassword));
        user.setIsAdmin(Boolean.TRUE.equals(isAdmin));

        return userRepository.save(user);
    }

    public User login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, user.getHashedPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return user;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User addFavouriteTeam(Long userId, Long teamId) {
        User user = getUserById(userId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        Set<Team> favouriteTeams = user.getFavouriteTeams();
        if (favouriteTeams == null) {
            favouriteTeams = new HashSet<>();
            user.setFavouriteTeams(favouriteTeams);
        }

        if (!favouriteTeams.add(team)) {
            throw new IllegalArgumentException("Team already in favourites");
        }

        return userRepository.save(user);
    }

    public User removeFavouriteTeam(Long userId, Long teamId) {
        User user = getUserById(userId);

        if (!teamRepository.existsById(teamId)) {
            throw new IllegalArgumentException("Team not found");
        }

        Set<Team> favouriteTeams = user.getFavouriteTeams();
        if (favouriteTeams == null || favouriteTeams.isEmpty()) {
            throw new IllegalArgumentException("Team not in favourites");
        }

        boolean removed = favouriteTeams.removeIf(team -> teamId.equals(team.getId()));
        if (!removed) {
            throw new IllegalArgumentException("Team not in favourites");
        }

        return userRepository.save(user);
    }
}