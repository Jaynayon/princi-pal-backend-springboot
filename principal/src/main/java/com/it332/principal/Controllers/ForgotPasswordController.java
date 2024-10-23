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
        // This will check if the user exists and may throw NotFoundException
        service.forgotPass(email);
        
        // If successful, return a success message
        return ResponseEntity.ok("Email sent"); // This will only be executed if no exception is thrown
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body("Invalid email format");
    } catch (NotFoundException e) {
        // This should trigger for unregistered emails
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email is not registered");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred, please try again later");
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

    @GetMapping("/validate-token")
    public ResponseEntity<String> validateToken(@RequestParam String token) {
        boolean isValid = service.validateToken(token);
        if (isValid) {
            return ResponseEntity.ok("Valid token");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        }
    }
}