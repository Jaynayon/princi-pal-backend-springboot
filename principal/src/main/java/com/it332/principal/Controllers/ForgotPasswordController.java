package com.it332.principal.Controllers;

import com.it332.principal.Services.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "https://localhost:3000")
public class ForgotPasswordController {
    @Autowired
    private ForgotPasswordService service;

    @PostMapping("/forgot-password")
    public String forgotPass(@RequestParam String email) {
        String response = service.forgotPass(email);

        if (response.startsWith("Invalid")) {
            return response; // Return the error message directly
        }
        
        // Construct the reset link using the token returned by the service
        return "https://localhost:3000/reset-password?token=" + response;
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send test email: " + e.getMessage());
        }
    }
}
