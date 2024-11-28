package com.it332.principal.Controllers;

import com.it332.principal.Models.School;
import com.it332.principal.Security.NotFoundException;
import com.it332.principal.Services.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/code")
public class CodeController {
    @Autowired
    private CodeService codeService;

    // @PostMapping("/")
    // public ResponseEntity<Object> forgotPass(@RequestParam String code) {
    // try {
    // // This will check if the user exists and may throw NotFoundException
    // service.forgotPass(email);

    // // If successful, return a success message
    // return ResponseEntity.ok("Email sent"); // This will only be executed if no
    // exception is thrown
    // } catch (IllegalArgumentException e) {
    // return ResponseEntity.badRequest().body("Invalid email format");
    // } catch (NotFoundException e) {
    // // This should trigger for unregistered emails
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email is not
    // registered");
    // } catch (Exception e) {
    // e.printStackTrace();
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    // .body("An error occurred, please try again later");
    // }
    // }

    @GetMapping("/{code}")
    public ResponseEntity<Object> getSchoolByCode(@PathVariable String code) {
        try {
            // Call the service to verify email
            School school = codeService.getSchoolByCode(code);
            // return ResponseEntity.ok("Email verified successfully.");
            return new ResponseEntity<>(school, HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Code not found.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid or expired code.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during fetching school.");
        }
    }

    @GetMapping("/school/{id}")
    public ResponseEntity<String> getReferralCode(@PathVariable String id) {
        try {
            // Call the service to verify email
            String code = codeService.getReferralCode(id);
            // return ResponseEntity.ok("Email verified successfully.");
            return new ResponseEntity<>(code, HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Code not found.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid or expired code.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during fetching code.");
        }
    }
}