package com.it332.principal.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid JWT Token");
            }
        } else {
            throw new BadCredentialsException("Authorization header is missing or invalid");
        }

        // Set up the authentication in the SecurityContext if token is valid
        if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user, null, null); // Assuming no roles/authorities for simplicity

            // additional details
            authToken.setDetails(request.getRemoteAddr());

            SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
            throw new BadCredentialsException("Invalid token or user not found");
        }

        return true;
    }

}
