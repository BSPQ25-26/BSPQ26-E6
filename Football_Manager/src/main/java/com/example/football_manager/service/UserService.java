package com.example.football_manager.service;

import com.example.football_manager.model.User;
import com.example.football_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(UUID id, String username) {
        if (userRepository.existsById(id)) {
            throw new IllegalArgumentException("User already exists");
        }

        User user = new User();
        user.setId(id);
        user.setUsername(username);

        return userRepository.save(user);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}