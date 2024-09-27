package com.it332.principal.Controllers;

import java.util.List;
import java.util.Map;

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
import com.it332.principal.DTO.UserAdminRequest;
import com.it332.principal.DTO.UserResponse;
import com.it332.principal.Models.User;
import com.it332.principal.Models.UserCredentials;
import com.it332.principal.Security.NotFoundException;
import com.it332.principal.Services.UserService;

@RestController
@CrossOrigin(origins = "https://localhost:3000")
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        ErrorMessage err = new ErrorMessage("");

        try {
            // Check if the user is trying to create a Super Administrator or Principal
            // position
            String position = user.getPosition().toLowerCase();
            if ("super administrator".equals(position) || "principal".equals(position)) {
                err.setMessage("Creation of '" + position + "' position is not allowed.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(err);
            }
            User newUser = userService.createUser(user); // Corrected method invocation
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate school name is detected
            err.setMessage("Failed to create user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(err);
        } catch (NotFoundException e) {
            err.setMessage("Position not found: " + e.getMessage());
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

    @PostMapping("/create/principal")
    public ResponseEntity<Object> createPrincipal(@RequestBody UserAdminRequest user) {
        ErrorMessage err = new ErrorMessage("");
        try {
            User newUser = userService.createUser(user); // Corrected method invocation
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate school name is detected
            err.setMessage("Failed to create user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(err);
        } catch (NotFoundException e) {
            err.setMessage("Position not found: " + e.getMessage());
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
    public ResponseEntity<Object> getUserByEmailUsername(@Valid @PathVariable String id) {
        ErrorMessage err = new ErrorMessage("");
        try {
            UserResponse user = userService.getUserAssociationsById(id);
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

    @PostMapping("/schools")
    public ResponseEntity<Object> getUserById(@RequestBody Map<String, String> requestBody) {
        ErrorMessage err = new ErrorMessage("");
        try {
            String emailOrUsername = requestBody.get("emailOrUsername");
            if (emailOrUsername == null || emailOrUsername.isEmpty()) {
                throw new IllegalArgumentException("emailOrUsername is required");
            }

            UserResponse user = userService.getUserByEmailUsername(emailOrUsername);
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

    @PatchMapping("/{id}/password")
    public ResponseEntity<Object> updateUserPassword(@PathVariable String id,
            @RequestBody Map<String, String> passwords) {
        String newPassword = passwords.get("newPassword");

        try {
            userService.updateUserPassword(id, newPassword);
            return ResponseEntity.ok().body("Password updated successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to update password: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error occurred");
        }
    }

    @PatchMapping("/{id}/avatar")
    public ResponseEntity<Object> updateUserAvatar(@PathVariable String id,
            @RequestBody Map<String, String> requestBody) {
        try {
            String avatar = requestBody.get("avatar");
            userService.updateUserAvatar(id, avatar);
            String successMessage = "User " + id + " updated successfully";
            return ResponseEntity.ok().body(successMessage); // User updated successfully
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to update user: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error occurred");
        }
    }

}
