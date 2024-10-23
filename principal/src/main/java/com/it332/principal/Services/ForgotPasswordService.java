package com.it332.principal.Services;

import com.it332.principal.Repository.ForgotPasswordRepository;
import com.it332.principal.Models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ForgotPasswordService {

    private static final long EXPIRE_TOKEN = 15; // Token expiration time in minutes

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordService.class); // Logger initialization

    @Autowired
    private ForgotPasswordRepository repo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;  // Inject JavaMailSender

    public String forgotPass(String email) {
        try {
            Optional<User> userOptional = repo.findByEmail(email);
    
            if (userOptional.isEmpty()) {
                logger.warn("No user found with the email: {}", email);
                return "Invalid email id.";  // Return a generic message
            }
    
            User user = userOptional.get();
            String token = generateToken();
            user.setToken(token);
            user.setTokenCreationDate(LocalDateTime.now());
            
            // Save the user and log the success
            User updatedUser = repo.save(user);
            logger.info("User updated successfully: {}", updatedUser.getEmail());
    
            logger.info("Sending password reset email to: {}", user.getEmail());
            sendPasswordResetEmail(user.getEmail(), token);
    
            return "https://localhost:3000/reset-password?token=" + token; // Return the full URL
        } catch (Exception e) {
            logger.error("Error in forgotPass: {}", e.getMessage());
            return "An error occurred while processing your request."; // Generic error message
        }
    }
    

    public String resetPass(String token, String password) {
        Optional<User> userOptional = repo.findByToken(token); // Change here to use Optional<User>
    
        if (userOptional.isEmpty()) { // Use isEmpty() to check if user is present
            logger.warn("Invalid token: {}", token); // Log invalid token
            return "Invalid token";
        }
    
        User user = userOptional.get(); // Extract the User from Optional
    
        LocalDateTime tokenCreationDate = user.getTokenCreationDate();
        if (isTokenExpired(tokenCreationDate)) {
            return "Token expired.";
        }
    
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        user.setToken(null);
        user.setTokenCreationDate(null);
        repo.save(user);
    
        return "Your password has been successfully updated.";
    }
    

    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Password Reset Request";
        String resetUrl = "https://localhost:3000/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText("Click the link to reset your password: " + resetUrl);

        mailSender.send(message);
    }

    private String generateToken() {
        return UUID.randomUUID().toString() + UUID.randomUUID().toString();
    }

    private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {
        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(tokenCreationDate, now);
        return diff.toMinutes() >= EXPIRE_TOKEN;
    }
}
