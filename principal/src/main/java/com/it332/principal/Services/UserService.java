package com.it332.principal.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.it332.principal.Models.User;
import com.it332.principal.Repository.UserRepository;
import com.it332.principal.Security.JwtUtil;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil; // Inject your JwtUtil for token management

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String generateToken(String userId) {
        return jwtUtil.generateToken(userId);
    }

    @Transactional
    public User createUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public boolean validateUser(String emailOrUsername, String password) {
        // Find user by email or username
        User userByEmail = userRepository.findByEmail(emailOrUsername);
        User userByUsername = userRepository.findByUsername(emailOrUsername);
        User user;

        if (userByEmail != null || userByUsername != null) {
            if (userByEmail != null) {
                user = userByEmail;
            } else {
                user = userByUsername;
            }

            // Verify the password using BCrypt
            return passwordEncoder.matches(password, user.getPassword());
        }

        return false; // User not found
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
    public boolean checkIfUserExists(String emailOrUsername) {
        User userByEmail = userRepository.findByEmail(emailOrUsername);
        User userByUsername = userRepository.findByUsername(emailOrUsername);
        
        return userByEmail != null || userByUsername != null;
    }
}