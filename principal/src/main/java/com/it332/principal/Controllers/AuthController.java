package com.it332.principal.Controllers;

import com.it332.principal.Models.User;
import com.it332.principal.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/authenticate")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        String emailOrUsername = loginRequest.getEmailOrUsername();
        String password = loginRequest.getPassword();

        boolean isValid = userService.validateUser(emailOrUsername, password);

        LoginResponse respo = new LoginResponse();

        if (isValid) {
            // Attempt to retrieve user by email or username
            User user = userService.getUserByEmail(emailOrUsername);
            if (user == null) {
                user = userService.getUserByUsername(emailOrUsername);
            }

            if (user != null) {
                String userId = user.getId();
                String token = userService.generateToken(userId);

                // Set the token as a cookie in the response
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.SET_COOKIE, createJwtCookie(token).toString());

                respo.setIsMatch(isValid);

                return ResponseEntity.status(HttpStatus.CREATED)
                        .headers(headers)
                        .body(respo);

            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respo);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respo);
        }
    }

    private ResponseCookie createJwtCookie(String token) {
        return ResponseCookie.from("jwt", token)
                // .httpOnly(true) // Make the cookie accessible only via HTTP (not accessible
                // via JavaScript)
                .maxAge(86400) // Set cookie expiration time in seconds (e.g., 86400 seconds = 1 day)
                .sameSite("Lax")
                .secure(false)
                .path("/") // Set the cookie path to root ("/") so that it's accessible across the entire
                           // domain
                .build();
    }

    // Class representing login response (isMatch)
    static class LoginResponse {
        private boolean isMatch = false;

        // Constructor
        public LoginResponse(boolean isMatch) {
            this.isMatch = isMatch;
        }

        public LoginResponse() {
        }

        public boolean getIsMatch() {
            return isMatch;
        }

        public void setIsMatch(boolean result) {
            this.isMatch = result;
        }
    }

    // Class representing login request (email or username and password)
    static class LoginRequest {
        private String emailOrUsername;
        private String password;

        // Getters and setters
        public String getEmailOrUsername() {
            return emailOrUsername;
        }

        public void setEmailOrUsername(String emailOrUsername) {
            this.emailOrUsername = emailOrUsername;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
