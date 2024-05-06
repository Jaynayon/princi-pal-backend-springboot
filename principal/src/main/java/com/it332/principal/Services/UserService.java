package com.it332.principal.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.it332.principal.Models.User;
import com.it332.principal.Repository.UserRepository;
import com.it332.principal.Security.JwtUtil;
import com.it332.principal.Security.NotFoundException;

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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new NotFoundException("User not found with email: " + email);
        }

        return user;
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found with ID: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void updateUser(String userId, User updateUser) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
    
            // Update only the fields that are present in updateUser
            if (updateUser.getFname() != null) {
                user.setFname(updateUser.getFname());
            }
            if (updateUser.getMname() != null) {
                user.setMname(updateUser.getMname());
            }
            if (updateUser.getLname() != null) {
                user.setLname(updateUser.getLname());
            }
            if (updateUser.getUsername() != null) {
                user.setUsername(updateUser.getUsername());
            }
            if (updateUser.getEmail() != null) {
                user.setEmail(updateUser.getEmail());
            }
            if (updateUser.getPassword() != null) {
                String encodedPassword = passwordEncoder.encode(updateUser.getPassword());
                user.setPassword(encodedPassword);
            }
            if (updateUser.getPosition() != null) {
                user.setPosition(updateUser.getPosition());
            }
            if (updateUser.getAvatar() != null) {
                user.setAvatar(updateUser.getAvatar());
            }
    
            userRepository.save(user); // Save the updated user object
        } else {
            throw new NotFoundException("User not found with ID: " + userId);
        }
    }
    


    public void deleteUserById(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new NotFoundException("User not found with ID: " + userId);
        }
    }

    public boolean checkIfUserExists(String emailOrUsername) {
        User userByEmail = userRepository.findByEmail(emailOrUsername);
        User userByUsername = userRepository.findByUsername(emailOrUsername);

        return userByEmail != null || userByUsername != null;
    }
}