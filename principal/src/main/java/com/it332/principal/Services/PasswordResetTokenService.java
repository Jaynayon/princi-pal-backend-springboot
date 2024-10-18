package com.it332.principal.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage; 
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails; 
import org.springframework.security.core.userdetails.UsernameNotFoundException; 
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import com.it332.principal.Models.PasswordResetToken;
import com.it332.principal.Models.User;
import com.it332.principal.Repository.PasswordResetTokenRepository;
import com.it332.principal.Repository.UserRepository;
import com.it332.principal.DTO.UserDTO;


@Service
public class PasswordResetTokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                new HashSet<>());
    }

    public User save(UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setFname(userDTO.getFname()); // Now defined in UserDTO
        user.setMname(userDTO.getMname()); // Now defined in UserDTO
        user.setLname(userDTO.getLname()); // Now defined in UserDTO
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userRepository.save(user);
    }

    public String sendEmail(User user) {
        try {
            String resetLink = generateResetToken(user);

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom("your_email@example.com");
            msg.setTo(user.getEmail());

            msg.setSubject("Password Reset Request");
            msg.setText("Hello \n\n" + "Please click on this link to reset your password: " + resetLink + ". \n\n"
                    + "Regards, \n" + "ABC");

            javaMailSender.send(msg);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    public String generateResetToken(User user) {
        UUID uuid = UUID.randomUUID();
        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(30);
        
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(uuid.toString());
        resetToken.setExpiryDateTime(expiryDateTime);
        
        // Save the token and return the reset link
        passwordResetTokenRepository.save(resetToken);
        return "http://localhost:4000/resetPassword/" + resetToken.getToken();
    }
    

    public boolean hasExpired(LocalDateTime expiryDateTime) { 
        LocalDateTime currentDateTime = LocalDateTime.now();
        return expiryDateTime.isBefore(currentDateTime);
    }
}
