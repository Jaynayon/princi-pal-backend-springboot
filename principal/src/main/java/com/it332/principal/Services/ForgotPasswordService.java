package com.it332.principal.Services;
 
import com.it332.principal.Repository.UserRepository;
import com.it332.principal.Models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.it332.principal.Security.NotFoundException;

 
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
 
@Service
public class ForgotPasswordService {
 
    private static final long EXPIRE_TOKEN = 15; // Token expiration time in minutes
 
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordService.class); // Logger initialization
 
    @Autowired
    private UserService userService;
 
    @Autowired
    private UserRepository userRepository;
 
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
 
    @Autowired
    private JavaMailSender mailSender; // Inject JavaMailSender
 
    public void forgotPass(String email) {
        // Check if user exists
        User user = userService.getUserByEmail(email);
        
        if (user == null) {
            // If user does not exist, throw NotFoundException
            throw new NotFoundException("User with email " + email + " not found.");
        }
    
        // Generate token and set it in the user object
        String token = generateToken();
        user.setToken(token);
        user.setTokenCreationDate(LocalDateTime.now());
    
        // Save the user and log the success
        userRepository.save(user); // Ensure you're saving the user here
    
        // Log and send email
        logger.info("User updated successfully: {}", user.getEmail());
        logger.info("Sending password reset email to: {}", user.getEmail());
        
        sendPasswordResetEmail(user.getEmail(), token);
    }
    
    
 
    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Password Reset Request";
        String resetUrl = "https://localhost:3000/reset-password?token=" + token;
 
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("principalmailsender@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText("Click the link to reset your password: " + resetUrl);
 
        mailSender.send(message);
    }
 
    public String resetPass(String token, String password) {
        User user = userService.getUserByToken(token); // Change here to use Optional<User>
 
        LocalDateTime tokenCreationDate = user.getTokenCreationDate();
        if (isTokenExpired(tokenCreationDate)) {
            return "Token expired.";
        }
 
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        user.setToken(null);
        user.setTokenCreationDate(null);
        userRepository.save(user);
 
        return "Your password has been successfully updated.";
    }
 
    private String generateToken() {
        return UUID.randomUUID().toString() + UUID.randomUUID().toString();
    }
 
    private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {
        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(tokenCreationDate, now);
        return diff.toMinutes() >= EXPIRE_TOKEN;
    }

    public boolean validateToken(String token) {
        User user = userRepository.findByToken(token);
        if (user == null) {
            return false; // Token not found
        }
    
        return !isTokenExpired(user.getTokenCreationDate()); // Return true if the token is still valid
    }
}