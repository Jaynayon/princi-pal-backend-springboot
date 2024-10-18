package com.it332.principal.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.it332.principal.DTO.UserDTO;
import com.it332.principal.Models.PasswordResetToken;
import com.it332.principal.Models.User;
import com.it332.principal.Repository.PasswordResetTokenRepository;
import com.it332.principal.Repository.UserRepository;
import com.it332.principal.Services.PasswordResetTokenService;

import java.util.Optional;

@Controller
@CrossOrigin(origins = "https://localhost:3000")
public class PasswordResetTokenController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordResetTokenService userDetailsService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword() {
        return ResponseEntity.ok("Forgot Password page reached"); // For testing purposes
    }

    @PostMapping("/forgotPassword")
    public String forgotPasswordProcess(@ModelAttribute UserDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.getEmail());
        if (user != null) {
            String output = userDetailsService.sendEmail(user);
            if ("success".equals(output)) {
                return "redirect:/forgotPassword?success";
            }
        }
        return "redirect:/login?error"; // Redirect on failure
    }

    @GetMapping("/resetPassword/{token}")
    public String resetPasswordForm(@PathVariable String token, Model model) {
        Optional<PasswordResetToken> resetOpt = tokenRepository.findByToken(token);
        if (resetOpt.isPresent() && !userDetailsService.hasExpired(resetOpt.get().getExpiryDateTime())) {
            PasswordResetToken reset = resetOpt.get(); // Safely retrieve the value
            model.addAttribute("email", reset.getUser().getEmail());
            return "resetPassword"; // Return view for reset password
        }
        return "redirect:/forgotPassword?error"; // Redirect if token is invalid or expired
    }

    @PostMapping("/resetPassword")
    public String passwordResetProcess(@ModelAttribute UserDTO userDTO) {
        User user = userRepository.findByEmail(userDTO.getEmail());
        if (user != null) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userRepository.save(user); // Save the updated user
        }
        return "redirect:/login"; // Redirect after password reset
    }
}
