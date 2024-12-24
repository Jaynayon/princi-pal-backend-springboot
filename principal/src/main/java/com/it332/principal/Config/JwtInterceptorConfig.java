package com.it332.principal.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.it332.principal.Security.AdminInterceptor;
import com.it332.principal.Security.JwtInterceptor;

@Configuration
public class JwtInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Define paths to exclude
        String[] excludedPaths = { "/api/users/create", "/api/users/exists" };

        // Apply the JWT interceptor to API routes only
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(excludedPaths);

        // Define paths to include for the second interceptor
        String[] selectedPaths = { "/api/users/create/principal", "/api/schools/create", "/api/positions/**",
                "/api/associations/insert", "/api/uacs/create" };

        // Apply the second interceptor only to selected API routes
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns(selectedPaths);
    }

}
