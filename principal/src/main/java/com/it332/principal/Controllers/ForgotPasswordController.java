package com.it332.principal.Controllers;

import com.it332.principal.Security.NotFoundException;
import com.it332.principal.Services.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ForgotPasswordController {
    @Autowired
    private ForgotPasswordService service;

    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPass(@RequestParam String email) {
        try {
            // Call send reset link to email service
            service.forgotPass(email);

            return new ResponseEntity<>("Email sent", HttpStatus.OK); // Return affirmation
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Email sent"); // Return false-positive affirmation
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Email sent"); // Return false-positive affirmation
        } catch (Exception e) {
            // Catching any other unexpected exceptions
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Email sent"); // Return false-positive affirmation
        }
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String password) {
        String result = service.resetPass(token, password);

        if (result.equals("Your password has been successfully updated.")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/test-email")
    public ResponseEntity<String> testEmail(@RequestParam String email) {
        try {
            // Call the service method to send the email
            service.sendPasswordResetEmail(email, "test-token");
            return ResponseEntity.ok("Test email sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send test email: " + e.getMessage());
        }
    }
}
