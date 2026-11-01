package com.example.karateframework.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.karateframework.model.User;
import com.example.karateframework.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        log.debug("Creating user with username: {}", sanitizeForLog(user.getUsername()));
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        User savedUser = userRepository.save(user);
        log.info("User created with id: {}", savedUser.getId());
        return savedUser;
    }

    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "User not found with username: " + username));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<User> getActiveUsers() {
        return userRepository.findByActive(true);
    }

    @Transactional(readOnly = true)
    public List<User> searchUsers(String searchTerm) {
        return userRepository.searchUsers(searchTerm);
    }

    public User updateUser(UUID id, User userDetails) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "User not found with id: " + id));

        // Check username uniqueness if changed
        if (!user.getUsername().equals(userDetails.getUsername())) {
            if (userRepository.existsByUsername(userDetails.getUsername())) {
                throw new IllegalArgumentException(
                        "Username already exists: " + userDetails.getUsername());
            }
        }

        // Check email uniqueness if changed
        if (!user.getEmail().equals(userDetails.getEmail())) {
            if (userRepository.existsByEmail(userDetails.getEmail())) {
                throw new IllegalArgumentException(
                        "Email already exists: " + userDetails.getEmail());
            }
        }

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setActive(userDetails.isActive());

        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public void deactivateUser(UUID id) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "User not found with id: " + id));
        user.setActive(false);
        userRepository.save(user);
    }

    private String sanitizeForLog(String input) {
        if (input == null) {
            return null;
        }
        return input.replace('\r', '_').replace('\n', '_');
    }
}
