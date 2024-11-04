package com.it332.principal.Services;

import com.it332.principal.Models.Token;
import com.it332.principal.Models.User;
import com.it332.principal.Repository.TokenRepository;
import com.it332.principal.Repository.UserRepository;
import com.it332.principal.Security.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import java.util.Optional;



import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class TokenService {

    private static final long EXPIRE_TOKEN = 15; // Token expiration time in minutes

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class); // Logger initialization

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender; // Inject JavaMailSender

    @Autowired
    private Configuration freemarkerConfig;

    @Value("${base.url}")
    private String baseUrl;

    public void forgotPass(String email) {
        // Check if user exists
        User user = userService.getUserByEmail(email);

        if (user == null) {
            // If user does not exist, throw NotFoundException
            throw new NotFoundException("User with email " + email + " not found.");
        }

        // Generate token and create Token instance
        String token = generateToken();
        Token tokenEntity = new Token(token, LocalDateTime.now(), user.getId(), Token.TokenType.PASSWORD_RESET);

        // Save token in TokenRepository
        tokenRepository.save(tokenEntity);

        // Log and send email
        logger.info("Token created successfully for user: {}", user.getEmail());
        logger.info("Sending password reset email to: {}", user.getEmail());

        sendPasswordResetEmail(user.getEmail(), token);
    }

    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Password Reset Request";
        String resetUrl = baseUrl + "/reset-password?token=" + token;

        // Create the model for FreeMarker, using a Map<String, Object>
        Map<String, Object> model = new HashMap<>();
        model.put("resetUrl", resetUrl);
        model.put("baseUrl", baseUrl);

        MimeMessage msg = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);

            // Load the FreeMarker template
            Template t = freemarkerConfig.getTemplate("email-template.ftl");
            // Process the template with the provided model data
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

            helper.setText(html, true); // true indicates HTML content
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(new InternetAddress("principalmailsender@gmail.com", "PrinciPal"));

            // Add the image as an inline attachment
            helper.addInline("logoImage", new ClassPathResource("logo.png")); // Update the path as necessary

            mailSender.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String resetPass(String token, String password) {
        // Retrieve token from TokenRepository
        Optional<Token> tokenEntityOpt = tokenRepository.findByToken(token);
    
        if (tokenEntityOpt.isEmpty()) {
            return "Invalid token.";
        }
    
        Token tokenEntity = tokenEntityOpt.get();
    
        // Check if token is expired
        if (isTokenExpired(tokenEntity.getTokenCreationDate())) {
            return "Token expired.";
        }
    
        // Retrieve the user associated with the token
        User user = userRepository.findById(tokenEntity.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found."));
    
        // Update user password
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    
        // Remove the token after use
        tokenRepository.delete(tokenEntity);
    
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
        // Retrieve token from TokenRepository
        Optional<Token> tokenEntityOpt = tokenRepository.findByToken(token);
        
        // Check if the token exists in the repository
        if (tokenEntityOpt.isEmpty()) {
            return false; // Token not found
        }
    
        // Retrieve the actual Token from the Optional
        Token tokenEntity = tokenEntityOpt.get();
    
        // Check if the token is expired
        return !isTokenExpired(tokenEntity.getTokenCreationDate()); // Return true if the token is still valid
    }

    public void verifyEmail(String token) throws NotFoundException, IllegalArgumentException {
        Optional<Token> optionalToken = tokenRepository.findByToken(token);
        if (!optionalToken.isPresent()) {
            throw new NotFoundException("Token not found.");
        }

        Token emailToken = optionalToken.get();
        
        // Use isTokenExpired method instead of calling isExpired()
        if (isTokenExpired(emailToken.getTokenCreationDate())) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }
        
        User user = userRepository.findById(emailToken.getUserId()).orElse(null);
        if (user == null) {
            throw new NotFoundException("User not found.");
        }

        user.setVerified(true);
        userRepository.save(user);
        
        tokenRepository.delete(emailToken); // Optionally, delete the token after successful verification
    }

    public void sendEmailVerification(User user) {
        if (user == null) {
            throw new NotFoundException("User not found.");
        }
    
        String token = generateToken();
        Token tokenEntity = new Token(token, LocalDateTime.now(), user.getId(), Token.TokenType.EMAIL_VERIFICATION); // Ensure you're using EMAIL_VERIFICATION
        tokenRepository.save(tokenEntity);
        
        logger.info("Token created successfully for user: {}", user.getEmail());
        logger.info("Sending email verification to: {}", user.getEmail());
        
        sendVerificationEmail(user.getEmail(), token);
    }
    

    public void sendVerificationEmail(String to, String token) {
        String subject = "Email Verification Request";
        String verificationUrl = baseUrl + "/verify-email?token=" + token;

        // Create the model for FreeMarker, using a Map<String, Object>
        Map<String, Object> model = new HashMap<>();
        model.put("verificationUrl", verificationUrl);
        model.put("baseUrl", baseUrl);

        MimeMessage msg = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);

            // Load the FreeMarker template
            Template template = freemarkerConfig.getTemplate("email-verify-template.ftl");
            // Process the template with the provided model data
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

            helper.setText(html, true); // true indicates HTML content
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(new InternetAddress("principalmailsender@gmail.com", "PrinciPal"));

            // Add the image as an inline attachment
            helper.addInline("logoImage", new ClassPathResource("logo.png")); // Update the path as necessary

            mailSender.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    


    public boolean isValidEmail(String email) {
        // Basic email validation logic
        return email != null && email.contains("@");
    }
    
    public boolean isEmailVerified(String email) {
        User user = userService.getUserByEmail(email);
        if (user != null) {
            return user.isVerified();
        }
        return false; // If user not found, return false
    }
}
