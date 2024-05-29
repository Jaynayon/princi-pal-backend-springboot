package com.it332.principal.Controllers;

import com.it332.principal.Models.User;
import com.it332.principal.Security.JwtTokenService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authenticate")
public class TokenVerificationController {

    @Autowired
    private JwtTokenService jwtTokenService;

    @GetMapping("/verifys/")
    public ResponseEntity<?> verifyTokenAndTransform(@RequestParam("token") String token) {
        try {
            User claims = jwtTokenService.verifyTokenAndTransform(token);
            // Token is valid, return decoded claims
            return ResponseEntity.ok(claims);
        } catch (IllegalArgumentException e) {
            // Invalid token
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication denied");
        }
    }
}