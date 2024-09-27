package com.it332.principal.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://principals.engineer",
                                "https://localhost:3000") // Back & frontend domains
                        .allowCredentials(true) // Allow cookies or credentials
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Define allowed HTTP methods
                        .allowedHeaders("*"); // Allow all headers
            }
        };
    }
}
