package com.it332.principal.Security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.it332.principal.Models.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // Retrieve the user object set by the first interceptor
        User user = (User) request.getAttribute("user");

        if (user == null) {
            throw new BadCredentialsException("User not found");
        }

        // Check if the user has the correct position
        if (!"Super administrator".equals(user.getPosition())) {
            throw new BadCredentialsException("Insufficient privilege");
        }

        return true;
    }

}
