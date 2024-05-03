package com.it332.principal.Controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.it332.principal.Models.User;
import com.it332.principal.Models.UserCredentials;
import com.it332.principal.Services.UserService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User newUser = userService.createUser(user);
        String token = userService.generateToken(newUser.getId()); // Generate JWT token

        // Set the token as a cookie in the response
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, createJwtCookie(token).toString());

        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(headers)
                .body(newUser);
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateUser(@RequestBody UserCredentials credentials) {
        String emailOrUsername = credentials.getEmailOrUsername();
        String password = credentials.getPassword();

        if (userService.validateUser(emailOrUsername, password)) {
            return ResponseEntity.ok().build(); // Successful login
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Unauthorized
        }
    }

    @PostMapping("/exists")
    public ResponseEntity<Boolean> checkIfUserExists(@RequestBody UserCredentials credentials) {
        String emailOrUsername = credentials.getEmailOrUsername();
        boolean exists = userService.checkIfUserExists(emailOrUsername);
        return ResponseEntity.ok(exists);
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

}
