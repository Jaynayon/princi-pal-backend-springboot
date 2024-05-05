package com.it332.principal.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    @Value("${jwt.secret}")
    private String secretKey;

    public Claims verifyTokenAndTransform(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

            // Check if the "sub" claim exists
            if (claims.containsKey("sub")) {
                // Retrieve the value of "sub" claim
                String userId = claims.get("sub", String.class);

                // Remove "sub" and add "id" with the same value
                claims.put("id", userId);
                claims.remove("sub");
            }

            return claims;
        } catch (SignatureException e) {
            // JWT signature validation failed
            throw new IllegalArgumentException("Invalid token. Authentication denied.");
        }
    }
}