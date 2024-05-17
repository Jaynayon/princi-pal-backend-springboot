package com.it332.principal.Controllers;

import com.it332.principal.DTO.ErrorMessage;
import com.it332.principal.Security.JwtTokenService;
import com.it332.principal.Security.NotFoundException;
import com.it332.principal.Services.UserService;

import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/authenticate")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenService jwtTokenService;

    @GetMapping("/verify/")
    public ResponseEntity<?> verifyTokenAndTransform(@RequestParam("token") String token) {
        try {
            Claims claims = jwtTokenService.verifyTokenAndTransform(token);
            // Token is valid, return decoded claims
            return ResponseEntity.ok(claims);
        } catch (IllegalArgumentException e) {
            // Invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication denied");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody LoginRequest loginRequest) {
        ErrorMessage err = new ErrorMessage("");
        try {
            String emailOrUsername = loginRequest.getEmailOrUsername();
            String password = loginRequest.getPassword();

            if (userService.validateUser(emailOrUsername, password)) {
                String userId = userService.getUserByEmailUsername(emailOrUsername).getId();
                String token = userService.generateToken(userId);

                // Set the token as a cookie in the response
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.SET_COOKIE, createJwtCookie(token).toString());

                return ResponseEntity.status(HttpStatus.OK)
                        .headers(headers)
                        .body(new LoginResponse(true));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // Unauthorized
                    .body(new LoginResponse(false));
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate school name is detected
            err.setMessage("Failed to get user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(err);
        } catch (NotFoundException e) {
            // This exception is thrown when a no school is detected
            err.setMessage("Failed to get user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(err);
        } catch (Exception e) {
            // Catching any other unexpected exceptions
            e.printStackTrace();
            err.setMessage("Internal server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(err);
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
