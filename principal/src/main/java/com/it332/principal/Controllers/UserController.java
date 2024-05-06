package com.it332.principal.Controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.it332.principal.DTO.ErrorMessage;
import com.it332.principal.Models.School;
import com.it332.principal.Models.User;
import com.it332.principal.Models.UserCredentials;
import com.it332.principal.Security.NotFoundException;
import com.it332.principal.Services.UserService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        // User newUser = userService.createUser(user);
        // // String token = userService.generateToken(newUser.getId()); // Generate JWT token

        // // Set the token as a cookie in the response
        // // HttpHeaders headers = new HttpHeaders();
        // // headers.add(HttpHeaders.SET_COOKIE, createJwtCookie(token).toString());

        // return ResponseEntity.status(HttpStatus.CREATED)
        //         //.headers(headers)
        //         .body(newUser);
        ErrorMessage err = new ErrorMessage("");
        try {
            User newUser = userService.createUser(user); // Corrected method invocation
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate school name is detected
            err.setMessage("Failed to create user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(err);
        } catch (Exception e) {
            // Catching any other unexpected exceptions
            e.printStackTrace();
            err.setMessage("Internal server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(err);
        }
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

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@Valid @PathVariable String id) {
        ErrorMessage err = new ErrorMessage("");
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
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

    @GetMapping("/email/{email}")
    public ResponseEntity<Object> getUserByEmail(@Valid @PathVariable String email) {
        ErrorMessage err = new ErrorMessage("");
        try {
            User user = userService.getUserByEmail(email);
            if (user != null) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
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

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable String id) {
        try {
            userService.deleteUserById(id);
            String successMessage = "User " + id + " is successfully deleted";
            return ResponseEntity.ok().body(successMessage); // User deleted successfully
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error occurred");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable String id, @RequestBody User updateUser) {
        try {
            userService.updateUser(id, updateUser);
            String successMessage = "User " + id + " updated successfully";
            return ResponseEntity.ok().body(successMessage); // User updated successfully
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to update user: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error occurred");
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

}
