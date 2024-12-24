package com.it332.principal.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.it332.principal.Models.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        final String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        User user = null;

        // Extract the Bearer token from the Authorization header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);

            // Use JwtTokenService to verify the token
            try {
                user = jwtTokenService.verifyTokenAndTransform(token);

                // Store the user in the request so the second interceptor can access it
                request.setAttribute("user", user);
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid JWT Token");
            }
        } else {
            throw new BadCredentialsException("Authorization header is missing or invalid");
        }

        return true;
    }

}
