package com.it332.principal.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.it332.principal.DTO.UserAssociation;
import com.it332.principal.Models.User;
import com.it332.principal.Repository.UserRepository;
import com.it332.principal.Services.UserService;

@Service
public class JwtTokenService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    private UserService userService;

    public User verifyTokenAndTransform(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            User exist = new User();

            // Check if the "sub" claim exists
            if (claims.containsKey("sub")) {
                // Retrieve the value of "sub" claim
                String userId = claims.get("sub", String.class);
                exist = userService.getUserById(userId);

                // Remove "sub" and add "id" with the same value
                // claims.put("id", userId);
                // claims.remove("sub");
            }

            return exist;
        } catch (SignatureException e) {
            // JWT signature validation failed
            throw new IllegalArgumentException("Invalid token. Authentication denied.");
        }
    }
}